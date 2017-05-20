package gov.nist.toolkit.xdstools2Framework.client.framework;


import gov.nist.toolkit.toolkitFramework.client.commands.InitializationCommand;
import gov.nist.toolkit.toolkitFramework.client.environment.EnvironmentState;
import gov.nist.toolkit.toolkitFramework.client.events.SystemsNeedReloadingEvent;
import gov.nist.toolkit.toolkitFramework.client.injector.ToolkitEventBus;
import gov.nist.toolkit.toolkitFramework.client.testSession.TestSessionManager;
import gov.nist.toolkit.toolkitFramework.client.util.CurrentCommandContext;
import gov.nist.toolkit.toolkitFramework.client.widgets.PopupMessage;
import gov.nist.toolkit.toolkitFramework.shared.InitializationResponse;

import javax.inject.Inject;

/**
 * this is a singleton because at the moment there are 282 static references
 * to the data() method and I'm lazy.
 */
public class XdsTools2Presenter {
    private boolean enableHomeTab = true;
    private String toolkitName;
    private String toolkitBaseUrl;
    private String wikiBaseUrl;

    private EnvironmentState environmentState;

    private TestSessionManager testSessionManager;

    private ToolkitEventBus eventBus;

    private XdsTools2AppView view;

    @Inject
    public XdsTools2Presenter(EnvironmentState environmentState, TestSessionManager testSessionManager, XdsTools2AppView view, ToolkitEventBus eventBus) {
        this.environmentState = environmentState;
        this.testSessionManager = testSessionManager;
        this.view = view;
        this.eventBus = eventBus;
    }

    public void blockHomeTab() { enableHomeTab = false; }

    public void run() {
        new InitializationCommand() {

            @Override
            public void onComplete(InitializationResponse var1) {
                // default environment
                // environment names
                // test session names
                setToolkitName(var1.getServletContextName());
                environmentState.setEnvironmentNameChoices(var1.getEnvironments());
                if (environmentState.getEnvironmentName() == null)
                    environmentState.setEnvironmentName(var1.getDefaultEnvironment());
//                testSessionManager.setTestSessions(var1.getTestSessions());
                setToolkitBaseUrl(var1.getToolkitBaseUrl());
                setWikiBaseUrl(var1.getWikiBaseUrl());
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
        }.run(CurrentCommandContext.GET());

    }

    private void run2() {
        view.buildTabsWrapper();

        // If using ConfActor activity then home tab is a distraction
//        HomeTab homeTab = new HomeTab();
//        if (!enableHomeTab) {
//            homeTab.setDisplayTab(false);
//        }
//        homeTab.onTabLoad(false, "Home");

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
        eventBus.fireEvent(new SystemsNeedReloadingEvent());
    }

    public String getToolkitName() {
        return toolkitName;
    }

    public void setToolkitName(String toolkitName) {
        this.toolkitName = toolkitName;
    }

    public String getToolkitBaseUrl() {
        return toolkitBaseUrl;
    }

    public void setToolkitBaseUrl(String toolkitBaseUrl) {
        this.toolkitBaseUrl = toolkitBaseUrl;
    }

    public String getWikiBaseUrl() {
        return wikiBaseUrl;
    }

    public void setWikiBaseUrl(String wikiBaseUrl) {
        this.wikiBaseUrl = wikiBaseUrl;
    }

    public void setEnvironmentState(EnvironmentState environmentState) {
        this.environmentState = environmentState;
    }

    public void setTestSessionManager(TestSessionManager testSessionManager) {
        this.testSessionManager = testSessionManager;
    }

    public void setEventBus(ToolkitEventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void setView(XdsTools2AppView view) { this.view = view; }

}
