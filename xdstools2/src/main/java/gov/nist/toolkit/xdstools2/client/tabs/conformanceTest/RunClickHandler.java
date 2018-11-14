package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import gov.nist.toolkit.results.client.TestInstance;

/**
 *
 */
public class RunClickHandler implements ClickHandler {
    private TestInstance testInstance;
    private TestRunner testRunner;
    private TestContext testContext;
    private TestContextView testContextView;
    private boolean ignoreSiteSelection = false;
    private Controller controller = null;
    private OnTestRunComplete onRunComplete;

    RunClickHandler(TestRunner testRunner, TestInstance testInstance, TestContext testContext, TestContextView testContextView, Controller controller, boolean ignoreSiteSelection, OnTestRunComplete onRunComplete) {
        this.testRunner = testRunner;
        this.testInstance = testInstance;
        this.testContext = testContext;
        this.testContextView = testContextView;
        this.controller = controller;
        this.ignoreSiteSelection = ignoreSiteSelection;
        this.onRunComplete = onRunComplete;
    }

    RunClickHandler(TestRunner testRunner, TestInstance testInstance, TestContext testContext, TestContextView testContextView, Controller controller, OnTestRunComplete onRunComplete) {
        this.testRunner = testRunner;
        this.testInstance = testInstance;
        this.testContext = testContext;
        this.controller = controller;
        this.testContextView = testContextView;
        this.onRunComplete = onRunComplete;
    }

    @Override
    public void onClick(ClickEvent clickEvent) {
        clickEvent.preventDefault();
        clickEvent.stopPropagation();

        String msg = testContext.verifyTestContext(ignoreSiteSelection);
        if (msg == null)
            testRunner.runTest(testInstance, null, null, onRunComplete);
        else {
            if (testContextView != null)
                testContextView.launchDialog(msg);
            else
                Window.alert(msg);
        }
//        if (controller != null) {
//            controller.getRefreshTestCollectionClickHandler().onClick(null);
//        }
    }
}
