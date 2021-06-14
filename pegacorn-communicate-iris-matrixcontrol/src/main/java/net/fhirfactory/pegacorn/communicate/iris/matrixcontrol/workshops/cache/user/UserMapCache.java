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
package net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.workshops.cache.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class UserMapCache {
    private static final Logger LOG = LoggerFactory.getLogger(UserMapCache.class);

    private ConcurrentHashMap<String, String> userPractitionerRoleMap;
    private ConcurrentHashMap<String, String> userHealthcareServiceMap;
    private ConcurrentHashMap<String, String> userPractitionerMap;

    private Object userPractitionerRoleMapLock;
    private Object userHealthcareServiceMapLock;
    private Object userPractitionerMapLock;

    public UserMapCache(){
        userPractitionerRoleMap = new ConcurrentHashMap<>();
        userHealthcareServiceMap = new ConcurrentHashMap<>();
        userPractitionerMap = new ConcurrentHashMap<>();
        userHealthcareServiceMapLock = new Object();
        userPractitionerRoleMapLock = new Object();
        userPractitionerMapLock = new Object();
    }

    //
    // PractitionerRole Methods
    //
    public void mapUserToPractitionerRole(String userID, String practitionerRoleID){
        synchronized(userPractitionerRoleMapLock) {
            userPractitionerRoleMap.put(userID, practitionerRoleID);
        }
    }

    public boolean isPractitionerRoleUser(String userID){
        if(userPractitionerRoleMap.containsKey(userID)){
            return(true);
        } else {
            return(false);
        }
    }

    public void removeMappingPractitionerRoleForUser(String userID){
        synchronized (userPractitionerRoleMapLock){
            if(userPractitionerRoleMap.containsKey(userID)){
                userPractitionerRoleMap.remove(userID);
            }
        }
    }

    public void removeMappingForPractitionerRole(String practitionerRoleID){
        synchronized (userPractitionerRoleMapLock){
            if(userPractitionerRoleMap.containsValue(practitionerRoleID)){
                Enumeration<String> userIDSet = userPractitionerRoleMap.keys();
                while(userIDSet.hasMoreElements()){
                    String currentUserID = userIDSet.nextElement();
                    String currentPractitionerRoleID = userPractitionerRoleMap.get(currentUserID);
                    if(currentPractitionerRoleID.equals(practitionerRoleID)){
                        userPractitionerRoleMap.remove(currentUserID);
                        break;
                    }
                }
            }
        }
    }

    public String getPractitionerRoleIDFromUserID(String userID){
        if(userPractitionerRoleMap.contains(userID)){
            String practitionerRoleID = userPractitionerRoleMap.get(userID);
            return(practitionerRoleID);
        } else {
            return(null);
        }
    }

    //
    // HealthcareService Methods
    //
    public void mapUserToHealthcareService(String userID, String serviceID){
        synchronized(userHealthcareServiceMapLock) {
            userHealthcareServiceMap.put(userID, serviceID);
        }
    }

    public boolean isHealthcareServiceUser(String userID){
        if(userHealthcareServiceMap.containsKey(userID)){
            return(true);
        } else {
            return(false);
        }
    }

    public void removeHealthcareServiceMappingForUser(String userID){
        synchronized (userHealthcareServiceMapLock){
            if(userHealthcareServiceMap.containsKey(userID)){
                userHealthcareServiceMap.remove(userID);
            }
        }
    }

    public void removeMappingForHealthcareService(String serviceID){
        synchronized (userHealthcareServiceMapLock){
            if(userHealthcareServiceMap.containsValue(serviceID)){
                Enumeration<String> userIDSet = userHealthcareServiceMap.keys();
                while(userIDSet.hasMoreElements()){
                    String currentUserID = userIDSet.nextElement();
                    String currentServiceID = userHealthcareServiceMap.get(currentUserID);
                    if(currentServiceID.equals(serviceID)){
                        userHealthcareServiceMap.remove(currentUserID);
                        break;
                    }
                }
            }
        }
    }

    public String getHealthcareServiceIDFromUserID(String userID){
        if(userHealthcareServiceMap.contains(userID)){
            String serviceID = userHealthcareServiceMap.get(userID);
            return(serviceID);
        } else {
            return(null);
        }
    }

    //
    // Practitioner Methods
    //
    public void mapUserToPractitioner(String userID, String practitionerID){
        synchronized(userPractitionerMapLock) {
            userPractitionerRoleMap.put(userID, practitionerID);
        }
    }

    public boolean isPractitionerUser(String userID){
        if(userPractitionerMap.containsKey(userID)){
            return(true);
        } else {
            return(false);
        }
    }

    public void removeMappingPractitionerForUser(String userID){
        synchronized (userPractitionerMapLock){
            if(userPractitionerMap.containsKey(userID)){
                userPractitionerMap.remove(userID);
            }
        }
    }

    public void removeMappingForPractitioner(String practitionerID){
        synchronized (userPractitionerMapLock){
            if(userPractitionerMap.containsValue(practitionerID)){
                Enumeration<String> userIDSet = userPractitionerMap.keys();
                while(userIDSet.hasMoreElements()){
                    String currentUserID = userIDSet.nextElement();
                    String currentPractitionerID = userPractitionerMap.get(currentUserID);
                    if(currentPractitionerID.equals(practitionerID)){
                        userPractitionerMap.remove(currentUserID);
                        break;
                    }
                }
            }
        }
    }

    public String getPractitionerIDFromUserID(String userID){
        if(userPractitionerMap.contains(userID)){
            String practitionerID = userPractitionerMap.get(userID);
            return(practitionerID);
        } else {
            return(null);
        }
    }
}
