/*
 * The MIT License
 *
 * Copyright 2020 ACT Health.
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
package net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.workshops.interact.ingres.beans;

import net.fhirfactory.pegacorn.common.model.generalid.FDN;
import net.fhirfactory.pegacorn.common.model.generalid.RDN;
import net.fhirfactory.pegacorn.common.model.topicid.TopicToken;
import net.fhirfactory.pegacorn.common.model.topicid.TopicTypeEnum;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.WorkUnitProcessorTopologyNode;
import net.fhirfactory.pegacorn.petasos.model.configuration.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.petasos.model.uow.UoW;
import net.fhirfactory.pegacorn.petasos.model.uow.UoWPayload;
import net.fhirfactory.pegacorn.petasos.model.uow.UoWPayloadSet;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 *
 * @author ACT Health
 */
@ApplicationScoped
public class IncomingMatrixEventSet2UoW
{
    private static final Logger LOG = LoggerFactory.getLogger(IncomingMatrixEventSet2UoW.class);

    @Inject
    IncomingMatrixMessageSplitter messageSplitter;

    public UoW encapsulateMatrixMessage(String matrixMessage, Exchange camelExchange)
    {
        LOG.debug(".encapsulateMatrixMessage(): Entry, Matrix Message --> {}", matrixMessage);
        WorkUnitProcessorTopologyNode wupTopologyNode = camelExchange.getProperty(PetasosPropertyConstants.WUP_TOPOLOGY_NODE_EXCHANGE_PROPERTY_NAME, WorkUnitProcessorTopologyNode.class);
        LOG.trace(".encapsulateMatrixMessage(): Creating new Payload element, first the Payload TopicToken");
        FDN payloadTopicFDN = new FDN();
        payloadTopicFDN.appendRDN(new RDN(TopicTypeEnum.DATASET_DEFINER.getTopicType(), "Matrix"));
        payloadTopicFDN.appendRDN(new RDN(TopicTypeEnum.DATASET_CATEGORY.getTopicType(), "ClientServerAPI"));
        payloadTopicFDN.appendRDN(new RDN(TopicTypeEnum.DATASET_SUBCATEGORY.getTopicType(), "General"));
        payloadTopicFDN.appendRDN(new RDN(TopicTypeEnum.DATASET_RESOURCE.getTopicType(), "RawEventSet"));
        TopicToken payloadTopicToken = new TopicToken();
        payloadTopicToken.setIdentifier(payloadTopicFDN.getToken());
        payloadTopicToken.setVersion("0.6.1"); // TODO This version should be set & extracted somewhere
        LOG.trace(".encapsulateMatrixMessage(): Creating new Payload element, now the Payload itself");
        UoWPayload contentPayload = new UoWPayload();
        contentPayload.setPayloadTopicID(payloadTopicToken);
        contentPayload.setPayload(matrixMessage);
        UoW newUoW = new UoW(contentPayload);
        UoWPayloadSet payloadSet = new UoWPayloadSet();
        payloadSet.addPayloadElement(contentPayload);
        newUoW.setEgressContent(payloadSet);
        LOG.debug("encapsulateMatrixMessage(): Exit, UoW created, newUoW --> {}", newUoW);
        return(newUoW);
    }
}
