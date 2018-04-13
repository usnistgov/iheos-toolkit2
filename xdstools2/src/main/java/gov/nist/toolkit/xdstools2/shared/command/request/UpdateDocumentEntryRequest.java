package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.registrymetadata.client.DocumentEntry;
import gov.nist.toolkit.registrymetadata.client.MetadataCollection;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.inspector.QueryOrigin;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 * Created by skb1 on 3/26/18.
 */
public class UpdateDocumentEntryRequest extends CommandContext {
    SiteSpec siteSpec;
    MetadataCollection mc;
    DocumentEntry toBeUpdated;
    TestInstance originalQueryTestInstance;
    boolean noCompare;
    QueryOrigin queryOrigin;

    public UpdateDocumentEntryRequest(){}

    public UpdateDocumentEntryRequest(CommandContext context, SiteSpec siteSpec, MetadataCollection mc, DocumentEntry toBeUpdated, TestInstance originalQueryTestInstance, boolean noCompare, QueryOrigin queryOrigin) {
        copyFrom(context);
        this.siteSpec = siteSpec;
        this.mc = mc;
        this.toBeUpdated = toBeUpdated;
        this.originalQueryTestInstance = originalQueryTestInstance;
        this.noCompare = noCompare;
        this.queryOrigin = queryOrigin;
    }

    public DocumentEntry getToBeUpdated() {
        return toBeUpdated;
    }

    public MetadataCollection getMc() {
        return mc;
    }

    public TestInstance getOriginalQueryTestInstance() {
        return originalQueryTestInstance;
    }

    public SiteSpec getSiteSpec() {
        return siteSpec;
    }

    public boolean isNoCompare() {
        return noCompare;
    }

    public QueryOrigin getQueryOrigin() {
        return queryOrigin;
    }
}
