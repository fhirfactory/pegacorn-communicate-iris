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
package net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.workshops.matrixtwinbehaviours.practitioner;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.fhirfactory.pegacorn.common.model.generalid.FDN;
import net.fhirfactory.pegacorn.common.model.generalid.RDN;
import net.fhirfactory.pegacorn.common.model.topicid.TopicToken;
import net.fhirfactory.pegacorn.common.model.topicid.TopicTypeEnum;
import net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.workshops.matrixtwinbehaviours.MatrixBehavioursWorkshop;
import net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.workshops.cache.room.RoomMapCache;
import net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.workshops.cache.user.UserMapCache;
import net.fhirfactory.pegacorn.components.interfaces.topology.WorkshopInterface;
import net.fhirfactory.pegacorn.internals.esr.brokers.PractitionerESRBroker;
import net.fhirfactory.pegacorn.internals.esr.transactions.ESRMethodOutcome;
import net.fhirfactory.pegacorn.internals.matrix.r061.events.presence.MPresenceEvent;
import net.fhirfactory.pegacorn.petasos.model.uow.UoW;
import net.fhirfactory.pegacorn.petasos.model.uow.UoWPayload;
import net.fhirfactory.pegacorn.petasos.model.uow.UoWProcessingOutcomeEnum;
import net.fhirfactory.pegacorn.wups.archetypes.petasosenabled.messageprocessingbased.core.MOAStandardWUP;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

@ApplicationScoped
public class PractitionerPresenceReflexAction extends MOAStandardWUP {
    private static final Logger LOG = LoggerFactory.getLogger(PractitionerPresenceReflexAction.class);

    private String WUP_NAME = "PractitionerPresenceReflexAction";
    private String WUP_VERSION = "1.0.0";

    private ObjectMapper jsonObjectMapper;

    public PractitionerPresenceReflexAction(){
        super();
        jsonObjectMapper = new ObjectMapper();
        jsonObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Inject
    private RoomMapCache roomMapCache;

    @Inject
    private UserMapCache userMapCache;

    @Inject
    private MatrixBehavioursWorkshop workshop;

    @Inject
    private PractitionerESRBroker lingoPractitionerBroker;

    @Override
    protected Logger specifyLogger() {
        return (LOG);
    }

    @Override
    protected Set<TopicToken> specifySubscriptionTopics() {
        FDN presenceEventsFDN = new FDN();
        presenceEventsFDN.appendRDN(new RDN(TopicTypeEnum.DATASET_DEFINER.getTopicType(), "Matrix"));
        presenceEventsFDN.appendRDN(new RDN(TopicTypeEnum.DATASET_CATEGORY.getTopicType(), "ClientServerAPI"));
        presenceEventsFDN.appendRDN(new RDN(TopicTypeEnum.DATASET_SUBCATEGORY.getTopicType(), "Presence"));
        presenceEventsFDN.appendRDN(new RDN(TopicTypeEnum.DATASET_RESOURCE.getTopicType(), "m.presence"));
        TopicToken presenceEventsTopicToken = new TopicToken();
        presenceEventsTopicToken.setIdentifier(presenceEventsFDN.getToken());
        presenceEventsTopicToken.setVersion("0.6.1");
        HashSet<TopicToken> subscribedTopicSet = new HashSet<>();
        subscribedTopicSet.add(presenceEventsTopicToken);
        return(subscribedTopicSet);
    }

    @Override
    protected String specifyWUPInstanceName() {
        return (WUP_NAME);
    }

    @Override
    protected String specifyWUPInstanceVersion() {
        return (WUP_VERSION);
    }

    @Override
    protected WorkshopInterface specifyWorkshop() {
        return (workshop);
    }

    @Override
    public void configure() throws Exception {
        LOG.debug(".configure(): Entry");

        String wupFunctionTokenString  = getWUPTopologyNode().getNodeFunctionFDN().getFunctionToken().getToken();
        String wupInstanceIDString = getWUPTopologyNode().getNodeFDN().getToken().getTokenValue();

        String ingresFeed = getIngresTopologyEndpoint().getEndpointSpecification();
        String egressFeed = getEgressTopologyEndpoint().getEndpointSpecification();

        ProcessPresenceEvent presenceEventProcessor = new ProcessPresenceEvent();

        LOG.info("Route->{}, ingresFeed->{}, egressFeed->{}", getNameSet().getRouteCoreWUP(), ingresFeed, egressFeed);

        from(ingresFeed)
                .process(presenceEventProcessor)
                .to(egressFeed);
    }

    private class ProcessPresenceEvent implements Processor{

        @Override
        public void process(Exchange exchange) throws Exception {
            getLogger().debug("ProcessPresenceEvent.process(): Entry");
            // Extract the MPresenceEvent
            UoW uow = (UoW)exchange.getIn().getBody();
            JSONObject localMessageObject = new JSONObject(uow.getIngresContent().getPayload());
            UoWPayload uowPayload = uow.getIngresContent();
            String synapseEvent = uowPayload.getPayload();
            MPresenceEvent presenceEvent = jsonObjectMapper.readValue(synapseEvent, MPresenceEvent.class);
            // Check to see if it relates to a Practitioner
            String userID = presenceEvent.getSender();
            if(userMapCache.isPractitionerRoleUser(userID) || userMapCache.isHealthcareServiceUser(userID)){
                uow.setProcessingOutcome(UoWProcessingOutcomeEnum.UOW_OUTCOME_NO_PROCESSING_REQUIRED);
                return;
            }
            // Process
            ESRMethodOutcome outcome = lingoPractitionerBroker.getResource(userID);

        }

    }
}
