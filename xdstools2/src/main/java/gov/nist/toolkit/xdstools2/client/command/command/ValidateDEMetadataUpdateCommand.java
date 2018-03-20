package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.SetOdSupplyStateIndexRequest;
import gov.nist.toolkit.xdstools2.shared.command.request.ValidateDEMetadataUpdateRequest;

/**
 * Created by skb1 on 3/16/18.
 */
public abstract class ValidateDEMetadataUpdateCommand extends GenericCommand<SetOdSupplyStateIndexRequest,Boolean>{
    @Override
    public void run(ValidateDEMetadataUpdateRequest request) {
        ClientUtils.INSTANCE.getToolkitServices().setOdSupplyStateIndex(request, this);
    }
}
