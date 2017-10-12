package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.datasets.shared.DatasetElement;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 *
 */
public class GetDatasetElementContentRequest extends CommandContext {
    DatasetElement datasetElement;

    public GetDatasetElementContentRequest() {}

    public GetDatasetElementContentRequest(CommandContext context, DatasetElement datasetElement) {
        copyFrom(context);
        this.datasetElement = datasetElement;
    }

    public DatasetElement getDatasetElement() {
        return datasetElement;
    }
}
