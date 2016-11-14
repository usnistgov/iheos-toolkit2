package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.MpqFindDocumentsRequest;

import java.util.List;

/**
 * Created by onh2 on 11/14/16.
 */
public abstract class MpqFindDocumentsCommand extends GenericCommand<MpqFindDocumentsRequest,List<Result>>{
    @Override
    public void run(MpqFindDocumentsRequest var1) {
        ClientUtils.INSTANCE.getToolkitServices().mpqFindDocuments(var1,this);
    }
}
