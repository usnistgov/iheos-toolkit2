package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.installation.shared.TestCollectionCode;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 * Created by onh2 on 11/7/16.
 */
public class GetCollectionRequest extends CommandContext {
    private TestCollectionCode tcId;
    private String collectionSetName;

    public GetCollectionRequest() {
    }

    public GetCollectionRequest(CommandContext context, String collectionSetName) {
        copyFrom(context);
        this.collectionSetName = collectionSetName;
    }

    public GetCollectionRequest(CommandContext context, String collectionSetName, TestCollectionCode tcId) {
        this(context, collectionSetName);
        this.tcId = tcId;
    }

    public TestCollectionCode getTcId() {
        return tcId;
    }

    public void setTcId(TestCollectionCode tcId) {
        this.tcId = tcId;
    }

    public String getCollectionSetName() {
        return collectionSetName;
    }

    public void setCollectionSetName(String collectionSetName) {
        this.collectionSetName = collectionSetName;
    }
}
