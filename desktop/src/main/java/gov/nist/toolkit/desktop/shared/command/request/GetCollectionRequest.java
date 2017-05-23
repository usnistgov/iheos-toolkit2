package gov.nist.toolkit.desktop.shared.command.request;


import gov.nist.toolkit.desktop.client.commands.util.CommandContext;

/**
 * Created by onh2 on 11/7/16.
 */
public class GetCollectionRequest extends CommandContext {
    private String collectionName;
    private String collectionSetName;

    public GetCollectionRequest() {
    }

    public GetCollectionRequest(CommandContext context, String collectionSetName) {
        copyFrom(context);
        this.collectionSetName = collectionSetName;
    }

    public GetCollectionRequest(CommandContext context, String collectionSetName, String collectionName) {
        this(context, collectionSetName);
        this.collectionName = collectionName;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public String getCollectionSetName() {
        return collectionSetName;
    }

    public void setCollectionSetName(String collectionSetName) {
        this.collectionSetName = collectionSetName;
    }
}
