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
package net.fhirfactory.pegacorn.communicate.iris.statespace.twinpathway.orchestrator.common.caches;

import net.fhirfactory.pegacorn.internals.communicate.workflow.model.behaviours.CDTBehaviourIdentifier;
import net.fhirfactory.pegacorn.internals.communicate.workflow.model.behaviours.CDTBehaviourOutcome;
import net.fhirfactory.pegacorn.internals.communicate.workflow.model.behaviours.CDTBehaviourOutcomeIdentifier;
import net.fhirfactory.pegacorn.internals.communicate.workflow.model.behaviours.CDTBehaviourOutcomeSet;
import net.fhirfactory.pegacorn.internals.communicate.workflow.model.stimulus.CDTStimulusIdentifier;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CDTOutcomeCache {
    private ConcurrentHashMap<CDTBehaviourOutcomeIdentifier, CDTBehaviourOutcome> outcomePool;
    private ConcurrentHashMap<CDTBehaviourIdentifier, Set<CDTBehaviourOutcomeIdentifier>> behaviour2OutcomeMap;

    public CDTOutcomeCache(){
        this.outcomePool = new ConcurrentHashMap<>();
        this.behaviour2OutcomeMap = new ConcurrentHashMap<>();
    }

    public void addOutcome(CDTBehaviourOutcome newOutcome){
        if(newOutcome == null){
            return;
        }
        if(newOutcome.getId() == null){
            return;
        }
        this.outcomePool.put(newOutcome.getId(), newOutcome);
        if(newOutcome.getSourceBehaviour() != null){
            addOutcomeAssociation2Behaviour(newOutcome.getId(), newOutcome.getSourceBehaviour());
        }
    }

    public void removeOutcome(CDTBehaviourOutcomeIdentifier outcomeToRemove){
        if(outcomeToRemove == null){
            return;
        }
        if(!outcomePool.containsKey(outcomeToRemove)){
            return;
        }
        CDTBehaviourOutcome workingOutcome = outcomePool.get(outcomeToRemove);
        removeOutcomeAssociation2Behaviour(workingOutcome.getId(), workingOutcome.getSourceBehaviour());
        outcomePool.remove(outcomeToRemove);
    }

    public CDTBehaviourOutcome getOutcome(CDTBehaviourOutcomeIdentifier outcomeId){
        if(outcomeId == null){
            return(null);
        }
        if(!outcomePool.containsKey(outcomeId)){
            return(null);
        }
        return(outcomePool.get(outcomeId));
    }

    public void addOutcomeSet(CDTBehaviourOutcomeSet outcomeSet){
        if(outcomeSet == null){
            return;
        }
        for(CDTBehaviourOutcome outcome: outcomeSet.getOutcomes()){
            if(outcome.getSourceBehaviour() == null){
                outcome.setSourceBehaviour(outcomeSet.getSourceBehaviour());
            }
            if(outcome.getAffectingTwin() == null){
                outcome.setAffectingTwin(outcomeSet.getSourceTwin());
            }
            addOutcome(outcome);
        }
    }

    public void addOutcomeAssociation2Behaviour(CDTBehaviourOutcomeIdentifier outcomeId, CDTBehaviourIdentifier behaviourId){
        if(outcomeId == null || behaviourId == null){
            return;
        }
        if(!behaviour2OutcomeMap.containsKey(behaviourId)){
            HashSet<CDTBehaviourOutcomeIdentifier> outcomeSet = new HashSet<>();
            behaviour2OutcomeMap.put(behaviourId, outcomeSet);
        }
        behaviour2OutcomeMap.get(behaviourId).add(outcomeId);
    }

    public void removeOutcomeAssociation2Behaviour(CDTBehaviourOutcomeIdentifier outcomeId, CDTBehaviourIdentifier behaviourId){
        if(outcomeId == null || behaviourId == null){
            return;
        }
        if(!behaviour2OutcomeMap.containsKey(behaviourId)){
            return;
        }
        Set<CDTBehaviourOutcomeIdentifier> outcomeSet = behaviour2OutcomeMap.get(behaviourId);
        if(!outcomeSet.contains(outcomeId)){
            outcomeSet.remove(outcomeId);
            if(outcomeSet.isEmpty()){
                behaviour2OutcomeMap.remove(behaviourId);
            }
        }
    }

    public void removeOutcomesDerivedFromStimulus(CDTStimulusIdentifier stimulusId){
        if(stimulusId == null){
            return;
        }
        Collection<CDTBehaviourOutcome> outcomes = outcomePool.values();
        for(CDTBehaviourOutcome outcome: outcomes) {
            if (outcome.getSourceStimulus() == stimulusId) {
                removeOutcome(outcome.getId());
            }
        }
    }

    public Set<CDTBehaviourOutcomeIdentifier> getBehaviourBasedOutcomes(CDTBehaviourIdentifier behaviourId){
        if(behaviourId == null){
            return(new HashSet<>());
        }
        if(!behaviour2OutcomeMap.containsKey(behaviourId)){
            return(new HashSet<>());
        }
        return(behaviour2OutcomeMap.get(behaviourId));
    }

    public Set<CDTBehaviourOutcome> getStimulusDerivedOutcomes(CDTStimulusIdentifier stimulusId){
        if(stimulusId == null){
            return(new HashSet<>());
        }
        HashSet<CDTBehaviourOutcome> derivedOutcomes = new HashSet<>();
        Collection<CDTBehaviourOutcome> outcomes = outcomePool.values();
        for(CDTBehaviourOutcome outcome: outcomes){
            if(outcome.getSourceStimulus() == stimulusId){
                derivedOutcomes.add(outcome);
            }
        }
        return(derivedOutcomes);
    }
}
