package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.registrymetadata.client.MetadataCollection;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.GetMetadataFromRegIndexRequest;
import gov.nist.toolkit.xdstools2.shared.command.request.GetTestsOverviewRequest;


public abstract class GetMetadataFromRegIndexCommand extends GenericCommand<GetMetadataFromRegIndexRequest,MetadataCollection> {
    @Override
    public void run(GetTestsOverviewRequest request) {
        ClientUtils.INSTANCE.getToolkitServices().getActorTestProgress(request,this);
    }
}
