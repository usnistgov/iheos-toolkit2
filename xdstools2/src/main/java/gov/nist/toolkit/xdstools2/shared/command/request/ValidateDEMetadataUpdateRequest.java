package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.registrymetadata.client.DocumentEntry;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 * Created by skb1 on 3/15/18.
 */
public class ValidateDEMetadataUpdateRequest extends CommandContext{
    DocumentEntry de;

    public ValidateDEMetadataUpdateRequest(){}

    public ValidateDEMetadataUpdateRequest(CommandContext context, DocumentEntry de) {
        copyFrom(context);
        this.de = de;
    }

    public DocumentEntry getDe() {
        return de;
    }

    public void setDe(DocumentEntry de) {
        this.de = de;
    }
}
