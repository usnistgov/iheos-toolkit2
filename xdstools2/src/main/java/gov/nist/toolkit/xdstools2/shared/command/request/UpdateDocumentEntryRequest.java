package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.registrymetadata.client.DocumentEntry;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 * Created by skb1 on 3/26/18.
 */
public class UpdateDocumentEntryRequest extends CommandContext {
    DocumentEntry toBeUpdatedDe;
    TestInstance originalGetDocsTestInstance;

    public UpdateDocumentEntryRequest(){}

    public UpdateDocumentEntryRequest(CommandContext context, DocumentEntry toBeUpdatedDe, TestInstance originalGetDocsTestInstance) {
        copyFrom(context);
        this.toBeUpdatedDe = toBeUpdatedDe;
        this.originalGetDocsTestInstance = originalGetDocsTestInstance;
    }

    public DocumentEntry getToBeUpdatedDe() {
        return toBeUpdatedDe;
    }


    public TestInstance getOriginalGetDocsTestInstance() {
        return originalGetDocsTestInstance;
    }

    public void setOriginalGetDocsTestInstance(TestInstance originalGetDocsTestInstance) {
        this.originalGetDocsTestInstance = originalGetDocsTestInstance;
    }

}
