package gov.nist.toolkit.xdstools2.client.tabs.GatewayTestsTabs;

import com.google.gwt.user.client.rpc.AsyncCallback;
import gov.nist.toolkit.sitemanagement.client.TransactionOfferings;
import gov.nist.toolkit.xdstools2.client.PopupMessage;

import static gov.nist.toolkit.xdstools2.client.ToolWindow.toolkitService;

/**
 *
 */
public class TransactionOfferingsLoader {
//    ToolkitServiceAsync toolkitService;

    public TransactionOfferingsLoader(/*ToolkitServiceAsync toolkitService*/) {
//        this.toolkitService = toolkitService;
    }

    public void run(final ServiceCallCompletionHandler<TransactionOfferings> transOff) {

        try {
            toolkitService.getTransactionOfferings(new AsyncCallback<TransactionOfferings>() {

                public void onFailure(Throwable caught) {
                    new PopupMessage("Error: " + caught.getMessage());
                }

                public void onSuccess(TransactionOfferings to) {
                    transOff.onCompletion(to);
                }

            });
        } catch (Exception e) {
            new PopupMessage("Error: " + e.getMessage());
        }
    }

}
