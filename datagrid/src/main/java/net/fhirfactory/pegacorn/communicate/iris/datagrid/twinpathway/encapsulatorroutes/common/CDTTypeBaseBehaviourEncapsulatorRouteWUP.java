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
package net.fhirfactory.pegacorn.communicate.iris.datagrid.twinpathway.encapsulatorroutes.common;

import net.fhirfactory.pegacorn.camel.BaseRouteBuilder;
import net.fhirfactory.pegacorn.core.model.component.SoftwareComponent;
import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;
import net.fhirfactory.pegacorn.core.model.componentid.ComponentTypeTypeEnum;
import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeFunctionFDNToken;
import net.fhirfactory.pegacorn.communicate.iris.datagrid.twinpathway.orchestrator.common.CDTOrchestratorBase;
import net.fhirfactory.pegacorn.components.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.components.interfaces.topology.PegacornTopologyFactoryInterface;
import net.fhirfactory.pegacorn.components.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.dricats.model.petasos.wup.PetasosTaskJobCard;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.core.model.SoftwareComponent;
import net.fhirfactory.pegacorn.core.model.topology.nodes.WorkUnitProcessorSoftwareComponent;
import net.fhirfactory.pegacorn.core.model.topology.nodes.WorkshopSoftwareComponent;
import net.fhirfactory.dricats.internals.fhir.r4.internal.topics.FHIRElementTopicFactory;
import net.fhirfactory.pegacorn.petasos.tasking.moa.brokers.PetasosMOAServicesBroker;
import net.fhirfactory.pegacorn.petasos.tasking.fulfilment.naming.RouteElementNames;
import net.fhirfactory.pegacorn.petasos.tasking.participants.manager.DataParcelSubscriptionMapIM;
import net.fhirfactory.dricats.model.petasos.participant.IntraSubsystemPubSubParticipant;
import net.fhirfactory.dricats.model.petasos.participant.IntraSubsystemPubSubParticipantIdentifier;
import net.fhirfactory.dricats.model.petasos.participant.PubSubParticipant;
import net.fhirfactory.dricats.model.petasos.wup.valuesets.WUPArchetypeEnum;
import org.apache.camel.CamelContext;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public abstract class CDTTypeBaseBehaviourEncapsulatorRouteWUP extends BaseRouteBuilder{
    abstract protected Logger getLogger();

    private SoftwareComponent topologyNode;
    private PetasosTaskJobCard wupInstanceJobCard;
    private RouteElementNames nameSet;

    @Inject
    private PetasosMOAServicesBroker servicesBroker;

    @Inject
    private TopologyIM wupTopologyProxy;

    @Inject
    private FHIRElementTopicFactory fhirTopicIDBuilder;

    @Inject
    private ProcessingPlantInterface processingPlantServices;
    
    @Inject 
    private DataParcelSubscriptionMapIM topicServer;

    public CDTTypeBaseBehaviourEncapsulatorRouteWUP() {
        super();
    }

    /**
     * This function essentially establishes the WUP itself, by first calling all the (abstract classes realised within subclasses)
     * and setting the core attributes of the WUP. Then, it executes the buildWUPFramework() function, which invokes the Petasos
     * framework around this WUP.
     *
     * It is automatically called by the CDI framework following Constructor invocation (see @PostConstruct tag).
     */
    @PostConstruct
    protected void initialise(){
        getLogger().debug(".initialise(): Entry, Default Post Constructor function to setup the WUP");
        getLogger().trace(".initialise(): wupInstanceName --> {}", this.getWUPInstanceName());
        getLogger().trace(".initialise(): wupInstanceVersion --> {}", this.getWUPVersion());
//        getLogger().trace(".iniitalise(): wupFunctionToken --> {}", this.getWUPFunctionToken());
        getLogger().trace(".initialise(): Setting if the WUP uses the Petasos generated Ingres/Egress Endpoints");
        getLogger().trace(".initialise(): Setting up the wupTopologyElement (NodeElement) instance, which is the Topology Server's representation of this WUP ");
        buildWUPNodeElement();
        getLogger().trace(".initialise(): Setting the WUP nameSet, which is the set of Route EndPoints that the WUP Framework will use to link various enablers");
        nameSet = new RouteElementNames(getTopologyNode().getComponentFDN().getToken());
        getLogger().trace(".initialise(): Now call the WUP Framework constructure - which builds the Petasos framework around this WUP");
        buildWUPFramework(this.getContext());
        getLogger().trace(".initialise(): Now invoking subclass initialising function(s)");
        executePostInitialisationActivities();
        getLogger().debug(".initialise(): Exit");
    }

    // To be implemented methods (in Specialisations)

    abstract protected String specifyTwinTypeName();
    abstract protected String specifyTwinTypeVersion();
    abstract protected CDTOrchestratorBase getOrchestrator();
    
    public String getWUPInstanceName() {
    	return(specifyTwinTypeName());
    }
    
    public String getWUPVersion() {
    	return(specifyTwinTypeVersion());
    }
    
    private String getWUPWorkshopName() {
    	return("StateSpace");
    }
    
    
    public ComponentIdType getWUPIdentifier() {
        ComponentIdType wupID = getTopologyNode().getComponentID();
    	return(wupID);
    }

    protected void executePostInitialisationActivities(){
        // Subclasses can optionally override
    }

    public void registerNodeInstantiation(){
        getLogger().debug(".registerTopologyElementInstantiation(): Entry");
//        getOrchestrator().reg(this.topologyNode);
        getLogger().debug(".registerTopologyElementInstantiation(): Exit");
    }

    public void buildWUPFramework(CamelContext routeContext) {
        getLogger().debug(".buildWUPFramework(): Entry");
        // By default, the set of Topics this WUP subscribes to will be empty - as we need to the Behaviours to initialise first to tell us.
        List<DataParcelManifest> emptyTopicList = new ArrayList<DataParcelManifest>();
        WorkUnitProcessorSoftwareComponent wupNode = (WorkUnitProcessorSoftwareComponent)this.topologyNode;
        servicesBroker.registerWorkUnitProcessor(wupNode, emptyTopicList, this.getWUPArchetype());
        getLogger().debug(".buildWUPFramework(): Exit");
    }

    public TopologyNodeFunctionFDNToken getWUPFunctionToken() {
        return (this.getTopologyNode().getNodeFunctionFDN().getFunctionToken());
    }

    public String ingresFeed() {
        return (getRouteElementNameSet().getEndPointWUPContainerIngresProcessorIngres());
    }

    public String egressFeed() {
        return ("BeanDriven-DynamicRouter");
    }

    public PetasosMOAServicesBroker getServicesBroker(){
        return(this.servicesBroker);
    }

    public TopologyIM getTopologyServer(){
        return(this.wupTopologyProxy);
    }

    public SoftwareComponent getTopologyNode() {
        return topologyNode;
    }

    public void setTopologyNode(SoftwareComponent wupTopologyNodeElement) {
        this.topologyNode = wupTopologyNodeElement;
    }

    public RouteElementNames getRouteElementNameSet() {
        return nameSet;
    }

    public WUPArchetypeEnum getWUPArchetype() {
        return (WUPArchetypeEnum.WUP_NATURE_LADON_BEHAVIOUR_WRAPPER);
    }

    public FHIRElementTopicFactory getFHIRTopicIDBuilder(){
        return(this.fhirTopicIDBuilder);
    }
    
    public void requestSubscription(List<DataParcelManifest> tokenList) {
    	
    }

    private void buildWUPNodeElement(){
        getLogger().debug(".buildWUPNodeElement(): Entry, Workshop->{}, WUPInstanceName->{}, WUPVersion->{}", getWUPWorkshopName(), getWUPInstanceName(), getWUPVersion());
        WorkshopSoftwareComponent workshopNode = processingPlantServices.getWorkshop(getWUPWorkshopName());
        getLogger().trace(".buildWUPNodeElement(): Entry, Workshop NodeElement--> {}", workshopNode);
        PegacornTopologyFactoryInterface topologyFactory = getProcessingPlantServices().getTopologyFactory();
        WorkUnitProcessorSoftwareComponent newWUPNode = topologyFactory.createWorkUnitProcessor(getWUPInstanceName(), getWUPVersion(), workshopNode, ComponentTypeTypeEnum.WUP);
        this.setTopologyNode(newWUPNode);
        getOrchestrator().registerEncapsulatorWUPNode(newWUPNode);
        getLogger().debug(".buildWUPNodeElement(): Exit");
    }
    
    
    public void subscribeToTopics(Set<DataParcelManifest> subscribedTopics){
        getLogger().debug(".uowTopicSubscribe(): Entry, subscribedTopics --> {}, wupNode --> {}", subscribedTopics, getWUPIdentifier() );
        if(subscribedTopics.isEmpty()){
        	getLogger().debug(".uowTopicSubscribe(): Not topics provided as input, exiting");
            return;
        }
        Iterator<DataParcelManifest> topicIterator = subscribedTopics.iterator();
        while(topicIterator.hasNext()) {
            DataParcelManifest currentTopicID = topicIterator.next();
            getLogger().trace(".uowTopicSubscribe(): wupNode->{} is subscribing to UoW Content Topic->{}", getTopologyNode().getComponentFDN().getToken(), currentTopicID);
            PubSubParticipant participant = new PubSubParticipant();
            IntraSubsystemPubSubParticipant subsystemParticipant = new IntraSubsystemPubSubParticipant();
            IntraSubsystemPubSubParticipantIdentifier subsystemParticipantIdentifier = new IntraSubsystemPubSubParticipantIdentifier(getTopologyNode().getComponentFDN().getToken());
            subsystemParticipant.setIdentifier(subsystemParticipantIdentifier);
            participant.setIntraSubsystemParticipant(subsystemParticipant);
            topicServer.addTopicSubscriber(currentTopicID, participant);
        }
        getLogger().debug(".uowTopicSubscribe(): Exit");
    }

    //
    // Getters (and Setters)
    //


    public ProcessingPlantInterface getProcessingPlantServices() {
        return processingPlantServices;
    }

    public DataParcelSubscriptionMapIM getTopicServer() {
        return topicServer;
    }

    public TopologyIM getWupTopologyProxy() {
        return wupTopologyProxy;
    }
}
