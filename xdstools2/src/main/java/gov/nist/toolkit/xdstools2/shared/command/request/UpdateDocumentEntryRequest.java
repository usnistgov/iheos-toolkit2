package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.registrymetadata.client.DocumentEntry;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 * Created by skb1 on 3/26/18.
 */
public class UpdateDocumentEntryRequest extends CommandContext {
    int logEntryindex;
    DocumentEntry toBeUpdatedDe;
    TestInstance originalGetDocsTestInstance;

    public UpdateDocumentEntryRequest(){}

    public UpdateDocumentEntryRequest(CommandContext context, DocumentEntry toBeUpdatedDe, TestInstance originalGetDocsTestInstance, int logEntryindex) {
        copyFrom(context);
        this.toBeUpdatedDe = toBeUpdatedDe;
        this.originalGetDocsTestInstance = originalGetDocsTestInstance;
        this.logEntryindex = logEntryindex;
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

    public int getLogEntryindex() {
        return logEntryindex;
    }
}
