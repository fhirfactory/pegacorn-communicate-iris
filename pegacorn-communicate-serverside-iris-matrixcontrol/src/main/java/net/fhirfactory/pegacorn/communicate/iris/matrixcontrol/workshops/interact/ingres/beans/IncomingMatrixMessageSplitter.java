/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.workshops.interact.ingres.beans;

import net.fhirfactory.pegacorn.common.model.generalid.FDN;
import net.fhirfactory.pegacorn.common.model.generalid.RDN;
import net.fhirfactory.pegacorn.common.model.topicid.TopicToken;
import net.fhirfactory.pegacorn.common.model.topicid.TopicTypeEnum;
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
            LOG.trace("splitMessageIntoEvents(): Exctracted JSONObject --> " + eventInstance.toString());
            if (eventInstance.has("type")) {
                FDN payloadTopicFDN = new FDN();
                payloadTopicFDN.appendRDN(new RDN(TopicTypeEnum.DATASET_DEFINER.getTopicType(), "Matrix"));
                payloadTopicFDN.appendRDN(new RDN(TopicTypeEnum.DATASET_CATEGORY.getTopicType(), "ClientServerAPI"));
                switch (eventInstance.getString("type")) {
                    case "m.room.message":
                        payloadTopicFDN.appendRDN(new RDN(TopicTypeEnum.DATASET_SUBCATEGORY.getTopicType(), "InstantMessaging"));
                        payloadTopicFDN.appendRDN(new RDN(TopicTypeEnum.DATASET_RESOURCE.getTopicType(), "m.room.message"));
                        qualifyInstantMessageType(eventInstance, payloadTopicFDN);
                        break;
                    case "m.room.message.feedback":
                        payloadTopicFDN.appendRDN(new RDN(TopicTypeEnum.DATASET_SUBCATEGORY.getTopicType(), "InstantMessaging"));
                        payloadTopicFDN.appendRDN(new RDN(TopicTypeEnum.DATASET_RESOURCE.getTopicType(), "m.room.message.feedback"));
                        break;
                    case "m.room.name":
                        payloadTopicFDN.appendRDN(new RDN(TopicTypeEnum.DATASET_SUBCATEGORY.getTopicType(), "InstantMessaging"));
                        payloadTopicFDN.appendRDN(new RDN(TopicTypeEnum.DATASET_RESOURCE.getTopicType(), "m.room.name"));
                        break;
                    case "m.room.topic":
                        payloadTopicFDN.appendRDN(new RDN(TopicTypeEnum.DATASET_SUBCATEGORY.getTopicType(), "InstantMessaging"));
                        payloadTopicFDN.appendRDN(new RDN(TopicTypeEnum.DATASET_RESOURCE.getTopicType(), "m.room.topic"));
                        break;
                    case "m.room.avatar":
                        payloadTopicFDN.appendRDN(new RDN(TopicTypeEnum.DATASET_SUBCATEGORY.getTopicType(), "InstantMessaging"));
                        payloadTopicFDN.appendRDN(new RDN(TopicTypeEnum.DATASET_RESOURCE.getTopicType(), "m.room.avatar"));
                        break;
                    case "m.room.pinned_events":
                        payloadTopicFDN.appendRDN(new RDN(TopicTypeEnum.DATASET_SUBCATEGORY.getTopicType(), "InstantMessaging"));
                        payloadTopicFDN.appendRDN(new RDN(TopicTypeEnum.DATASET_RESOURCE.getTopicType(), "m.room.pinned_events"));
                        break;
                    case "m.room.canonical_alias":
                        payloadTopicFDN.appendRDN(new RDN(TopicTypeEnum.DATASET_SUBCATEGORY.getTopicType(), "RoomEvents"));
                        payloadTopicFDN.appendRDN(new RDN(TopicTypeEnum.DATASET_RESOURCE.getTopicType(), "m.room.canonical_alias"));
                        break;
                    case "m.room.create":
                        payloadTopicFDN.appendRDN(new RDN(TopicTypeEnum.DATASET_SUBCATEGORY.getTopicType(), "RoomEvents"));
                        payloadTopicFDN.appendRDN(new RDN(TopicTypeEnum.DATASET_RESOURCE.getTopicType(), "m.room.create"));
                        break;
                    case "m.room.join_rules":
                        payloadTopicFDN.appendRDN(new RDN(TopicTypeEnum.DATASET_SUBCATEGORY.getTopicType(), "RoomEvents"));
                        payloadTopicFDN.appendRDN(new RDN(TopicTypeEnum.DATASET_RESOURCE.getTopicType(), "m.room.join_rules"));
                        break;
                    case "m.room.member":
                        payloadTopicFDN.appendRDN(new RDN(TopicTypeEnum.DATASET_SUBCATEGORY.getTopicType(), "RoomEvents"));
                        payloadTopicFDN.appendRDN(new RDN(TopicTypeEnum.DATASET_RESOURCE.getTopicType(), "m.room.member"));
                        break;
                    case "m.room.power_levels":
                        payloadTopicFDN.appendRDN(new RDN(TopicTypeEnum.DATASET_SUBCATEGORY.getTopicType(), "RoomEvents"));
                        payloadTopicFDN.appendRDN(new RDN(TopicTypeEnum.DATASET_RESOURCE.getTopicType(), "m.room.power_levels"));
                        break;
                    case "m.room.redaction":
                        payloadTopicFDN.appendRDN(new RDN(TopicTypeEnum.DATASET_SUBCATEGORY.getTopicType(), "RoomEvents"));
                        payloadTopicFDN.appendRDN(new RDN(TopicTypeEnum.DATASET_RESOURCE.getTopicType(), "m.room.redaction"));
                        break;
                    case "m.presence":
                        payloadTopicFDN.appendRDN(new RDN(TopicTypeEnum.DATASET_SUBCATEGORY.getTopicType(), "Presence"));
                        payloadTopicFDN.appendRDN(new RDN(TopicTypeEnum.DATASET_RESOURCE.getTopicType(), "m.presence"));
                        break;
                    case "m.typing":
                        payloadTopicFDN.appendRDN(new RDN(TopicTypeEnum.DATASET_SUBCATEGORY.getTopicType(), "Typing"));
                        payloadTopicFDN.appendRDN(new RDN(TopicTypeEnum.DATASET_RESOURCE.getTopicType(), "m.typing"));
                        break;
                    default:
                        payloadTopicFDN.appendRDN(new RDN(TopicTypeEnum.DATASET_SUBCATEGORY.getTopicType(), "General"));
                        payloadTopicFDN.appendRDN(new RDN(TopicTypeEnum.DATASET_RESOURCE.getTopicType(), "Unknown"));
                }
                UoWPayload newPayload = new UoWPayload();
                TopicToken payloadToken = new TopicToken();
                payloadToken.setIdentifier(payloadTopicFDN.getToken());
                payloadToken.setVersion("0.6.1");
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
        payloadTopicFDN.appendRDN(new RDN(TopicTypeEnum.DATASET_DISCRIMINATOR_TYPE.getTopicType(), "InstantMessagingContentType"));
        String contentType = content.getString("msgtype");
        payloadTopicFDN.appendRDN(new RDN(TopicTypeEnum.DATASET_DISCRIMINATOR_VALUE.getTopicType(), contentType));
        LOG.debug(".qualifyInstantMessageType(): Exit, payloadTopicFDN->{}", payloadTopicFDN);
    }
}
