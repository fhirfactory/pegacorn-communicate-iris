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
package net.fhirfactory.pegacorn.communicate.iris.statespace.normaliser.matrixevent.roombased;

import net.fhirfactory.pegacorn.common.model.generalid.FDN;
import net.fhirfactory.pegacorn.common.model.generalid.RDN;
import net.fhirfactory.pegacorn.common.model.topicid.DataParcelNormalisationStatusEnum;
import net.fhirfactory.pegacorn.common.model.topicid.DataParcelToken;
import net.fhirfactory.pegacorn.common.model.topicid.DataParcelTypeKeyEnum;
import net.fhirfactory.pegacorn.common.model.topicid.DataParcelValidationStatusEnum;
import net.fhirfactory.pegacorn.communicate.iris.statespace.CommunicateStateSpaceWorkshop;
import net.fhirfactory.pegacorn.communicate.iris.statespace.normaliser.matrixevent.roombased.beans.MatrixRoomEventNormaliserBean;
import net.fhirfactory.pegacorn.components.interfaces.topology.WorkshopInterface;
import net.fhirfactory.pegacorn.internals.matrix.r061.events.common.contenttypes.MEventTypeEnum;
import net.fhirfactory.pegacorn.internals.matrix.r061.events.room.message.contenttypes.MRoomMessageTypeEnum;
import net.fhirfactory.pegacorn.wups.archetypes.petasosenabled.messageprocessingbased.core.MOAStandardWUP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class MatrixRoomEventNormaliser extends MOAStandardWUP {
    private static final Logger LOG = LoggerFactory.getLogger(MatrixRoomEventNormaliser.class);
    
    @Override
    protected Logger getLogger(){return(LOG);}

    private static final String WUP_NAME="Normaliser.RoomBasedMatrixEvents";
    private static final String WUP_VERSION="1.0.0";

    @Inject
    private CommunicateStateSpaceWorkshop statespaceWorkshop;

    @Override
    protected Set<DataParcelToken> specifySubscriptionTopics() {
        List<DataParcelToken> uglyList = createUglyDataParcelList();
        Set<DataParcelToken> uglySet = new HashSet<>();
        uglySet.addAll(uglyList);
        return(uglySet);
    }


    @Override
    protected Logger specifyLogger() {
        return (LOG);
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
        return (statespaceWorkshop);
    }

    @Override
    public void configure() throws Exception {

        String ingresFeed = getIngresTopologyEndpoint().getEndpointSpecification();
        String egressFeed = getEgressTopologyEndpoint().getEndpointSpecification();

        LOG.info("Route->{}, ingresFeed->{}, egressFeed->{}", getNameSet().getRouteCoreWUP(), ingresFeed, egressFeed);

        from(ingresFeed)
                .routeId(getNameSet().getRouteCoreWUP())
                .bean(MatrixRoomEventNormaliserBean.class,"normaliseMatrixRoomEvent(*)")
                .to(egressFeed);
    }

    List<DataParcelToken> createUglyDataParcelList(){
        List<DataParcelToken> uglyList = new ArrayList<>();
        for(MEventTypeEnum messageType: MEventTypeEnum.values()){
            if(messageType.getEventType().startsWith("m.room.")){
                DataParcelToken currentToken = createRoomDataParcelToken(messageType);
                uglyList.add(currentToken);
            }
        }
        for(MRoomMessageTypeEnum instantMessageType: MRoomMessageTypeEnum.values()){
            DataParcelToken currentToken = createInstantMessageDataParcelToken(instantMessageType);
            uglyList.add(currentToken);
        }
        return(uglyList);
    }

    DataParcelToken createRoomDataParcelToken(MEventTypeEnum messageType){
        FDN payloadTopicFDN = new FDN();
        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_DEFINER.getTopicType(), "Matrix"));
        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_CATEGORY.getTopicType(), "ClientServerAPI"));
        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_SUBCATEGORY.getTopicType(), "RoomEvents"));
        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_RESOURCE.getTopicType(), messageType.getEventType()));
        DataParcelToken payloadToken = new DataParcelToken();
        payloadToken.setToken(payloadTopicFDN.getToken());
        payloadToken.setVersion("0.6.1");
        payloadToken.setValidationStatus(DataParcelValidationStatusEnum.DATA_PARCEL_CONTENT_VALIDATED_FALSE);
        payloadToken.setNormalisationStatus(DataParcelNormalisationStatusEnum.DATA_PARCEL_CONTENT_NORMALISATION_FALSE);
        return(payloadToken);
    }

    DataParcelToken createInstantMessageDataParcelToken(MRoomMessageTypeEnum messageType){
        FDN payloadTopicFDN = new FDN();
        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_DEFINER.getTopicType(), "Matrix"));
        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_CATEGORY.getTopicType(), "ClientServerAPI"));
        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_SUBCATEGORY.getTopicType(), "RoomEvents"));
        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_RESOURCE.getTopicType(), MEventTypeEnum.M_ROOM_MESSAGE.getEventType()));
        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_DISCRIMINATOR_TYPE.getTopicType(), "Messaging.ContentType"));
        payloadTopicFDN.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_DISCRIMINATOR_VALUE.getTopicType(), messageType.getMsgtype()));
        DataParcelToken payloadToken = new DataParcelToken();
        payloadToken.setToken(payloadTopicFDN.getToken());
        payloadToken.setVersion("0.6.1");
        payloadToken.setValidationStatus(DataParcelValidationStatusEnum.DATA_PARCEL_CONTENT_VALIDATED_FALSE);
        payloadToken.setNormalisationStatus(DataParcelNormalisationStatusEnum.DATA_PARCEL_CONTENT_NORMALISATION_FALSE);
        return(payloadToken);
    }
}
