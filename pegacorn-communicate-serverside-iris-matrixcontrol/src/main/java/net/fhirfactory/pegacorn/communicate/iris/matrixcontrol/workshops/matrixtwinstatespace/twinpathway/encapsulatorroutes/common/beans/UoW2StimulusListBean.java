/*
 * Copyright (c) 2020 Mark A. Hunter
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
package net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.workshops.matrixtwinstatespace.twinpathway.encapsulatorroutes.common.beans;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeFDN;
import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeRDN;
import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeTypeEnum;
import net.fhirfactory.pegacorn.common.model.generalid.FDN;
import net.fhirfactory.pegacorn.common.model.topicid.DataParcelToken;
import net.fhirfactory.pegacorn.common.model.topicid.DataParcelTypeKeyEnum;
import net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.model.stimulus.MTStimulus;
import net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.model.stimulus.MTStimulusIdentifier;
import net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.model.stimulus.MTStimulusReasonTypeEnum;
import net.fhirfactory.pegacorn.components.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.internals.esr.resources.*;
import net.fhirfactory.pegacorn.internals.esr.resources.common.ExtremelySimplifiedResource;
import net.fhirfactory.pegacorn.internals.esr.resources.common.ExtremelySimplifiedResourceTypeEnum;
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
import net.fhirfactory.pegacorn.petasos.model.uow.UoW;
import net.fhirfactory.pegacorn.petasos.model.uow.UoWPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 */
@ApplicationScoped
public class UoW2StimulusListBean {
    private static final Logger LOG = LoggerFactory.getLogger(UoW2StimulusListBean.class );

    private ObjectMapper jsonMapper;

    public UoW2StimulusListBean(){
    	super();
    	jsonMapper = new ObjectMapper();
    	jsonMapper.configure(JsonParser.Feature.ALLOW_MISSING_VALUES, true);
	}



	@Inject
	private ProcessingPlantInterface processingPlant;

	public ObjectMapper getJsonMapper() {
		return jsonMapper;
	}

	/**
	 *
	 * @param incomingUoW
	 * @return
	 */
	public List<MTStimulus> convertUoWContent2StimulusList(UoW incomingUoW){
        LOG.debug(".convertUoWContent2StimulusList(): Entry, incomingUoW (UoW) --> {}", incomingUoW);
        List<MTStimulus> stimulusList = new ArrayList<MTStimulus>();
		DataParcelToken payloadTopicID = incomingUoW.getPayloadTopicID();
		FDN payloadFDN = new FDN(payloadTopicID.getToken());
        String definer = payloadFDN.extractRDNViaQualifier(DataParcelTypeKeyEnum.DATASET_DEFINER.getTopicType()).getValue();
        String category = payloadFDN.extractRDNViaQualifier(DataParcelTypeKeyEnum.DATASET_CATEGORY.getTopicType()).getValue();
        String subcategory = payloadFDN.extractRDNViaQualifier(DataParcelTypeKeyEnum.DATASET_SUBCATEGORY.getTopicType()).getValue();
		String resourceTypeName = payloadFDN.extractRDNViaQualifier(DataParcelTypeKeyEnum.DATASET_RESOURCE.getTopicType()).getValue();

        // check if ESR
		boolean isFHIRFactory = definer.contentEquals("FHIRFactory");
		boolean isDirectoryServices = category.contentEquals("DirectoryServices");
		boolean isExtremelySimplifiedResources = subcategory.contentEquals("ExtremelySimplifiedResources");
		// check if Matrix
		boolean isMatrix = definer.contentEquals("Matrix");
		boolean isMatrixClientServices = category.contentEquals("ClientServerAPI");

		MTStimulusReasonTypeEnum reason = null;
		if(isFHIRFactory && isDirectoryServices && isExtremelySimplifiedResources){
			reason = MTStimulusReasonTypeEnum.MT_STIMULUS_REASON_ESR_EVENT;
		}
		if(isMatrix && isMatrixClientServices){
			reason = MTStimulusReasonTypeEnum.MT_STIMULUS_REASON_MATRIX_EVENT;
		}
		MTStimulus stimulus = null;
    	switch(reason) {
			case MT_STIMULUS_REASON_ESR_EVENT:{
    			stimulus = deriveStimulusFromUoWPayloadForESR(incomingUoW.getIngresContent(), resourceTypeName);
    			break;
    		}
			case MT_STIMULUS_REASON_MATRIX_EVENT:{
				stimulus = deriveStimulusFromUoWForMatrixEvent(incomingUoW.getIngresContent(), resourceTypeName);
			}
    		default:{
				// Do nothing
    		}
    	}
    	if(stimulus != null){
    		stimulus.setOriginalUoW(incomingUoW.getInstanceID());
    		TopologyNodeFDN processingPlantFDN = processingPlant.getProcessingPlantNode().getNodeFDN();
    		TopologyNodeRDN processingPlantRDN = processingPlantFDN.extractRDNForNodeType(TopologyNodeTypeEnum.PROCESSING_PLANT);
    		String simpleProcessingPlantName = processingPlantRDN.getNodeName()+processingPlantRDN.getNodeVersion().replace(".","");
    		String stimulusIdentifierValue = simpleProcessingPlantName +"-"+incomingUoW.getInstanceID().toString();
    		MTStimulusIdentifier stimulusIdentifier = new MTStimulusIdentifier();
    		stimulusIdentifier.setId(stimulusIdentifierValue);
    		stimulus.setStimulusID(stimulusIdentifier);
			stimulusList.add(stimulus);
		}
   		return(stimulusList);
    }

	/**
	 *
	 * @param payload
	 * @param resourceTypeString
	 * @return
	 */
	private MTStimulus deriveStimulusFromUoWPayloadForESR(UoWPayload payload, String resourceTypeString) {
		MTStimulus stimulus = new MTStimulus();
    	String payloadContent = payload.getPayload();
		ExtremelySimplifiedResourceTypeEnum resourceType = ExtremelySimplifiedResourceTypeEnum.fromString(resourceTypeString);
    	ExtremelySimplifiedResource resource = null;
    	try {
			switch (resourceType){
				case ESR_CARE_TEAM:
					resource = getJsonMapper().readValue(payloadContent, CareTeamESR.class);
					break;
				case ESR_GROUP:
					resource = getJsonMapper().readValue(payloadContent, GroupESR.class);
					break;
				case ESR_HEALTHCARE_SERVICE:
					resource = getJsonMapper().readValue(payloadContent, HealthcareServiceESR.class);
					break;
				case ESR_LOCATION:
					resource = getJsonMapper().readValue(payloadContent, LocationESR.class);
					break;
				case ESR_MATRIX_ROOM:
					resource = getJsonMapper().readValue(payloadContent, MatrixRoomESR.class);
					break;
				case ESR_ORGANIZATION:
					resource = getJsonMapper().readValue(payloadContent, OrganizationESR.class);
					break;
				case ESR_PATIENT:
					resource = getJsonMapper().readValue(payloadContent, PatientESR.class);
					break;
				case ESR_PERSON:
					resource = getJsonMapper().readValue(payloadContent, PersonESR.class);
					break;
				case ESR_PRACTITIONER:
					resource = getJsonMapper().readValue(payloadContent, PractitionerESR.class);
					break;
				case ESR_PRACTITIONER_ROLE:
					resource = getJsonMapper().readValue(payloadContent, PractitionerRoleESR.class);
					break;
				case ESR_ROLE:
					resource = getJsonMapper().readValue(payloadContent, RoleESR.class);
					break;
				case ESR_ROLE_CATEGORY:
					resource = getJsonMapper().readValue(payloadContent, RoleCategoryESR.class);
					break;
				default:
					resource = null;
					break;
			}
		} catch (JsonMappingException e) {
    		LOG.error(".getESRFromUoWPayload(): Cannot resolve ESR from UoW, (JsonMappingException) error -> {}", e.getMessage());
		} catch (JsonParseException e) {
			LOG.error(".getESRFromUoWPayload(): Cannot resolve ESR from UoW, (JsonParseException) error -> {}", e.getMessage());
		} catch (IOException e) {
			LOG.error(".getESRFromUoWPayload(): Cannot resolve ESR from UoW, (IOException) error -> {}", e.getMessage());
		}
    	if(resource != null){
			stimulus.setResource(resource);
			stimulus.setCreationDate(Date.from(Instant.now()));
			return(stimulus);
		}
    	return(null);
	}

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
