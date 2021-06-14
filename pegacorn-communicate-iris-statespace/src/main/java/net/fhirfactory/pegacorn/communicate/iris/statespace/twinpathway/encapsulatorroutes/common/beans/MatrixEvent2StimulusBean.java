package net.fhirfactory.pegacorn.communicate.iris.statespace.twinpathway.encapsulatorroutes.common.beans;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.fhirfactory.pegacorn.common.model.generalid.FDN;
import net.fhirfactory.pegacorn.common.model.topicid.DataParcelToken;
import net.fhirfactory.pegacorn.common.model.topicid.DataParcelTypeKeyEnum;
import net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.model.stimulus.MTStimulus;
import net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.workshops.cache.room.RoomMapCache;
import net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.workshops.cache.user.UserMapCache;
import net.fhirfactory.pegacorn.internals.matrix.r061.events.common.MatrixEvent;
import net.fhirfactory.pegacorn.internals.matrix.r061.events.common.contenttypes.MEventTypeEnum;
import net.fhirfactory.pegacorn.internals.matrix.r061.events.fullyread.MFullyReadEvent;
import net.fhirfactory.pegacorn.internals.matrix.r061.events.presence.MPresenceEvent;
import net.fhirfactory.pegacorn.internals.matrix.r061.events.readreceipts.MReceiptEvent;
import net.fhirfactory.pegacorn.internals.matrix.r061.events.room.*;
import net.fhirfactory.pegacorn.internals.matrix.r061.events.room.message.*;
import net.fhirfactory.pegacorn.internals.matrix.r061.events.room.message.contenttypes.MRoomMessageTypeEnum;
import net.fhirfactory.pegacorn.internals.matrix.r061.events.typing.MTypingEvent;
import net.fhirfactory.pegacorn.internals.matrix.r061.events.voip.MCallAnswerEvent;
import net.fhirfactory.pegacorn.internals.matrix.r061.events.voip.MCallCandidatesEvent;
import net.fhirfactory.pegacorn.internals.matrix.r061.events.voip.MCallHangupEvent;
import net.fhirfactory.pegacorn.internals.matrix.r061.events.voip.MCallInviteEvent;
import net.fhirfactory.pegacorn.petasos.model.uow.UoWPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.time.Instant;
import java.util.Date;

@ApplicationScoped
public class MatrixEvent2StimulusBean {
    private static final Logger LOG = LoggerFactory.getLogger(MatrixEvent2StimulusBean.class);

    private ObjectMapper jsonMapper;

    public MatrixEvent2StimulusBean(){
        super();
        jsonMapper = new ObjectMapper();
        jsonMapper.configure(JsonParser.Feature.ALLOW_MISSING_VALUES, true);
    }

    public static Logger getLOG() {
        return LOG;
    }

    public ObjectMapper getJsonMapper() {
        return jsonMapper;
    }

    @Inject
    private UserMapCache userMap;

    @Inject
    private RoomMapCache roomMap;

    /**
     *
     * @param payload
     * @param matrixEventTypeString
     * @return
     */
    private MTStimulus deriveStimulusFromUoWForMatrixEvent(UoWPayload payload, String matrixEventTypeString)  {
        MTStimulus stimulus = new MTStimulus();
        String payloadContent = payload.getPayload();
        MatrixEvent matrixEvent = null;
        MEventTypeEnum eventType = MEventTypeEnum.fromString(matrixEventTypeString);
        try {
            switch(eventType) {
                case M_CALL_ANSWER:
                    matrixEvent = getJsonMapper().readValue(payloadContent, MCallAnswerEvent.class);
                    break;
                case M_CALL_CANDIDATES:
                    matrixEvent = getJsonMapper().readValue(payloadContent, MCallCandidatesEvent.class);
                    break;
                case M_CALL_HANGUP:
                    matrixEvent = getJsonMapper().readValue(payloadContent, MCallHangupEvent.class);
                    break;
                case M_CALL_INVITE:
                    matrixEvent = getJsonMapper().readValue(payloadContent, MCallInviteEvent.class);
                    break;
                case M_DIRECT:
                    //				matrixEvent = jsonMapper.readValue(payloadContent, M)
                    break;
                case M_FULLY_READ:
                    matrixEvent = getJsonMapper().readValue(payloadContent, MFullyReadEvent.class);
                    break;
                case M_IGNORED_USER_LIST:
                    //				matrixEvent = getJsonMapper().readValue(payloadContent, M)
                    break;
                case M_PRESENCE:
                    matrixEvent = getJsonMapper().readValue(payloadContent, MPresenceEvent.class);
                    break;
                case M_RECEIPT:
                    matrixEvent = getJsonMapper().readValue(payloadContent, MReceiptEvent.class);
                    break;
                case M_TAG:
                    //				matrixEvent = getJsonMapper().readValue(payloadContent, MTag)
                    break;
                case M_TYPING:
                    matrixEvent = getJsonMapper().readValue(payloadContent, MTypingEvent.class);
                    break;
                case M_POLICY_RULE_ROOM:
                    //				matrixEvent = getJsonMapper().readValue(payloadContent, M)
                    break;
                case M_POLICY_RULE_SERVER:
                    //				matrixEvent = getJsonMapper().readValue(payloadContent, M)
                    break;
                case M_POLICY_RULE_USER:
                    //				matrixEvent = getJsonMapper().readValue(payloadContent, M)
                    break;
                case M_ROOM_CANONICAL_ALIAS:
                    matrixEvent = getJsonMapper().readValue(payloadContent, MRoomCanonicalAliasEvent.class);
                    break;
                case M_ROOM_CREATE:
                    matrixEvent = getJsonMapper().readValue(payloadContent, MRoomCreateEvent.class);
                    break;
                case M_ROOM_GUEST_ACCESS:
                    //				matrixEvent = getJsonMapper().readValue(payloadContent, M)
                    break;
                case M_ROOM_HISTORY_VISIBILITY:
                    //				matrixEvent = getJsonMapper().readValue(payloadContent, M)
                    break;
                case M_ROOM_JOIN_RULES:
                    matrixEvent = getJsonMapper().readValue(payloadContent, MRoomJoinRulesEvent.class);
                    break;
                case M_ROOM_MEMBER:
                    matrixEvent = getJsonMapper().readValue(payloadContent, MRoomMemberEvent.class);
                    break;
                case M_ROOM_MESSAGE:
                    matrixEvent = deriveRoomMessageTypeFromUoWPayload(payload);
                    break;
                case M_ROOM_MESSAGE_FEEDBACK:
                    //				matrixEvent = getJsonMapper().readValue(payloadContent, M);
                    break;
                case M_ROOM_NAME:
                    matrixEvent = getJsonMapper().readValue(payloadContent, MRoomNameEvent.class);
                    break;
                case M_ROOM_POWER_LEVELS:
                    matrixEvent = getJsonMapper().readValue(payloadContent, MRoomPowerLevelsEvent.class);
                    break;
                case M_ROOM_REDACTION:
                    matrixEvent = getJsonMapper().readValue(payloadContent, MRoomRedactionEvent.class);
                    break;
                case M_ROOM_SERVER_ACL:
                    //				matrixEvent = getJsonMapper().readValue(payloadContent, M);
                    break;
                case M_ROOM_THIRD_PARTY_INVITE:
                    //				matrixEvent = getJsonMapper().readValue(payloadContent, M);
                    break;
                case M_ROOM_TOMBSTONE:
                    //				matrixEvent = getJsonMapper().readValue(payloadContent, M);
                    break;
                default:
                    // Do nothing
            }
        } catch (IOException e) {
            LOG.error(".deriveStimulusFromEvent(): Cannot resolve ESR from UoW, (IOException) error -> {}", e.getMessage());
        }
        if(matrixEvent != null){
            if(matrixEvent.getMatrixEventFineGrainType() == null){
                matrixEvent.setMatrixEventFineGrainType(matrixEventTypeString);
            }
            stimulus.setMatrixEventTrigger(matrixEvent);
            stimulus.setCreationDate(Date.from(Instant.now()));
            return(stimulus);
        } else {
            return(null);
        }
    }

    /**
     *
     * @param uowPayload
     * @return
     */
    public MatrixEvent deriveRoomMessageTypeFromUoWPayload(UoWPayload uowPayload){
        DataParcelToken payloadTopicID = uowPayload.getPayloadTopicID();
        FDN payloadFDN = new FDN(payloadTopicID.getToken());
        String message_type = payloadFDN.extractRDNViaQualifier(DataParcelTypeKeyEnum.DATASET_DISCRIMINATOR_VALUE.getTopicType()).getValue();
        if(message_type == null){
            return(null);
        }
        String payloadContent = uowPayload.getPayload();
        MatrixEvent matrixEvent = null;
        MRoomMessageTypeEnum msgEventType = MRoomMessageTypeEnum.fromString(message_type);
        try{
            switch(msgEventType){
                case AUDIO:
                    matrixEvent = getJsonMapper().readValue(payloadContent, MRoomAudioMessageEvent.class);
                    break;
                case EMOTE:
                    matrixEvent = getJsonMapper().readValue(payloadContent, MRoomEmoteMessageEvent.class);
                    break;
                case FILE:
                    matrixEvent = getJsonMapper().readValue(payloadContent, MRoomFileMessageEvent.class);
                    break;
                case IMAGE:
                    matrixEvent = getJsonMapper().readValue(payloadContent, MRoomImageMessageEvent.class);
                    break;
                case LOCATION:
                    matrixEvent = getJsonMapper().readValue(payloadContent, MRoomLocationMessageEvent.class);
                    break;
                case NOTICE:
                    matrixEvent = getJsonMapper().readValue(payloadContent, MRoomNoticeMessageEvent.class);
                    break;
                case SERVER_NOTICE:
                    //				matrixEvent = getJsonMapper().readValue(payloadContent, MRoomServerNoticeMessageEvent.class);
                    break;
                case TEXT:
                    matrixEvent = getJsonMapper().readValue(payloadContent, MRoomTextMessageEvent.class);
                    break;
                case VIDEO:
                    matrixEvent = getJsonMapper().readValue(payloadContent, MRoomVideoMessageEvent.class);
                    break;
                default:
                    // Do nothing
            }
        } catch (JsonMappingException e) {
            LOG.error(".deriveRoomMessageTypeFromUoWPayload(): Cannot resolve ESR from UoW, (JsonMappingException) error -> {}", e.getMessage());
        } catch (JsonParseException e) {
            LOG.error(".deriveRoomMessageTypeFromUoWPayload(): Cannot resolve ESR from UoW, (JsonParseException) error -> {}", e.getMessage());
        } catch (IOException e) {
            LOG.error(".deriveRoomMessageTypeFromUoWPayload(): Cannot resolve ESR from UoW, (IOException) error -> {}", e.getMessage());
        }
        if(matrixEvent != null){
            matrixEvent.setMatrixEventFineGrainType(message_type);
        }
        return(matrixEvent);
    }
}
