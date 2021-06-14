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
package net.fhirfactory.pegacorn.communicate.iris.statespace.common.api;

import net.fhirfactory.pegacorn.internals.communicate.entities.careteam.CommunicateCareTeam;
import net.fhirfactory.pegacorn.internals.communicate.entities.practitioner.CommunicatePractitioner;
import net.fhirfactory.pegacorn.internals.communicate.entities.rooms.CommunicateRoom;

public interface CommunicateCareTeamDomainInterface {
    /**
     *
     * @param careTeam
     * @return
     */
    public CommunicateRoom activateCareTeam(CommunicateCareTeam careTeam);

    /**
     *
     * @param careTeam
     * @return
     */
    public CommunicateRoom updatedCareTeam(CommunicateCareTeam careTeam);

    /**
     *
     * @param careTeam
     * @return
     */
    public CommunicateRoom suspendCareTeam(CommunicateCareTeam careTeam);

    /**
     *
     * @param careTeam
     * @return
     */
    public CommunicateRoom retireCareTeam(CommunicateCareTeam careTeam);

    /**
     *
     * @param practitionerRole
     * @param fulfillerPractitionerRole
     * @param clientPractitionerRole
     * @return
     */
    public CommunicateRoom activateCareTeamFulfillerService(
            CommunicateCareTeam practitionerRole,
            CommunicatePractitioner fulfillerPractitionerRole,
            CommunicatePractitioner clientPractitionerRole);

    /**
     *
     * @param fulfillerRoom
     * @return
     */
    public CommunicateRoom activateCareTeamFulfillerService(CommunicateRoom fulfillerRoom);

    /**
     *
     * @param fulfillerRoom
     * @return
     */
    public CommunicateRoom suspendCareTeamFulfillerService(CommunicateRoom fulfillerRoom);

    /**
     *
     * @param fulfillerRoom
     * @return
     */
    public CommunicateRoom retireCareTeamFulfillerService(CommunicateRoom fulfillerRoom);

    /**
     *
     * @param fulfillerRoom
     * @return
     */
    public CommunicateRoom migrateCareTeamFulfillerService(CommunicateRoom fulfillerRoom);
}
