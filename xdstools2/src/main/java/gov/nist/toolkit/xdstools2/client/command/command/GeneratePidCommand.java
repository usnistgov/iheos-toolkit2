package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.configDatatypes.client.Pid;
import gov.nist.toolkit.xdstools2.client.command.request.GeneratePidRequest;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;

/**
 *
 */
abstract public class GeneratePidCommand extends GenericCommand<GeneratePidRequest, Pid> {

    public GeneratePidCommand() {
        super();
    }

    @Override
    public void run(GeneratePidRequest var1) {
        ClientUtils.INSTANCE.getToolkitServices().createPid(var1, this);
    }
}
