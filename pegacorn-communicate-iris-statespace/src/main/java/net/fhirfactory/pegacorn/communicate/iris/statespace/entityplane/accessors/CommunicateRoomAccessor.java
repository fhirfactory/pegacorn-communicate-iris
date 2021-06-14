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
package net.fhirfactory.pegacorn.communicate.iris.statespace.entityplane.accessors;

import net.fhirfactory.pegacorn.communicate.iris.statespace.entityplane.accessors.common.EntityPlaneUpdateOriginEnum;
import net.fhirfactory.pegacorn.communicate.iris.statespace.entityplane.matrixdomain.PractitionerMatrixDomainServices;
import net.fhirfactory.pegacorn.internals.communicate.entities.practitioner.CommunicatePractitioner;
import net.fhirfactory.pegacorn.internals.communicate.entities.rooms.CommunicateRoom;
import net.fhirfactory.pegacorn.internals.esr.brokers.GroupESRBroker;
import net.fhirfactory.pegacorn.internals.esr.brokers.MatrixRoomESRBroker;
import net.fhirfactory.pegacorn.internals.esr.resources.common.ExtremelySimplifiedResource;
import net.fhirfactory.pegacorn.internals.esr.transactions.ESRMethodOutcome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class CommunicateRoomAccessor extends MatrixRoomESRBroker{
    private static final Logger LOG = LoggerFactory.getLogger(CommunicateRoomAccessor.class);

    @Inject
    private GroupESRBroker groupEntityConfigCache;

    @Inject
    private PractitionerMatrixDomainServices matrixDomainPractitionerCentricServices;

    @Override
    protected Logger getLogger(){
        return(LOG);
    }

    /**
     *
     * @param roomID
     * @return
     */
    public boolean roomExistsInEntityPlane(String roomID){
        boolean roomExistsInConfigCached = hasEntry(roomID);
        return(true);
    }

    /**
     *
     * @param discussion
     * @param updateOrigin
     * @return
     */
    public CommunicateRoom synchroniseRoom(CommunicateRoom discussion, EntityPlaneUpdateOriginEnum updateOrigin){
        switch(updateOrigin){
            case UPDATE_ORIGIN_FHIR_DOMAIN:
                // do nothing
                break;
            case UPDATE_ORIGIN_MATRIX_DOMAIN:
                // do something
                break;
            case UPDATE_ORIGIN_WORKFLOW_DOMAIN:
                // do something
                break;
        }
        return(discussion);
    }

    @Override
    protected ExtremelySimplifiedResource synchroniseResource(ExtremelySimplifiedResource resource){
        CommunicateRoom room = (CommunicateRoom) resource;
        CommunicateRoom synchronisedRoom = synchroniseRoom(room, EntityPlaneUpdateOriginEnum.UPDATE_ORIGIN_WORKFLOW_DOMAIN);
        return(synchronisedRoom);
    }

    /**
     *
     * @param discussion
     * @return
     */
    public CommunicateRoom addRoomToRoomConfigCache(CommunicateRoom discussion){
        ESRMethodOutcome outcome = createMatrixRoom(discussion);
        CommunicateRoom outcomeDiscussionEntity = (CommunicateRoom) outcome.getEntry();
        return(outcomeDiscussionEntity);
    }

    /**
     *
     * @param discussion
     * @return
     */
    public CommunicateRoom addGroupToFHIRDomain(CommunicateRoom discussion){
        return(discussion);
    }

    /**
     *
     * @param discussion
     * @return
     */
    public CommunicateRoom addRoomToMatrixDomain(CommunicateRoom discussion){
        switch(discussion.getRoomType()){
            case COMMUNICATE_GENERAL_DISCUSSION_ROOM:
        }
    }

    /**
     *
     * @param practitioner
     */
    public void synchronisePractitionerSystemRoomSetContent(CommunicatePractitioner practitioner){


    }
}
