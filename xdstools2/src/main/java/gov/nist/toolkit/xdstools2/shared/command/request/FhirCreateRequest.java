package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.datasets.shared.DatasetElement;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 *
 */
public class FhirCreateRequest extends CommandContext {
    private SiteSpec site;
    private DatasetElement datasetElement;

    public FhirCreateRequest() {
    }

    public FhirCreateRequest(CommandContext context, SiteSpec site, DatasetElement datasetElement) {
        copyFrom(context);
        this.site = site;
        this.datasetElement = datasetElement;
    }

    public DatasetElement getDatasetElement() {
        return datasetElement;
    }

    public SiteSpec getSite() {
        return site;
    }
}
