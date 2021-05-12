/*
 * Copyright (c) 2020 Mark A. Hunter (ACT Health)
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
package net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.workshops.matrixtwinbehaviours.archetypes.framework.manager;

import net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.model.behaviours.MTBehaviourTypeEnum;
import net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.model.MTTypeEnum;
import net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.workshops.matrixtwinbehaviours.archetypes.framework.worker.common.MTTimerBasedBehaviourRouteFrameworkTemplate;
import net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.workshops.matrixtwinbehaviours.archetypes.framework.worker.twintypecentrc.*;
import net.fhirfactory.pegacorn.deployment.topology.model.common.TopologyNode;
import org.apache.camel.CamelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class MTBehaviourRouteManager {
    private static final Logger LOG = LoggerFactory.getLogger(MTBehaviourRouteManager.class);

    @Inject
    CamelContext camelctx;

    public void buildBehaviourRoutes(TopologyNode behaviourNode, MTTypeEnum twinType, MTBehaviourTypeEnum behaviourArchetype) {
        LOG.debug(".buildBehaviourRoutes(): Entry, behaviourNode --> {}, subscribedTopics --> {}, behaviourArchetype --> {}", behaviourNode, behaviourArchetype);
        try {
            switch (behaviourArchetype) {
                case STIMULI_BASED_MATRIX_TWIN_BEHAVIOUR: {
                    LOG.trace(".buildBehaviourRoutes(): Building a STIMULI_BASED_BEHAVIOUR route");
                    switch (twinType) {
                        case MATRIX_TWIN_CARE_TEAM: {
                            CareTeamBehaviourFrameworkTemplateMT newRoute = new CareTeamBehaviourFrameworkTemplateMT(camelctx, behaviourNode.getNodeFDN().toTag());
                            LOG.trace(".buildBehaviourRoutes(): CareTeamBehaviourFrameworkTemplate created, now adding it to he CamelContext!");
                            camelctx.addRoutes(newRoute);
                            break;
                        }
                        case MATRIX_TWIN_GROUP: {
                            GroupCentricBehaviourFrameworkTemplateMT newRoute = new GroupCentricBehaviourFrameworkTemplateMT(camelctx, behaviourNode.getNodeFDN().toTag());
                            LOG.trace(".buildBehaviourRoutes(): GroupCentricBehaviourFrameworkTemplate created, now adding it to he CamelContext!");
                            camelctx.addRoutes(newRoute);
                            break;
                        }
                        case MATRIX_TWIN_HEALTHCARE_SERVICE: {
                            HealthcareServiceCentricBehaviourFrameworkTemplateMT newRoute = new HealthcareServiceCentricBehaviourFrameworkTemplateMT(camelctx, behaviourNode.getNodeFDN().toTag());
                            LOG.trace(".buildBehaviourRoutes(): HealthcareServiceCentricBehaviourFrameworkTemplate created, now adding it to he CamelContext!");
                            camelctx.addRoutes(newRoute);
                            break;
                        }
                        case MATRIX_TWIN_PRACTITIONER: {
                            PractitionerCentricBehaviourFrameworkTemplateMT newRoute = new PractitionerCentricBehaviourFrameworkTemplateMT(camelctx, behaviourNode.getNodeFDN().toTag());
                            LOG.trace(".buildBehaviourRoutes(): PractitionerCentricBehaviourFrameworkTemplate created, now adding it to he CamelContext!");
                            camelctx.addRoutes(newRoute);
                            break;
                        }
                        case MATRIX_TWIN_PRACTITIONER_ROLE: {
                            PractitionerRoleCentricBehaviourFrameworkTemplateMT newRoute = new PractitionerRoleCentricBehaviourFrameworkTemplateMT(camelctx, behaviourNode.getNodeFDN().toTag());
                            LOG.trace(".buildBehaviourRoutes(): PractitionerRoleCentricBehaviourFrameworkTemplate created, now adding it to he CamelContext!");
                            camelctx.addRoutes(newRoute);
                            break;
                        }
                    }
                    LOG.trace(".buildBehaviourRoutes(): Subscribed to Topics, work is done!");
                    break;
                }
                case TIMER_BASED_MATRIX_TWIN_BEHAVIOUR: {
                    LOG.trace(".buildBehaviourRoutes(): Building a TIMER_BASED_BEHAVIOUR route");
                    MTTimerBasedBehaviourRouteFrameworkTemplate timerRoute = new MTTimerBasedBehaviourRouteFrameworkTemplate(camelctx, behaviourNode.getNodeFDN().toTag());
                    camelctx.addRoutes(timerRoute);
                    LOG.trace(".buildBehaviourRoutes(): Note, this type of WUP/Route does not subscribe to Topics (it is purely a producer)");
                    break;
                }
            }
        } catch(Exception Ex){
            LOG.error(".buildBehaviourRoutes(): Route install failed! Exception --> {}", Ex);
            // TODO Need to handle this better - potentially failing entire Processing Plant
        }
    }
}
