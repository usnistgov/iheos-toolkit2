package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.GetAdminToolkitPropertiesRequest;

import java.util.Map;

public abstract class GetAdminToolkitPropertiesCommand extends GenericCommand<GetAdminToolkitPropertiesRequest, Map<String,String>> {
    @Override
    public void run(GetAdminToolkitPropertiesRequest var1) {
        ClientUtils.INSTANCE.getToolkitServices().getAdminToolkitProperties(var1, this);
    }
}
