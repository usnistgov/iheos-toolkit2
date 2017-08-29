package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.xdstools2.client.tabs.conformanceTest.TabConfig;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.GetTabConfigRequest;

/**
 * Created by skb1 on 7/20/17.
 */
public abstract class GetTabConfigCommand extends GenericCommand<GetTabConfigRequest,TabConfig>{
    @Override
    public void run(GetTabConfigRequest req) {
          ClientUtils.INSTANCE.getToolkitServices().getToolTabConfig(req, this);
    }
}
