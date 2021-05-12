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
package net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.model.stimulus;

import net.fhirfactory.pegacorn.common.model.generalid.FDNToken;
import net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.model.MTIdentifier;
import net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.model.behaviours.MTBehaviourIdentifier;

public class MTStimulusPackage {
    private FDNToken stimulusUoWIdentifier;
    private MTIdentifier targetTwin;
    private MTBehaviourIdentifier targetBehaviour;
    private MTStimulus stimulus;


    public MTStimulusPackage(FDNToken stimulusUoW, MTIdentifier targetTwin, MTBehaviourIdentifier targetBehaviour, MTStimulus stimulus) {
        this.stimulusUoWIdentifier = stimulusUoW;
        this.targetTwin = targetTwin;
        this.targetBehaviour = targetBehaviour;
        this.stimulus = stimulus;
    }

    public MTStimulusPackage() {
        this.stimulusUoWIdentifier = null;
        this.targetTwin = null;
        this.targetBehaviour = null;
        this.stimulus = null;
    }


    public FDNToken getStimulusUoWIdentifier() {
        return stimulusUoWIdentifier;
    }

    public void setStimulusUoWIdentifier(FDNToken stimulusUoWIdentifier) {
        this.stimulusUoWIdentifier = stimulusUoWIdentifier;
    }

    public MTIdentifier getTargetTwin() {
        return targetTwin;
    }

    public void setTargetTwin(MTIdentifier targetTwin) {
        this.targetTwin = targetTwin;
    }

    public MTBehaviourIdentifier getTargetBehaviour() {
        return targetBehaviour;
    }

    public void setTargetBehaviour(MTBehaviourIdentifier targetBehaviour) {
        this.targetBehaviour = targetBehaviour;
    }

    public MTStimulus getStimulus() {
        return stimulus;
    }

    public void setStimulus(MTStimulus stimulus) {
        this.stimulus = stimulus;
    }
}