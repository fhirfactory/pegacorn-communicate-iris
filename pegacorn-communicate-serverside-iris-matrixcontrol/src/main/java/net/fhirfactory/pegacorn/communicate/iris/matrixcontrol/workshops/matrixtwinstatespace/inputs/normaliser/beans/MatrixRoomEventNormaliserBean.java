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
package net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.workshops.matrixtwinstatespace.inputs.normaliser.beans;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.fhirfactory.pegacorn.common.model.topicid.DataParcelToken;
import net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.workshops.cache.room.RoomMapCache;
import net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.workshops.work.microtasks.matrixserver.MatrixRoomMicroTasks;
import net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.workshops.work.microtasks.synapseserver.SynapseRoomMicroTasks;
import net.fhirfactory.pegacorn.internals.matrix.r061.api.common.MAPIResponse;
import net.fhirfactory.pegacorn.internals.synapse.api.SynapseRoom;
import net.fhirfactory.pegacorn.petasos.model.uow.UoW;
import net.fhirfactory.pegacorn.petasos.model.uow.UoWPayload;
import net.fhirfactory.pegacorn.petasos.model.uow.UoWProcessingOutcomeEnum;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;

@ApplicationScoped
public class MatrixRoomEventNormaliserBean {
    private static final Logger LOG = LoggerFactory.getLogger(MatrixRoomEventNormaliserBean.class);

    private ObjectMapper jsonObjectMapper;

    @Inject
    private MatrixRoomMicroTasks matrixRoomMicroTasks;

    @Inject
    private SynapseRoomMicroTasks synapseRoomMicroTasks;

    @Inject
    private RoomMapCache roomMap;

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
        String version = incomingUoW.getPayloadTopicID().getVersion();
        DataParcelToken token = incomingUoW.getIngresContent().getPayloadTopicID();




        UoWPayload outgoingPayload = new UoWPayload();
        outgoingPayload.setPayload(incomingUoW.getIngresContent().getPayload());
        i
        outgoingPayload.setPayloadTopicID(topicId);
        incomingUoW.getEgressContent().addPayloadElement(outgoingPayload);
        incomingUoW.setProcessingOutcome(UoWProcessingOutcomeEnum.UOW_OUTCOME_SUCCESS);
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
        MAPIResponse mapiResponse = synapseRoomMicroTasks.getRoomDetail(roomID);
        String roomDetailString = mapiResponse.getResponseContent();
        SynapseRoom roomDetail = null;
        try{
            roomDetail = jsonObjectMapper.readValue(roomDetailString, SynapseRoom.class);
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return(roomDetail);
    }
}
