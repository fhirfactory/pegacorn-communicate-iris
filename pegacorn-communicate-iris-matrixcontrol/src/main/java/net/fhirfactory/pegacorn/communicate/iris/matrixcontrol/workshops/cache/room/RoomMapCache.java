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
package net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.workshops.cache.room;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class RoomMapCache {
    private static final Logger LOG = LoggerFactory.getLogger(RoomMapCache.class);

    private ConcurrentHashMap<String, String> roomPractitionerRoleMap;
    private ConcurrentHashMap<String, String> roomHealthcareServiceMap;
    private ConcurrentHashMap<String, String> roomIDNameMap;

    private Object roomPractitionerRoleMapLock;
    private Object roomHealthcareServiceMapLock;
    private Object roomIDNameMapLock;

    public RoomMapCache(){
        roomPractitionerRoleMap = new ConcurrentHashMap<>();
        roomHealthcareServiceMap = new ConcurrentHashMap<>();
        roomIDNameMap = new ConcurrentHashMap<>();
        roomHealthcareServiceMapLock = new Object();
        roomPractitionerRoleMapLock = new Object();
        roomIDNameMapLock = new Object();
    }

    //
    // PractitionerRole Methods
    //
    public void mapRoomToPractitionerRole(String roomID, String practitionerRoleID){
        synchronized(roomPractitionerRoleMapLock) {
            roomPractitionerRoleMap.put(roomID, practitionerRoleID);
        }
    }

    public boolean isPractitionerRoleRoom(String roomID){
        if(roomPractitionerRoleMap.containsKey(roomID)){
            return(true);
        } else {
            return(false);
        }
    }

    public void removeMappingForRoom(String roomID){
        synchronized (roomPractitionerRoleMapLock){
            if(roomPractitionerRoleMap.containsKey(roomID)){
                roomPractitionerRoleMap.remove(roomID);
            }
        }
    }

    public void removeMappingForPractitionerRole(String practitionerRoleID){
        synchronized (roomPractitionerRoleMapLock){
            if(roomPractitionerRoleMap.containsValue(practitionerRoleID)){
                Enumeration<String> roomIDSet = roomPractitionerRoleMap.keys();
                while(roomIDSet.hasMoreElements()){
                    String currentRoomID = roomIDSet.nextElement();
                    String currentPractitionerRoleID = roomPractitionerRoleMap.get(currentRoomID);
                    if(currentPractitionerRoleID.equals(practitionerRoleID)){
                        roomPractitionerRoleMap.remove(currentRoomID);
                        break;
                    }
                }
            }
        }
    }

    public String getPractitionerRoleIDFromRoomID(String roomID){
        if(roomPractitionerRoleMap.contains(roomID)){
            String practitionerRoleID = roomPractitionerRoleMap.get(roomID);
            return(practitionerRoleID);
        } else {
            return(null);
        }
    }

    //
    // Room Name Methods
    //
    public void mapRoomIDToRoomName(String roomID, String roomName){
        synchronized(roomIDNameMapLock) {
            roomHealthcareServiceMap.put(roomID, roomName);
        }
    }

    public boolean hasRoomName(String roomID){
        if(roomIDNameMap.containsKey(roomID)){
            return(true);
        } else {
            return(false);
        }
    }

    public void removeRoomNameMappingForRoom(String roomID){
        synchronized (roomIDNameMapLock){
            if(roomIDNameMap.containsKey(roomID)){
                roomIDNameMap.remove(roomID);
            }
        }
    }

    public void removeMappingForRoomName(String roomName){
        synchronized (roomIDNameMap){
            if(roomIDNameMap.containsValue(roomName)){
                Enumeration<String> roomIDSet = roomIDNameMap.keys();
                while(roomIDSet.hasMoreElements()){
                    String currentRoomID = roomIDSet.nextElement();
                    String currentName = roomIDNameMap.get(currentRoomID);
                    if(currentName.equals(roomName)){
                        roomIDNameMap.remove(currentRoomID);
                        break;
                    }
                }
            }
        }
    }

    public String getRoomNameFromRoomID(String roomID){
        if(roomIDNameMap.contains(roomID)){
            String roomName = roomIDNameMap.get(roomID);
            return(roomName);
        } else {
            return(null);
        }
    }

    //
    // Healthcare Service Methods
    //
    public void mapRoomIDToHealthcareService(String roomID, String serviceID){
        synchronized(roomHealthcareServiceMapLock) {
            roomHealthcareServiceMap.put(roomID, serviceID);
        }
    }

    public boolean isHealthcareServiceRoom(String roomID){
        if(roomHealthcareServiceMap.containsKey(roomID)){
            return(true);
        } else {
            return(false);
        }
    }

    public void removeHealthcareServiceMappingForRoom(String roomID){
        synchronized (roomHealthcareServiceMapLock){
            if(roomHealthcareServiceMap.containsKey(roomID)){
                roomHealthcareServiceMap.remove(roomID);
            }
        }
    }

    public void removeMappingForHealthcareService(String serviceID){
        synchronized (roomHealthcareServiceMapLock){
            if(roomHealthcareServiceMap.containsValue(serviceID)){
                Enumeration<String> roomIDSet = roomHealthcareServiceMap.keys();
                while(roomIDSet.hasMoreElements()){
                    String currentRoomID = roomIDSet.nextElement();
                    String currentServiceID = roomHealthcareServiceMap.get(currentRoomID);
                    if(currentServiceID.equals(serviceID)){
                        roomHealthcareServiceMap.remove(currentRoomID);
                        break;
                    }
                }
            }
        }
    }

    public String getHealthcareServiceIDFromRoomID(String roomID){
        if(roomHealthcareServiceMap.contains(roomID)){
            String serviceID = roomHealthcareServiceMap.get(roomID);
            return(serviceID);
        } else {
            return(null);
        }
    }
}
