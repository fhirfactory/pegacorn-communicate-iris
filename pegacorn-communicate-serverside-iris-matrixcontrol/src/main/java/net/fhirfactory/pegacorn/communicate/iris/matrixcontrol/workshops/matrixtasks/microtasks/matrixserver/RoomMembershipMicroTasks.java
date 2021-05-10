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
package net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.workshops.matrixtasks.microtasks.matrixserver;

import net.fhirfactory.pegacorn.internals.matrix.r061.api.common.MAPIResponse;
import net.fhirfactory.pegacorn.internals.matrix.r061.api.rooms.MJoinedRooms;

public class RoomMembershipMicroTasks {
    /**
     * This API returns a list of the user's current rooms.
     *
     * @param userID
     * @return
     */
    public MJoinedRooms getJoinedRooms(String userID){
        MJoinedRooms joinedRooms = new MJoinedRooms();

        return(joinedRooms);
    }

    /**
     * This API invites a user to participate in a particular room. They do not start participating in the room until
     * they actually join the room.
     *
     * Only users currently in a particular room can invite other users to join that room.
     *
     * @param userID
     * @param roomID
     * @return
     */
    public MAPIResponse invite(String userID, String roomID){
        MAPIResponse taskResponse = new MAPIResponse();

        return(taskResponse); // All Good!
    }

    /**
     * This API starts a user participating in a particular room, if that user is allowed to participate in that room.
     * After this call, the client is allowed to see all current state events in the room, and all subsequent events
     * associated with the room until the user leaves the room.
     *
     * @param userID
     * @param roomID
     * @return
     */
    public MAPIResponse join(String userID, String roomID){
        MAPIResponse taskResponse = new MAPIResponse();

        return(taskResponse);
    }

    /**
     * This API starts a user participating in a particular room, if that user is allowed to participate in that room.
     * After this call, the client is allowed to see all current state events in the room, and all subsequent events
     * associated with the room until the user leaves the room.
     *
     * @param userID
     * @param roomID
     * @return
     */
    public MAPIResponse joinUsingAlias(String userID, String roomID){
        MAPIResponse taskResponse = new MAPIResponse();

        return(taskResponse);
    }

    /**
     * This API stops a user participating in a particular room.
     *
     * If the user was already in the room, they will no longer be able to see new events in the room. If the room
     * requires an invite to join, they will need to be re-invited before they can re-join.
     *
     * If the user was invited to the room, but had not joined, this call serves to reject the invite.
     *
     * The user will still be allowed to retrieve history from the room which they were previously allowed to see.
     *
     * @param userID
     * @param roomID
     * @return
     */
    public MAPIResponse leaveRoom(String userID, String roomID){
        MAPIResponse taskResponse = new MAPIResponse();

        return(taskResponse);
    }

    /**
     * This API stops a user remembering about a particular room.
     *
     * In general, history is a first class citizen in Matrix. After this API is called, however, a user will no longer
     * be able to retrieve history for this room. If all users on a homeserver forget a room, the room is eligible for
     * deletion from that homeserver.
     *
     * If the user is currently joined to the room, they must leave the room before calling this API.
     *
     * @param userID
     * @param roomID
     * @return
     */
    public MAPIResponse forgetRoom(String userID, String roomID){
        MAPIResponse taskResponse = new MAPIResponse();

        return(taskResponse);
    }

    /**
     * Kick a user from the room.
     *
     * The caller must have the required power level in order to perform this operation.
     *
     * @param userID
     * @param roomID
     * @return
     */
    public MAPIResponse kickUserFromRoom(String userID, String roomID){
        MAPIResponse taskResponse = new MAPIResponse();

        return(taskResponse);
    }

    /**
     * A user may decide to ban another user in a room. 'Banning' forces the target user to leave the room and prevents
     * them from re-joining the room. A banned user will not be treated as a joined user, and so will not be able to
     * send or receive events in the room. In order to ban someone, the user performing the ban MUST have the required
     * power level.
     *
     * @param userID
     * @param roomID
     * @return
     */
    public MAPIResponse banUserFromRoom(String userID, String roomID){
        MAPIResponse taskResponse = new MAPIResponse();

        return(taskResponse);
    }

    /**
     * Unban a user from the room. This allows them to be invited to the room, and join if they would otherwise be
     * allowed to join according to its join rules.
     *
     * The caller must have the required power level in order to perform this operation.
     *
     * @param userID
     * @param roomID
     * @return
     */
    public MAPIResponse unbanUserFromRoom(String userID, String roomID){
        MAPIResponse taskResponse = new MAPIResponse();

        return(taskResponse);
    }

}
