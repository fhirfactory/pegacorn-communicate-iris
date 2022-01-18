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
package net.fhirfactory.pegacorn.communicate.iris.datagrid.common.api;

import net.fhirfactory.pegacorn.internals.communicate.entities.practitioner.CommunicatePractitioner;
import net.fhirfactory.pegacorn.internals.communicate.entities.practitionerrole.CommunicatePractitionerRole;
import net.fhirfactory.pegacorn.internals.communicate.entities.rooms.CommunicatePractitionerRoleCentralRoom;
import net.fhirfactory.pegacorn.internals.communicate.entities.rooms.CommunicatePractitionerRoleFulfilmentRoom;

public interface CommunicatePractitionerRoleDomainInterface {

    /**
     *
     * @param practitionerRole
     * @return
     */
    public CommunicatePractitionerRoleCentralRoom activatePractitionerRole(CommunicatePractitionerRole practitionerRole);

    /**
     *
     * @param practitionerRoleRoom
     * @return
     */
    public CommunicatePractitionerRoleCentralRoom updatedPractitionerRole(CommunicatePractitionerRoleCentralRoom practitionerRoleRoom);

    /**
     *
     * @param practitionerRoleCentralRoom
     * @return
     */
    public CommunicatePractitionerRoleCentralRoom suspendPractitionerRole(CommunicatePractitionerRoleCentralRoom practitionerRoleCentralRoom);

    /**
     *
     * @param practitionerRoleCentralRoom
     * @return
     */
    public CommunicatePractitionerRoleCentralRoom retirePractitionerRole(CommunicatePractitionerRoleCentralRoom practitionerRoleCentralRoom);

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
    public CommunicatePractitionerRoleFulfilmentRoom activatePractitionerRoleFulfillerService(
            CommunicatePractitionerRole practitionerRole,
            CommunicatePractitioner fulfillerPractitionerRole,
            CommunicatePractitioner clientPractitionerRole);

    /**
     *
     * @param fulfillerRoom
     * @return
     */
    public CommunicatePractitionerRoleFulfilmentRoom activatePractitionerRoleFulfillerService(CommunicatePractitionerRoleFulfilmentRoom fulfillerRoom);

    /**
     *
     * @param fulfillerRoom
     * @return
     */
    public CommunicatePractitionerRoleFulfilmentRoom suspendPractitionerRoleFulfillerService(CommunicatePractitionerRoleFulfilmentRoom fulfillerRoom);

    /**
     *
     * @param fulfillerRoom
     * @return
     */
    public CommunicatePractitionerRoleFulfilmentRoom retirePractitionerRoleFulfillerService(CommunicatePractitionerRoleFulfilmentRoom fulfillerRoom);

    /**
     *
     * @param fulfillerRoom
     * @return
     */
    public CommunicatePractitionerRoleFulfilmentRoom migratePractitionerRoleFulfillerService(CommunicatePractitionerRoleFulfilmentRoom fulfillerRoom);
}
