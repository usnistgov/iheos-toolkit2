package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.configDatatypes.client.Pid;
import gov.nist.toolkit.xdstools2.client.initialization.XdsTools2Presenter;
import gov.nist.toolkit.xdstools2.shared.command.request.GeneratePidRequest;

/**
 *
 */
public abstract class GeneratePidCommand extends GenericCommand<GeneratePidRequest, Pid> {
    @Override
    public void run(GeneratePidRequest var1) {
        XdsTools2Presenter.data().getToolkitServices().createPid(var1, this);
    }
}
