/*
 * Copyright (c) 2020 Mark A. Hunter (ACT Health)
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
package net.fhirfactory.pegacorn.communicate.iris.statespace.normaliser.matrixevent.roombased.beans;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.workshops.cache.room.RoomMapCache;
import net.fhirfactory.pegacorn.communicate.iris.statespace.normaliser.matrixevent.common.MatrixEventNormaliserBeanBase;
import net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.workshops.actions.simpleactions.matrixserver.MatrixRoomSimpleActions;
import net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.workshops.actions.simpleactions.synapseserver.SynapseRoomSimpleActions;
import net.fhirfactory.pegacorn.internals.esr.brokers.MatrixRoomESRBroker;
import net.fhirfactory.pegacorn.internals.matrix.r061.api.common.MAPIResponse;
import net.fhirfactory.pegacorn.internals.synapse.api.SynapseRoom;
import net.fhirfactory.pegacorn.petasos.model.uow.UoW;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;

@ApplicationScoped
public class MatrixRoomEventNormaliserBean extends MatrixEventNormaliserBeanBase {
    private static final Logger LOG = LoggerFactory.getLogger(MatrixRoomEventNormaliserBean.class);

    private ObjectMapper jsonObjectMapper;

    @Inject
    private MatrixRoomSimpleActions matrixRoomMicroTasks;

    @Inject
    private SynapseRoomSimpleActions synapseRoomSimpleActions;

    @Inject
    private RoomMapCache roomMap;

    @Inject
    private MatrixRoomESRBroker matrixRoomBroker;

    public MatrixRoomEventNormaliserBean(){
        jsonObjectMapper = new ObjectMapper();
        jsonObjectMapper.configure(JsonParser.Feature.ALLOW_MISSING_VALUES, true);
    }

    /**
     * This functions does nothing to the actual incoming (Ingres) payload - merely copying it to the Egress payload
     * and assigning the processing outcome as a Success. It also modifies the TopicID of the Egress payload to remove
     * the "unnormalised" discriminator.
     * @param incomingUoW The incoming UoW of work
     * @return A unit of work with the Egress Content matching the Ingres Content and with discriminator --> "Source":"Ladon.StateSpace.Normaliser"
     */
    public UoW normaliseMatrixRoomEvent(UoW incomingUoW){

        String matrixEventMessage = incomingUoW.getIngresContent().getPayload();
        String roomID = extractRoomID(matrixEventMessage);
        if(roomID == null){
            failUoW(incomingUoW, "Unable to resolve RoomID from Event Message");
            return(incomingUoW);
        }
        boolean isHealthcareServiceRoom = roomMap.isHealthcareServiceRoom(roomID);
        boolean isPractitionerRoleRoom = roomMap.isPractitionerRoleRoom(roomID);
        boolean hasName = roomMap.hasRoomName(roomID);
        if( isHealthcareServiceRoom || isPractitionerRoleRoom || hasName){
            passUoW(incomingUoW);
            return(incomingUoW);
        }
        SynapseRoom roomDetail = getRoomDetail(roomID);
        if(roomDetail == null){
            failUoW(incomingUoW, "Unable to get Room Detail from Synapse for RoomID in Event Message");
            return(incomingUoW);
        }
        // Parse Name to see what type of room it is and updated appropriate cache(s)

        // Create a MatrixRoomESR instance and associate it to the appropriate (other) Resource

        return(incomingUoW);
    }



    private String extractRoomID(String roomEventMsg){
        if(roomEventMsg == null){
            return(null);
        }
        JSONObject messageObject = new JSONObject(roomEventMsg);
        String roomID = messageObject.getString("room_id");
        return(roomID);
    }

    private SynapseRoom getRoomDetail(String roomID){
        MAPIResponse mapiResponse = synapseRoomSimpleActions.getRoomDetail(roomID);
        String roomDetailString = mapiResponse.getResponseContent();
        SynapseRoom roomDetail = null;
        try{
            roomDetail = jsonObjectMapper.readValue(roomDetailString, SynapseRoom.class);
        } catch (JsonMappingException e) {
            LOG.error(".getRoomDetail(): Unable to map message event to POJO (JsonMappingException, error->{}", e);
            return(null);
        } catch (JsonParseException e) {
            LOG.error(".getRoomDetail(): Unable to parse message event to POJO (JsonParseException, error->{}", e);
            return(null);
        } catch (IOException e) {
            LOG.error(".getRoomDetail(): Message event processing error (IOException, error->{}", e);
            return(null);
        }
        return(roomDetail);
    }
}
