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

public enum MTStimulusReasonTypeEnum {
    MT_STIMULUS_REASON_EVENT("pegacorn.platform.matrix.061.stimulus.reason.general_event"),
    MT_STIMULUS_REASON_ROOM_EVENT("pegacorn.platform.matrix.061.stimulus.reason.room_event"),
    MT_STIMULUS_REASON_USER_EVENT("pegacorn.platform.matrix.061.stimulus.reason.user_event"),
    MT_STIMULUS_REASON_INSTANT_MESSAGE("pegacorn.platform.matrix.061.stimulus.reason.instant_message");

    private String reasonType;

    private MTStimulusReasonTypeEnum(String reasonType){
        this.reasonType = reasonType;
    }

    public String getReasonType(){
        return(this.reasonType);
    }
}
