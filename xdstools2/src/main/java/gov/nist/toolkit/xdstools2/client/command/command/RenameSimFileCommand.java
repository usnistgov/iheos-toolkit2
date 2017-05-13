package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.xdstools2.client.initialization.XdsTools2Presenter;
import gov.nist.toolkit.xdstools2.shared.command.request.RenameSimFileRequest;

/**
 * Created by onh2 on 11/1/16.
 */
public abstract class RenameSimFileCommand extends GenericCommand<RenameSimFileRequest,Void>{
    @Override
    public void run(RenameSimFileRequest request) {
        XdsTools2Presenter.data().getToolkitServices().renameSimFile(request,this);
    }
}
