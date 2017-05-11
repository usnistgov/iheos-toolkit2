package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.configDatatypes.client.Pid;
import gov.nist.toolkit.xdstools2.client.initialization.FrameworkInitialization;
import gov.nist.toolkit.xdstools2.shared.command.request.GeneratePidRequest;

/**
 *
 */
public abstract class GeneratePidCommand extends GenericCommand<GeneratePidRequest, Pid> {
    @Override
    public void run(GeneratePidRequest var1) {
        FrameworkInitialization.data().getToolkitServices().createPid(var1, this);
    }
}
