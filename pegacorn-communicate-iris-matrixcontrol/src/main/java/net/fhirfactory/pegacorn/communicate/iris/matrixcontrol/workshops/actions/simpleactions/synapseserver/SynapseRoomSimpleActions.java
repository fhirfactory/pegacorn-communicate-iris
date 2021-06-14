package net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.workshops.actions.simpleactions.synapseserver;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.fhirfactory.pegacorn.internals.matrix.r061.api.common.MAPIResponse;
import net.fhirfactory.pegacorn.internals.synapse.api.SynapseRoom;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class SynapseRoomSimpleActions {
    private static final Logger LOG = LoggerFactory.getLogger(SynapseRoomSimpleActions.class);

    ObjectMapper jsonMapper;

    public SynapseRoomSimpleActions(){
        jsonMapper = new ObjectMapper();
        jsonMapper.configure(JsonParser.Feature.ALLOW_MISSING_VALUES, true);
    }

    public ObjectMapper getJSONMapper() {
        return jsonMapper;
    }

    /**
     * The List Room admin API allows server admins to get a list of rooms on their server. There are various parameters
     * available that allow for filtering and sorting the returned list. This API supports pagination.
     * @param searchTerm
     * @return
     */
    public MAPIResponse getRooms(String searchTerm){
        MAPIResponse taskResponse = new MAPIResponse();

        return(taskResponse);
    }

    /**
     * The List Room admin API supporting the searching for rooms owned/created-by a specific userID;
     *
     * @param userID
     * @return
     */
    public MAPIResponse getRoomsForUser(String userID){
        MAPIResponse taskResponse = new MAPIResponse();

        return(taskResponse);
    }

    /**
     * The Room Details admin API allows server admins to get all details of a room.
     *
     * @param roomID
     * @return
     */
    public MAPIResponse getRoomDetail(String roomID){
        MAPIResponse taskResponse = new MAPIResponse();

        return(taskResponse);
    }

    /**
     * The Room Members admin API allows server admins to get a list of all members of a room.
     *
     * @param roomID
     * @return
     */
    public MAPIResponse getRoomMembers(String roomID){
        MAPIResponse taskResponse = new MAPIResponse();

        return(taskResponse);
    }

    /**
     * The Room State admin API allows server admins to get a list of all state events in a room.
     *
     * @param roomID
     * @return
     */
    public MAPIResponse getRoomState(String roomID){
        MAPIResponse taskResponse = new MAPIResponse();

        return(taskResponse);
    }

    /**
     * The Delete Room admin API allows server admins to remove rooms from server and block these rooms.
     *
     * The new room will be created with the user specified by the new_room_user_id parameter as room administrator and
     * will contain a message explaining what happened. Users invited to the new room will have power level -10 by
     * default, and thus be unable to speak.
     *
     * If block is True it prevents new joins to the old room.
     *
     * This API will remove all trace of the old room from your database after removing all local users. If purge is
     * true (the default), all traces of the old room will be removed from your database after removing all local users.
     * If you do not want this to happen, set purge to false. Depending on the amount of history being purged a call to
     * the API may take several minutes or longer.
     *
     * The local server will only have the power to move local user and room aliases to the new room. Users on other
     * servers will be unaffected.
     *
     * @param roomID
     * @return
     */
    public MAPIResponse deleteRoom(String roomID){
        MAPIResponse taskResponse = new MAPIResponse();

        return(taskResponse);
    }

    //
    // General Helper Methods
    //

    /**
     * This method searches the Synapse Database for any Rooms that contain the roomName as part of the "name"
     * attribute and returns true if one matches. Be very careful using this method, as many rooms may exist with
     * the same name. If you want to be sure about whether a room exists for a particular user (Practitioner), you
     * should use the roomExists(String roomName, String userID) method.
     *
     * @param roomName
     * @return
     */
    public boolean roomExists(String roomName){
        if(StringUtils.isBlank(roomName)){
            return(false);
        }
        MAPIResponse response = getRooms(roomName);
        if(response.isSuccessful()){
            List<SynapseRoom> roomList = extractSearchResponseDetail(response.getResponseContent());
            for(SynapseRoom currentRoom: roomList){
                String currentRoomName = currentRoom.getName();
                if(StringUtils.isNotBlank(currentRoomName)){
                    if(currentRoomName.contentEquals(roomName)){
                        return(true);
                    }
                }
            }
        }
        return(false);
    }

    /**
     * This method searches the Synapse Database for any Rooms that contain the roomName as part of the "name"
     * attribute and returns true if one matches AND the userID matches.
     *
     * @param roomName
     * @param userID
     * @return
     */
    public boolean roomExists(String roomName, String userID){
        if(StringUtils.isBlank(userID) || StringUtils.isBlank(roomName)) {
            return (false);
        }
        MAPIResponse response = getRooms(roomName);
        if(response.isSuccessful()){
            List<SynapseRoom> roomList = extractSearchResponseDetail(response.getResponseContent());
            for(SynapseRoom currentRoom: roomList){
                String currentRoomName = currentRoom.getName();
                if(roomName.contentEquals(currentRoomName)){
                    String currentRoomCreator = currentRoom.getCreator();
                    if( userID.contentEquals(currentRoomCreator)){
                        return(true);
                    }
                }
            }
        }
        return(false);
    }

    /**
     * This method extracts the (set of) SynapseRoom details (typically) provided in response to
     * a getRooms() method on the Synapse Admin API.
     *
     * @param searchResponseString The JSON Payload response to the getRooms() Synapse Admin API Method.
     * @return a List of SynapseRoom POJOs as converted from the JSON Paylood
     */
    public List<SynapseRoom> extractSearchResponseDetail(String searchResponseString){
        List<SynapseRoom> roomList = new ArrayList<>();
        if(StringUtils.isBlank(searchResponseString)){
            return(roomList);
        }
        JSONObject searchResponse = new JSONObject(searchResponseString);
        JSONArray roomElementSet = searchResponse.getJSONArray("rooms");
        if(roomElementSet.isEmpty()){
            return(roomList);
        }
        try {
            for (Integer counter = 0; counter < roomElementSet.length(); counter += 1) {
                String roomDetailString = roomElementSet.getString(counter);
                SynapseRoom currentRoom = getJSONMapper().readValue(roomDetailString, SynapseRoom.class);
                roomList.add(currentRoom);
            }
        } catch (JsonMappingException e) {
            LOG.error(".extractSearchResponseDetail(): Unable to decode room detail (JSON Mapping Exception), error ->{}",e.getMessage());
        } catch (JsonParseException e) {
            LOG.error(".extractSearchResponseDetail(): Unable to decode room detail (JSON Parsing Exception), error ->{}",e.getMessage());
        } catch (IOException e) {
            LOG.error(".extractSearchResponseDetail(): Unable to decode room detail (IO Exception), error ->{}",e.getMessage());
        }
        return(roomList);
    }

    public List<String> extractMembersFromRoom(String roomID){
        List<String> memberIDList = new ArrayList<>();
        if(StringUtils.isBlank(roomID)){
            return(memberIDList);
        }
        MAPIResponse response = getRoomMembers(roomID);
        if(response.isSuccessful()){
            String responseString = response.getResponseContent();
            if(StringUtils.isNotBlank(responseString)){
                JSONObject responseObject = new JSONObject(responseString);
                JSONArray memberIDListArray = responseObject.getJSONArray("members");

            }
        }
        return(memberIDList);
    }
}
