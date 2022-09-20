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

package net.fhirfactory.pegacorn.communicate.iris.datagrid.twinpathway.orchestrator.common.caches;

import net.fhirfactory.pegacorn.internals.communicate.workflow.model.stimulus.CDTStimulus;
import net.fhirfactory.pegacorn.internals.communicate.workflow.model.stimulus.CDTStimulusIdentifier;
import net.fhirfactory.dricats.model.petasos.uow.UoWIdentifier;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CDTStimulusCache {
    private ConcurrentHashMap<CDTStimulusIdentifier, CDTStimulus> stimulusPool;
    private ConcurrentHashMap<UoWIdentifier, Set<CDTStimulusIdentifier>> uow2StimulusMap;

    public CDTStimulusCache(){
        this.stimulusPool = new ConcurrentHashMap<>();
        this.uow2StimulusMap = new ConcurrentHashMap<>();
    }

    public void addStimulus(CDTStimulus newStimulus){
        if(newStimulus == null){
            return;
        }
        stimulusPool.put(newStimulus.getStimulusID(), newStimulus);
        if(newStimulus.getOriginalUoW() != null){
            addStimulusAssociation2UoW(newStimulus.getStimulusID(), newStimulus.getOriginalUoW());
        }
    }

    public void removeStimulus(CDTStimulusIdentifier stimulusToRemove){
        if(stimulusToRemove == null){
            return;
        }
        if(stimulusPool.containsKey(stimulusToRemove)){
            CDTStimulus workingStimulus = stimulusPool.get(stimulusToRemove);
            removeStimulusAssociation2UoW(stimulusToRemove,workingStimulus.getOriginalUoW());
            stimulusPool.remove(stimulusToRemove);
        }
    }

    public CDTStimulus getStimulus(CDTStimulusIdentifier stimulusId){
        if(stimulusPool.containsKey(stimulusId)){
            return(stimulusPool.get(stimulusId));
        }
        return(null);
    }

    public void addStimulusAssociation2UoW(CDTStimulusIdentifier stimulusId, UoWIdentifier uowId){
        if(stimulusId == null || uowId == null){
            return;
        }
        if(!uow2StimulusMap.containsKey(uowId)){
            HashSet<CDTStimulusIdentifier> idSet = new HashSet<CDTStimulusIdentifier>();
            uow2StimulusMap.put(uowId, idSet);
        }
        uow2StimulusMap.get(uowId).add(stimulusId);
    }

    public Set<CDTStimulusIdentifier> getStimulusAssociatedWithUoW(UoWIdentifier uowId){
        if(uowId == null){
            return(new HashSet<CDTStimulusIdentifier>());
        }
        if(!uow2StimulusMap.containsKey(uowId)){
            return(new HashSet<CDTStimulusIdentifier>());
        }
        return(uow2StimulusMap.get(uowId));
    }

    public void removeStimulusAssociation2UoW(CDTStimulusIdentifier stimulusId, UoWIdentifier uowId){
        if(stimulusId == null || uowId == null){
            return;
        }
        if(!uow2StimulusMap.containsKey(uowId)){
            return;
        }
        Set<CDTStimulusIdentifier> uowStimulus = uow2StimulusMap.get(uowId);
        if(uowStimulus.contains(stimulusId)){
            uowStimulus.remove(stimulusId);
        }
        if(uowStimulus.isEmpty()){
            uow2StimulusMap.remove(uowId);
        }
    }

}
