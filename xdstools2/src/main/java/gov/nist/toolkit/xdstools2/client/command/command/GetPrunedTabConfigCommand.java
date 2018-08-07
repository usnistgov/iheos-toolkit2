package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.xdstools2.client.tabs.conformanceTest.TabConfig;
import gov.nist.toolkit.xdstools2.client.tabs.conformanceTest.UserTestCollection;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.GetTabConfigRequest;

public abstract class GetPrunedTabConfigCommand extends GenericCommand<GetTabConfigRequest,UserTestCollection>{
    @Override
    public void run(GetTabConfigRequest req) {
          ClientUtils.INSTANCE.getToolkitServices().getPrunedToolTabConfig(req, this);
    }
}
