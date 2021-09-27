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
package net.fhirfactory.pegacorn.communicate.iris.statespace.twinpathway.orchestrator.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.fhirfactory.pegacorn.communicate.iris.statespace.twinpathway.forwardermap.CDTInstance2EdgeForwarderMap;
import net.fhirfactory.pegacorn.communicate.iris.statespace.twinpathway.orchestrator.common.caches.*;
import net.fhirfactory.pegacorn.components.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.components.dataparcel.DataParcelTypeDescriptor;
import net.fhirfactory.pegacorn.components.dataparcel.valuesets.DataParcelDirectionEnum;
import net.fhirfactory.pegacorn.components.dataparcel.valuesets.DataParcelNormalisationStatusEnum;
import net.fhirfactory.pegacorn.components.dataparcel.valuesets.DataParcelValidationStatusEnum;
import net.fhirfactory.pegacorn.components.dataparcel.valuesets.PolicyEnforcementPointApprovalStatusEnum;
import net.fhirfactory.pegacorn.components.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.deployment.topology.model.common.TopologyNode;
import net.fhirfactory.pegacorn.internals.communicate.entities.common.valuesets.CommunicateResourceTypeEnum;
import net.fhirfactory.pegacorn.internals.communicate.workflow.model.CDTIdentifier;
import net.fhirfactory.pegacorn.internals.communicate.workflow.model.behaviours.*;
import net.fhirfactory.pegacorn.internals.communicate.workflow.model.stimulus.CDTStimulus;
import net.fhirfactory.pegacorn.internals.communicate.workflow.model.stimulus.CDTStimulusIdentifier;
import net.fhirfactory.pegacorn.internals.communicate.workflow.model.stimulus.CDTStimulusPackage;
import net.fhirfactory.pegacorn.internals.fhir.r4.internal.topics.FHIRElementTopicFactory;
import net.fhirfactory.pegacorn.petasos.core.moa.pathway.naming.RouteElementNames;
import net.fhirfactory.pegacorn.petasos.datasets.manager.DataParcelSubscriptionMapIM;
import net.fhirfactory.pegacorn.petasos.model.pathway.WorkUnitTransportPacket;
import net.fhirfactory.pegacorn.petasos.model.pubsub.IntraSubsystemPubSubParticipant;
import net.fhirfactory.pegacorn.petasos.model.pubsub.IntraSubsystemPubSubParticipantIdentifier;
import net.fhirfactory.pegacorn.petasos.model.pubsub.PubSubParticipant;
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
import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeFDN;
import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeFDNToken;

public abstract class CDTOrchestratorBase {

    private ConcurrentHashMap<CDTBehaviourIdentifier, CDTBehaviourCentricInclusionFilterRulesInterface> inclusionFilterMap;
    private ConcurrentHashMap<CDTBehaviourIdentifier, CDTBehaviourCentricExclusionFilterRulesInterface> exclusionFilterMap;
    private ConcurrentHashMap<CDTIdentifier, CDTBehaviourIdentifier> twinInstanceBusyStatus;
    private List<DataParcelManifest> subscribedTopicList;
    private ConcurrentHashMap<CDTBehaviourIdentifier, TopologyNode> behaviourSet;
    private CDTUoWCache uowCacheMT;
    private CDTStimulusCache CDTStimulusCache;
    private CDTInstanceWorkQueues twinWorkQueues;
    private CDTCausalityMapCache causalityMap;
	private CDTOutcomeCache CDTOutcomeCache;
	private TopologyNode associatedBehaviourEncapsulatorNode;
	private boolean initialised;
	private ObjectMapper jsonObjectMapper;
    
    static final long INITIAL_DELAY = 1000; // Delay (in Milliseconds) before scanning of the Per-Instance Activity Queue occurs
    static final long DELAY = 500; // Delay (in Milliseconds) between scans of the Per-Instance Activity Queue

    @Inject
    private ProcessingPlantInterface processingPlant;
    
    @Inject
    private DataParcelSubscriptionMapIM topicServer;
    
    @Resource
    ManagedScheduledExecutorService scheduler;

	@Inject
	private TopologyIM topologyIM;
    
    @Inject 
    private CamelContext camelCTX;

    @Inject
	private FHIRElementTopicFactory fhirTopicBuilder;

    @Inject
	private FHIRContextUtility fhirContextUtility;

    @Inject
	private CDTInstance2EdgeForwarderMap twinInstance2EdgeForwarderMap;

    public CDTOrchestratorBase(){
        this.inclusionFilterMap = new ConcurrentHashMap<>();
        this.exclusionFilterMap = new ConcurrentHashMap<>();
        this.twinInstanceBusyStatus = new ConcurrentHashMap<>();
        this.subscribedTopicList = new ArrayList<>();
        this.twinWorkQueues = new CDTInstanceWorkQueues();
        this.twinInstanceBusyStatus = new ConcurrentHashMap<>();
        this.uowCacheMT = new CDTUoWCache();
        this.CDTStimulusCache = new CDTStimulusCache();
        this.behaviourSet = new ConcurrentHashMap<>();
		this.causalityMap = new CDTCausalityMapCache();
		this.CDTOutcomeCache = new CDTOutcomeCache();
		this.associatedBehaviourEncapsulatorNode = null;
		this.initialised = false;
		//
		this.jsonObjectMapper = new ObjectMapper();
		JavaTimeModule module = new JavaTimeModule();
		jsonObjectMapper.registerModule(module);
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
	 3. Map Stimulus --> TwinInstance via Behaviour Input Filters
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

	public void registerNewStimulus(CDTStimulus newStimulus){
		CDTStimulusCache.addStimulus(newStimulus);
		stimulusFeeder2DigitalTwin(newStimulus);
	}

	// Stage 3

	public void stimulusFeeder2DigitalTwin(CDTStimulus newStimulus){
		Enumeration<CDTBehaviourIdentifier> behaviourList = inclusionFilterMap.keys();
		while(behaviourList.hasMoreElements()) {
			CDTBehaviourIdentifier currentBehaviour = behaviourList.nextElement();
			CDTBehaviourCentricInclusionFilterRulesInterface inclusionFilter = inclusionFilterMap.get(currentBehaviour);
			List<CDTIdentifier> digitalTwinIdentifiers = inclusionFilter.positiveDynamicFilterTwinInstancesForStimulus(newStimulus);
			for(CDTIdentifier currentTwin: digitalTwinIdentifiers){
				boolean allowStimulusToProgress = true;
				if(exclusionFilterMap.containsKey(currentBehaviour)){
					CDTBehaviourCentricExclusionFilterRulesInterface exclusionFilter = exclusionFilterMap.get(currentBehaviour);
					if(!exclusionFilter.blockStimulusForDigitalTwinInstance(newStimulus, currentTwin)){
						allowStimulusToProgress = false;
					}
				}
				if(allowStimulusToProgress){
					CDTStimulusPackage newStimulusPackage = new CDTStimulusPackage(newStimulus.getOriginalUoW(), currentTwin, currentBehaviour, newStimulus);
					twinWorkQueues.addStimulus2Queue(currentTwin, newStimulusPackage);
				}
			}
		}
	}

	// Stage 4 & 5

	public void manifestor(){
		Set<CDTIdentifier> twinsWithQueuedTraffic = twinWorkQueues.getTwinsWithQueuedWork();
		for(CDTIdentifier currentTwinInstance : twinsWithQueuedTraffic){
			// Check to see if the DigitalTwin is BUSY by checking the instaanceBusyStatus map.
			if(!twinInstanceBusyStatus.containsKey(currentTwinInstance)) {
				// DigitalTwin isn't busy - so let's give it something to do...
				CDTStimulusPackage nextStimulusForTwinInstance = twinWorkQueues.getNextStimulusPackage(currentTwinInstance);
				twinInstanceBusyStatus.put(currentTwinInstance, nextStimulusForTwinInstance.getTargetBehaviour());
				injectStimulusPackageIntoBehaviourQueue(nextStimulusForTwinInstance.getTargetBehaviour(), nextStimulusForTwinInstance );
				lockTwinInstance(currentTwinInstance, nextStimulusForTwinInstance.getTargetBehaviour());
			}
		}
	}

	// Stage 5

	private void injectStimulusPackageIntoBehaviourQueue(CDTBehaviourIdentifier behaviourId, CDTStimulusPackage stimulusPkg) {
		getLogger().debug(".injectStimulusPackageIntoBehaviourQueue(): Entry, BehaviourIdentifier --> {}, StimulusPackage --> {}", behaviourId, stimulusPkg);
		if(!behaviourSet.containsKey(behaviourId)) {
			return;
		}
		TopologyNode behaviourNode = behaviourSet.get(behaviourId);
		RouteElementNames nameSet = new RouteElementNames(behaviourNode.getNodeFDN().getToken());
		ProducerTemplate prodTemplate = camelCTX.createProducerTemplate();
		prodTemplate.sendBody(nameSet.getEndPointWUPContainerIngresProcessorIngres(), stimulusPkg);
	}


	// Stage 7

	public void registerBehaviourCompletion(CDTBehaviourOutcomeSet outcomes){
		if(outcomes == null){
			return;
		}
		unlockTwinInstance(outcomes.getSourceTwin());
		CDTOutcomeCache.addOutcomeSet(outcomes);
		ArrayList<UoWIdentifier> completedUoWProcessing = new ArrayList<>();
		for(CDTBehaviourOutcome outcome: outcomes.getOutcomes()) {
			CDTStimulus currentStimulus = CDTStimulusCache.getStimulus(outcome.getSourceStimulus());
			causalityMap.setProcessingStatus(CDTBehaviourProcessingOfStimulusStatusEnum.PROCESSING_STATUS_FINISHED,outcome.getSourceBehaviour(), outcome.getAffectingTwin(), outcome.getSourceStimulus(), currentStimulus.getOriginalUoW() );
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
		Set<CDTStimulusIdentifier> stimulusSet = CDTStimulusCache.getStimulusAssociatedWithUoW(uowId);
		ArrayList<CDTBehaviourOutcome> outcomeList = new ArrayList<>();
		for(CDTStimulusIdentifier stimulusId: stimulusSet){
			outcomeList.addAll(CDTOutcomeCache.getStimulusDerivedOutcomes(stimulusId));
		}
		ArrayList<UoWPayload> payloadList = new ArrayList<>();
		UoW theUoW = uowCacheMT.getUoW(uowId);
		for(CDTBehaviourOutcome outcome: outcomeList) {
			UoWPayload payload = new UoWPayload();
			try {
				String resourceAsString = getJsonObjectMapper().writeValueAsString(outcome.getOutputResource());
				payload.setPayload(resourceAsString);
				if (outcome.isEchoedToFHIR()) {
					Set<String> forwarderSet = twinInstance2EdgeForwarderMap.getForwarderAssociation2DigitalTwin(outcome.getAffectingTwin());
					for (String forwarderInstance : forwarderSet) {
						DataParcelTypeDescriptor payloadTopic = fhirTopicBuilder.newTopicToken(outcome.getOutputResource().getResourceESRType().name(), "4.0.1");
						DataParcelManifest manifest = new DataParcelManifest();
						manifest.setContentDescriptor(payloadTopic);
						manifest.setNormalisationStatus(DataParcelNormalisationStatusEnum.DATA_PARCEL_CONTENT_NORMALISATION_TRUE);
						manifest.setValidationStatus(DataParcelValidationStatusEnum.DATA_PARCEL_CONTENT_VALIDATED_TRUE);
						manifest.setDataParcelFlowDirection(DataParcelDirectionEnum.WORKFLOW_OUTPUT_DATA_PARCEL);
						manifest.setEnforcementPointApprovalStatus(PolicyEnforcementPointApprovalStatusEnum.POLICY_ENFORCEMENT_POINT_APPROVAL_NEGATIVE);
						manifest.setSourceSystem(processingPlant.getIPCServiceName());
						manifest.setIntendedTargetSystem(forwarderInstance);
						payload.setPayloadManifest(manifest);
						theUoW.getEgressContent().addPayloadElement(payload);
					}
				} else {
					DataParcelTypeDescriptor payloadTopic = fhirTopicBuilder.newTopicToken(outcome.getOutputResource().getResourceESRType().name(), "4.0.1");
					DataParcelManifest manifest = new DataParcelManifest();
					manifest.setContentDescriptor(payloadTopic);
					manifest.setNormalisationStatus(DataParcelNormalisationStatusEnum.DATA_PARCEL_CONTENT_NORMALISATION_TRUE);
					manifest.setValidationStatus(DataParcelValidationStatusEnum.DATA_PARCEL_CONTENT_VALIDATED_TRUE);
					manifest.setDataParcelFlowDirection(DataParcelDirectionEnum.WORKFLOW_OUTPUT_DATA_PARCEL);
					manifest.setEnforcementPointApprovalStatus(PolicyEnforcementPointApprovalStatusEnum.POLICY_ENFORCEMENT_POINT_APPROVAL_NEGATIVE);
					manifest.setSourceSystem(processingPlant.getIPCServiceName());
					payload.setPayloadManifest(manifest);
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
		for(CDTStimulusIdentifier stimulusId: stimulusSet){
			CDTOutcomeCache.removeOutcomesDerivedFromStimulus(stimulusId);
			CDTStimulusCache.removeStimulus(stimulusId);
		}
		causalityMap.purgeUoWFromMap(uowId);
	}

	// Stage 9

	public void publishUoW(UoW outputUoW){
		getLogger().debug(".publishUoW(): Entry, outputUoW --> {}", outputUoW);
		TopologyNodeFDN wupInstanceKey = uowCacheMT.getAssociatedWUPKey(outputUoW.getInstanceID());
		TopologyNode node = topologyIM.getNode(wupInstanceKey);
		getLogger().trace(".publishUoW(): Node Element retrieved --> {}", node);
		TopologyNodeFDNToken wupToken = node.getNodeFDN().getToken();
		getLogger().trace(".publishUoW(): wupFunctionToken (NodeElementFunctionToken) for this activity --> {}", wupToken);
		RouteElementNames elementNames = new RouteElementNames(wupToken);
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
    
    public CommunicateResourceTypeEnum getTwinType() {
    	return(specifyTwinType());
    }
    
	protected List<DataParcelManifest> getSubscribedTopicList(){
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
    abstract protected CommunicateResourceTypeEnum specifyTwinType();

    //
    //
    // Configuration Methods for Behaviour Encapsulation Route WUP
    //
    //

	public void requestSubscription(List<DataParcelManifest> topicList) {
		getSubscribedTopicList().addAll(topicList);
		if(getAssociatedBehaviourEncapsulatorNode() != null) {
			for (DataParcelManifest dataParcelToken : getSubscribedTopicList()) {
				PubSubParticipant participant = new PubSubParticipant();
				IntraSubsystemPubSubParticipant intraParticipant = new IntraSubsystemPubSubParticipant();
				IntraSubsystemPubSubParticipantIdentifier participantIdentifier = new IntraSubsystemPubSubParticipantIdentifier(getAssociatedBehaviourEncapsulatorNode().getNodeFDN().getToken());
				intraParticipant.setIdentifier(participantIdentifier);
				participant.setIntraSubsystemParticipant(intraParticipant);
				topicServer.addTopicSubscriber(dataParcelToken, participant);
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
    
	public void registerBehaviourCentricInclusiveFilterRules(CDTBehaviourIdentifier behaviourId, CDTBehaviourCentricInclusionFilterRulesInterface inclusionRules) {
		getLogger().debug(".registerBehaviourCentricInclusiveFilterRules(): Entry, BehaviourIdentifier --> {}", behaviourId);
		inclusionFilterMap.put(behaviourId, inclusionRules);
		getLogger().debug(".registerBehaviourCentricInclusiveFilterRules(): Exit");
	}

	public void registerBehaviourCentricExclusiveFilterRules(CDTBehaviourIdentifier behaviourId, CDTBehaviourCentricExclusionFilterRulesInterface exclusionRules) {
		getLogger().debug(".registerBehaviourCentricExclusiveFilterRules(): Entry, BehaviourIdentifier --> {}", behaviourId);
		exclusionFilterMap.put(behaviourId, exclusionRules);
		getLogger().debug(".registerBehaviourCentricExclusiveFilterRules(): Exit");
	}
    
	public void registerBehaviourNode(CDTBehaviourIdentifier behaviourId, TopologyNode behaviourNode) {
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

    public void lockTwinInstance(CDTIdentifier twinIdentifier, CDTBehaviourIdentifier behaviourIdentifier){
        if(twinInstanceBusyStatus.containsKey(twinIdentifier)){
            return;
        }
        twinInstanceBusyStatus.put(twinIdentifier, behaviourIdentifier);
    }

    public void unlockTwinInstance(CDTIdentifier twinIdentifier){
        if(twinInstanceBusyStatus.containsKey(twinIdentifier)){
            twinInstanceBusyStatus.remove(twinIdentifier);
        }
    }

    public boolean isTwinLocked(CDTIdentifier twinIdentifier){
        if(twinInstanceBusyStatus.containsKey(twinIdentifier)){
            return(true);
        } else {
            return(false);
        }
    }

    public boolean isBusy(CDTIdentifier twinIdentifier){
        return(isTwinLocked(twinIdentifier));
    }

    public CDTBehaviourIdentifier getTwinActiveBehaviour(CDTIdentifier twinIdentifier){
        if(twinInstanceBusyStatus.containsKey(twinIdentifier)){
            return(twinInstanceBusyStatus.get(twinIdentifier));
        } else {
            return(null);
        }
    }
}
