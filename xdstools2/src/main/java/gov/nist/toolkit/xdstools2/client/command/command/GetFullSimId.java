package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.GetFullSimIdRequest;
import gov.nist.toolkit.xdstools2.shared.command.request.GetSimIdsForUserRequest;

import java.util.List;

/**
 *
 */
public abstract class GetFullSimId extends GenericCommand<GetFullSimIdRequest,SimId> {
    @Override
    public void run(GetFullSimIdRequest request) {
        ClientUtils.INSTANCE.getToolkitServices().getFullSimId(request, this);
    }
}