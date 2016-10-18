package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.SetAssignedSiteForTestSessionRequest;

/**
 * Created by onh2 on 10/14/16.
 */
public class SetAssignedSiteForTestSessionCommand extends GenericCommand<SetAssignedSiteForTestSessionRequest,Void>{
    @Override
    public void run(SetAssignedSiteForTestSessionRequest var1) {
        ClientUtils.INSTANCE.getToolkitServices().setAssignedSiteForTestSession(var1,this);
    }

    @Override
    public void onComplete(Void result) {
        // nothing to do with the Void result.
    }
}
