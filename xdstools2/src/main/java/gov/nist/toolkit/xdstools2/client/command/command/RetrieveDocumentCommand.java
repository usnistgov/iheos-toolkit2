package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.RetrieveDocumentRequest;

import java.util.List;

/**
 * Created by onh2 on 11/4/16.
 */
public abstract class RetrieveDocumentCommand extends GenericCommand<RetrieveDocumentRequest,List<Result>>{
    @Override
    public void run(RetrieveDocumentRequest var1) {
        ClientUtils.INSTANCE.getToolkitServices().retrieveDocument(var1,this);
    }
}
