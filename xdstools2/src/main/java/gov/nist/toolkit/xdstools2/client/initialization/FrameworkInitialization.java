package gov.nist.toolkit.xdstools2.client.initialization;

import gov.nist.toolkit.xdstools2.client.command.command.InitializationCommand;
import gov.nist.toolkit.xdstools2.client.tabs.EnvironmentState;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;
import gov.nist.toolkit.xdstools2.shared.command.InitializationResponse;

/**
 * This is init stuff that is being pulled out of Xdstools2 so it can be shared with Desktop
 * Xdstools2 is the old framework and Desktop is the new one.
 */
public class FrameworkInitialization {
    static private FrameworkSupport theFramework;

    public static FrameworkSupport data() { return theFramework; }

    static public void init(FrameworkSupport framework) {
        theFramework = framework;
    }

    public void run() {
        new InitializationCommand() {

            @Override
            public void onComplete(InitializationResponse var1) {
                // default environment
                // environment names
                // test session names
                theFramework.setToolkitName(var1.getServletContextName());
                EnvironmentState environmentState= ClientUtils.INSTANCE.getEnvironmentState();
                environmentState.setEnvironmentNameChoices(var1.getEnvironments());
                if (environmentState.getEnvironmentName() == null)
                    environmentState.setEnvironmentName(var1.getDefaultEnvironment());
                theFramework.getTestSessionManager().setTestSessions(var1.getTestSessions());
                theFramework.setToolkitBaseUrl(var1.getToolkitBaseUrl());
                theFramework.setWikiBaseUrl(var1.getWikiBaseUrl());
                run2();  // cannot be run until this completes
            }

            // this is included because even if init fails (bad EC location for example)
            // startup must continue
            @Override
            public void onFailure(Throwable throwable) {
                String msg = throwable.getMessage();
                if (msg == null)
                    msg = this.getClass().getName();
                new PopupMessage("Request to server failed: " + msg);

                run2();  // cannot be run until this completes
            }
        }.run(ClientUtils.INSTANCE.getCommandContext());  // command context will be ignored by this cmd

    }

    private void run2() {
        theFramework.buildTabsWrapper();

        // If using ConfActor activity then home tab is a distraction
        theFramework.displayHomeTab();

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


        String currentTestSession = theFramework.getTestSessionManager().fromCookie();
        if (currentTestSession == null)
            currentTestSession = "default";
        if (theFramework.getTestSessionManager().isLegalTestSession(currentTestSession)) {
            // Don't overwrite initialization by ConfActor activity
            if (theFramework.getTestSessionManager().getCurrentTestSession() == null)
                theFramework.getTestSessionManager().setCurrentTestSession(currentTestSession);
        }
        theFramework.reloadTransactionOfferings();
    }

}
