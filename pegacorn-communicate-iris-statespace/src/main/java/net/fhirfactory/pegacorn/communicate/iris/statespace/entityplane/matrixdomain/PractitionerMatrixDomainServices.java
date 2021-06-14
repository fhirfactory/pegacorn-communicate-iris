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
package net.fhirfactory.pegacorn.communicate.iris.statespace.entityplane.matrixdomain;

import net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.workshops.actions.complexactions.CommunicatePractitionerGeneralNotificationsRoomActions;
import net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.workshops.actions.complexactions.CommunicatePractitionerMyCallsRoomActions;
import net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.workshops.actions.complexactions.CommunicatePractitionerMyMediaRoomActions;
import net.fhirfactory.pegacorn.communicate.iris.statespace.common.api.CommunicatePractitionerDomainInterface;
import net.fhirfactory.pegacorn.internals.communicate.entities.practitioner.CommunicatePractitioner;
import net.fhirfactory.pegacorn.internals.communicate.entities.rooms.CommunicatePractitionerMyCallsRoom;
import net.fhirfactory.pegacorn.internals.communicate.entities.rooms.CommunicatePractitionerMyMediaRoom;
import net.fhirfactory.pegacorn.internals.communicate.entities.rooms.CommunicateRoom;
import net.fhirfactory.pegacorn.internals.esr.resources.valuesets.IdentifierESDTTypesEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class PractitionerMatrixDomainServices implements CommunicatePractitionerDomainInterface {
    private static final Logger LOG = LoggerFactory.getLogger(PractitionerMatrixDomainServices.class);

    @Inject
    private CommunicatePractitionerMyMediaRoomActions myMediaRoomActions;

    @Inject
    private CommunicatePractitionerMyCallsRoomActions myCallsRoomActions;

    @Inject
    private CommunicatePractitionerGeneralNotificationsRoomActions myGeneralNotificationsRoomActions;

    @Inject
    private IdentifierESDTTypesEnum identifierESDTTypesEnum;

    //
    // Practitioner Management
    //

    @Override
    public CommunicatePractitioner activatePractitioner(CommunicatePractitioner practitioner){

        return(practitioner);
    }

    @Override
    public CommunicatePractitioner suspendPractitioner(CommunicatePractitioner practitioner){

        return(practitioner);
    }

    @Override
    public CommunicatePractitioner retirePractitioner(CommunicatePractitioner practitioner){

        return(practitioner);
    }

    //
    // Practitioner Centric Rooms
    //

    @Override
    public List<CommunicateRoom> validatePractitionerSystemRooms(CommunicatePractitioner practitioner){
        List<CommunicateRoom> roomSet = new ArrayList<>();
        if(practitioner == null){
            return(roomSet);
        }
        CommunicateRoom callsRoom = synchronisePractitionerMyCallsRoom(practitioner);
        CommunicateRoom mediaRoom = synchronisePractitionerMyMediaRoom(practitioner);
        CommunicateRoom notificationsRoom = synchronisePractitionerGeneralNotificationRoom(practitioner);
        roomSet.add(callsRoom);
        roomSet.add(mediaRoom);
        roomSet.add(notificationsRoom);
        return(roomSet);
    }

    @Override
    public CommunicatePractitionerMyCallsRoom synchronisePractitionerMyCallsRoom(CommunicatePractitioner practitioner){
        CommunicatePractitionerMyCallsRoom myCallsMatrixRoom = null;
        if(!myCallsRoomActions.validateRoomExistence(practitioner)){
            myCallsMatrixRoom = (CommunicatePractitionerMyCallsRoom)myCallsRoomActions.createRoomInMatrixDomain(practitioner);
        } else {
            myCallsMatrixRoom = (CommunicatePractitionerMyCallsRoom)myCallsRoomActions.getRoom(practitioner);
        }
        return(myCallsMatrixRoom);
    }

    @Override
    public CommunicatePractitionerMyMediaRoom synchronisePractitionerMyMediaRoom(CommunicatePractitioner practitioner){
        CommunicatePractitionerMyMediaRoom myCallsMatrixRoom = null;
        if(!myMediaRoomActions.validateRoomExistence(practitioner)){
            myCallsMatrixRoom = (CommunicatePractitionerMyMediaRoom)myMediaRoomActions.createRoomInMatrixDomain(practitioner);
        } else {
            myCallsMatrixRoom = (CommunicatePractitionerMyMediaRoom)myMediaRoomActions.getRoom(practitioner);
        }
        return(myCallsMatrixRoom);
    }

    @Override
    public CommunicateRoom synchronisePractitionerGeneralNotificationRoom(CommunicatePractitioner practitioner){
        CommunicateRoom myCallsMatrixRoom = null;
        if(!myGeneralNotificationsRoomActions.validateRoomExistence(practitioner)){
            myCallsMatrixRoom = myGeneralNotificationsRoomActions.createRoomInMatrixDomain(practitioner);
        } else {
            myCallsMatrixRoom = myGeneralNotificationsRoomActions.getRoom(practitioner);
        }
        return(myCallsMatrixRoom);
    }
}
