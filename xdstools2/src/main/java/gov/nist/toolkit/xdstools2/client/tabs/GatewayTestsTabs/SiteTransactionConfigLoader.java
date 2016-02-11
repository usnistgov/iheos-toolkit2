package gov.nist.toolkit.xdstools2.client.tabs.GatewayTestsTabs;

import com.google.gwt.user.client.rpc.AsyncCallback;
import gov.nist.toolkit.sitemanagement.client.TransactionOfferings;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.ToolkitServiceAsync;

/**
 *
 */
public class SiteTransactionConfigLoader {
    ToolkitServiceAsync toolkitService;

    public SiteTransactionConfigLoader(ToolkitServiceAsync toolkitService) {
        this.toolkitService = toolkitService;
    }

    public void load(final CompletionHandler<TransactionOfferings> handler) {
        try {
            toolkitService.getTransactionOfferings(new AsyncCallback<TransactionOfferings>() {

                public void onFailure(Throwable caught) {
                    new PopupMessage("SiteTransactionConfigLoader failed: " + caught.getMessage());
                }

                public void onSuccess(TransactionOfferings to) {
                    handler.OnCompletion(to);
                }

            });
        } catch (Exception e) {
            new PopupMessage("SiteTransactionConfigLoader failed: " + e.getMessage());
        }

    }
}
