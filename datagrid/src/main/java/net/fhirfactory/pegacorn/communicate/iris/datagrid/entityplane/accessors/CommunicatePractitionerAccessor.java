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
package net.fhirfactory.pegacorn.communicate.iris.datagrid.entityplane.accessors;

import net.fhirfactory.pegacorn.communicate.iris.datagrid.entityplane.accessors.common.EntityPlaneUpdateOriginEnum;
import net.fhirfactory.pegacorn.communicate.iris.datagrid.entityplane.matrixdomain.PractitionerMatrixDomainServices;
import net.fhirfactory.pegacorn.internals.communicate.entities.practitioner.CommunicatePractitioner;
import net.fhirfactory.pegacorn.internals.communicate.entities.rooms.CommunicatePractitionerRoleFulfilmentRoom;
import net.fhirfactory.pegacorn.internals.communicate.entities.rooms.CommunicateRoom;
import net.fhirfactory.pegacorn.core.model.ui.brokers.PractitionerESRBroker;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.common.ExtremelySimplifiedResource;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.search.exceptions.ESRPaginationException;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.search.exceptions.ESRSortingException;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.valuesets.IdentifierESDTTypesEnum;
import net.fhirfactory.pegacorn.core.model.ui.transactions.ESRMethodOutcome;
import net.fhirfactory.pegacorn.core.model.ui.transactions.exceptions.ResourceInvalidSearchException;
import net.fhirfactory.pegacorn.core.model.ui.transactions.exceptions.ResourceInvalidSortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class CommunicatePractitionerAccessor extends PractitionerESRBroker{
    private static final Logger LOG = LoggerFactory.getLogger(CommunicatePractitionerAccessor.class);

    @Inject
    private IdentifierESDTTypesEnum identifierESDTTypesEnum;

    @Inject
    private PractitionerMatrixDomainServices matrixRoomAdminServices;

    @Inject
    private CommunicateRoomAccessor communicateRoomAccessor;

    @Override
    protected Logger getLogger() {
        return (LOG);
    }

    @Override
    protected CommunicateRoomAccessor specifyMatrixRoomESRBroker(){
        return(communicateRoomAccessor);
    }

    //
    // Practitioner Management (Business Functions)
    //

    public CommunicatePractitioner activatePractitioner(CommunicatePractitioner practitioner){

        return(practitioner);
    }

    public CommunicatePractitioner suspendPractitioner(CommunicatePractitioner practitioner){

        return(practitioner);
    }

    public CommunicatePractitioner retirePractitioner(CommunicatePractitioner practitioner){

        return(practitioner);
    }

    //
    // Practitioner Management (Synchronisation Functions)
    //

    /**
     *
     * @param practitioner
     * @param updateOrigin
     * @return
     */
    public CommunicatePractitioner synchronisePractitioner(CommunicatePractitioner practitioner, EntityPlaneUpdateOriginEnum updateOrigin){
        return(practitioner);
    }

    @Override
    protected ExtremelySimplifiedResource synchroniseResource(ExtremelySimplifiedResource resource){
        CommunicatePractitioner practitioner = (CommunicatePractitioner) resource;
        CommunicatePractitioner synchronisedPractitioner = synchronisePractitioner(practitioner, EntityPlaneUpdateOriginEnum.UPDATE_ORIGIN_WORKFLOW_DOMAIN);
        return(synchronisedPractitioner);
    }

    /**
     *
     * @param practitioner
     * @param updateOrigin
     * @return
     */
    public List<CommunicateRoom> synchronisePractitionerManagedRooms(CommunicatePractitioner practitioner, EntityPlaneUpdateOriginEnum updateOrigin) {
        LOG.debug(".synchronisePractitionerManagedRooms(): Entry, practitioner->{}, updateOrigin->{}", practitioner, updateOrigin);
        if (practitioner == null) {
            return (null);
        }
        List<CommunicateRoom> roomSet = new ArrayList<>();
        switch (updateOrigin) {
            case UPDATE_ORIGIN_WORKFLOW_DOMAIN: {
                roomSet.addAll(matrixRoomAdminServices.validatePractitionerSystemRooms(practitioner));
                // TODO Add FHIR synchronisation code here
                break;
            }
            case UPDATE_ORIGIN_MATRIX_DOMAIN: {
                roomSet.addAll(matrixRoomAdminServices.validatePractitionerSystemRooms(practitioner));
                for (CommunicateRoom currentRoom : roomSet) {
                    switch (currentRoom.getRoomType()) {
                        case COMMUNICATE_PRACTITIONER_MY_CALLS_ROOM:
                            practitioner.setMyCallsRoom(currentRoom.getRoomReference());
                            break;
                        case COMMUNICATE_PRACTITIONER_MY_MEDIA_ROOM:
                            practitioner.setMyMediaRoom(currentRoom.getRoomReference());
                            break;
                        case COMMUNICATE_PRACTITIONER_MY_GENERAL_NOTIFICATIONS_ROOM:
                            practitioner.setMyGeneralNotificationsRoom(currentRoom.getRoomReference());
                    }
                    addOrUpdateToRoomESRCache(currentRoom);
                }
            }
            case UPDATE_ORIGIN_FHIR_DOMAIN: {
                // Do Nothing
            }
        }
        if(LOG.isDebugEnabled()){
            LOG.debug(".synchronisePractitionerManagedRooms(): Exit, roomSet.size()->{}", roomSet.size());
        }
        return(roomSet);
    }

    /**
     *
     * @param practitioner
     * @param updateOrigin
     * @return
     */
    public List<CommunicateRoom> synchronisePractitionerRoleRooms(CommunicatePractitioner practitioner, EntityPlaneUpdateOriginEnum updateOrigin){
        List<CommunicateRoom> practitionerRoleList = new ArrayList<>();
        return(practitionerRoleList);
    }

    /**
     *
     * @param practitioner
     * @param fulfillmentRoom
     * @return
     */
    public CommunicatePractitionerRoleFulfilmentRoom synchronisePractitionerRoleRoom(CommunicatePractitioner practitioner, CommunicatePractitionerRoleFulfilmentRoom fulfillmentRoom){
        return(fulfillmentRoom);
    }


    /**
     * This functions takes a room and either adds it to the MatrixRoomESRCache or updates the existing entry there.
     *
     * @param room The (subclassed) CommunicateGeneralDiscussionRoom to be updated.
     */
    public void addOrUpdateToRoomESRCache(CommunicateRoom room){
        LOG.debug(".addOrUpdateToRoomESRCache(): Entry, room->{}", room);
        if(room == null){
            LOG.debug(".addOrUpdateToRoomESRCache(): Exit, room is null");
            return;
        }
        CommunicateRoom existingRoom = null;
        try {
            ESRMethodOutcome outcome = getMatrixRoomESRBroker().getResource(room.getSimplifiedID());
            if(outcome.getEntry() != null) {
                existingRoom = (CommunicateRoom) outcome.getEntry();
            }
        } catch (ResourceInvalidSearchException e) {
            LOG.error(".addOrUpdateToRoomESRCache(): Error getting/looking-up Resource in Cache, error->{}", e.getMessage());
        }
        if(existingRoom == null){
            try{
                ESRMethodOutcome outcome = getMatrixRoomESRBroker().searchForESRsUsingAttribute("canonicalAlias", room.getCanonicalAlias(),0,0,null, true);
                if(outcome.getEntry() != null) {
                    existingRoom = (CommunicateRoom) outcome.getEntry();
                }
            } catch (ESRPaginationException e) {
                LOG.error(".addOrUpdateToRoomESRCache(): Error getting/looking-up Resource in Cache (Pagination Error), error->{}", e.getMessage());
            } catch (ResourceInvalidSortException e) {
                LOG.error(".addOrUpdateToRoomESRCache(): Error getting/looking-up Resource in Cache (Sorting Attribute Error), error->{}", e.getMessage());
            } catch (ResourceInvalidSearchException e) {
                LOG.error(".addOrUpdateToRoomESRCache(): Error getting/looking-up Resource in Cache (Search Error), error->{}", e.getMessage());
            } catch (ESRSortingException e) {
                LOG.error(".addOrUpdateToRoomESRCache(): Error getting/looking-up Resource in Cache (Sorting Error), error->{}", e.getMessage());
            }
        }
        if(existingRoom == null) {
            ESRMethodOutcome outcome = getMatrixRoomESRBroker().createDirectoryEntry(room);
            existingRoom = (CommunicateRoom) outcome.getEntry();
            LOG.debug(".addOrUpdateToRoomESRCache(): Exit, created (inserted) room in MatrixRoomESRCache");
        } else {
            existingRoom.setDisplayName(room.getDisplayName());
            existingRoom.setRoomOwner(room.getRoomOwner());
            existingRoom.setCanonicalAlias(room.getCanonicalAlias());
            LOG.debug(".addOrUpdateToRoomESRCache(): Exit, Updated room in MatrixRoomESRCache");
        }
    }
}
