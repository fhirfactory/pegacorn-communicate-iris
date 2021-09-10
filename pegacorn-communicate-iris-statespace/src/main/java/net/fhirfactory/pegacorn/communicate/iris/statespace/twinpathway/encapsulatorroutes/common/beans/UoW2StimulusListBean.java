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
package net.fhirfactory.pegacorn.communicate.iris.statespace.twinpathway.encapsulatorroutes.common.beans;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.fhirfactory.pegacorn.components.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.components.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.internals.communicate.entities.careteam.CommunicateCareTeam;
import net.fhirfactory.pegacorn.internals.communicate.entities.coderesponderteam.CommunicateCodeResponderTeam;
import net.fhirfactory.pegacorn.internals.communicate.entities.common.valuesets.CommunicateResourceTypeEnum;
import net.fhirfactory.pegacorn.internals.communicate.entities.group.CommunicateGroup;
import net.fhirfactory.pegacorn.internals.communicate.entities.healthcareservice.CommunicateHealthcareService;
import net.fhirfactory.pegacorn.internals.communicate.entities.location.CommunicateLocation;
import net.fhirfactory.pegacorn.internals.communicate.entities.media.CommunicateMedia;
import net.fhirfactory.pegacorn.internals.communicate.entities.message.CommunicateMessage;
import net.fhirfactory.pegacorn.internals.communicate.entities.organization.CommunicateOrganization;
import net.fhirfactory.pegacorn.internals.communicate.entities.patient.CommunicatePatient;
import net.fhirfactory.pegacorn.internals.communicate.entities.practitioner.CommunicatePractitioner;
import net.fhirfactory.pegacorn.internals.communicate.entities.practitionerrole.CommunicatePractitionerRole;
import net.fhirfactory.pegacorn.internals.communicate.entities.rooms.*;
import net.fhirfactory.pegacorn.internals.communicate.entities.session.CommunicateSession;
import net.fhirfactory.pegacorn.internals.communicate.entities.user.CommunicateUser;
import net.fhirfactory.pegacorn.internals.communicate.workflow.model.stimulus.CDTStimulus;
import net.fhirfactory.pegacorn.internals.communicate.workflow.model.stimulus.CDTStimulusIdentifier;
import net.fhirfactory.pegacorn.internals.esr.resources.common.ExtremelySimplifiedResource;
import net.fhirfactory.pegacorn.petasos.model.uow.UoW;
import net.fhirfactory.pegacorn.petasos.model.uow.UoWPayload;
import org.apache.commons.lang3.SerializationUtils;
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
	public List<CDTStimulus> convertUoWContent2StimulusList(UoW incomingUoW){
        LOG.debug(".convertUoWContent2StimulusList(): Entry, incomingUoW (UoW) --> {}", incomingUoW);
        List<CDTStimulus> stimulusList = new ArrayList<CDTStimulus>();
		DataParcelManifest payloadTopicID = SerializationUtils.clone(incomingUoW.getPayloadTopicID());

		boolean isFHIRFactory = false;
		boolean isCollaboration = false;
		boolean isCommunicate = false;

		if(payloadTopicID.getContentDescriptor().hasDataParcelDefiner()){
			if(payloadTopicID.getContentDescriptor().getDataParcelDefiner().contentEquals(CommunicateResourceTypeEnum.getDataParcelDefiner())){
				isFHIRFactory = true;
			}
		}
		if(payloadTopicID.getContentDescriptor().hasDataParcelCategory()){
			if(payloadTopicID.getContentDescriptor().getDataParcelCategory().contentEquals(CommunicateResourceTypeEnum.getDataParcelCategory())){
				isCollaboration = true;
			}
		}
		if(payloadTopicID.getContentDescriptor().hasDataParcelSubCategory()){
			if(payloadTopicID.getContentDescriptor().getDataParcelSubCategory().contentEquals(CommunicateResourceTypeEnum.getDataParcelSubCategory())){
				isCommunicate = true;
			}
		}
		CDTStimulus stimulus;
		if(isFHIRFactory && isCollaboration && isCommunicate) {
			CommunicateResourceTypeEnum resourceType = CommunicateResourceTypeEnum.fromResourceName(payloadTopicID.getContentDescriptor().getDataParcelResource());
			if (resourceType != null) {
				stimulus = deriveStimulusFromUoWPayload(incomingUoW.getIngresContent(), resourceType);
				if (stimulus != null) {
					stimulus.setOriginalUoW(incomingUoW.getInstanceID());
					CDTStimulusIdentifier identifier = new CDTStimulusIdentifier();
					identifier.setId(stimulus.getResource().getSimplifiedID());
					identifier.setResourceType(resourceType);
					stimulus.setStimulusID(identifier);
					stimulusList.add(stimulus);
				}
			}
		}
   		return(stimulusList);
    }

	/**
	 *
	 * @param payload
	 * @param resourceTypeString
	 * @return
	 */
	private CDTStimulus deriveStimulusFromUoWPayload(UoWPayload payload, CommunicateResourceTypeEnum resourceTypeString) {
		CDTStimulus stimulus = new CDTStimulus();
    	String payloadContent = payload.getPayload();
    	ExtremelySimplifiedResource resource = null;
    	try {
			switch (resourceTypeString){
				case COMMUNICATE_GROUP:
					resource = getJsonMapper().readValue(payloadContent, CommunicateGroup.class);
					break;
				case COMMUNICATE_CARETEAM:
					resource = getJsonMapper().readValue(payloadContent, CommunicateCareTeam.class);
					break;
				case COMMUNICATE_CODE_RESPONDER_TEAM:
					resource = getJsonMapper().readValue(payloadContent, CommunicateCodeResponderTeam.class);
					break;
				case COMMUNICATE_HEALTHCARESERVICE:
					resource = getJsonMapper().readValue(payloadContent, CommunicateHealthcareService.class);
					break;
				case COMMUNICATE_LOCATION:
					resource = getJsonMapper().readValue(payloadContent, CommunicateLocation.class);
					break;
				case COMMUNICATE_MEDIA:
					resource = getJsonMapper().readValue(payloadContent, CommunicateMedia.class);
					break;
				case COMMUNICATE_MESSAGE:
					resource = getJsonMapper().readValue(payloadContent, CommunicateMessage.class);
					break;
				case COMMUNICATE_ORGANIZATION:
					resource = getJsonMapper().readValue(payloadContent, CommunicateOrganization.class);
					break;
				case COMMUNICATE_PATIENT:
					resource = getJsonMapper().readValue(payloadContent, CommunicatePatient.class);
					break;
				case COMMUNICATE_PRACTITIONER:
					resource = getJsonMapper().readValue(payloadContent, CommunicatePractitioner.class);
					break;
				case COMMUNICATE_PRACTITIONER_ROLE:
					resource = getJsonMapper().readValue(payloadContent, CommunicatePractitionerRole.class);
					break;
				case COMMUNICATE_ROOM:
					resource = getJsonMapper().readValue(payloadContent, CommunicateRoom.class);
					break;
				case COMMUNICATE_ROOM_CODE_RESPONDER:
					resource = getJsonMapper().readValue(payloadContent, CommunicateCodeResponderRoom.class);
					break;
				case COMMUNICATE_ROOM_HISTORIC:
					resource = getJsonMapper().readValue(payloadContent, CommunicateHistoricRoom.class);
					break;
				case COMMUNICATE_ROOM_PATIENT_CENTRAL:
					resource = getJsonMapper().readValue(payloadContent, CommunicatePatientCentralRoom.class);
					break;
				case COMMUNICATE_ROOM_PATIENT_CENTRAL_TASK_FULFILLMENT:
					resource = getJsonMapper().readValue(payloadContent, CommunicatePatientCentricTaskFulfilmentRoom.class);
					break;
				case COMMUNICATE_ROOM_PRACTITIONER_MY_CALLS:
					resource = getJsonMapper().readValue(payloadContent, CommunicatePractitionerMyCallsRoom.class);
					break;
				case COMMUNICATE_ROOM_PRACTITIONER_MY_MEDIA:
					resource = getJsonMapper().readValue(payloadContent, CommunicatePractitionerMyMediaRoom.class);
					break;
				case COMMUNICATE_ROOM_PRACTITIONER_ROLE_CENTRAL:
					resource = getJsonMapper().readValue(payloadContent, CommunicatePractitionerRoleCentralRoom.class);
					break;
				case COMMUNICATE_ROOM_PRACTITIONER_ROLE_FULFILLMENT:
					resource = getJsonMapper().readValue(payloadContent, CommunicatePractitionerRoleFulfilmentRoom.class);
					break;
				case COMMUNICATE_SESSION:
					resource = getJsonMapper().readValue(payloadContent, CommunicateSession.class);
					break;
				case COMMUNICATE_USER:
					resource = getJsonMapper().readValue(payloadContent, CommunicateUser.class);
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


}
