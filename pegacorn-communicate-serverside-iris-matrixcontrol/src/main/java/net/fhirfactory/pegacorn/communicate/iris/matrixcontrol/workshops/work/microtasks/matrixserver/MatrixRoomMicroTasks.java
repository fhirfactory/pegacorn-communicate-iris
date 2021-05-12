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
package net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.workshops.work.microtasks.matrixserver;

import net.fhirfactory.pegacorn.internals.matrix.r061.api.common.MAPIResponse;
import net.fhirfactory.pegacorn.internals.matrix.r061.api.rooms.MRoomCreation;
import net.fhirfactory.pegacorn.internals.matrix.r061.api.rooms.MRoomVisibilityEnum;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;

@ApplicationScoped
public class MatrixRoomMicroTasks {

    public MAPIResponse getRoom(String roomID){
        MAPIResponse taskResponse = new MAPIResponse();

        return(taskResponse);
    }

    /**
     * Lists the public rooms on the server.
     *
     * This API returns paginated responses. The rooms are ordered by the number of joined members,
     * with the largest rooms first.
     *
     * @return
     */
    public MAPIResponse getPublicRooms(){
        MAPIResponse taskResponse = new MAPIResponse();

        return(taskResponse);
    }

    /**
     * Gets the visibility of a given room on the server's public room directory.
     *
     * @param roomID
     * @return
     */
    public MAPIResponse getRoomVisibility(String roomID){
        MAPIResponse taskResponse = new MAPIResponse();

        return(taskResponse);
    }

    /**
     * Sets the visibility of a given room in the server's public room directory.
     *
     * @param roomID
     * @param roomVisibility
     * @return
     */
    public MAPIResponse setRoomVisibility(String roomID, MRoomVisibilityEnum roomVisibility){
        MAPIResponse taskResponse = new MAPIResponse();

        return(taskResponse);
    }

    //
    // Room Status Detail
    //

    /**
     * Get the state events for the current state of a room.
     *
     * @param roomID
     * @return
     */
    public MAPIResponse getRoomState(String roomID){
        MAPIResponse response = new MAPIResponse();

        return(response);
    }

    /**
     * Get the list of members for this room.
     *
     * @param roomID
     * @return
     */
    public MAPIResponse getRoomMembers(String roomID){
        MAPIResponse response = new MAPIResponse();

        return(response);
    }

    /**
     * This API returns a map of MXIDs to member info objects for members of the room. The current user must be in the
     * room for it to work, unless it is an Application Service in which case any of the AS's users must be in the room.
     * This API is primarily for Application Services and should be faster to respond than /members as it can be
     * implemented more efficiently on the server.
     *
     * @param roomID
     * @return
     */
    public MAPIResponse getRoomJoinedMembers(String roomID){
        MAPIResponse response = new MAPIResponse();

        return(response);
    }

    public MAPIResponse getRoomMessages(String roomID){
        MAPIResponse response = new MAPIResponse();

        return(response);
    }

    /**
     * Strips all information out of an event which isn't critical to the integrity of the server-side representation
     * of the room.
     *
     * This cannot be undone.
     *
     * @param roomID
     * @param eventID
     * @param transactionID
     * @return
     */
    public MAPIResponse redactRoomEvent(String roomID, String eventID, String transactionID){
        MAPIResponse response = new MAPIResponse();

        return(response);
    }

    //
    // Room Creation/Deletion
    //

    /**
     * Create a new room with various configuration options.
     *
     * @param roomCreation
     * @return
     */
    public MAPIResponse createRoom(MRoomCreation roomCreation){
        MAPIResponse response = new MAPIResponse();

        return(response);
    }

    /**
     * Create a new mapping from room alias to room ID.
     *
     * @param roomID
     * @param roomAlias
     * @return
     */
    public MAPIResponse setRoomAlias(String roomID, String roomAlias){
        MAPIResponse response = new MAPIResponse();

        return(response);
    }

    /**
     * Requests that the server resolve a room alias to a room ID.
     *
     * The server will use the federation API to resolve the alias if the domain part of the alias does not correspond
     * to the server's own domain.
     *
     * @param roomAlias
     * @return
     */
    public MAPIResponse getRoomFromAlias(String roomAlias){
        MAPIResponse response = new MAPIResponse();

        return(response);
    }

    //
    // Auxillary MicroTasks
    //

    public boolean doesRoomExistForUser(String userID, String roomName){
        return(false);
    }
}
