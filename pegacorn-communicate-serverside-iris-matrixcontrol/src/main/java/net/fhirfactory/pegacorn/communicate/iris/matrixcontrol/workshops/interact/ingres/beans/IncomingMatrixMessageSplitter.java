/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.workshops.interact.ingres.beans;

import net.fhirfactory.pegacorn.common.model.generalid.FDN;
import net.fhirfactory.pegacorn.common.model.generalid.RDN;
import net.fhirfactory.pegacorn.common.model.topicid.DataParcelNormalisationStatusEnum;
import net.fhirfactory.pegacorn.common.model.topicid.DataParcelToken;
import net.fhirfactory.pegacorn.common.model.topicid.DataParcelTypeKeyEnum;
import net.fhirfactory.pegacorn.internals.matrix.r061.events.common.contenttypes.MEventTypeEnum;
import net.fhirfactory.pegacorn.petasos.model.uow.UoW;
import net.fhirfactory.pegacorn.petasos.model.uow.UoWPayload;
import net.fhirfactory.pegacorn.petasos.model.uow.UoWPayloadSet;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

/**
 *
 * @author Mark A. Hunter (ACT Health)
 */
@ApplicationScoped
public class IncomingMatrixMessageSplitter {

    private static final Logger LOG = LoggerFactory.getLogger(IncomingMatrixMessageSplitter.class);

    public UoW splitMessageIntoEvents(UoW incomingUoW) {
        LOG.debug("splitMessageIntoEvents(): Entry: Message to split -->" + incomingUoW);
        if (incomingUoW.getIngresContent().getPayload().isEmpty()) {
            LOG.debug("splitMessageIntoEvents(): Exit: Empty message");
            return (incomingUoW);
        }
        UoWPayloadSet payloadSet = incomingUoW.getEgressContent();
        JSONObject localMessageObject = new JSONObject(incomingUoW.getIngresContent().getPayload());
        LOG.trace("splitMessageIntoEvents(): Converted to JSONObject --> " + localMessageObject.toString());
        JSONArray localMessageEvents = localMessageObject.getJSONArray("events");
        LOG.trace("splitMessageIntoEvents(): Converted to JSONArray, number of elements --> " + localMessageEvents.length());
        for (Integer counter = 0; counter < localMessageEvents.length(); counter += 1) {
            JSONObject eventInstance = localMessageEvents.getJSONObject(counter);
            LOG.trace("splitMessageIntoEvents(): Extracted JSONObject --> " + eventInstance.toString());
            if (eventInstance.has("type")) {
                FDN payloadTopicFDN = new FDN();
                payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_DEFINER.getTopicType(), "Matrix"));
                payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_CATEGORY.getTopicType(), "ClientServerAPI"));
                String messageType = eventInstance.getString("type");
                switch (messageType) {
                    case "m.call.answer":
                        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_SUBCATEGORY.getTopicType(), "CallEvents"));
                        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_RESOURCE.getTopicType(), MEventTypeEnum.M_CALL_ANSWER.getEventType()));
                        break;
                    case "m.call.candidates":
                        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_SUBCATEGORY.getTopicType(), "CallEvents"));
                        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_RESOURCE.getTopicType(), MEventTypeEnum.M_CALL_CANDIDATES.getEventType()));
                        break;
                    case "m.call.hangup":
                        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_SUBCATEGORY.getTopicType(), "CallEvents"));
                        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_RESOURCE.getTopicType(), MEventTypeEnum.M_CALL_HANGUP.getEventType()));
                        break;
                    case "m.call.invite":
                        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_SUBCATEGORY.getTopicType(), "CallEvents"));
                        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_RESOURCE.getTopicType(), MEventTypeEnum.M_CALL_INVITE.getEventType()));
                        break;
                    case "m.direct":
                        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_SUBCATEGORY.getTopicType(), "UserEvents"));
                        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_RESOURCE.getTopicType(), MEventTypeEnum.M_DIRECT.getEventType()));
                        break;
                    case "m.fully_read":
                        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_SUBCATEGORY.getTopicType(), "UserEvents"));
                        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_RESOURCE.getTopicType(), MEventTypeEnum.M_FULLY_READ.getEventType()));
                        break;
                    case "m.ignored_user_list":
                        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_SUBCATEGORY.getTopicType(), "UserEvents"));
                        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_RESOURCE.getTopicType(), MEventTypeEnum.M_IGNORED_USER_LIST.getEventType()));
                        break;
                    case "m.presence":
                        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_SUBCATEGORY.getTopicType(), "UserEvents"));
                        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_RESOURCE.getTopicType(), MEventTypeEnum.M_PRESENCE.getEventType()));
                        break;
                    case "m.receipt":
                        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_SUBCATEGORY.getTopicType(), "UserEvents"));
                        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_RESOURCE.getTopicType(), MEventTypeEnum.M_RECEIPT.getEventType()));
                        break;
                    case "m.tag":
                        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_SUBCATEGORY.getTopicType(), "UserEvents"));
                        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_RESOURCE.getTopicType(), MEventTypeEnum.M_TAG.getEventType()));
                        break;
                    case "m.typing":
                        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_SUBCATEGORY.getTopicType(), "Typing"));
                        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_RESOURCE.getTopicType(), MEventTypeEnum.M_TYPING.getEventType()));
                        break;
                    case "m.policy.rule.room":
                        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_SUBCATEGORY.getTopicType(), "PolicyEvents"));
                        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_RESOURCE.getTopicType(), MEventTypeEnum.M_POLICY_RULE_ROOM.getEventType()));
                        break;
                    case "m.policy.rule.server":
                        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_SUBCATEGORY.getTopicType(), "PolicyEvents"));
                        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_RESOURCE.getTopicType(), MEventTypeEnum.M_POLICY_RULE_SERVER.getEventType()));
                        break;
                    case "m.policy.rule.user":
                        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_SUBCATEGORY.getTopicType(), "PolicyEvents"));
                        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_RESOURCE.getTopicType(), MEventTypeEnum.M_POLICY_RULE_USER.getEventType()));
                        break;
                    case "m.room.canonical_alias":
                        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_SUBCATEGORY.getTopicType(), "RoomEvents"));
                        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_RESOURCE.getTopicType(), MEventTypeEnum.M_ROOM_CANONICAL_ALIAS.getEventType()));
                        break;
                    case "m.room.create":
                        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_SUBCATEGORY.getTopicType(), "RoomEvents"));
                        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_RESOURCE.getTopicType(), MEventTypeEnum.M_ROOM_CREATE.getEventType()));
                        break;
                    case "m.room.guest_access":
                        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_SUBCATEGORY.getTopicType(), "RoomEvents"));
                        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_RESOURCE.getTopicType(), MEventTypeEnum.M_ROOM_GUEST_ACCESS.getEventType()));
                        break;
                    case "m.room.history_visibility":
                        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_SUBCATEGORY.getTopicType(), "RoomEvents"));
                        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_RESOURCE.getTopicType(), MEventTypeEnum.M_ROOM_HISTORY_VISIBILITY.getEventType()));
                        break;
                    case "m.room.join_rules":
                        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_SUBCATEGORY.getTopicType(), "RoomEvents"));
                        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_RESOURCE.getTopicType(), MEventTypeEnum.M_ROOM_JOIN_RULES.getEventType()));
                        break;
                    case "m.room.member":
                        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_SUBCATEGORY.getTopicType(), "RoomEvents"));
                        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_RESOURCE.getTopicType(), MEventTypeEnum.M_ROOM_MEMBER.getEventType()));
                        break;
                    case "m.room.message":
                        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_SUBCATEGORY.getTopicType(), "RoomEvents"));
                        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_RESOURCE.getTopicType(), MEventTypeEnum.M_ROOM_MESSAGE.getEventType()));
                        qualifyInstantMessageType(eventInstance, payloadTopicFDN);
                        break;
                    case "m.room.message.feedback":
                        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_SUBCATEGORY.getTopicType(), "RoomEvents"));
                        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_RESOURCE.getTopicType(), MEventTypeEnum.M_ROOM_MESSAGE_FEEDBACK.getEventType()));
                        break;
                    case "m.room.name":
                        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_SUBCATEGORY.getTopicType(), "RoomEvents"));
                        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_RESOURCE.getTopicType(), MEventTypeEnum.M_ROOM_NAME.getEventType()));
                        break;
                    case "m.room.power_levels":
                        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_SUBCATEGORY.getTopicType(), "RoomEvents"));
                        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_RESOURCE.getTopicType(), MEventTypeEnum.M_ROOM_POWER_LEVELS.getEventType()));
                        break;
                    case "m.room.redaction":
                        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_SUBCATEGORY.getTopicType(), "RoomEvents"));
                        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_RESOURCE.getTopicType(), MEventTypeEnum.M_ROOM_REDACTION.getEventType()));
                        break;
                    case "m.room.server_acl":
                        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_SUBCATEGORY.getTopicType(), "RoomEvents"));
                        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_RESOURCE.getTopicType(), MEventTypeEnum.M_ROOM_SERVER_ACL.getEventType()));
                        break;
                    case "m.room.third_party_invite":
                        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_SUBCATEGORY.getTopicType(), "RoomEvents"));
                        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_RESOURCE.getTopicType(), MEventTypeEnum.M_ROOM_THIRD_PARTY_INVITE.getEventType()));
                        break;
                    case "m.room.tombstone":
                        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_SUBCATEGORY.getTopicType(), "RoomEvents"));
                        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_RESOURCE.getTopicType(), MEventTypeEnum.M_ROOM_TOMBSTONE.getEventType()));
                        break;
                    case "m.room.topic":
                        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_SUBCATEGORY.getTopicType(), "RoomEvents"));
                        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_RESOURCE.getTopicType(), "m.room.topic"));
                        break;
                    case "m.room.avatar":
                        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_SUBCATEGORY.getTopicType(), "RoomEvents"));
                        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_RESOURCE.getTopicType(), "m.room.avatar"));
                        break;
                    case "m.room.pinned_events":
                        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_SUBCATEGORY.getTopicType(), "RoomEvents"));
                        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_RESOURCE.getTopicType(), "m.room.pinned_events"));
                        break;
                    default:
                        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_SUBCATEGORY.getTopicType(), "General"));
                        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_RESOURCE.getTopicType(), "Unknown"));
                }
                UoWPayload newPayload = new UoWPayload();
                DataParcelToken payloadToken = new DataParcelToken();
                payloadToken.setToken(payloadTopicFDN.getToken());
                payloadToken.setVersion("0.6.1");
                payloadToken.setNormalisationStatus(DataParcelNormalisationStatusEnum.DATA_PARCEL_CONTENT_NORMALISATION_FALSE);
                newPayload.setPayload(eventInstance.toString());
                payloadSet.addPayloadElement(newPayload);
                if(LOG.isTraceEnabled()){
                    LOG.trace("splitMessageIntoEvents(): Added another payload to payloadSet, count --> " + payloadSet.getPayloadElements().size());
                }
            }            
        }
        LOG.trace("splitMessageIntoEvents(): Add payloadSet as the Egress Content for the UoW");
        incomingUoW.setEgressContent(payloadSet);
        LOG.debug("splitMessageIntoEvents(): Exit: incomingUoW has been updated --> {}", incomingUoW);
        return (incomingUoW);
    }

    private void qualifyInstantMessageType(JSONObject message, FDN payloadTopicFDN){
        LOG.debug(".qualifyInstantMessageType(): Entry, message->{}, payloadTopicFDN->{}", message, payloadTopicFDN);
        if(message==null){
            LOG.debug(".qualifyInstantMessageType(): Exit, message is null/empty");
            return;
        }
        if(payloadTopicFDN == null){
            LOG.debug(".qualifyInstantMessageType(): Exit, payloadTopicFDN is empty");
            return;
        }
        JSONObject content = message.getJSONObject("content");
        if(content == null){
            LOG.debug(".qualifyInstantMessageType(): Exit, message has not content segment");
            return;
        }
        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_DISCRIMINATOR_TYPE.getTopicType(), "Messaging.ContentType"));
        String contentType = content.getString("msgtype");
        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_DISCRIMINATOR_VALUE.getTopicType(), contentType));
        LOG.debug(".qualifyInstantMessageType(): Exit, payloadTopicFDN->{}", payloadTopicFDN);
    }
}
