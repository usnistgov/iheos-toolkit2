package gov.nist.toolkit.desktop.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import gov.nist.toolkit.desktop.client.commands.GetTransactionOfferingsCommand;
import gov.nist.toolkit.desktop.client.commands.InitializationCommand;
import gov.nist.toolkit.desktop.client.events.ToolkitEventBus;
import gov.nist.toolkit.desktop.client.events.TransactionOfferingsReloadedEvent;
import gov.nist.toolkit.desktop.client.legacy.genericQueryTab.GenericQueryTab;
import gov.nist.toolkit.desktop.client.legacy.widgets.PopupMessage;
import gov.nist.toolkit.desktop.shared.command.InitializationResponse;
import gov.nist.toolkit.sitemanagement.client.TransactionOfferings;
import org.eclipse.jetty.server.Server;

import javax.inject.Inject;

/**
 * Singleton
 */
public class ServerContext {
    public static ServerContext INSTANCE;
    private String toolkitName;
    private String toolkitBaseUrl;
    private String wikiBaseUrl;
    private TransactionOfferings transactionOfferings;

    @Inject
    ToolkitEventBus eventBus;

    public ServerContext() {
        INSTANCE = this;
        GWT.log("ServerContext loading");

        // Delay this until all initialization is done
        Timer t = new Timer() {

            @Override
            public void run() {
                initialize();
            }
        };

        t.schedule(3);

    }

    private void initialize() {
        new InitializationCommand() {

            @Override
            public void onComplete(InitializationResponse var1) {
                // default environment
                // environment names
                // test session names
                toolkitName = var1.getServletContextName();
                toolkitBaseUrl = var1.getToolkitBaseUrl();
                wikiBaseUrl = var1.getWikiBaseUrl();
                init2();  // cannot be run until this completes
            }

            // this is included because even if init fails (bad EC location for example)
            // startup must continue
            @Override
            public void onFailure(Throwable throwable) {
                String msg = throwable.getMessage();
                if (msg == null)
                    msg = this.getClass().getName();
                new PopupMessage("Request to server failed: " + msg);

                init2();  // cannot be run until this completes
            }
        }.run(ClientUtils.INSTANCE.getCurrentCommandContext());  // command context will be ignored by this cmd
    }

    private void init2() {

//        // If using ConfActor activity then home tab is a distraction
//        if (!displayHomeTab)
//            ht.setDisplayTab(false);
//        ht.onTabLoad(false, "Home");

//        History.addValueChangeHandler(new ValueChangeHandler<String>() {
//            public void onValueChange(ValueChangeEvent<String> event) {
//                String historyToken = event.getValue();
//
//                // Parse the history token
//                try {
//                    if (historyToken.equals("mv")) {
//                        new MessageValidatorTab().onTabLoad(true, "MsgVal");
//                    }
//
//                } catch (IndexOutOfBoundsException e) {
//                    TabContainer.selectTab(0);
//                }
//            }
//        });

        reloadTransactionOfferings();
    }

    public void reloadTransactionOfferings() {
        new GetTransactionOfferingsCommand() {

            @Override
            public void onComplete(TransactionOfferings transactionOfferings) {
                eventBus.fireEvent(new TransactionOfferingsReloadedEvent(transactionOfferings));
            }
        }.run(ClientUtils.INSTANCE.getCurrentCommandContext());

    }

    public String getToolkitName() {
        return toolkitName;
    }

    public String getToolkitBaseUrl() {
        return toolkitBaseUrl;
    }

    public String getWikiBaseUrl() {
        return wikiBaseUrl;
    }
}
