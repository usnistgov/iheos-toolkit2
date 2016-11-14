package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.RegisterRequest;

import java.util.Map;

/**
 * Created by onh2 on 11/10/16.
 */
public abstract class RegisterWithLocalizedTrackingInODDSCommand extends GenericCommand<RegisterRequest,Map<String,String>> {
    @Override
    public void run(RegisterRequest var1) {
        ClientUtils.INSTANCE.getToolkitServices().registerWithLocalizedTrackingInODDS(var1,this);
    }
}
