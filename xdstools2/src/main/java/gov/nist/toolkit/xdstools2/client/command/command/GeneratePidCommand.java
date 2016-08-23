package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.configDatatypes.client.Pid;
import gov.nist.toolkit.xdstools2.client.ToolWindow;
import gov.nist.toolkit.xdstools2.client.command.request.GeneratePidRequest;

import static gov.nist.toolkit.xdstools2.client.ToolWindow.toolkitService;

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
