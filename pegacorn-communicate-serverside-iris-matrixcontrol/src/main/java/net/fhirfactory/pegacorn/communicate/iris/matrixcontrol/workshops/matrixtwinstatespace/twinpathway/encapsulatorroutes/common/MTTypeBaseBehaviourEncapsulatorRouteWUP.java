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
package net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.workshops.matrixtwinstatespace.twinpathway.encapsulatorroutes.common;

import net.fhirfactory.pegacorn.camel.BaseRouteBuilder;
import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeFunctionFDNToken;
import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeTypeEnum;
import net.fhirfactory.pegacorn.common.model.topicid.TopicToken;
import net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.workshops.matrixtwinstatespace.twinpathway.orchestrator.common.MTOrchestratorBase;
import net.fhirfactory.pegacorn.components.interfaces.topology.PegacornTopologyFactoryInterface;
import net.fhirfactory.pegacorn.components.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.deployment.topology.model.common.TopologyNode;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.WorkUnitProcessorTopologyNode;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.WorkshopTopologyNode;
import net.fhirfactory.pegacorn.internals.fhir.r4.internal.topics.FHIRElementTopicIDBuilder;
import net.fhirfactory.pegacorn.petasos.core.moa.brokers.PetasosMOAServicesBroker;
import net.fhirfactory.pegacorn.petasos.core.moa.pathway.naming.RouteElementNames;
import net.fhirfactory.pegacorn.petasos.datasets.manager.TopicIM;
import net.fhirfactory.pegacorn.petasos.model.wup.WUPArchetypeEnum;
import net.fhirfactory.pegacorn.petasos.model.wup.WUPIdentifier;
import net.fhirfactory.pegacorn.petasos.model.wup.WUPJobCard;
import org.apache.camel.CamelContext;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public abstract class MTTypeBaseBehaviourEncapsulatorRouteWUP extends BaseRouteBuilder{
    abstract protected Logger getLogger();

    private TopologyNode topologyNode;
    private WUPJobCard wupInstanceJobCard;
    private RouteElementNames nameSet;

    @Inject
    private PetasosMOAServicesBroker servicesBroker;

    @Inject
    private TopologyIM wupTopologyProxy;

    @Inject
    private FHIRElementTopicIDBuilder fhirTopicIDBuilder;

    @Inject
    private ProcessingPlantInterface processingPlantServices;
    
    @Inject 
    private TopicIM topicServer;

    public MTTypeBaseBehaviourEncapsulatorRouteWUP() {
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
        nameSet = new RouteElementNames(getWUPFunctionToken());
        getLogger().trace(".initialise(): Now call the WUP Framework constructure - which builds the Petasos framework around this WUP");
        buildWUPFramework(this.getContext());
        getLogger().trace(".initialise(): Now invoking subclass initialising function(s)");
        executePostInitialisationActivities();
        getLogger().debug(".initialise(): Exit");
    }

    // To be implemented methods (in Specialisations)

    abstract protected String specifyTwinTypeName();
    abstract protected String specifyTwinTypeVersion();
    abstract protected MTOrchestratorBase getOrchestrator();
    
    public String getWUPInstanceName() {
    	return(specifyTwinTypeName());
    }
    
    public String getWUPVersion() {
    	return(specifyTwinTypeVersion());
    }
    
    private String getWUPWorkshopName() {
    	return("StateSpace");
    }
    
    
    public WUPIdentifier getWUPIdentifier() {
    	WUPIdentifier wupID = new WUPIdentifier(this.getTopologyNode().getNodeFDN().getToken());
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
        Set<TopicToken> emptyTopicList = new HashSet<TopicToken>();
        WorkUnitProcessorTopologyNode wupNode = (WorkUnitProcessorTopologyNode)this.topologyNode;
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

    public TopologyNode getTopologyNode() {
        return topologyNode;
    }

    public void setTopologyNode(TopologyNode wupTopologyNodeElement) {
        this.topologyNode = wupTopologyNodeElement;
    }

    public RouteElementNames getRouteElementNameSet() {
        return nameSet;
    }

    public WUPArchetypeEnum getWUPArchetype() {
        return (WUPArchetypeEnum.WUP_NATURE_LADON_BEHAVIOUR_WRAPPER);
    }

    public FHIRElementTopicIDBuilder getFHIRTopicIDBuilder(){
        return(this.fhirTopicIDBuilder);
    }
    
    public void requestSubscription(List<TopicToken> tokenList) {
    	
    }

    private void buildWUPNodeElement(){
        getLogger().debug(".buildWUPNodeElement(): Entry, Workshop->{}, WUPInstanceName->{}, WUPVersion->{}", getWUPWorkshopName(), getWUPInstanceName(), getWUPVersion());
        WorkshopTopologyNode workshopNode = processingPlantServices.getWorkshop(getWUPWorkshopName());
        getLogger().trace(".buildWUPNodeElement(): Entry, Workshop NodeElement--> {}", workshopNode);
        PegacornTopologyFactoryInterface topologyFactory = getProcessingPlantServices().getTopologyFactory();
        WorkUnitProcessorTopologyNode newWUPNode = topologyFactory.addWorkUnitProcessor(getWUPInstanceName(), getWUPVersion(), workshopNode, TopologyNodeTypeEnum.WUP);
        this.setTopologyNode(newWUPNode);
        getOrchestrator().registerEncapsulatorWUPNode(newWUPNode);
        getLogger().debug(".buildWUPNodeElement(): Exit");
    }
    
    
    public void subscribeToTopics(Set<TopicToken> subscribedTopics){
        getLogger().debug(".uowTopicSubscribe(): Entry, subscribedTopics --> {}, wupNode --> {}", subscribedTopics, getWUPIdentifier() );
        if(subscribedTopics.isEmpty()){
        	getLogger().debug(".uowTopicSubscribe(): Not topics provided as input, exiting");
            return;
        }
        Iterator<TopicToken> topicIterator = subscribedTopics.iterator();
        while(topicIterator.hasNext()) {
            TopicToken currentTopicID = topicIterator.next();
            getLogger().trace(".uowTopicSubscribe(): wupNode->{} is subscribing to UoW Content Topic->{}", getTopologyNode().getNodeFDN().getToken(), currentTopicID);
            topicServer.addTopicSubscriber(currentTopicID, getTopologyNode().getNodeFDN().getToken() );
        }
        getLogger().debug(".uowTopicSubscribe(): Exit");
    }

    //
    // Getters (and Setters)
    //


    public ProcessingPlantInterface getProcessingPlantServices() {
        return processingPlantServices;
    }

    public TopicIM getTopicServer() {
        return topicServer;
    }

    public TopologyIM getWupTopologyProxy() {
        return wupTopologyProxy;
    }
}
