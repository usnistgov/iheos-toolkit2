package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.xdstools2.client.command.request.GetAllSimConfigsRequest;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;

import java.util.List;

/**
 *
 */
abstract public class GetAllSimConfigsCommand extends GenericCommand<GetAllSimConfigsRequest, List<SimulatorConfig>> {
    public GetAllSimConfigsCommand() {
        super();
    }

    @Override
    public void run(GetAllSimConfigsRequest var1) {
        ClientUtils.INSTANCE.getToolkitServices().getAllSimConfigs(var1, this);
    }
}
