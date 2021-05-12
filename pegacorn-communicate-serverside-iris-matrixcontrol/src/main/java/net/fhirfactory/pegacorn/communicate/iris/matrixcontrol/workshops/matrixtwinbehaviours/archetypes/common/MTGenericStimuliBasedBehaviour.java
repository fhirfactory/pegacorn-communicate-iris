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
package net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.workshops.matrixtwinbehaviours.archetypes.common;

import net.fhirfactory.pegacorn.common.model.topicid.DataParcelToken;
import net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.model.behaviours.MTBehaviourCentricExclusionFilterRulesInterface;
import net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.model.behaviours.MTBehaviourCentricInclusionFilterRulesInterface;
import net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.model.behaviours.MTBehaviourIdentifier;
import net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.model.behaviours.MTBehaviourTypeEnum;
import net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.workshops.matrixtwinbehaviours.archetypes.framework.manager.MTBehaviourRouteManager;
import net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.workshops.matrixtwinstatespace.twinpathway.encapsulatorroutes.common.MTTypeBaseBehaviourEncapsulatorRouteWUP;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class MTGenericStimuliBasedBehaviour extends MTGenericBehaviour {

    @Inject
    private MTBehaviourRouteManager behaviourRouteMgr;

    abstract protected List<MTBehaviourCentricExclusionFilterRulesInterface> exclusionFilterSet();
    abstract protected List<MTBehaviourCentricInclusionFilterRulesInterface> inclusionFilterSet();

    abstract protected MTTypeBaseBehaviourEncapsulatorRouteWUP getEncapsulatingWUP();

    @Override
    protected MTBehaviourTypeEnum specifyBehaviourType(){
        return(MTBehaviourTypeEnum.STIMULI_BASED_MATRIX_TWIN_BEHAVIOUR);
    }

    /**
     * Within the context of a Behaviour, it extracts the Type of Resource (set) that this Behaviour is expecting for
     * Stimulus and registers these with the (corresponding) StimulusCollector entity.
     *
     * It then extracts the relationship map BehaviourStimulusRequirementSet and injects this into the "upstream"
     * (corresponding) DigitalTwinOrchestrator instance. This function orders and limits the DigitalTwin instances being
     * fed into this behaviour.
     *
     * Because the input (StimulusPackages) are injected by the TwinManifestor directly, we don't actually want to
     * have this WUP (which is the super-class of this one) subscribing to any topics. So we return an empty set.
     *
     * @return An empty TopicToken set.
     */
    protected Set<DataParcelToken> specifySubscriptionTopics() {
        getLogger().debug(".specifySubscriptionTopics(): Entry");
        //
        // 1st, lets do the (macro) Topic (Stimulus) registration process
        //
        getLogger().trace(".specifySubscriptionTopics(): First, get the DigitalTwinStimulusSubscriptionCriteriaInterface list");
        ArrayList<DataParcelToken> subscribedTopics = new ArrayList<>();
        for(MTBehaviourCentricInclusionFilterRulesInterface positiveFilterRulesInterface: inclusionFilterSet()){
            for(DataParcelToken currentToken: positiveFilterRulesInterface.positiveStaticFilterStimulus()){
                getLogger().trace(".specifySubscriptionTopics(): Topic of interest --> {}", currentToken);
                getLogger().trace(".specifySubscriptionTopics(): Now, append the right discriminator so as to get the Topics ONLY from the PubSub service");
                currentToken.addDiscriminator("Source", "Ladon.StateSpace.PubSub");
                getLogger().trace(".specifySubscriptionTopics(): Call the addTopicToSubscription method on the corresponding CollectorService");
                subscribedTopics.add(currentToken);
                getLogger().trace(".specifySubscriptionTopics(): Topic added... continue");
            }
        }
        getMyTwinOrchestrationService().requestSubscription(subscribedTopics);
        //
        // Next: lets inject the BehaviourStimulusSubscription into the appropriate DigitalTwinOrchestrator instance
        //
        for(MTBehaviourCentricInclusionFilterRulesInterface criteriaInterface: inclusionFilterSet()){
            getLogger().trace(".specifySubscriptionTopics(): Get the BehaviourStimulusRequirementSet for each interface instance");
            MTBehaviourIdentifier behaviourId = new MTBehaviourIdentifier(specifyBehaviourName(), specifyBehaviourVersion());
            getMyTwinOrchestrationService().registerBehaviourCentricInclusiveFilterRules(behaviourId, criteriaInterface);
        }
        //
        // Lastly: return an empty set.
        //
        HashSet<DataParcelToken> myTopics = new HashSet<DataParcelToken>();
        getLogger().debug(".specifySubscriptionTopics(): Exit");
        return(myTopics);
    }


}
