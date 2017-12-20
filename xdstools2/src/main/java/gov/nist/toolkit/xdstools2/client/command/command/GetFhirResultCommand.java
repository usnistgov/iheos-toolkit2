package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.session.shared.Message;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.GetRawLogsRequest;

/**
 * Created by onh2 on 10/31/16.
 */
public abstract class GetFhirResultCommand extends GenericCommand<GetRawLogsRequest,Message>{
    @Override
    public void run(GetRawLogsRequest var1) {
        ClientUtils.INSTANCE.getToolkitServices().getFhirResult(var1,this);
    }
}
