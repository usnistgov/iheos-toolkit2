package gov.nist.toolkit.desktop.client.commands;

import gov.nist.toolkit.desktop.client.ClientUtils;
import gov.nist.toolkit.desktop.client.commands.util.GenericCommand;
import gov.nist.toolkit.desktop.shared.command.request.GetDocumentsRequest;
import gov.nist.toolkit.results.client.Result;

import java.util.List;

/**
 * Created by onh2 on 11/3/16.
 */
public abstract class GetDocumentsCommand extends GenericCommand<GetDocumentsRequest,List<Result>> {
    @Override
    public void run(GetDocumentsRequest var1) {
        ClientUtils.INSTANCE.getToolkitServices().getDocuments(var1,this);
    }
}
