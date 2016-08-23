package gov.nist.toolkit.xdstools2.client.tabs.GatewayTestsTabs;

import gov.nist.toolkit.sitemanagement.client.TransactionOfferings;
import gov.nist.toolkit.xdstools2.client.Xdstools2;
import gov.nist.toolkit.xdstools2.client.command.command.GetTransactionOfferingsCommand;

/**
 *
 */
public class SiteTransactionConfigLoader {
//    ToolkitServiceAsync toolkitService;

    public SiteTransactionConfigLoader(/*ToolkitServiceAsync toolkitService*/) {
//        this.toolkitService = toolkitService;
    }

    public void load(final CompletionHandler<TransactionOfferings> handler) {
        new GetTransactionOfferingsCommand(Xdstools2.getHomeTab()) {
            @Override
            public void onComplete(TransactionOfferings var1) {
                handler.OnCompletion(var1);
            }
        }.run(Xdstools2.getHomeTab().getCommandContext());
    }
}
