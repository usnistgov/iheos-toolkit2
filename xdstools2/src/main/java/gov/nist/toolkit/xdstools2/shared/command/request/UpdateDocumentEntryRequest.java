package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.registrymetadata.client.MetadataCollection;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 * Created by skb1 on 3/26/18.
 */
public class UpdateDocumentEntryRequest extends CommandContext {
    MetadataCollection toBeUpdatedMc;
    TestInstance originalGetDocsTestInstance;

    public UpdateDocumentEntryRequest(){}

    public UpdateDocumentEntryRequest(CommandContext context, MetadataCollection toBeUpdatedMc, TestInstance originalGetDocsTestInstance) {
        copyFrom(context);
        this.toBeUpdatedMc = toBeUpdatedMc;
        this.originalGetDocsTestInstance = originalGetDocsTestInstance;
    }

    public MetadataCollection getToBeUpdatedMc() {
        return toBeUpdatedMc;
    }

    public void setToBeUpdatedMc(MetadataCollection toBeUpdatedMc) {
        this.toBeUpdatedMc = toBeUpdatedMc;
    }

    public TestInstance getOriginalGetDocsTestInstance() {
        return originalGetDocsTestInstance;
    }

    public void setOriginalGetDocsTestInstance(TestInstance originalGetDocsTestInstance) {
        this.originalGetDocsTestInstance = originalGetDocsTestInstance;
    }
}
