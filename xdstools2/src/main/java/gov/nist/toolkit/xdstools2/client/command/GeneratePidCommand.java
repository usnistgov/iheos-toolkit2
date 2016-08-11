package gov.nist.toolkit.xdstools2.client.command;

import gov.nist.toolkit.configDatatypes.client.Pid;
import gov.nist.toolkit.xdstools2.client.ToolWindow;

/**
 *
 */
abstract public class GeneratePidCommand extends GenericCommand<GeneratePidRequest, Pid> {

    public GeneratePidCommand(ToolWindow toolWindow) {
        super(toolWindow);
    }

    @Override
    public void run(GeneratePidRequest var1) {
        toolkitService.createPid(var1, this);
    }
}
