package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.UpdateDocumentEntryRequest;

import java.util.List;

/**
 * Created by skb1 on 3/27/18.
 */
public abstract class UpdateDocumentEntryCommand extends GenericCommand<UpdateDocumentEntryRequest,List<Result>>{
    @Override
    public void run(UpdateDocumentEntryRequest request) {
        ClientUtils.INSTANCE.getToolkitServices().updateDocumentEntry(request, this);
    }
}
