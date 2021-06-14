/*
 * Copyright (c) 2020 mhunter
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

package net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.workshops.interact.ingres;

import net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.workshops.interact.ingres.beans.IncomingMatrixEventSet2UoW;
import net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.workshops.interact.ingres.beans.IncomingMatrixEventSetValidator;
import net.fhirfactory.pegacorn.components.interfaces.topology.WorkshopInterface;
import net.fhirfactory.pegacorn.deployment.names.subsystems.CommunicateIrisComponentNames;
import net.fhirfactory.pegacorn.deployment.topology.model.common.IPCInterfaceDefinition;
import net.fhirfactory.pegacorn.deployment.topology.model.common.IPCTopologyEndpoint;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.common.HTTPProcessingPlantTopologyEndpointPort;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.common.HTTPServerClusterServiceTopologyEndpointPort;
import net.fhirfactory.pegacorn.internals.esr.transactions.exceptions.ResourceUpdateException;
import net.fhirfactory.pegacorn.internals.matrix.exceptions.MatrixEventNotFoundException;
import net.fhirfactory.pegacorn.petasos.core.moa.wup.GenericMessageBasedWUPEndpoint;
import net.fhirfactory.pegacorn.petasos.wup.helper.IngresActivityBeginRegistration;
import net.fhirfactory.pegacorn.workshops.InteractWorkshop;
import net.fhirfactory.pegacorn.wups.archetypes.petasosenabled.messageprocessingbased.InteractIngresMessagingGatewayWUP;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.model.OnExceptionDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class MatrixApplicationServicesEventsReceiverWUP extends InteractIngresMessagingGatewayWUP {
    private static final Logger LOG = LoggerFactory.getLogger(MatrixApplicationServicesEventsReceiverWUP.class);

    private static String INGRES_GATEWAY_COMPONENT = "netty-http";

    @Inject
    private InteractWorkshop workshop;

    @Inject
    private CommunicateIrisComponentNames componentNames;
    
    public MatrixApplicationServicesEventsReceiverWUP(){
        super();
    }
   
    @Override
    public void configure() throws Exception {
        LOG.debug(".configure(): Entry");

        String ingresFeed = getIngresTopologyEndpoint().getEndpointSpecification();
        String egressFeed = getEgressTopologyEndpoint().getEndpointSpecification();

        LOG.info("Route->{}, ingresFeed->{}, egressFeed->{}", getNameSet().getRouteCoreWUP(), ingresFeed, egressFeed);

        //
        // Exceptions
        //
        routeMatrixEventNotFoundException();
        routeGeneralException();

        //
        // Main Route
        //
        from(ingresFeed)
                .routeId(getNameSet().getRouteCoreWUP())
                .transform(simple("${bodyAs(String)}"))
                .log(LoggingLevel.TRACE, ": Message received!!!")
                .bean(IncomingMatrixEventSetValidator.class, "validateEventSetMessage")
                .bean(IncomingMatrixEventSet2UoW.class, "encapsulateMatrixMessage(*, Exchange)")
                .bean(IngresActivityBeginRegistration.class, "registerActivityStart(*,  Exchange)")
                .log(LoggingLevel.TRACE, "Message Validated, Forwarding!!!")
                .to(egressFeed);
    }



    @Override
    public String specifyWUPInstanceName() {
        return("MatrixApplicationServicesEventsReceiverWUP");
    }

    @Override
    public String specifyWUPInstanceVersion() {
        return("0.0.1");
    }

    @Override
    protected String specifyIngresTopologyEndpointName() {
        String endpointName = componentNames.getEndpointServerName(componentNames.getFunctionNameInteractMatrixClientServices());
        return (endpointName);
    }

    @Override
    protected String specifyIngresEndpointVersion() {
        String endpointVersion = componentNames.getVersionInteractMatrixApplicationServices();
        return (endpointVersion);
    }

    @Override
    protected Logger specifyLogger() {
        return (LOG);
    }

    @Override
    protected WorkshopInterface specifyWorkshop() {
        return (workshop);
    }

    @Override
    protected GenericMessageBasedWUPEndpoint specifyIngresTopologyEndpoint() {
        LOG.debug(".specifyIngresTopologyEndpoint(): Entry");
        GenericMessageBasedWUPEndpoint ingresEndpoint = new GenericMessageBasedWUPEndpoint();
        ingresEndpoint.setFrameworkEnabled(false);
        IPCInterfaceDefinition interfaceDefinition = componentNames.getMatrixApplicationServicesPortDefinition();
        LOG.trace(".specifyIngresTopologyEndpoint(): portDefinition->{}", interfaceDefinition);
        IPCTopologyEndpoint endpoint = deriveServerEndpoint(interfaceDefinition);
        if(endpoint == null){
            LOG.error(".specifyIngresTopologyEndpoint(): Unable to derive endpoint for Matrix Application Services API  Server");
            setIngresEndpoint(ingresEndpoint);
            return(ingresEndpoint);
        }
        LOG.trace(".specifyIngresTopologyEndpoint(): Resolved endpoint->{}", endpoint);
        ingresEndpoint.setEndpointTopologyNode(endpoint);
        setIngresEndpoint(ingresEndpoint);
        String ingresString = buildIngresString(getServerHost(), getServerPort(), getServerPath());
        ingresEndpoint.setEndpointSpecification(ingresString);
        LOG.debug(".specifyIngresTopologyEndpoint(): Exit, ingresEndpoint->{}", ingresEndpoint);
        return(ingresEndpoint);
    }

    private String buildIngresString(String host, String port, String path){
        String ingresString = "netty-http:http://"+host+":"+port+"/"+path+"/transactions/{id}";
        return(ingresString);
    }

    protected String getPathSuffix() {
        String suffix = "?matchOnUriPrefix=true&option.enableCORS=true&option.corsAllowedCredentials=true";
        return (suffix);
    }

    public String getServerPort() {
        String portValue = Integer.toString(getIngresEndpoint().getEndpointTopologyNode().getPortValue());
        return (portValue);
    }

    public String getServerHost() {
        getLogger().debug(".getServerHost(): Entry");
        GenericMessageBasedWUPEndpoint localIngresEndpoint = getIngresEndpoint();
        IPCTopologyEndpoint endpointTopologyNode = localIngresEndpoint.getEndpointTopologyNode();
        String portServerName = endpointTopologyNode.getInterfaceDNSName();
        return (portServerName);
    }

    public String getServerPath(){
        getLogger().debug(".getServerPath(): Entry");
        if(getIngresEndpoint().getEndpointTopologyNode() instanceof HTTPServerClusterServiceTopologyEndpointPort) {
            HTTPServerClusterServiceTopologyEndpointPort serviceEndpoint = (HTTPServerClusterServiceTopologyEndpointPort)getIngresEndpoint().getEndpointTopologyNode();
            String serverPath = serviceEndpoint.getBasePath();
            getLogger().debug(".getServerPath(): Exit, (ClusterService) serverPath->{}", serverPath);
            return(serverPath);
        }
        if(getIngresEndpoint().getEndpointTopologyNode() instanceof HTTPProcessingPlantTopologyEndpointPort){
            HTTPProcessingPlantTopologyEndpointPort processingPlantEndpoint = (HTTPProcessingPlantTopologyEndpointPort)getIngresEndpoint().getEndpointTopologyNode();
            String serverPath = processingPlantEndpoint.getBasePath();
            getLogger().debug(".getServerPath(): Exit, (ProcessingPlant) serverPath->{}", serverPath);
            return(serverPath);
        }
        getLogger().error(".getServerPath(): Cannot resolve Matrix Application Services server Base Path!");
        return("");
    }

    private OnExceptionDefinition routeMatrixEventNotFoundException() {
        OnExceptionDefinition exceptionDef = onException(MatrixEventNotFoundException.class)
                .handled(true)
                .log(LoggingLevel.ERROR, "MatrixEventNotFoundException...")
                // use HTTP status 404 when data was not found
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(404))
                .setBody(simple("${exception.message}\n"));

        return(exceptionDef);
    }

    private OnExceptionDefinition routeResourceUpdateException() {
        OnExceptionDefinition exceptionDef = onException(ResourceUpdateException.class)
                .handled(true)
                .log(LoggingLevel.INFO, "ResourceUpdateException...")
                // use HTTP status 404 when data was not found
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
                .setBody(simple("${exception.message}\n"));

        return(exceptionDef);
    }

    private OnExceptionDefinition routeGeneralException() {
        OnExceptionDefinition exceptionDef = onException(Exception.class)
                .handled(true)
                .log(LoggingLevel.ERROR, "${exception.message}\n")
                // use HTTP status 500 when we had a server side error
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
                .setBody(simple("${exception.message}\n"));
        return (exceptionDef);
    }
}
