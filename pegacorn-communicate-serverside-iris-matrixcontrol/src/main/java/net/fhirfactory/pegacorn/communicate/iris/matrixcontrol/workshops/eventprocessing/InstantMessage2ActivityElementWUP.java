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
package net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.workshops.eventprocessing;

import net.fhirfactory.pegacorn.common.model.topicid.TopicToken;
import net.fhirfactory.pegacorn.components.interfaces.topology.WorkshopInterface;
import net.fhirfactory.pegacorn.wups.archetypes.petasosenabled.messageprocessingbased.core.MOAStandardWUP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Set;

public class InstantMessage2ActivityElementWUP extends MOAStandardWUP {
    private static final Logger LOG = LoggerFactory.getLogger(InstantMessage2ActivityElementWUP.class);
    private static String WUP_NAME = "InstantMessage2EventTriggerWUP";
    private static String WUP_VERSION = "1.0.0";

    @Inject
    EventProcessingWorkshop workshop;

    @PostConstruct


    @Override
    protected Logger getLogger() {
        return (LOG);
    }

    @Override
    protected Set<TopicToken> specifySubscriptionTopics() {
        return null;
    }

    @Override
    protected String specifyWUPInstanceName() {
        return (WUP_NAME);
    }

    @Override
    protected String specifyWUPInstanceVersion() {
        return (WUP_VERSION);
    }

    @Override
    protected WorkshopInterface specifyWorkshop() {
        return null;
    }

    @Override
    protected Logger specifyLogger() {
        return null;
    }

    @Override
    public void configure() throws Exception {

    }
}
