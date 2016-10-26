package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.configDatatypes.client.Pid;
import gov.nist.toolkit.xdstools2.shared.command.request.GeneratePidRequest;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;

/**
 *
 */
public abstract class GeneratePidCommand extends GenericCommand<GeneratePidRequest, Pid> {
    @Override
    public void run(GeneratePidRequest var1) {
        ClientUtils.INSTANCE.getToolkitServices().createPid(var1, this);
    }
}
