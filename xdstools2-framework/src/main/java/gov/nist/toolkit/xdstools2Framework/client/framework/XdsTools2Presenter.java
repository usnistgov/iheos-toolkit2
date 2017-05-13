package gov.nist.toolkit.xdstools2Framework.client.framework;


import javax.inject.Inject;

/**
 * this is a singleton because at the moment there are 282 static references
 * to the data() method and I'm lazy.
 */
public class XdsTools2Presenter {
    private static XdsTools2Presenter INSTANCE;
    private boolean enableHomeTab = true;

    @Inject
    EnvironmentState environmentState;

    @Inject
    TestSessionManager testSessionManager;

    XdsTools2AppView view;

    public XdsTools2Presenter() {
        INSTANCE = this;
    }

    public void setView(XdsTools2AppView view) { this.view = view; }

    // This is a BiG PROBLEM
    public static FrameworkSupport data() { return INSTANCE.theFramework; }

    public void blockHomeTab() { enableHomeTab = false; }

    public void run() {
        new InitializationCommand() {

            @Override
            public void onComplete(InitializationResponse var1) {
                // default environment
                // environment names
                // test session names
                theFramework.setToolkitName(var1.getServletContextName());
                environmentState.setEnvironmentNameChoices(var1.getEnvironments());
                if (environmentState.getEnvironmentName() == null)
                    environmentState.setEnvironmentName(var1.getDefaultEnvironment());
                testSessionManager.setTestSessions(var1.getTestSessions());
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
        }.run(theFramework.getCommandContext());

    }

    private void run2() {
        view.buildTabsWrapper();

        // If using ConfActor activity then home tab is a distraction
        HomeTab homeTab = new HomeTab();
        if (!enableHomeTab) {
            homeTab.setDisplayTab(false);
        }
        homeTab.onTabLoad(false, "Home");

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


        String currentTestSession = testSessionManager.fromCookie();
        if (currentTestSession == null)
            currentTestSession = "default";
        if (testSessionManager.isLegalTestSession(currentTestSession)) {
            // Don't overwrite initialization by ConfActor activity
            if (testSessionManager.getCurrentTestSession() == null)
                testSessionManager.setCurrentTestSession(currentTestSession);
        }
        theFramework.reloadTransactionOfferings();
    }

}
