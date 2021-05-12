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
package net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.workshops.matrixtwin.common;

import net.fhirfactory.pegacorn.internals.esr.resources.MatrixRoomESR;
import net.fhirfactory.pegacorn.internals.esr.resources.common.ExtremelySimplifiedResource;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public abstract class ResourceMatrixTwin {

    private ConcurrentHashMap<String, MatrixRoomESR> roomSet;
    private Object roomSetLock;
    private ExtremelySimplifiedResource simplifiedResource;

    public ResourceMatrixTwin(){
        this.roomSet = new ConcurrentHashMap<>();
        this.roomSetLock = new Object();
    }

    protected ExtremelySimplifiedResource getSimplifiedResource(){
        return(this.simplifiedResource);
    }

    protected void setSimplifiedResource(ExtremelySimplifiedResource resource){
        this.simplifiedResource = resource;
    }

    abstract protected Logger specifyLogger();
    protected Logger getLogger(){
        return(specifyLogger());
    }

    public boolean hasRoom(String roomID){
        getLogger().debug(".hasRoom(): Entry, roomID->{}", roomID);
        if(roomID == null){
            getLogger().debug(".hasRoom(): Exit, roomID is null, returning FALSE");
            return(false);
        }
        if(roomSet.containsKey(roomID)){
            getLogger().debug(".hasRoom(): Exit, roomSet contains room, returning TRUE");
            return(true);
        }
        getLogger().debug(".hasRoom(): Exit, roomSet does not contain room, returning FALSE");
        return(false);
    }

    public List<MatrixRoomESR> getRoomSet(){
        getLogger().debug(".getRoomSet(): Entry");
        List<MatrixRoomESR> rooms = new ArrayList<>();
        if(roomSet.isEmpty()){
            getLogger().debug(".getRoomSet(): roomSet is empty, returning empty List");
            return(rooms);
        }
        synchronized (roomSetLock){
            Collection<MatrixRoomESR> roomCollection = roomSet.values();
            rooms.addAll(roomCollection);
        }
        getLogger().debug(".getRoomSet(): Exit, returning non-empty List");
        return(rooms);
    }

    public void addRoom(MatrixRoomESR matrixRoom){

    }
}
