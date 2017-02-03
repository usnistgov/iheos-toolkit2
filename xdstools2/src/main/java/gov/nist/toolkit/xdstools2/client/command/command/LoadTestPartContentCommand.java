package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.session.client.logtypes.TestPartFileDTO;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.LoadTestPartContentRequest;

/**
 * Created by onh2 on 11/7/16.
 */
public abstract class LoadTestPartContentCommand extends GenericCommand<LoadTestPartContentRequest,TestPartFileDTO>{
    @Override
    public void run(LoadTestPartContentRequest var1) {
        ClientUtils.INSTANCE.getToolkitServices().loadTestPartContent(var1,this);
    }
}
