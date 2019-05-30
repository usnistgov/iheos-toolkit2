package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.registrymetadata.client.MetadataCollection;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.GetMetadataFromRegIndexRequest;


public abstract class GetMetadataFromRegIndexCommand extends GenericCommand<GetMetadataFromRegIndexRequest,MetadataCollection> {
    @Override
    public void run(GetMetadataFromRegIndexRequest request) {
        ClientUtils.INSTANCE.getToolkitServices().getMetadataFromRegIndex(request, this);
    }
}
