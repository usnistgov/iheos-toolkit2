package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.session.shared.gov.nist.toolkit.session.shared.DatasetElement;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 *
 */
public class FhirCreateRequest extends CommandContext {
    private SiteSpec site;
    private DatasetElement datasetElement;
    private String urlExtension;

    public FhirCreateRequest() {
    }

    public FhirCreateRequest(CommandContext context, SiteSpec site, DatasetElement datasetElement, String urlExtension) {
        copyFrom(context);
        this.site = site;
        this.datasetElement = datasetElement;
        this.urlExtension = urlExtension;
    }

    public DatasetElement getResourcePath() {
        return datasetElement;
    }

    public String getUrlExtension() {
        return urlExtension;
    }

    public SiteSpec getSite() {
        return site;
    }
}
