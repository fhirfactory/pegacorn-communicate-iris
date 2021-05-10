/*
 * Copyright (c) 2020 Mark A. Hunter
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.workshops.matrixtwinstatespace.twinpathway.orchestrator.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeFDN;
import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeFunctionFDNToken;
import net.fhirfactory.pegacorn.common.model.topicid.TopicToken;
import net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.model.*;
import net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.workshops.matrixtwinstatespace.twinpathway.forwardermap.MatrixTwinInstance2EdgeForwarderMap;
import net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.workshops.matrixtwinstatespace.twinpathway.orchestrator.common.caches.*;
import net.fhirfactory.pegacorn.components.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.deployment.topology.model.common.TopologyNode;
import net.fhirfactory.pegacorn.internals.fhir.r4.internal.topics.FHIRElementTopicIDBuilder;
import net.fhirfactory.pegacorn.petasos.core.moa.pathway.naming.RouteElementNames;
import net.fhirfactory.pegacorn.petasos.datasets.manager.TopicIM;
import net.fhirfactory.pegacorn.petasos.model.pathway.WorkUnitTransportPacket;
import net.fhirfactory.pegacorn.petasos.model.resilience.activitymatrix.moa.ParcelStatusElement;
import net.fhirfactory.pegacorn.petasos.model.resilience.parcel.ResilienceParcelProcessingStatusEnum;
import net.fhirfactory.pegacorn.petasos.model.uow.UoW;
import net.fhirfactory.pegacorn.petasos.model.uow.UoWIdentifier;
import net.fhirfactory.pegacorn.petasos.model.uow.UoWPayload;
import net.fhirfactory.pegacorn.petasos.model.uow.UoWProcessingOutcomeEnum;
import net.fhirfactory.pegacorn.petasos.model.wup.WUPActivityStatusEnum;
import net.fhirfactory.pegacorn.petasos.model.wup.WUPJobCard;
import net.fhirfactory.pegacorn.util.FHIRContextUtility;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.inject.Inject;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public abstract class MTOrchestratorBase {

    private ConcurrentHashMap<MTBehaviourIdentifier, MTBehaviourCentricInclusionFilterRulesInterface> inclusionFilterMap;
    private ConcurrentHashMap<MTBehaviourIdentifier, MTBehaviourCentricExclusionFilterRulesInterface> exclusionFilterMap;
    private ConcurrentHashMap<MTIdentifier, MTBehaviourIdentifier> twinInstanceBusyStatus;
    private List<TopicToken> subscribedTopicList;
    private ConcurrentHashMap<MTBehaviourIdentifier, TopologyNode> behaviourSet;
    private MTUoWCache uowCacheMT;
    private MTStimulusCache MTStimulusCache;
    private MTInstanceWorkQueues twinWorkQueues;
    private MTCausalityMapCache causalityMap;
	private MTOutcomeCache MTOutcomeCache;
	private TopologyNode associatedBehaviourEncapsulatorNode;
	private boolean initialised;
	private ObjectMapper jsonObjectMapper;
    
    static final long INITIAL_DELAY = 1000; // Delay (in Milliseconds) before scanning of the Per-Instance Activity Queue occurs
    static final long DELAY = 500; // Delay (in Milliseconds) between scans of the Per-Instance Activity Queue

    @Inject
    private ProcessingPlantInterface processingPlant;
    
    @Inject
    private TopicIM topicServer;
    
    @Resource
    ManagedScheduledExecutorService scheduler;

	@Inject
	private TopologyIM topologyIM;
    
    @Inject 
    private CamelContext camelCTX;

    @Inject
	private FHIRElementTopicIDBuilder fhirTopicBuilder;

    @Inject
	private FHIRContextUtility fhirContextUtility;

    @Inject
	private MatrixTwinInstance2EdgeForwarderMap twinInstance2EdgeForwarderMap;

    public MTOrchestratorBase(){
        this.inclusionFilterMap = new ConcurrentHashMap<>();
        this.exclusionFilterMap = new ConcurrentHashMap<>();
        this.twinInstanceBusyStatus = new ConcurrentHashMap<>();
        this.subscribedTopicList = new ArrayList<>();
        this.twinWorkQueues = new MTInstanceWorkQueues();
        this.twinInstanceBusyStatus = new ConcurrentHashMap<>();
        this.uowCacheMT = new MTUoWCache();
        this.MTStimulusCache = new MTStimulusCache();
        this.behaviourSet = new ConcurrentHashMap<>();
		this.causalityMap = new MTCausalityMapCache();
		this.MTOutcomeCache = new MTOutcomeCache();
		this.associatedBehaviourEncapsulatorNode = null;
		this.initialised = false;
		//
		this.jsonObjectMapper = new ObjectMapper();
		this.jsonObjectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
    }

	public ObjectMapper getJsonObjectMapper() {
		return jsonObjectMapper;
	}

	@PostConstruct
    protected void initialise(){
    	if(!initialised) {
			processingPlant.initialisePlant();
			this.scheduler.scheduleAtFixedRate(this::manifestor, INITIAL_DELAY, DELAY, TimeUnit.MILLISECONDS);
			initialised = true;
		}
    }

    protected Logger getLogger(){
    return(specifyLogger());
    }

    public void initialiseService(){
    	initialise();
	}

    /** ------------------------- Overall Flow --------------------

	 1. UoW Reception
	 2. Stimulus Instantiation (from UoW Ingres Content)
	 3. Map Stimulus --> TwinInstance via Behaivour Input Filters
	 4. Wait for TwinInstance to be Idle
	 5. Insert into Behaviour Instance Queue / Lock TwinInstance
	 6. Execute Behaviour / Update Stimulus Processing Status
	 7. Collect Outcomes / Unlock TwinInstance / Update Stimulus Processing Status
	 8. Aggregate Outcomes / Finalise Stimulus Processing
	 9. Publish Completed UoW / Flush Status / Clear Pools

	 ------------------------------------------------------------- */

	// Stage 1

	public void registerNewUoW(UoW newUoW, WUPJobCard jobCard, ParcelStatusElement statusElement, TopologyNodeFDN wupKey){
		uowCacheMT.addUoW(newUoW, jobCard, statusElement, wupKey );
	}

	// Stage 2 & 3

	public void registerNewStimulus(MTStimulus newStimulus){
		MTStimulusCache.addStimulus(newStimulus);
		stimulusFeeder2DigitalTwin(newStimulus);
	}

	// Stage 3

	public void stimulusFeeder2DigitalTwin(MTStimulus newStimulus){
		Enumeration<MTBehaviourIdentifier> behaviourList = inclusionFilterMap.keys();
		while(behaviourList.hasMoreElements()) {
			MTBehaviourIdentifier currentBehaviour = behaviourList.nextElement();
			MTBehaviourCentricInclusionFilterRulesInterface inclusionFilter = inclusionFilterMap.get(currentBehaviour);
			List<MTIdentifier> digitalTwinIdentifiers = inclusionFilter.positiveDynamicFilterTwinInstancesForStimulus(newStimulus);
			for(MTIdentifier currentTwin: digitalTwinIdentifiers){
				if(exclusionFilterMap.containsKey(currentBehaviour)){
					MTBehaviourCentricExclusionFilterRulesInterface exclusionFilter = exclusionFilterMap.get(currentBehaviour);
					if(!exclusionFilter.blockStimulusForDigitalTwinInstance(newStimulus, currentTwin)){
						MTStimulusPackage newStimulusPackage = new MTStimulusPackage(newStimulus.getOriginalUoW(), currentTwin, currentBehaviour, newStimulus);
						twinWorkQueues.addStimulus2Queue(currentTwin, newStimulusPackage);
					}
				} else {
					MTStimulusPackage newStimulusPackage = new MTStimulusPackage(newStimulus.getOriginalUoW(), currentTwin, currentBehaviour, newStimulus);
					twinWorkQueues.addStimulus2Queue(currentTwin, newStimulusPackage);
				}
			}
		}
	}

	// Stage 4 & 5

	public void manifestor(){
		Set<MTIdentifier> twinsWithQueuedTraffic = twinWorkQueues.getTwinsWithQueuedWork();
		for(MTIdentifier currentTwinInstance : twinsWithQueuedTraffic){
			// Check to see if the DigitalTwin is BUSY by checking the instaanceBusyStatus map.
			if(!twinInstanceBusyStatus.containsKey(currentTwinInstance)) {
				// DigitalTwin isn't busy - so let's give it something to do...
				MTStimulusPackage nextStimulusForTwinInstance = twinWorkQueues.getNextStimulusPackage(currentTwinInstance);
				twinInstanceBusyStatus.put(currentTwinInstance, nextStimulusForTwinInstance.getTargetBehaviour());
				injectStimulusPackageIntoBehaviourQueue(nextStimulusForTwinInstance.getTargetBehaviour(), nextStimulusForTwinInstance );
				lockTwinInstance(currentTwinInstance, nextStimulusForTwinInstance.getTargetBehaviour());
			}
		}
	}

	// Stage 5

	private void injectStimulusPackageIntoBehaviourQueue(MTBehaviourIdentifier behaviourId, MTStimulusPackage stimulusPkg) {
		getLogger().debug(".injectStimulusPackageIntoBehaviourQueue(): Entry, BehaviourIdentifier --> {}, StimulusPackage --> {}", behaviourId, stimulusPkg);
		if(!behaviourSet.containsKey(behaviourId)) {
			return;
		}
		TopologyNode behaviourNode = behaviourSet.get(behaviourId);
		RouteElementNames nameSet = new RouteElementNames(behaviourNode.getNodeFunctionFDN().getFunctionToken());
		ProducerTemplate prodTemplate = camelCTX.createProducerTemplate();
		prodTemplate.sendBody(nameSet.getEndPointWUPContainerIngresProcessorIngres(), stimulusPkg);
	}


	// Stage 7

	public void registerBehaviourCompletion(MTBehaviourOutcomeSet outcomes){
		if(outcomes == null){
			return;
		}
		unlockTwinInstance(outcomes.getSourceTwin());
		MTOutcomeCache.addOutcomeSet(outcomes);
		ArrayList<UoWIdentifier> completedUoWProcessing = new ArrayList<>();
		for(MTBehaviourOutcome outcome: outcomes.getOutcomes()) {
			MTStimulus currentStimulus = MTStimulusCache.getStimulus(outcome.getSourceStimulus());
			causalityMap.setProcessingStatus(MTBehaviourProcessingOfStimulusStatusEnum.PROCESSING_STATUS_FINISHED,outcome.getSourceBehaviour(), outcome.getAffectingTwin(), outcome.getSourceStimulus(), currentStimulus.getOriginalUoW() );
			if(causalityMap.checkForCompletionOfProcessingByAllBehavioursForAllTwins(currentStimulus.getOriginalUoW())){
				if(!completedUoWProcessing.contains(currentStimulus.getOriginalUoW()))
					completedUoWProcessing.add(currentStimulus.getOriginalUoW());
			}
		}
		for(UoWIdentifier uowId: completedUoWProcessing){
			aggregateAndPublishOutcomes(uowId);
		}
	}

	// Stage 8

	public void aggregateAndPublishOutcomes(UoWIdentifier uowId){
		if(uowId == null){
			return;
		}
		Set<MTStimulusIdentifier> stimulusSet = MTStimulusCache.getStimulusAssociatedWithUoW(uowId);
		ArrayList<MTBehaviourOutcome> outcomeList = new ArrayList<>();
		for(MTStimulusIdentifier stimulusId: stimulusSet){
			outcomeList.addAll(MTOutcomeCache.getStimulusDerivedOutcomes(stimulusId));
		}
		ArrayList<UoWPayload> payloadList = new ArrayList<>();
		UoW theUoW = uowCacheMT.getUoW(uowId);
		for(MTBehaviourOutcome outcome: outcomeList) {
			UoWPayload payload = new UoWPayload();
			try {
				String resourceAsString = getJsonObjectMapper().writeValueAsString(outcome.getOutputResource());
				payload.setPayload(resourceAsString);
				if (outcome.isEchoedToFHIR()) {
					Set<String> forwarderSet = twinInstance2EdgeForwarderMap.getForwarderAssociation2DigitalTwin(outcome.getAffectingTwin());
					for (String forwarderInstance : forwarderSet) {
						TopicToken payloadTopic = fhirTopicBuilder.createTopicToken(outcome.getOutputResource().getResourceType().name(), "4.0.1");
						payloadTopic.addDescriminator("Destination", forwarderInstance);
						payload.setPayloadTopicID(payloadTopic);
						theUoW.getEgressContent().addPayloadElement(payload);
					}
				} else {
					TopicToken payloadTopic = fhirTopicBuilder.createTopicToken(outcome.getOutputResource().getResourceType().name(), "4.0.1");
					payload.setPayloadTopicID(payloadTopic);
					theUoW.getEgressContent().addPayloadElement(payload);
				}
				theUoW.setProcessingOutcome(UoWProcessingOutcomeEnum.UOW_OUTCOME_SUCCESS);
			} catch (JsonProcessingException e) {
				getLogger().error(".aggregateAndPublishOutcomes(): Cannot encode ESR to JSON String, error --> {}", e.toString());
				theUoW.setProcessingOutcome(UoWProcessingOutcomeEnum.UOW_OUTCOME_FAILED);
				theUoW.setFailureDescription(e.toString());
				break;
			}
		}
		publishUoW(theUoW);
		// Now Clean Up
		for(MTStimulusIdentifier stimulusId: stimulusSet){
			MTOutcomeCache.removeOutcomesDerivedFromStimulus(stimulusId);
			MTStimulusCache.removeStimulus(stimulusId);
		}
		causalityMap.purgeUoWFromMap(uowId);
	}

	// Stage 9

	public void publishUoW(UoW outputUoW){
		getLogger().debug(".publishUoW(): Entry, outputUoW --> {}", outputUoW);
		TopologyNodeFDN wupInstanceKey = uowCacheMT.getAssociatedWUPKey(outputUoW.getInstanceID());
		TopologyNode node = topologyIM.getNode(wupInstanceKey);
		getLogger().trace(".publishUoW(): Node Element retrieved --> {}", node);
		TopologyNodeFunctionFDNToken wupFunctionToken = node.getNodeFunctionFDN().getFunctionToken();
		getLogger().trace(".publishUoW(): wupFunctionToken (NodeElementFunctionToken) for this activity --> {}", wupFunctionToken);
		RouteElementNames elementNames = new RouteElementNames(wupFunctionToken);
		WUPJobCard jobCard = uowCacheMT.getAssociatedJobCard(outputUoW.getInstanceID());
		ParcelStatusElement statusElement = uowCacheMT.getAssociatedStatusElement(outputUoW.getInstanceID());
		WorkUnitTransportPacket transportPacket = new WorkUnitTransportPacket(jobCard.getActivityID(), Date.from(Instant.now()), outputUoW);
		switch (outputUoW.getProcessingOutcome()) {
			case UOW_OUTCOME_SUCCESS:
				getLogger().trace(".receiveFromWUP(): UoW was processed successfully - updating JobCard/StatusElement to FINISHED!");
				jobCard.setCurrentStatus(WUPActivityStatusEnum.WUP_ACTIVITY_STATUS_FINISHED);
				jobCard.setRequestedStatus(WUPActivityStatusEnum.WUP_ACTIVITY_STATUS_FINISHED);
				statusElement.setParcelStatus(ResilienceParcelProcessingStatusEnum.PARCEL_STATUS_FINISHED);
				statusElement.setEntryDate(Date.from(Instant.now()));
				break;
			case UOW_OUTCOME_NOTSTARTED:
			case UOW_OUTCOME_INCOMPLETE:
			case UOW_OUTCOME_FAILED:
			default:
				getLogger().trace(".receiveFromWUP(): UoW was not processed or processing failed - updating JobCard/StatusElement to FAILED!");
				jobCard.setCurrentStatus(WUPActivityStatusEnum.WUP_ACTIVITY_STATUS_FAILED);
				jobCard.setRequestedStatus(WUPActivityStatusEnum.WUP_ACTIVITY_STATUS_FAILED);
				statusElement.setParcelStatus(ResilienceParcelProcessingStatusEnum.PARCEL_STATUS_FAILED);
				statusElement.setEntryDate(Date.from(Instant.now()));
				break;
		}
		transportPacket.setCurrentJobCard(jobCard);
		transportPacket.setCurrentParcelStatus(statusElement);
		ProducerTemplate prodTemplate = camelCTX.createProducerTemplate();
		prodTemplate.sendBody(elementNames.getEndPointWUPContainerEgressProcessorIngres(), transportPacket);
	}

    //
    // 
    // General Getters/Setters
    //
    //
    
    public MTTypeEnum getTwinType() {
    	return(specifyTwinType());
    }
    
	protected List<TopicToken> getSubscribedTopicList(){
		return(this.subscribedTopicList);
	}
	
	protected TopologyNode getAssociatedBehaviourEncapsulatorNode() {
		return(this.associatedBehaviourEncapsulatorNode);
	}

    //
    //
    // Abstracted Methods to be Implemented by sub-types
    //
    //

	abstract protected Logger specifyLogger();
    abstract protected MTTypeEnum specifyTwinType();

    //
    //
    // Configuration Methods for Behaviour Encapsulation Route WUP
    //
    //

	public void requestSubscription(List<TopicToken> topicList) {
		getSubscribedTopicList().addAll(topicList);
		if(getAssociatedBehaviourEncapsulatorNode() != null) {
			for (TopicToken topicToken : getSubscribedTopicList()) {
				topicServer.addTopicSubscriber(topicToken, getAssociatedBehaviourEncapsulatorNode().getContainingNodeFDN().getToken());
			}
		}
	}

	public void registerEncapsulatorWUPNode(TopologyNode encapsulatorNode){
		this.associatedBehaviourEncapsulatorNode = encapsulatorNode;
	}

	//
    //
    // Business / Configuration Methods for Behaviours
    //
    //
    
	public void registerBehaviourCentricInclusiveFilterRules(MTBehaviourIdentifier behaviourId, MTBehaviourCentricInclusionFilterRulesInterface inclusionRules) {
		getLogger().debug(".registerBehaviourCentricInclusiveFilterRules(): Entry, BehaviourIdentifier --> {}", behaviourId);
		inclusionFilterMap.put(behaviourId, inclusionRules);
		getLogger().debug(".registerBehaviourCentricInclusiveFilterRules(): Exit");
	}

	public void registerBehaviourCentricExclusiveFilterRules(MTBehaviourIdentifier behaviourId, MTBehaviourCentricExclusionFilterRulesInterface exclusionRules) {
		getLogger().debug(".registerBehaviourCentricExclusiveFilterRules(): Entry, BehaviourIdentifier --> {}", behaviourId);
		exclusionFilterMap.put(behaviourId, exclusionRules);
		getLogger().debug(".registerBehaviourCentricExclusiveFilterRules(): Exit");
	}
    
	public void registerBehaviourNode(MTBehaviourIdentifier behaviourId, TopologyNode behaviourNode) {
		getLogger().debug(".registerBehaviourNode() Entry, BehaviourIdentifier --> {}, NodeElement --> {}", behaviourId, behaviourNode);
		if(!behaviourSet.containsKey(behaviourId)) {
			behaviourSet.put(behaviourId,  behaviourNode);
		}
		getLogger().debug(".registerBehaviourNode() Exit");
	}
    
	//
	//
    // Twin Instance Active/Busy Status Logic
	//
	//

    public void lockTwinInstance(MTIdentifier twinIdentifier, MTBehaviourIdentifier behaviourIdentifier){
        if(twinInstanceBusyStatus.containsKey(twinIdentifier)){
            return;
        }
        twinInstanceBusyStatus.put(twinIdentifier, behaviourIdentifier);
    }

    public void unlockTwinInstance(MTIdentifier twinIdentifier){
        if(twinInstanceBusyStatus.containsKey(twinIdentifier)){
            twinInstanceBusyStatus.remove(twinIdentifier);
        }
    }

    public boolean isTwinLocked(MTIdentifier twinIdentifier){
        if(twinInstanceBusyStatus.containsKey(twinIdentifier)){
            return(true);
        } else {
            return(false);
        }
    }

    public boolean isBusy(MTIdentifier twinIdentifier){
        return(isTwinLocked(twinIdentifier));
    }

    public MTBehaviourIdentifier getTwinActiveBehaviour(MTIdentifier twinIdentifier){
        if(twinInstanceBusyStatus.containsKey(twinIdentifier)){
            return(twinInstanceBusyStatus.get(twinIdentifier));
        } else {
            return(null);
        }
    }
}
