package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.registrymetadata.client.DocumentEntry;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 * Created by skb1 on 3/15/18.
 */
public class ValidateDEUpdateRequest extends CommandContext{
    DocumentEntry de;

    public ValidateDEUpdateRequest(){}

    public ValidateDEUpdateRequest(DocumentEntry de) {
        this.de = de;
    }
}
