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
package net.fhirfactory.pegacorn.communicate.iris.workflow.practitioner.activities;

import net.fhirfactory.pegacorn.communicate.iris.workflow.common.EntityActivityBase;
import org.hl7.fhir.r4.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PractitionerLifecycleActivity extends EntityActivityBase {
    private static final Logger LOG = LoggerFactory.getLogger(PractitionerLifecycleActivity.class);

    @Override
    protected Logger specifyLogger() {
        return (LOG);
    }

    public Task practitionerCreate(Task practitionerAddTask){
        getLogger().debug(".practitionerCreate(): Entry");

        return(practitionerAddTask);
    }

    public Task practitionerUpdate(Task practitionerAddTask){
        getLogger().debug(".practitionerUpdate(): Entry");

        return(practitionerAddTask);
    }

    public Task practitionerDelete(Task practitionerAddTask){
        getLogger().debug(".practitionerDelete(): Entry");

        return(practitionerAddTask);
    }
}
