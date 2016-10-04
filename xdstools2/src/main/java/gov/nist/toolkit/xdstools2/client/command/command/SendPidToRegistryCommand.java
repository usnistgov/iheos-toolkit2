package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.xdstools2.shared.command.SendPidToRegistryRequest;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;

import java.util.List;


/**
 *
 */
abstract public class SendPidToRegistryCommand  extends GenericCommand<SendPidToRegistryRequest, List<Result>> {
    public SendPidToRegistryCommand() {
        super();
    }

    @Override
    public void run(SendPidToRegistryRequest var1) {
        ClientUtils.INSTANCE.getToolkitServices().sendPidToRegistry(var1, this);
    }
}
