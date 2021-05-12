/*
 * Copyright (c) 2021 Mark A. Hunter (ACT Health)
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
package net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.workshops.cache.im;

import net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.workshops.cache.room.RoomMapCache;
import net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.workshops.cache.user.UserMapCache;
import net.fhirfactory.pegacorn.internals.esr.brokers.*;
import net.fhirfactory.pegacorn.internals.esr.resources.datatypes.ReferenceESDT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Dependent
public class IDMapIM {
    private static final Logger LOG = LoggerFactory.getLogger(IDMapIM.class);

    @Inject
    private RoomMapCache roomMapCache;

    @Inject
    private UserMapCache userMapCache;

    @Inject
    private CareTeamESRBroker careTeamBroker;

    @Inject
    private GroupESRBroker groupBroker;

    @Inject
    private HealthcareServiceESRBroker healthcareServiceESRBroker;

    @Inject
    private MatrixRoomESRBroker matrixRoomBroker;

    @Inject
    private PractitionerESRBroker practitionerBroker;

    @Inject
    private PractitionerRoleESRBroker practitionerRoleBroker;

    public List<ReferenceESDT> getResourcesAssociatedWithRoomID(String roomID){
        LOG.debug(".getResourcesAssociatedWithRoomID(): Entry, roomID->{}", roomID);
        List<ReferenceESDT> references = new ArrayList<>();
        if(roomID == null){
            return(references);
        }
        // 1st Check Practitioner based Rooms
    }

}
