package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.services.client.PifType;
import gov.nist.toolkit.xdstools2.client.tabs.conformanceTest.TabConfig;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.GetOrchestrationPifTypeRequest;
import gov.nist.toolkit.xdstools2.shared.command.request.GetTabConfigRequest;

/**
 * Created by skb1 on 11/16/18.
 */
public abstract class GetOrchestrationPifTypeCommand extends GenericCommand<GetOrchestrationPifTypeRequest,PifType>{
    @Override
    public void run(GetOrchestrationPifTypeRequest request) {
          ClientUtils.INSTANCE.getToolkitServices().getOrchestrationPifType(request, this);
    }
}
