package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.xdstools2.client.initialization.XdsTools2Presenter;
import gov.nist.toolkit.xdstools2.shared.command.request.SaveSiteRequest;

/**
 * Created by onh2 on 10/18/16.
 */
public abstract class SaveSiteCommand extends GenericCommand<SaveSiteRequest,String>{
    @Override
    public void run(SaveSiteRequest var1) {
        XdsTools2Presenter.data().getToolkitServices().saveSite(var1,this);
    }
}
