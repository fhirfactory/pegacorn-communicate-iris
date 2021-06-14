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
package net.fhirfactory.pegacorn.communicate.iris.statespace.normaliser.matrixevent.userbased.beans;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.fhirfactory.pegacorn.common.model.topicid.DataParcelToken;
import net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.workshops.cache.user.UserMapCache;
import net.fhirfactory.pegacorn.communicate.iris.statespace.normaliser.matrixevent.common.MatrixEventNormaliserBeanBase;
import net.fhirfactory.pegacorn.petasos.model.uow.UoW;
import net.fhirfactory.pegacorn.petasos.model.uow.UoWProcessingOutcomeEnum;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class MatrixPresenceEventNormaliserBean extends MatrixEventNormaliserBeanBase {
    private static final Logger LOG = LoggerFactory.getLogger(MatrixPresenceEventNormaliserBean.class);
    private ObjectMapper jsonObjectMapper;

    @Inject
    private UserMapCache userMap;

    public MatrixPresenceEventNormaliserBean(){
        jsonObjectMapper = new ObjectMapper();
        jsonObjectMapper.configure(JsonParser.Feature.ALLOW_MISSING_VALUES, true);
    }

    public UoW normaliseMatrixPresenceEvent(UoW incomingUoW){
        String version = incomingUoW.getPayloadTopicID().getVersion();
        DataParcelToken token = incomingUoW.getIngresContent().getPayloadTopicID();

        String matrixEventMessage = incomingUoW.getIngresContent().getPayload();
        String userID = extractUserID(matrixEventMessage);
        if(userID == null){
            failUoW(incomingUoW, "Unable to resolve UserID (sender) from Presence Event Message");
            return(incomingUoW);
        }
        if(getUserMap().isPractitionerUser(userID)){
            passUoW(incomingUoW);
            return(incomingUoW);
        }
        incomingUoW.setProcessingOutcome(UoWProcessingOutcomeEnum.UOW_OUTCOME_NO_PROCESSING_REQUIRED);
        return(incomingUoW);
    }


    private String extractUserID(String presenceEventMsg){
        if(presenceEventMsg == null){
            return(null);
        }
        JSONObject messageObject = new JSONObject(presenceEventMsg);
        String userID = messageObject.getString("sender");
        return(userID);
    }

    public static Logger getLOG() {
        return LOG;
    }

    public ObjectMapper getJsonObjectMapper() {
        return jsonObjectMapper;
    }

    public UserMapCache getUserMap() {
        return userMap;
    }
}
