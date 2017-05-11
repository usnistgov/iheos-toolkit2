package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.xdstools2.client.initialization.FrameworkInitialization;
import gov.nist.toolkit.xdstools2.shared.command.request.PatientIdsRequest;

/**
 * Created by onh2 on 11/7/16.
 */
public abstract class AddPatientIdsCommand extends GenericCommand<PatientIdsRequest,String>{
    @Override
    public void run(PatientIdsRequest var1) {
        FrameworkInitialization.data().getToolkitServices().addPatientIds(var1,this);
    }
}
