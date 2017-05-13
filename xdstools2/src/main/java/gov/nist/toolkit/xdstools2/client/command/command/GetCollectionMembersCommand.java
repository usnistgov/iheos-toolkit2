package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.xdstools2.client.initialization.XdsTools2Presenter;
import gov.nist.toolkit.xdstools2.shared.command.request.GetCollectionRequest;

import java.util.List;

/**
 * Created by onh2 on 11/7/16.
 */
public abstract class GetCollectionMembersCommand extends GenericCommand<GetCollectionRequest, List<TestInstance>> {
    @Override
    public void run(GetCollectionRequest var1) {
        XdsTools2Presenter.data().getToolkitServices().getCollectionMembers(var1, this);
    }
}
