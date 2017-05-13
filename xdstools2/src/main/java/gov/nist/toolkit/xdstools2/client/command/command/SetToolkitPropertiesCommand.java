package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.xdstools2.client.initialization.XdsTools2Presenter;
import gov.nist.toolkit.xdstools2.shared.command.request.SetToolkitPropertiesRequest;

/**
 * Created by onh2 on 11/4/16.
 */
public abstract class SetToolkitPropertiesCommand extends GenericCommand<SetToolkitPropertiesRequest,String>{
    @Override
    public void run(SetToolkitPropertiesRequest var1) {
        XdsTools2Presenter.data().getToolkitServices().setToolkitProperties(var1,this);
    }
}
