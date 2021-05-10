package net.fhirfactory.pegacorn.communicate.iris;

import net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.common.archetypes.ClusterServiceDeliverySubsystemPropertyFile;
import net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.communicate.iris.im.CommunicateIrisIMPropertyFile;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.SolutionTopologyNode;
import net.fhirfactory.pegacorn.processingplant.ProcessingPlant;


public abstract class CommunicateIrisProcessingPlant extends ProcessingPlant {

    public CommunicateIrisProcessingPlant(){
        super();
    }

    @Override
    protected ClusterServiceDeliverySubsystemPropertyFile specifyPropertyFile() {
        ClusterServiceDeliverySubsystemPropertyFile subsystemPropertyFile = (ClusterServiceDeliverySubsystemPropertyFile)specifyCommunicateIrisIMPropertyFile();
        return (subsystemPropertyFile);
    }

    abstract protected CommunicateIrisIMPropertyFile specifyCommunicateIrisIMPropertyFile();

    @Override
    public SolutionTopologyNode getSolutionNode() {
        return (getTopologyIM().getSolutionTopology());
    }
}
