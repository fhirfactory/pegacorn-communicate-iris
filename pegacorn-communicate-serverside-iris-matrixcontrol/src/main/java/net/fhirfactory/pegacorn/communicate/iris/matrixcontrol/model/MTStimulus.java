/*
 * Copyright (c) 2020 Mark A. Hunter
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
package net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.model;

import net.fhirfactory.pegacorn.internals.esr.resources.common.ExtremelySimplifiedResource;
import net.fhirfactory.pegacorn.internals.matrix.r061.events.common.MatrixEventBase;
import net.fhirfactory.pegacorn.petasos.model.uow.UoWIdentifier;
import org.hl7.fhir.r4.model.Resource;

import java.util.Date;

public class MTStimulus {
    private MTStimulusIdentifier stimulusID;
    private MTIdentifier resourceID;
    private ExtremelySimplifiedResource resource;
    private UoWIdentifier originalUoW;
    private MatrixEventBase matrixEventTrigger;
    private Resource fhirEventTrigger;
    private Date creationDate;

    public MTStimulusIdentifier getStimulusID() {
        return stimulusID;
    }

    public void setStimulusID(MTStimulusIdentifier stimulusID) {
        this.stimulusID = stimulusID;
    }

    public MTIdentifier getResourceID() {
        return resourceID;
    }

    public void setResourceID(MTIdentifier resourceID) {
        this.resourceID = resourceID;
    }

    public ExtremelySimplifiedResource getResource() {
        return resource;
    }

    public void setResource(ExtremelySimplifiedResource resource) {
        this.resource = resource;
    }

    public MatrixEventBase getMatrixEventTrigger() {
        return matrixEventTrigger;
    }

    public void setMatrixEventTrigger(MatrixEventBase matrixEventTrigger) {
        this.matrixEventTrigger = matrixEventTrigger;
    }

    public Resource getFhirEventTrigger() {
        return fhirEventTrigger;
    }

    public void setFhirEventTrigger(Resource fhirEventTrigger) {
        this.fhirEventTrigger = fhirEventTrigger;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public UoWIdentifier getOriginalUoW() {
        return originalUoW;
    }

    public void setOriginalUoW(UoWIdentifier originalUoW) {
        this.originalUoW = originalUoW;
    }
}
