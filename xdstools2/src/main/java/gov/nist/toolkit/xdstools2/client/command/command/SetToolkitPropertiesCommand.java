package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.xdstools2.client.initialization.FrameworkInitialization;
import gov.nist.toolkit.xdstools2.shared.command.request.SetToolkitPropertiesRequest;

/**
 * Created by onh2 on 11/4/16.
 */
public abstract class SetToolkitPropertiesCommand extends GenericCommand<SetToolkitPropertiesRequest,String>{
    @Override
    public void run(SetToolkitPropertiesRequest var1) {
        FrameworkInitialization.data().getToolkitServices().setToolkitProperties(var1,this);
    }
}
