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
package net.fhirfactory.pegacorn.communicate.iris.datagrid.entityplane.fhirdomain;

import net.fhirfactory.pegacorn.communicate.iris.datagrid.common.api.CommunicatePractitionerRoleDomainInterface;
import net.fhirfactory.pegacorn.internals.communicate.entities.practitioner.CommunicatePractitioner;
import net.fhirfactory.pegacorn.internals.communicate.entities.practitionerrole.CommunicatePractitionerRole;
import net.fhirfactory.pegacorn.internals.communicate.entities.rooms.CommunicatePractitionerRoleCentralRoom;
import net.fhirfactory.pegacorn.internals.communicate.entities.rooms.CommunicatePractitionerRoleFulfilmentRoom;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PractitionerRoleFHIRDomainServices implements CommunicatePractitionerRoleDomainInterface {
    //
    // PractitionerRole Central Room Activities
    //

    /**
     *
     * @param practitionerRole
     * @return
     */
    @Override
    public CommunicatePractitionerRoleCentralRoom activatePractitionerRole(CommunicatePractitionerRole practitionerRole){
        CommunicatePractitionerRoleCentralRoom practitionerRoleCentralRoom = new CommunicatePractitionerRoleCentralRoom();
        return(practitionerRoleCentralRoom);
    }

    /**
     *
     * @param practitionerRoleRoom
     * @return
     */
    @Override
    public CommunicatePractitionerRoleCentralRoom updatedPractitionerRole(CommunicatePractitionerRoleCentralRoom practitionerRoleRoom){

        // TODO --> add logic to support synchronisation activities
        return(practitionerRoleRoom);
    }

    /**
     *
     * @param practitionerRoleCentralRoom
     * @return
     */
    @Override
    public CommunicatePractitionerRoleCentralRoom suspendPractitionerRole(CommunicatePractitionerRoleCentralRoom practitionerRoleCentralRoom){

        // TODO --> add logic to support ongoing monitoring activities
        return(practitionerRoleCentralRoom);
    }

    /**
     *
     * @param practitionerRoleCentralRoom
     * @return
     */
    @Override
    public CommunicatePractitionerRoleCentralRoom retirePractitionerRole(CommunicatePractitionerRoleCentralRoom practitionerRoleCentralRoom){

        // TODO --> add logic to support content archiving activities
        return(practitionerRoleCentralRoom);
    }

    //
    // PractitionerRole Fulfillment Room Activities
    //

    /**
     *
     * @param practitionerRole
     * @param fulfillerPractitionerRole
     * @param clientPractitionerRole
     * @return
     */
    @Override
    public CommunicatePractitionerRoleFulfilmentRoom activatePractitionerRoleFulfillerService(
            CommunicatePractitionerRole practitionerRole,
            CommunicatePractitioner fulfillerPractitionerRole,
            CommunicatePractitioner clientPractitionerRole){

        CommunicatePractitionerRoleFulfilmentRoom fulfillmentRoom = new CommunicatePractitionerRoleFulfilmentRoom();

        return(fulfillmentRoom);
    }

    /**
     *
     * @param fulfillerRoom
     * @return
     */
    @Override
    public CommunicatePractitionerRoleFulfilmentRoom activatePractitionerRoleFulfillerService(
            CommunicatePractitionerRoleFulfilmentRoom fulfillerRoom){

        return(fulfillerRoom);
    }

    public CommunicatePractitionerRoleFulfilmentRoom suspendPractitionerRoleFulfillerService(
            CommunicatePractitionerRoleFulfilmentRoom fulfillerRoom){

        CommunicatePractitionerRoleFulfilmentRoom fulfillmentRoom = new CommunicatePractitionerRoleFulfilmentRoom();

        return(fulfillmentRoom);

    }

    /**
     *
     * @param fulfillerRoom
     * @return
     */
    @Override
    public CommunicatePractitionerRoleFulfilmentRoom retirePractitionerRoleFulfillerService(
            CommunicatePractitionerRoleFulfilmentRoom fulfillerRoom){

        CommunicatePractitionerRoleFulfilmentRoom fulfillmentRoom = new CommunicatePractitionerRoleFulfilmentRoom();

        return(fulfillmentRoom);

    }

    /**
     *
     * @param fulfillerRoom
     * @return
     */
    @Override
    public CommunicatePractitionerRoleFulfilmentRoom migratePractitionerRoleFulfillerService(
            CommunicatePractitionerRoleFulfilmentRoom fulfillerRoom){

        CommunicatePractitionerRoleFulfilmentRoom fulfillmentRoom = new CommunicatePractitionerRoleFulfilmentRoom();

        return(fulfillmentRoom);

    }
}
