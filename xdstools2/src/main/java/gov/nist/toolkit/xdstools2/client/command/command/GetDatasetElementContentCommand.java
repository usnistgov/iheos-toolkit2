package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.datasets.shared.DatasetElement;
import gov.nist.toolkit.datasets.shared.DatasetModel;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;
import gov.nist.toolkit.xdstools2.shared.command.request.GetDatasetElementContentRequest;

import java.util.List;

/**
 *
 */
public abstract class GetDatasetElementContentCommand extends GenericCommand<GetDatasetElementContentRequest,String>{
    @Override
    public void run(GetDatasetElementContentRequest var1) {
        ClientUtils.INSTANCE.getToolkitServices().getDatasetContent(var1, this);}
}
