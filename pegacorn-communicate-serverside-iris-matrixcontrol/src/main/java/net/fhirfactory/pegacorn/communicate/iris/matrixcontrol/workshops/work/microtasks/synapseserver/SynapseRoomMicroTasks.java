package net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.workshops.work.microtasks.synapseserver;

import net.fhirfactory.pegacorn.internals.matrix.r061.api.common.MAPIResponse;

import javax.enterprise.context.ApplicationScoped;
import java.util.Map;

@ApplicationScoped
public class SynapseRoomMicroTasks {

    /**
     * The List Room admin API allows server admins to get a list of rooms on their server. There are various parameters
     * available that allow for filtering and sorting the returned list. This API supports pagination.
     * @param searchCriteria
     * @return
     */
    public MAPIResponse getRooms(Map<String, String> searchCriteria){
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
}
