package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.xdstools2.client.initialization.XdsTools2Presenter;
import gov.nist.toolkit.xdstools2.shared.command.request.SendPidToRegistryRequest;

import java.util.List;


/**
 *
 */
public abstract class SendPidToRegistryCommand  extends GenericCommand<SendPidToRegistryRequest, List<Result>> {
    @Override
    public void run(SendPidToRegistryRequest var1) {
        XdsTools2Presenter.data().getToolkitServices().sendPidToRegistry(var1, this);
    }
}
