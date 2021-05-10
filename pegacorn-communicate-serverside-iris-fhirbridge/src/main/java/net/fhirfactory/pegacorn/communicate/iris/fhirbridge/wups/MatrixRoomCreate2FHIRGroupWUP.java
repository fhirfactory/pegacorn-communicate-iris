/*
 * The MIT License
 *
 * Copyright 2020 Mark A. Hunter (ACT Health).
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.fhirfactory.pegacorn.communicate.iris.fhirbridge.wups;

import net.fhirfactory.pegacorn.common.model.generalid.FDN;
import net.fhirfactory.pegacorn.common.model.generalid.RDN;
import net.fhirfactory.pegacorn.common.model.topicid.TopicToken;
import net.fhirfactory.pegacorn.common.model.topicid.TopicTypeEnum;
import net.fhirfactory.pegacorn.communicate.iris.fhirbridge.core.matrxi2fhir.rooms.MatrixRoomEvent2FHIRGroup;
import net.fhirfactory.pegacorn.communicate.iris.fhirbridge.workshop.FHIRBridgeTransformWorkshop;
import net.fhirfactory.pegacorn.components.interfaces.topology.WorkshopInterface;
import net.fhirfactory.pegacorn.wups.archetypes.petasosenabled.messageprocessingbased.core.MOAStandardWUP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Mark A. Hunter
 */

@ApplicationScoped
public class MatrixRoomCreate2FHIRGroupWUP extends MOAStandardWUP{
    private static final Logger LOG = LoggerFactory.getLogger(MatrixRoomCreate2FHIRGroupWUP.class);

    @Inject
    private FHIRBridgeTransformWorkshop workshop;

    public MatrixRoomCreate2FHIRGroupWUP(){
        super();
    }
    
    @Override
    public Set<TopicToken> specifySubscriptionTopics() {
        LOG.debug(".getSubscribedTopics(): Entry");
        FDN payloadTopicFDN = new FDN();
        payloadTopicFDN.appendRDN(new RDN(TopicTypeEnum.DATASET_DEFINER.getTopicType(), "Matrix"));
        payloadTopicFDN.appendRDN(new RDN(TopicTypeEnum.DATASET_CATEGORY.getTopicType(), "ClientServerAPI"));
        payloadTopicFDN.appendRDN(new RDN(TopicTypeEnum.DATASET_SUBCATEGORY.getTopicType(), "RoomEvents"));
        payloadTopicFDN.appendRDN(new RDN(TopicTypeEnum.DATASET_RESOURCE.getTopicType(), "m.room.create"));
        TopicToken payloadTopicToken = new TopicToken();
        payloadTopicToken.setIdentifier(payloadTopicFDN.getToken());
        payloadTopicToken.setVersion("0.6.1"); // TODO This version should be set & extracted somewhere
        HashSet<TopicToken> myTopicsOfInterest = new HashSet<TopicToken>();
        myTopicsOfInterest.add(payloadTopicToken);
        LOG.debug("getSubscribedTopics(): Exit, myTopicsOfInterest --> {}", myTopicsOfInterest);
        return(myTopicsOfInterest);
    }

    @Override
    public String specifyWUPInstanceName() {
        return("MatrixRoomCreate2FHIRGroupWUP");
    }

    @Override
    protected Logger specifyLogger() {
        return (LOG);
    }

    @Override
    protected String specifyWUPInstanceVersion() {
        return ("1.0.0");
    }

    @Override
    protected WorkshopInterface specifyWorkshop() {
        return (workshop);
    }

    @Override
    public void configure() throws Exception {
        LOG.debug(".configure(): Entry!, for wupFunctionToken --> {}, wupInstanceID --> {}", this.getWUPTopologyNode().getNodeFunctionFDN(), this.getWUPTopologyNode().getNodeFDN());
        
        from(this.getIngresTopologyEndpoint().getEndpointSpecification())
                .bean(MatrixRoomEvent2FHIRGroup.class, "matrixRoomCreateEvent2FHIRGroupBundle")
                .to(this.getEgressTopologyEndpoint().getEndpointSpecification());
    }
}
