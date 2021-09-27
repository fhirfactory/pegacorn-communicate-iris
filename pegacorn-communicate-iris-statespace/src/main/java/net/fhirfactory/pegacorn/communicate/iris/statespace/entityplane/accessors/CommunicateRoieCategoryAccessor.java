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
package net.fhirfactory.pegacorn.communicate.iris.statespace.entityplane.accessors;

import net.fhirfactory.pegacorn.communicate.iris.statespace.entityplane.accessors.common.EntityPlaneUpdateOriginEnum;
import net.fhirfactory.pegacorn.internals.communicate.entities.practitionerrole.CommunicateRoleCategory;
import net.fhirfactory.pegacorn.internals.esr.brokers.RoleCategoryESRBroker;
import net.fhirfactory.pegacorn.internals.esr.resources.common.ExtremelySimplifiedResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CommunicateRoieCategoryAccessor extends RoleCategoryESRBroker {
    private static final Logger LOG = LoggerFactory.getLogger(CommunicateRoieCategoryAccessor.class);

    protected Logger getLogger(){
        return(LOG);
    }

    /**
     * 
     * @param location
     * @param updateOrigin
     * @return
     */
    public CommunicateRoleCategory synchroniseRoleCategory(CommunicateRoleCategory location, EntityPlaneUpdateOriginEnum updateOrigin){
        return(location);
    }

    @Override
    protected ExtremelySimplifiedResource synchroniseResource(ExtremelySimplifiedResource resource) {
        CommunicateRoleCategory location = (CommunicateRoleCategory) resource;
        CommunicateRoleCategory synchronisedRoleCategory = synchroniseRoleCategory(location, EntityPlaneUpdateOriginEnum.UPDATE_ORIGIN_WORKFLOW_DOMAIN);
        return(synchronisedRoleCategory);
    }
}