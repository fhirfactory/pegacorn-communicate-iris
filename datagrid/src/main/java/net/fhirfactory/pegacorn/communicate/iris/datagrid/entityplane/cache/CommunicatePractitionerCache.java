/*
 * Copyright (c) 2021 Mark Hunter
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
package net.fhirfactory.pegacorn.communicate.iris.datagrid.entityplane.cache;

import net.fhirfactory.pegacorn.communicate.iris.datagrid.entityplane.cache.common.CommunicateResourceCacheBase;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.PractitionerESR;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.common.ExtremelySimplifiedResource;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.datatypes.IdentifierESDT;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.valuesets.IdentifierESDTUseEnum;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.search.PractitionerSearchResult;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.search.common.ESRSearchResult;
import net.fhirfactory.pegacorn.core.model.ui.transactions.ESRMethodOutcome;
import net.fhirfactory.pegacorn.core.model.ui.transactions.exceptions.ResourceInvalidSearchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CommunicatePractitionerCache extends CommunicateResourceCacheBase {
    private static final Logger LOG = LoggerFactory.getLogger(CommunicatePractitionerCache.class);

    @Override
    protected Logger getLogger(){
        return(LOG);
    }

    public CommunicatePractitionerCache(){
        super();
    }

    public ESRMethodOutcome addPractitioner(PractitionerESR practitionerESR){
        ESRMethodOutcome outcome = addCacheEntry(practitionerESR);
        return(outcome);
    }

    public PractitionerESR getPractitioner(IdentifierESDT practitionerID){
        ExtremelySimplifiedResource foundEntry = this.getCacheEntry(practitionerID);
        PractitionerESR foundPractitionerESR = (PractitionerESR) foundEntry;
        return(foundPractitionerESR);
    }

    //
    // Search Functions
    //

    @Override
    protected ESRSearchResult instatiateNewESRSearchResult(){
        PractitionerSearchResult result = new PractitionerSearchResult();
        return(result);
    }

    @Override
    public ESRSearchResult search(String searchAttributeName, String searchAttributeValue)
            throws ResourceInvalidSearchException {
        getLogger().debug(".search(): Entry, searchAttributeName->{}, searchAttributeValue->{}", searchAttributeName, searchAttributeValue);
        if(searchAttributeName == null || searchAttributeValue == null){
            throw(new ResourceInvalidSearchException("Search Parameter Name or Value are null"));
        }
        if(searchAttributeName.isEmpty()){
            throw(new ResourceInvalidSearchException("Search Parameter Name is empty"));
        }
        ESRSearchResult result = instatiateNewESRSearchResult();
        if(searchAttributeValue.isEmpty()){
            return(result);
        }
        String searchAttributeNameLowerCase = searchAttributeName.toLowerCase();
        switch(searchAttributeNameLowerCase){
            case "simplifiedid":
            case "emailaddress":{
                result = this.searchCacheUsingSimplifiedID(searchAttributeValue);
                return (result);
            }
            case "shortname": {
                result = this.searchCacheForESRUsingIdentifierParameters(searchAttributeValue, "ShortName", IdentifierESDTUseEnum.USUAL);
                return(result);
            }
            case "longname": {
                result = this.searchCacheForESRUsingIdentifierParameters(searchAttributeValue, "LongName", IdentifierESDTUseEnum.USUAL);
                return(result);
            }
            case "displayname": {
                result = this.searchCacheUsingDisplayName(searchAttributeValue);
                return(result);
            }
            default: {
                return (result);
            }
        }
    }

    @Override
    public Boolean supportsSearchType(String attributeName) {
        String searchAttributeNameLowerCase = attributeName.toLowerCase();
        switch(searchAttributeNameLowerCase){
            case "simplifiedid":
            case "shortnmae":
            case "longname":
            case "displayname":
                return(true);
            default:
                return(false);
        }
    }
}
