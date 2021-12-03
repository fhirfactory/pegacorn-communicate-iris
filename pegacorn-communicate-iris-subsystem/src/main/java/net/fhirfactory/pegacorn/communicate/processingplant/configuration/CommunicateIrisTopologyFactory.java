/*
 * Copyright (c) 2021 Mark A. Hunter
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
package net.fhirfactory.pegacorn.communicate.processingplant.configuration;

import net.fhirfactory.pegacorn.communicate.common.CommunicateIrisNames;
import net.fhirfactory.pegacorn.core.model.topology.nodes.*;
import net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.common.segments.ports.interact.ClusteredInteractHTTPServerPortSegment;
import net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.common.segments.ports.interact.StandardInteractClientPortSegment;
import net.fhirfactory.pegacorn.deployment.topology.factories.archetypes.common.PetasosEnabledSubsystemTopologyFactory;
import net.fhirfactory.pegacorn.core.model.topology.nodes.common.EndpointProviderInterface;
import net.fhirfactory.pegacorn.util.PegacornEnvironmentProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class CommunicateIrisTopologyFactory extends PetasosEnabledSubsystemTopologyFactory {
    private static final Logger LOG = LoggerFactory.getLogger(CommunicateIrisTopologyFactory.class);

    @Inject
    private PegacornEnvironmentProperties pegacornEnvironmentProperties;

    @Inject
    private CommunicateIrisNames names;

    @Override
    protected Logger specifyLogger() {
        return (LOG);
    }

    @Override
    protected String specifyPropertyFileName() {
        LOG.info(".specifyPropertyFileName(): Entry");
        String configurationFileName = pegacornEnvironmentProperties.getMandatoryProperty("DEPLOYMENT_CONFIG_FILE");
        if(configurationFileName == null){
            throw(new RuntimeException("Cannot load configuration file!!!! (SUBSYSTEM-CONFIG_FILE="+configurationFileName+")"));
        }
        LOG.info(".specifyPropertyFileName(): Exit, filename->{}", configurationFileName);
        return configurationFileName;
    }

    @Override
    protected Class specifyPropertyFileClass() {
        return (CommunicateIrisConfigurationFile.class);
    }

    @Override
    protected ProcessingPlantSoftwareComponent buildSubsystemTopology() {
        SubsystemTopologyNode subsystemTopologyNode = addSubsystemNode(getTopologyIM().getSolutionTopology());
        BusinessServiceTopologyNode businessServiceTopologyNode = addBusinessServiceNode(subsystemTopologyNode);
        DeploymentSiteTopologyNode deploymentSiteTopologyNode = addDeploymentSiteNode(businessServiceTopologyNode);
        ClusterServiceTopologyNode clusterServiceTopologyNode = addClusterServiceNode(deploymentSiteTopologyNode);

        PlatformTopologyNode platformTopologyNode = addPlatformNode(clusterServiceTopologyNode);
        ProcessingPlantSoftwareComponent processingPlantSoftwareComponent = addPegacornProcessingPlant(platformTopologyNode);
        addPrometheusPort(processingPlantSoftwareComponent);
        addJolokiaPort(processingPlantSoftwareComponent);
        addKubeLivelinessPort(processingPlantSoftwareComponent);
        addKubeReadinessPort(processingPlantSoftwareComponent);
        addEdgeAnswerPort(processingPlantSoftwareComponent);
        addAllJGroupsEndpoints(processingPlantSoftwareComponent);

        // Unique to Whispers
        getLogger().trace(".buildSubsystemTopology(): Add the httpClient port to the ProcessingPlant Topology Node");
        addHTTPClientPorts(processingPlantSoftwareComponent);
        getLogger().trace(".buildSubsystemTopology(): Add the httpServer port to the ClusterService Topology Node");
        addHTTPServerPorts(clusterServiceTopologyNode);
        getLogger().trace(".buildSubsystemTopology(): Add the httpServer port to the ProcessingPlant Topology Node");
        addHTTPServerPorts(processingPlantSoftwareComponent);
        return(processingPlantSoftwareComponent);
    }

    protected void addHTTPClientPorts( EndpointProviderInterface endpointProvider) {
        getLogger().debug(".addHTTPClientPorts(): Entry, endpointProvider->{}", endpointProvider);

        getLogger().trace(".addHTTPClientPorts(): Creating the Application Services Client Server API HTTP Client (Used to Connect-To Communicate-RoomServer)");
        StandardInteractClientPortSegment interactClientServerAPI = ((CommunicateIrisConfigurationFile) getPropertyFile()).getInteractEgressMatrixApplicationServices();
        newHTTPClient(endpointProvider, names.getInteractEgressApplicationServicesClientServerAPIName(),interactClientServerAPI );

        getLogger().trace(".addHTTPClientPorts(): Creating the Synapse API HTTP Client (Used to Connect-To Communicate-RoomServer)");
        StandardInteractClientPortSegment interactEgressSynapseAPI = ((CommunicateIrisConfigurationFile) getPropertyFile()).getInteractEgressSynapseAPI();
        newHTTPClient(endpointProvider, names.getInteractEgressSynapseAPIName(),interactEgressSynapseAPI );

        getLogger().debug(".addHTTPClientPorts(): Exit");
    }

    protected void addHTTPServerPorts( EndpointProviderInterface endpointProvider ){
        getLogger().debug(".addHTTPServerPorts(): Entry, endpointProvider->{}", endpointProvider);

        getLogger().trace(".addHTTPServerPorts(): Creating the Application Services HTTP Server (Used to Receive Content from Communicate-RoomServer)");
        ClusteredInteractHTTPServerPortSegment interactIngresAppServices = ((CommunicateIrisConfigurationFile) getPropertyFile()).getInteractIngressMatrixApplicationServices();
        newHTTPServer(endpointProvider, names.getInteractIngressApplicationServices(),interactIngresAppServices );

        getLogger().debug(".addHTTPServerPorts(): Exit");
    }
}
