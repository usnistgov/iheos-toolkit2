package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.results.client.DocumentEntryDetail;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.GetOnDemandDocumentEntryDetailsRequest;

import java.util.List;

/**
 * Created by onh2 on 11/14/16.
 */
public abstract class GetOnDemandDocumentEntryDetailsCommand extends GenericCommand<GetOnDemandDocumentEntryDetailsRequest,List<DocumentEntryDetail>>{
    @Override
    public void run(GetOnDemandDocumentEntryDetailsRequest var1) {
        ClientUtils.INSTANCE.getToolkitServices().getOnDemandDocumentEntryDetails(var1,this);
    }
}
