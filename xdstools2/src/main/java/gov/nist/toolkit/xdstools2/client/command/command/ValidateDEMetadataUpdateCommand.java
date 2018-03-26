package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.valsupport.client.MessageValidationResults;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.ValidateDocumentEntryRequest;

/**
 * Created by skb1 on 3/16/18.
 */
public abstract class ValidateDEMetadataUpdateCommand extends GenericCommand<ValidateDocumentEntryRequest,MessageValidationResults>{
    @Override
    public void run(ValidateDocumentEntryRequest request) {
        ClientUtils.INSTANCE.getToolkitServices().validateDEMetadataUpdate(request, this);
    }
}
