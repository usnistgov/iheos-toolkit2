package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.valsupport.client.MessageValidationResults;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.ValidateDEMetadataUpdateRequest;

/**
 * Created by skb1 on 3/16/18.
 */
public abstract class ValidateDEMetadataUpdateCommand extends GenericCommand<ValidateDEMetadataUpdateRequest,MessageValidationResults>{
    @Override
    public void run(ValidateDEMetadataUpdateRequest request) {
        ClientUtils.INSTANCE.getToolkitServices().validateDEMetadataUpdate(request, this);
    }
}
