package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import gov.nist.toolkit.results.client.TestInstance;

/**
 *
 */
public class RunClickHandler implements ClickHandler {
    private TestInstance testInstance;
    private TestRunner testRunner;
    private TestContext testContext;
    private TestContextView testContextView;

    RunClickHandler(TestRunner testRunner, TestInstance testInstance, TestContext testContext, TestContextView testContextView) {
        this.testRunner = testRunner;
        this.testInstance = testInstance;
        this.testContext = testContext;
        this.testContextView = testContextView;
    }

    @Override
    public void onClick(ClickEvent clickEvent) {
        clickEvent.preventDefault();
        clickEvent.stopPropagation();

        String msg = testContext.verifyTestContext();
        if (msg == null)
            testRunner.runTest(testInstance, null);
        else
            testContextView.launchDialog(msg);

    }
}
