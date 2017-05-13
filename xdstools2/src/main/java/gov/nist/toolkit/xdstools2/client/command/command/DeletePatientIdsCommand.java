package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.xdstools2.client.initialization.XdsTools2Presenter;
import gov.nist.toolkit.xdstools2.shared.command.request.PatientIdsRequest;

/**
 * Created by onh2 on 11/7/16.
 */
public abstract class DeletePatientIdsCommand extends GenericCommand<PatientIdsRequest,Boolean>{
    @Override
    public void run(PatientIdsRequest var1) {
        XdsTools2Presenter.data().getToolkitServices().deletePatientIds(var1,this);
    }
}
