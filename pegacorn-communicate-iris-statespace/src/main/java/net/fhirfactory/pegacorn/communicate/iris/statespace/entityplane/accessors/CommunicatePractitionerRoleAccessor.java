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
import net.fhirfactory.pegacorn.communicate.iris.statespace.entityplane.matrixdomain.PractitionerRoleMatrixDomainServices;
import net.fhirfactory.pegacorn.internals.communicate.entities.practitionerrole.CommunicatePractitionerRole;
import net.fhirfactory.pegacorn.internals.communicate.entities.rooms.CommunicatePractitionerRoleCentralRoom;
import net.fhirfactory.pegacorn.internals.communicate.entities.rooms.CommunicateRoom;
import net.fhirfactory.pegacorn.internals.esr.brokers.PractitionerRoleESRBroker;
import net.fhirfactory.pegacorn.internals.esr.resources.common.ExtremelySimplifiedResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class CommunicatePractitionerRoleAccessor extends PractitionerRoleESRBroker {
    private static final Logger LOG = LoggerFactory.getLogger(CommunicatePractitionerRoleAccessor.class);

    @Override
    protected Logger getLogger(){
        return(LOG);
    }

    @Inject
    private PractitionerMatrixDomainServices practitionerMDRS;

    @Inject
    private PractitionerRoleMatrixDomainServices practitionerRoleMDRS;

    //
    // Business Functions
    //

    /**
     *
     * @param practitionerRole
     * @return
     */
    public CommunicatePractitionerRoleCentralRoom activatePractitionerRole(CommunicatePractitionerRole practitionerRole){
        CommunicateRoom room = practitionerRoleMDRS.activatePractitionerRole(practitionerRole);
        CommunicatePractitionerRoleCentralRoom practitionerRoleCentralRoom = (CommunicatePractitionerRoleCentralRoom)room;
        return(practitionerRoleCentralRoom);
    }

    /**
     *
     * @param practitionerRoleRoom
     * @return
     */
    public CommunicatePractitionerRoleCentralRoom updatedPractitionerRole(CommunicatePractitionerRoleCentralRoom practitionerRoleRoom){

        // TODO --> add logic to support synchronisation activities
        return(practitionerRoleRoom);
    }

    /**
     *
     * @param practitionerRoleCentralRoom
     * @return
     */
    public CommunicatePractitionerRoleCentralRoom suspendPractitionerRole(CommunicatePractitionerRoleCentralRoom practitionerRoleCentralRoom){

        // TODO --> add logic to support ongoing monitoring activities
        return(practitionerRoleCentralRoom);
    }

    /**
     *
     * @param practitionerRoleCentralRoom
     * @return
     */
    public CommunicatePractitionerRoleCentralRoom retirePractitionerRole(CommunicatePractitionerRoleCentralRoom practitionerRoleCentralRoom){

        // TODO --> add logic to support content archiving activities
        return(practitionerRoleCentralRoom);
    }

    //
    // Synchronisation Functions
    //

    /**
     *
     * @param practitionerRole
     * @param updateOrigin
     * @return
     */
    public CommunicatePractitionerRole synchronisePractitionerRole(CommunicatePractitionerRole practitionerRole, EntityPlaneUpdateOriginEnum updateOrigin){
        return(practitionerRole);
    }

    /**
     *
     * @param resource
     * @return
     */
    @Override
    protected ExtremelySimplifiedResource synchroniseResource(ExtremelySimplifiedResource resource){
        CommunicatePractitionerRole practitionerRole = (CommunicatePractitionerRole) resource;
        CommunicatePractitionerRole synchronisedPractitionerRole = synchronisePractitionerRole(practitionerRole, EntityPlaneUpdateOriginEnum.UPDATE_ORIGIN_WORKFLOW_DOMAIN);
        return(synchronisedPractitionerRole);
    }
}
