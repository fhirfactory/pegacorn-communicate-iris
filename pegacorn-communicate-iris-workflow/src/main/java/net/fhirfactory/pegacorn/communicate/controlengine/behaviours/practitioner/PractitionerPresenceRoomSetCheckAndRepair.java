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
package net.fhirfactory.pegacorn.communicate.controlengine.behaviours.practitioner;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.fhirfactory.pegacorn.common.model.generalid.FDN;
import net.fhirfactory.pegacorn.common.model.generalid.RDN;
import net.fhirfactory.pegacorn.common.model.topicid.DataParcelNormalisationStatusEnum;
import net.fhirfactory.pegacorn.common.model.topicid.DataParcelToken;
import net.fhirfactory.pegacorn.common.model.topicid.DataParcelTypeKeyEnum;
import net.fhirfactory.pegacorn.common.model.topicid.DataParcelValidationStatusEnum;
import net.fhirfactory.pegacorn.communicate.controlengine.behaviours.archetypes.PractitionerCDTCentricStimuliBasedBehaviour;
import net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.model.MTIdentifier;
import net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.model.MTTypeEnum;
import net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.model.behaviours.MTBehaviourCentricExclusionFilterRulesInterface;
import net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.model.behaviours.MTBehaviourCentricInclusionFilterRulesInterface;
import net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.model.stimulus.MTStimulus;
import net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.workshops.cache.room.RoomMapCache;
import net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.workshops.cache.user.UserMapCache;
import net.fhirfactory.pegacorn.internals.communicate.workflow.model.CDTIdentifier;
import net.fhirfactory.pegacorn.internals.communicate.workflow.model.CDTTypeEnum;
import net.fhirfactory.pegacorn.internals.communicate.workflow.model.behaviours.CDTBehaviourCentricExclusionFilterRulesInterface;
import net.fhirfactory.pegacorn.internals.communicate.workflow.model.behaviours.CDTBehaviourCentricInclusionFilterRulesInterface;
import net.fhirfactory.pegacorn.internals.communicate.workflow.model.stimulus.CDTStimulus;
import net.fhirfactory.pegacorn.internals.esr.brokers.PractitionerESRBroker;
import net.fhirfactory.pegacorn.internals.esr.transactions.ESRMethodOutcome;
import net.fhirfactory.pegacorn.internals.matrix.r061.events.presence.MPresenceEvent;
import net.fhirfactory.pegacorn.petasos.model.uow.UoW;
import net.fhirfactory.pegacorn.petasos.model.uow.UoWPayload;
import net.fhirfactory.pegacorn.petasos.model.uow.UoWProcessingOutcomeEnum;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class PractitionerPresenceRoomSetCheckAndRepair extends PractitionerCDTCentricStimuliBasedBehaviour {
    private static final Logger LOG = LoggerFactory.getLogger(PractitionerPresenceRoomSetCheckAndRepair.class);

    private String BEHAVIOUR_NAME = "PractitionerPresenceRoomSetCheckAndRepair";
    private String BEHAVIOUR_VERSION = "1.0.0";

    private ObjectMapper jsonObjectMapper;

    public PractitionerPresenceRoomSetCheckAndRepair(){
        super();
        jsonObjectMapper = new ObjectMapper();
        jsonObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Inject
    private RoomMapCache roomMapCache;

    @Inject
    private UserMapCache userMapCache;

    @Inject
    private PractitionerESRBroker esrPractitionerBroker;

    @Override
    protected Logger specifyLogger() {
        return (LOG);
    }

    @Override
    public void configure() throws Exception {
        LOG.debug(".configure(): Entry");

        ProcessPresenceEvent presenceEventProcessor = new ProcessPresenceEvent();

        LOG.info("Route->{}, ingresFeed->{}, egressFeed->{}", getNameSet(), ingresFeed(), egressFeed());

        from(ingresFeed())
                .process(presenceEventProcessor)
                .to(egressFeed());
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
            ESRMethodOutcome outcome = esrPractitionerBroker.getResource(userID);

        }

    }

    @Override
    protected String specifyBehaviourName() {
        return (BEHAVIOUR_NAME);
    }

    @Override
    protected String specifyBehaviourVersion() {
        return (BEHAVIOUR_VERSION);
    }

    @Override
    protected void executePostInitialisationActivities() {

    }

    @Override
    protected List<CDTBehaviourCentricExclusionFilterRulesInterface> exclusionFilterSet() {
        return null;
    }

    @Override
    protected List<CDTBehaviourCentricInclusionFilterRulesInterface> inclusionFilterSet() {
        return null;
    }

    public class PractitionerPresenceRoomSetCheckAndRepairExclusionFilter implements CDTBehaviourCentricExclusionFilterRulesInterface{
        @Override
        public boolean blockStimulusForDigitalTwinInstance(CDTStimulus incomingStimulus, CDTIdentifier twinInstanceIdentifier) {
            if(incomingStimulus.getMatrixTwinID().getTwinResourceType().equals(CDTTypeEnum.COMMUNICATE_TWIN_PRACTITIONER)){
                return(false);
            } else {
                return(true);
            }
        }
    }

    public class PractitionerPresenceRoomSetCheckAndRepairInclusionFilter implements CDTBehaviourCentricInclusionFilterRulesInterface{
        @Override
        public List<DataParcelToken> positiveStaticFilterStimulus() {
            FDN presenceEventsFDN = new FDN();
            presenceEventsFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_DEFINER.getTopicType(), "Matrix"));
            presenceEventsFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_CATEGORY.getTopicType(), "ClientServerAPI"));
            presenceEventsFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_SUBCATEGORY.getTopicType(), "Presence"));
            presenceEventsFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_RESOURCE.getTopicType(), "m.presence"));
            DataParcelToken presenceEventsDataParcelToken = new DataParcelToken();
            presenceEventsDataParcelToken.setToken(presenceEventsFDN.getToken());
            presenceEventsDataParcelToken.setVersion("0.6.1");
            presenceEventsDataParcelToken.setValidationStatus(DataParcelValidationStatusEnum.DATA_PARCEL_CONTENT_VALIDATED_FALSE);
            presenceEventsDataParcelToken.setNormalisationStatus(DataParcelNormalisationStatusEnum.DATA_PARCEL_CONTENT_NORMALISATION_TRUE);
            FDN normalisedPresenceEventsFDN = new FDN();
            normalisedPresenceEventsFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_DEFINER.getTopicType(), "Matrix"));
            normalisedPresenceEventsFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_CATEGORY.getTopicType(), "ClientServerAPI"));
            normalisedPresenceEventsFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_SUBCATEGORY.getTopicType(), "Presence"));
            normalisedPresenceEventsFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_RESOURCE.getTopicType(), "m.presence"));
            DataParcelToken normalisedPresenceEventsDataParcelToken = new DataParcelToken();
            normalisedPresenceEventsDataParcelToken.setToken(normalisedPresenceEventsFDN.getToken());
            normalisedPresenceEventsDataParcelToken.setVersion("0.6.1");
            normalisedPresenceEventsDataParcelToken.setValidationStatus(DataParcelValidationStatusEnum.DATA_PARCEL_CONTENT_VALIDATED_FALSE);
            normalisedPresenceEventsDataParcelToken.setNormalisationStatus(DataParcelNormalisationStatusEnum.DATA_PARCEL_CONTENT_NORMALISATION_FALSE);
            List<DataParcelToken> subscribedTopicSet = new ArrayList<>();
            subscribedTopicSet.add(presenceEventsDataParcelToken);
            subscribedTopicSet.add(normalisedPresenceEventsDataParcelToken);
            return(subscribedTopicSet);
        }

        @Override
        public List<CDTIdentifier> positiveDynamicFilterTwinInstancesForStimulus(CDTStimulus stimulusInstant) {
            CDTIdentifier id = new CDTIdentifier();
            id.set
        }
    }
}
