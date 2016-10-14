package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.GetSiteNamesRequest;

import java.util.List;

/**
 * Created by onh2 on 10/14/16.
 */
public abstract class GetSiteNamesCommand extends GenericCommand<GetSiteNamesRequest,List<String>>{
    @Override
    public void run(GetSiteNamesRequest var1) {
        ClientUtils.INSTANCE.getToolkitServices().getSiteNames(var1.getReload(),var1.getSimAlso(),this);
    }
}
