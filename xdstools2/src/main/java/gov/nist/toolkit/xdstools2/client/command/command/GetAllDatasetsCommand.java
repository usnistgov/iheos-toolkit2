package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.datasets.shared.DatasetModel;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

import java.util.List;
import java.util.Map;

/**
 *
 */
public abstract class GetAllDatasetsCommand extends GenericCommand<CommandContext,List<DatasetModel>>{
    @Override
    public void run(CommandContext var1) {
        ClientUtils.INSTANCE.getToolkitServices().getAllDatasets(var1,this);
    }
}
