package gov.nist.toolkit.xdstools2.client.tabs.GatewayTestsTabs;

import gov.nist.toolkit.sitemanagement.client.TransactionOfferings;
import gov.nist.toolkit.xdstools2.client.command.command.GetTransactionOfferingsCommand;
import gov.nist.toolkit.xdstools2.client.initialization.FrameworkInitialization;

/**
 *
 */
public class TransactionOfferingsLoader {

    public TransactionOfferingsLoader() {
    }

    public void run(final ServiceCallCompletionHandler<TransactionOfferings> transOff) {

        new GetTransactionOfferingsCommand() {
            @Override
            public void onComplete(TransactionOfferings var1) {
                transOff.onCompletion(var1);
            }
        }.run(FrameworkInitialization.data().getCommandContext());
    }

}
