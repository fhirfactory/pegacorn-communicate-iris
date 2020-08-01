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

package net.fhirfactory.pegacorn.communicate.iris.wups.interact.ingres;

import java.util.Set;
import net.fhirfactory.pegacorn.communicate.iris.wups.interact.IncomingMatrixEventUoWEncapsulator;
import net.fhirfactory.pegacorn.communicate.iris.wups.interact.IncomingEventListValidator;
import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.jms.ConnectionFactory;
import net.fhirfactory.pegacorn.communicate.iris.IrisWUPIntersectPoints;
import net.fhirfactory.pegacorn.communicate.iris.wups.interact.IncomingEventListValidator;
import net.fhirfactory.pegacorn.communicate.iris.wups.interact.IncomingMatrixEventUoWEncapsulator;
import net.fhirfactory.pegacorn.petasos.model.topics.TopicToken;
import net.fhirfactory.pegacorn.petasos.model.wup.WUPArchetypeEnum;
import net.fhirfactory.pegacorn.petasos.wup.archetypes.MessagingIngresGatewayWUP;
import org.apache.camel.ExchangePattern;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class MatrixGatewayReceiverWUP extends MessagingIngresGatewayWUP {
    private static final Logger LOG = LoggerFactory.getLogger(MatrixGatewayReceiverWUP.class);

   
    @Inject
    IncomingEventListValidator messageValidator;
    
    @Inject
    IrisWUPIntersectPoints wupHandoverPoints;
    
    @Inject
    IncomingMatrixEventUoWEncapsulator incomingMessageHandler;

    @Override
    public void configure() throws Exception {

        LOG.info(".configure(): Iris Room Event (RoomServer --> Iris) Endpoint = " + deploymentProperties.getIrisEndPointForRoomServerEvent());

        from(deploymentProperties.getIrisEndPointForRoomServerEvent())
                .routeId("MatrixEvents2FHIR-RoomServer2Iris-Route -->")
                .transform(simple("${bodyAs(String)}"))
                .log(LoggingLevel.DEBUG, "Message received!!!")
                .bean(messageValidator, "validateEventSetMessage")
                .bean(incomingMessageHandler, "encapsulateMatrixMessage")
                .log(LoggingLevel.DEBUG, "Message Validated, Forwarding!!!")
                .to(ExchangePattern.InOnly, wupHandoverPoints.getRAWMatrixRoomServerMessagePoint())
                .transform().simple("{}")
                .end();
    }

    @Override
    public Set<TopicToken> getSubscribedTopics() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getWUPInstanceName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public WUPArchetypeEnum getWUPArchitype() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
