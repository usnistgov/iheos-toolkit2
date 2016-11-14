package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.GetStsSamlAssertionRequest;

/**
 * Created by onh2 on 11/10/16.
 */
public abstract class GetStsSamlAssertionCommand extends GenericCommand<GetStsSamlAssertionRequest,String>{
    @Override
    public void run(GetStsSamlAssertionRequest var1) {
        ClientUtils.INSTANCE.getToolkitServices().getStsSamlAssertion(var1,this);
    }
}
