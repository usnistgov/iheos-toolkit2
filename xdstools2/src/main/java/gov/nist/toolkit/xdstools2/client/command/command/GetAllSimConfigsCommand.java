package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.xdstools2.client.command.request.GetAllSimConfigsRequest;

import java.util.List;

import static gov.nist.toolkit.xdstools2.client.ToolWindow.toolkitService;

/**
 *
 */
abstract public class GetAllSimConfigsCommand extends GenericCommand<GetAllSimConfigsRequest, List<SimulatorConfig>> {
    public GetAllSimConfigsCommand() {
        super();
    }

    @Override
    public void run(GetAllSimConfigsRequest var1) {
        toolkitService.getAllSimConfigs(var1, this);
    }
}
