package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.testkitutilities.client.TestCollectionDefinitionDAO;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.GetCollectionRequest;

import java.util.List;

/**
 * Created by onh2 on 11/7/16.
 */
public abstract class GetTestCollectionsCommand extends GenericCommand<GetCollectionRequest, List<TestCollectionDefinitionDAO>> {
    @Override
    public void run(GetCollectionRequest var1) {
        ClientUtils.INSTANCE.getToolkitServices().getTestCollections(var1, this);
    }
}
