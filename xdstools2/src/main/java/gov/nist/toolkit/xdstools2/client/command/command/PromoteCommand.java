package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.PromoteRequest;

public abstract class PromoteCommand extends GenericCommand<PromoteRequest,String>{
    @Override
    public void run(PromoteRequest var1) {
        ClientUtils.INSTANCE.getToolkitServices().promote(var1, this);
    }
}
