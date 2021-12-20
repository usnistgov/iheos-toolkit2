package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.GetAdminPasswordHashRequest;

public abstract class GetAdminPasswordHashCommand extends GenericCommand<GetAdminPasswordHashRequest, String> {

    @Override
    public void run(GetAdminPasswordHashRequest var1) {
        ClientUtils.INSTANCE.getToolkitServices().getAdminPasswordHash(var1, this);
    }
}
