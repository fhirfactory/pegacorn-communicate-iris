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

import java.util.Date;

public class MTBehaviourOutcome {
    private ExtremelySimplifiedResource outputResource;
    private Date creationDate;
    private MTStimulusIdentifier sourceStimulus;
    private MTBehaviourOutcomeIdentifier id;
    private MTBehaviourIdentifier sourceBehaviour;
    private MTIdentifier affectingTwin;
    private MTBehaviourOutcomeStatusEnum status;
    private boolean echoedToFHIR;

    public ExtremelySimplifiedResource getOutputResource() {
        return outputResource;
    }

    public void setOutputResource(ExtremelySimplifiedResource outputResource) {
        this.outputResource = outputResource;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public MTStimulusIdentifier getSourceStimulus() {
        return sourceStimulus;
    }

    public void setSourceStimulus(MTStimulusIdentifier sourceStimulus) {
        this.sourceStimulus = sourceStimulus;
    }

    public MTBehaviourOutcomeIdentifier getId() {
        return id;
    }

    public void setId(MTBehaviourOutcomeIdentifier id) {
        this.id = id;
    }

    public MTBehaviourIdentifier getSourceBehaviour() {
        return sourceBehaviour;
    }

    public void setSourceBehaviour(MTBehaviourIdentifier sourceBehaviour) {
        this.sourceBehaviour = sourceBehaviour;
    }

    public MTIdentifier getAffectingTwin() {
        return affectingTwin;
    }

    public void setAffectingTwin(MTIdentifier affectingTwin) {
        this.affectingTwin = affectingTwin;
    }

    public MTBehaviourOutcomeStatusEnum getStatus() {
        return status;
    }

    public void setStatus(MTBehaviourOutcomeStatusEnum status) {
        this.status = status;
    }

    public boolean isEchoedToFHIR() {
        return echoedToFHIR;
    }

    public void setEchoedToFHIR(boolean echoedToFHIR) {
        this.echoedToFHIR = echoedToFHIR;
    }
}