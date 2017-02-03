package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.DeleteSimFileRequest;

/**
 * Command requesting the server to delete a simulator file.
 * Created by onh2 on 10/31/16.
 */
public abstract class DeleteSimFileCommand extends GenericCommand<DeleteSimFileRequest,Void>{
    @Override
    public void run(DeleteSimFileRequest var1) {
        ClientUtils.INSTANCE.getToolkitServices().deleteSimFile(var1,this);
    }
}
