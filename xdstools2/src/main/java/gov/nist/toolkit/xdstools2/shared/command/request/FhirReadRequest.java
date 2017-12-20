package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 *
 */
public class FhirReadRequest extends CommandContext {
    private SiteSpec site;
    private String reference;

    public FhirReadRequest() {
    }

    public FhirReadRequest(CommandContext context, SiteSpec site, String reference) {
        copyFrom(context);
        this.site = site;
        this.reference = reference;
    }

    public String getReference() {
        return reference;
    }

    public SiteSpec getSite() {
        return site;
    }
}
