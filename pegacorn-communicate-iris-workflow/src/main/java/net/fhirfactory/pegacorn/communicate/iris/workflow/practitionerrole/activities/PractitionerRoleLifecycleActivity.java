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
package net.fhirfactory.pegacorn.communicate.iris.workflow.practitionerrole.activities;

import net.fhirfactory.pegacorn.communicate.iris.workflow.common.EntityActivityBase;
import net.fhirfactory.pegacorn.internals.communicate.activity.CommunicateActivityOutcome;
import net.fhirfactory.pegacorn.internals.communicate.entities.practitionerrole.CommunicatePractitionerRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PractitionerRoleLifecycleActivity  extends EntityActivityBase {
    private static final Logger LOG = LoggerFactory.getLogger(PractitionerRoleLifecycleActivity.class);

    @Override
    protected Logger specifyLogger() {
        return (LOG);
    }

    public CommunicateActivityOutcome practitionerRoleCreate(CommunicatePractitionerRole practitionerRole){
        getLogger().debug(".practitionerRoleCreate(): Entry");
        CommunicateActivityOutcome outcome = new CommunicateActivityOutcome();

        getLogger().debug(".practitionerRoleCreate(): Entry, outcome->{}", outcome);
        return(outcome);
    }

    public CommunicateActivityOutcome practitionerRoleUpdate(CommunicatePractitionerRole practitionerRole){
        getLogger().debug(".practitionerRoleUpdate(): Entry");
        CommunicateActivityOutcome outcome = new CommunicateActivityOutcome();

        getLogger().debug(".practitionerRoleUpdate(): Entry, outcome->{}", outcome);
        return(outcome);
    }

    public CommunicateActivityOutcome practitionerRoleDelete(CommunicatePractitionerRole practitionerRole){
        getLogger().debug(".practitionerRoleDelete(): Entry");
        CommunicateActivityOutcome outcome = new CommunicateActivityOutcome();

        getLogger().debug(".practitionerRoleDelete(): Entry, outcome->{}", outcome);
        return(outcome);
    }

}
