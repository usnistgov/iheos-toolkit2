package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import gov.nist.toolkit.xdstools2.client.ToolWindow;

/**
 * Test Context window shown at right of Conformance Test window.
 */
public class TestContextView implements ClickHandler {
    private TestContext testContext;
    private ToolWindow toolWindow;
    private HTML testSessionDescription;
    private SiteSelectionValidator siteSelectionValidator;

    public TestContextView(ToolWindow toolWindow, HTML testSessionDescription, TestContext testContext, SiteSelectionValidator siteSelectionValidator) {
        this.toolWindow = toolWindow;
        this.testSessionDescription = testSessionDescription;
        this.testContext = testContext;
        this.siteSelectionValidator = siteSelectionValidator;
    }

    void launchDialog(String msg) {
        TestContextDialog dialog = new TestContextDialog(toolWindow, testContext, siteSelectionValidator, msg);
        int left = Window.getClientWidth()/ 3;
        int top = Window.getClientHeight()/ 20;
        dialog.setPopupPosition(left, top);
        dialog.show();
    }

    @Override
    public void onClick(ClickEvent clickEvent) {
        launchDialog(null);
    }

    void updateTestingContextDisplay() {
        testSessionDescription.setHTML("Test Context<br />" +
                "Environment: " + toolWindow.getEnvironmentSelection() + "<br />" +
                "TestSesson: " + toolWindow.getCurrentTestSession() + "<br />" +
                "SUT: " + testContext.getSiteName());
    }


}
