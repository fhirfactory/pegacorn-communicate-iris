package net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.workshops.actions.simpleactions.synapseserver;

import net.fhirfactory.pegacorn.internals.esr.resources.PractitionerESR;
import net.fhirfactory.pegacorn.internals.matrix.r061.api.common.MAPIResponse;

import javax.enterprise.context.ApplicationScoped;
import java.util.Map;

@ApplicationScoped
public class SynapseUserSimpleActions {

    public MAPIResponse getUserAccountDetail(String userID){
        MAPIResponse taskResponse = new MAPIResponse();

        return(taskResponse);
    }

    public MAPIResponse createUserAccount(PractitionerESR practitioner){
        MAPIResponse taskResponse = new MAPIResponse();

        return(taskResponse);
    }

    public MAPIResponse updateUserAccount(PractitionerESR practitioner){
        MAPIResponse taskResponse = new MAPIResponse();

        return(taskResponse);
    }

    public MAPIResponse listALLAccounts(Map<String,String> searchCriteria){
        MAPIResponse taskResponse = new MAPIResponse();

        return(taskResponse);
    }

    public MAPIResponse getUserSessions(String userID){
        MAPIResponse taskResponse = new MAPIResponse();

        return(taskResponse);
    }

    public MAPIResponse deactivateUserAccount(String userID){
        MAPIResponse taskResponse = new MAPIResponse();

        return(taskResponse);
    }

    public MAPIResponse getUserRooms(String userID){
        MAPIResponse taskResponse = new MAPIResponse();

        return(taskResponse);
    }

    public MAPIResponse getUserMedia(String userID){
        MAPIResponse taskResponse = new MAPIResponse();

        return(taskResponse);
    }

    public MAPIResponse getUserDevices(String userID){
        MAPIResponse taskResponse = new MAPIResponse();

        return(taskResponse);
    }
}
