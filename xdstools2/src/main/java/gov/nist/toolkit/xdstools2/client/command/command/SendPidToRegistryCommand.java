package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.xdstools2.client.command.request.SendPidToRegistryRequest;

import java.util.List;

import static gov.nist.toolkit.xdstools2.client.ToolWindow.toolkitService;

/**
 *
 */
abstract public class SendPidToRegistryCommand  extends GenericCommand<SendPidToRegistryRequest, List<Result>> {
    public SendPidToRegistryCommand() {
        super();
    }

    @Override
    public void run(SendPidToRegistryRequest var1) {
        toolkitService.sendPidToRegistry(var1, this);
    }
}
