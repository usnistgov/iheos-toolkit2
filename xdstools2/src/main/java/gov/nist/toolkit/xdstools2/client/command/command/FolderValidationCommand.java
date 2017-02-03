package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.FoldersRequest;

import java.util.List;

/**
 * Created by onh2 on 11/14/16.
 */
public abstract class FolderValidationCommand extends GenericCommand<FoldersRequest,List<Result>>{
    @Override
    public void run(FoldersRequest var1) {
        ClientUtils.INSTANCE.getToolkitServices().folderValidation(var1,this);
    }
}
