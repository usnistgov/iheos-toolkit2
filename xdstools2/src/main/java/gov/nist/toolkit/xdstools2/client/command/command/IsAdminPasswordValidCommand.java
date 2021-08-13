package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.IsAdminPasswordValidRequest;

public abstract class IsAdminPasswordValidCommand extends GenericCommand<IsAdminPasswordValidRequest, Boolean>{
    @Override
    public void run(IsAdminPasswordValidRequest request) {
        ClientUtils.INSTANCE.getToolkitServices().isAdminPasswordValid(request, this);
    }
}
