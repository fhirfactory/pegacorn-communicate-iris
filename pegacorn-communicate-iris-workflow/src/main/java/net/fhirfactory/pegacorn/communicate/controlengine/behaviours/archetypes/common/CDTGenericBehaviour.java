package net.fhirfactory.pegacorn.communicate.controlengine.behaviours.archetypes.common;

import net.fhirfactory.pegacorn.camel.BaseRouteBuilder;
import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeTypeEnum;
import net.fhirfactory.pegacorn.common.model.topicid.DataParcelToken;
import net.fhirfactory.pegacorn.communicate.controlengine.behaviours.MatrixBehavioursWorkshop;
import net.fhirfactory.pegacorn.communicate.controlengine.behaviours.archetypes.framework.manager.MTBehaviourRouteManager;
import net.fhirfactory.pegacorn.internals.communicate.workflow.model.CDTTypeEnum;
import net.fhirfactory.pegacorn.internals.communicate.workflow.model.behaviours.CDTBehaviourTypeEnum;
import net.fhirfactory.pegacorn.communicate.iris.statespace.twinpathway.encapsulatorroutes.common.CDTBehaviourRouteNames;
import net.fhirfactory.pegacorn.communicate.iris.statespace.twinpathway.orchestrator.common.CDTOrchestratorBase;
import net.fhirfactory.pegacorn.components.interfaces.topology.PegacornTopologyFactoryInterface;
import net.fhirfactory.pegacorn.components.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.deployment.topology.model.common.TopologyNode;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.WorkUnitProcessorTopologyNode;
import net.fhirfactory.pegacorn.workshops.base.Workshop;
import org.apache.camel.CamelContext;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

public abstract class CDTGenericBehaviour extends BaseRouteBuilder {
    protected abstract Logger specifyLogger();

    private TopologyNode behaviourNodeElement;
    private CDTBehaviourRouteNames nameSet;

    abstract protected String specifyBehaviourName();
    abstract protected String specifyBehaviourVersion();
    abstract protected CDTOrchestratorBase getMyTwinOrchestrationService();
    abstract protected CDTBehaviourTypeEnum specifyBehaviourType();
    abstract protected void executePostInitialisationActivities();
    abstract protected CDTTypeEnum specifyTwinType();

    @Inject
    private MTBehaviourRouteManager routeManager;

    @Inject
    private ProcessingPlantInterface processingPlant;

    @Inject
    private TopologyIM topologyIM;

    @Inject
    private MatrixBehavioursWorkshop mtBehavioursWorkshop;

    public CDTGenericBehaviour(){
        super();
        getLogger().trace(".initialise(): Setting the WUP nameSet, which is the set of Route EndPoints that the WUP Framework will use to link various enablers");
        nameSet = new CDTBehaviourRouteNames(this.specifyBehaviourName());
    }

    protected Logger getLogger(){
        return(specifyLogger());
    }

    protected CDTBehaviourRouteNames getNameSet(){
        return(nameSet);
    }

    public String getBehaviourName(){
        return(specifyBehaviourName());
    }

    public String getBehaviourVersion(){
        return(specifyBehaviourVersion());
    }

    public Workshop getBehaviourWorkshop(){
        return(mtBehavioursWorkshop);
    }

    public TopologyIM getTopologyIM(){
        return(topologyIM);
    }

    public TopologyNode getBehaviourNodeElement(){
        return(this.behaviourNodeElement);
    }

    public CDTTypeEnum getTwinType(){return(specifyTwinType());}

    public CDTBehaviourTypeEnum getBehaviourType(){
        return(specifyBehaviourType());
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
        getLogger().trace(".initialise(): BehaviourName --> {}", this.specifyBehaviourName());
        getLogger().trace(".initialise(): BehaviourVersion --> {}", this.specifyBehaviourVersion());
        getLogger().trace(".initialise(): Initialising the Workshop");
        getBehaviourWorkshop().initialiseWorkshop();
        getLogger().trace(".initialise(): Setting up the wupTopologyElement (NodeElement) instance, which is the Topology Server's representation of this WUP ");
        buildBehaviourNodeElement();
        getLogger().trace(".initialise(): Now call the WUP Framework constructure - which builds the Petasos framework around this WUP");
        buildBehaviourFramework(this.getContext());
        getLogger().trace(".initialise(): Now invoking subclass initialising function(s)");
        executePostInitialisationActivities();
        getLogger().debug(".initialise(): Exit");
    }

    private void buildBehaviourNodeElement(){
        getLogger().debug(".buildBehaviourNodeElement(): Entry, Workshop --> {}", getBehaviourName());
        PegacornTopologyFactoryInterface topologyFactory = processingPlant.getTopologyFactory();
        WorkUnitProcessorTopologyNode newWUPNode = topologyFactory.addWorkUnitProcessor(getBehaviourName(), getBehaviourVersion(),getBehaviourWorkshop().getWorkshopNode(),TopologyNodeTypeEnum.WUP);
        getLogger().trace(".buildBehaviourNodeElement(): WUP Created");
        this.behaviourNodeElement = newWUPNode;
    }

    public void buildBehaviourFramework(CamelContext routeContext) {
        getLogger().debug(".buildBehaviourFramework(): Entry");
        // By default, the set of Topics this WUP subscribes to will be empty - as we need to the Behaviours to initialise first to tell us.
        Set<DataParcelToken> emptyTopicList = new HashSet<DataParcelToken>();
        routeManager.buildBehaviourRoutes(this.getBehaviourNodeElement(), this.getTwinType(), this.getBehaviourType());
        getLogger().debug(".buildBehaviourFramework(): Exit");
    }

    protected String ingresFeed(){
        return(nameSet.getBehaviourIngresPoint());
    }

    protected String egressFeed(){
        return(nameSet.getBehaviourEgressPoint());
    }
}
