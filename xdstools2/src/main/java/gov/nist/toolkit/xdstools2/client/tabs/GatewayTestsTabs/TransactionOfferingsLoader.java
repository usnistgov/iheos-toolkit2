package gov.nist.toolkit.xdstools2.client.tabs.GatewayTestsTabs;

import gov.nist.toolkit.sitemanagement.client.TransactionOfferings;
import gov.nist.toolkit.xdstools2.client.Xdstools2;
import gov.nist.toolkit.xdstools2.client.command.command.GetTransactionOfferingsCommand;

/**
 *
 */
public class TransactionOfferingsLoader {
//    ToolkitServiceAsync toolkitService;

    public TransactionOfferingsLoader(/*ToolkitServiceAsync toolkitService*/) {
//        this.toolkitService = toolkitService;
    }

    public void run(final ServiceCallCompletionHandler<TransactionOfferings> transOff) {

        new GetTransactionOfferingsCommand(Xdstools2.getHomeTab()) {
            @Override
            public void onComplete(TransactionOfferings var1) {
                transOff.onCompletion(var1);
            }
        }.run(Xdstools2.getHomeTab().getCommandContext());
    }

}
