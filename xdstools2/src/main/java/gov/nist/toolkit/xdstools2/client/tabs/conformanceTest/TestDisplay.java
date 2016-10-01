package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.session.client.TestOverviewDTO;
import gov.nist.toolkit.xdstools2.client.widgets.LaunchInspectorClickHandler;

/**
 *
 */
public class TestDisplay extends FlowPanel {
    TestDisplayHeader header = new TestDisplayHeader();
    FlowPanel body = new FlowPanel();
    DisclosurePanel panel = new DisclosurePanel(header);
    TestDisplayGroup testDisplayGroup;
    String testSession;
    private TestContext testContext;
    private TestRunner testRunner;
    private TestContextDisplay testContextDisplay;

    public TestDisplay(TestDisplayGroup testDisplayGroup, String testSession, TestRunner testRunner, TestContext testContext, TestContextDisplay testContextDisplay) {
        this.testSession = testSession;
        this.testRunner = testRunner;
        this.testDisplayGroup = testDisplayGroup;
        this.testContext = testContext;
        this.testContextDisplay = testContextDisplay;
        header.fullWidth();
        panel.setWidth("100%");
        panel.add(body);
    }

    public void build(TestOverviewDTO testOverview, TestInstance testInstance) {
        testDisplayGroup.put(testInstance, this);
        boolean isNew = !testDisplayExists(testOverview.getTestInstance());
        TestDisplay testDisplay = buildTestDisplay(testDisplayGroup, testOverview.getTestInstance());
        TestDisplayHeader header = testDisplay.header;
        FlowPanel body = testDisplay.body;

        header.clear();
        body.clear();

        if (testOverview.isRun()) {
            if (testOverview.isPass()) header.setBackgroundColorSuccess();
            else header.setBackgroundColorFailure();
        } else header.setBackgroundColorNotRun();

        HTML testHeader = new HTML("Test: " + testOverview.getName() + " - " +testOverview.getTitle());
        testHeader.addStyleName("test-title");
        header.add(testHeader);
        header.add(new HTML(testOverview.getLatestSectionTime()));
        if (testOverview.isRun()) {
            Image status = (testOverview.isPass()) ?
                    new Image("icons2/correct-24.png")
                    :
                    new Image("icons/ic_warning_black_24dp_1x.png");
            status.addStyleName("right");
            status.addStyleName("iconStyle");
            header.add(status);
        }

        Image play = new Image("icons2/play-24.png");
        play.setStyleName("iconStyle");
        play.setTitle("Run");
        play.addClickHandler(new RunClickHandler(testRunner, testInstance, testContext, testContextDisplay));
        header.add(play);
        if (testOverview.isRun()) {
            Image delete = new Image("icons2/garbage-24.png");
            delete.addStyleName("right");
            delete.addStyleName("iconStyle");
            delete.addClickHandler(new ConformanceTestTab.DeleteClickHandler(testOverview.getTestInstance()));
            delete.setTitle("Delete Log");
            header.add(delete);

            Image inspect = new Image("icons2/visible-32.png");
            inspect.addStyleName("right");
//			inspect.addStyleName("iconStyle");
            inspect.addClickHandler(new LaunchInspectorClickHandler(testOverview.getTestInstance(), testSession, testContext.getCurrentSiteSpec()));
            inspect.setTitle("Inspect results");
            header.add(inspect);
        }

        body.add(new HTML(testOverview.getDescription()));

        // display an interaction sequence diagram
        if (testOverview.isRun()) {
            displayInteractionDiagram(testOverview, body);
        }

        // display sections within test
        displaySections(testOverview, body);

        if (!isNew)
            updateTestsOverviewHeader();

    }

    private boolean testDisplayExists(TestInstance testInstance) { return testDisplayGroup.containsKey(testInstance); }

    private TestDisplay buildTestDisplay(TestDisplayGroup testDisplayGroup, TestInstance testInstance) {
        if (testDisplayExists(testInstance)) return testDisplayGroup.get(testInstance);
        return new TestDisplay(testDisplayGroup, testInstance);
    }

}
