package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.GetSimIdsForUserRequest;

import java.util.List;

/**
 * Created by onh2 on 10/20/16.
 */
public abstract class GetSimIdForUser extends GenericCommand<GetSimIdsForUserRequest,List<SimId>> {
    @Override
    public void run(GetSimIdsForUserRequest context) {
        ClientUtils.INSTANCE.getToolkitServices().getSimIdsForUser(context, this);
    }
}
