package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.session.client.logtypes.SectionOverviewDTO;
import gov.nist.toolkit.session.client.logtypes.TestOverviewDTO;
import gov.nist.toolkit.xdstools2.client.widgets.LaunchInspectorClickHandler;

/**
 * Display of a single test
 */
public class TestDisplay extends FlowPanel {
    private TestDisplayHeader header = new TestDisplayHeader();
    private FlowPanel body = new FlowPanel();
    private DisclosurePanel panel = new DisclosurePanel(header);
    private TestDisplayGroup testDisplayGroup;
    private TestContext testContext;
    private TestRunner testRunner;
    private TestContextView testContextView;
    private TestInstance testInstance;
    private boolean allowDelete= true;
    private boolean allowRun = true;
    private boolean startExternally = false;

    public TestDisplay(TestInstance testInstance, TestDisplayGroup testDisplayGroup, TestRunner testRunner, TestContext testContext, TestContextView testContextView) {
        this.testInstance = testInstance;
        this.testRunner = testRunner;
        this.testDisplayGroup = testDisplayGroup;
        this.testContext = testContext;
        this.testContextView = testContextView;
        header.fullWidth();
        panel.setWidth("100%");
        panel.add(body);
        add(panel);
    }

    public void allowDelete(boolean allowDelete) {
        this.allowDelete = allowDelete;
    }

    public void allowRun(boolean allowRun) {
        this.allowRun = allowRun;
    }

    public void startExternally(boolean startExternally) {
        this.startExternally = startExternally;
        this.allowRun = !startExternally;
    }

    public void display(TestOverviewDTO testOverview) {
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

        if (allowRun) {
            Image play = new Image("icons2/play-24.png");
            play.setStyleName("iconStyle");
            play.setTitle("Run");
            play.addClickHandler(new RunClickHandler(testRunner, testInstance, testContext, testContextView));
            header.add(play);
        }

        if (testOverview.isRun()) {
            if (allowDelete) {
                Image delete = new Image("icons2/garbage-24.png");
                delete.addStyleName("right");
                delete.addStyleName("iconStyle");
                delete.addClickHandler(new DeleteClickHandler(testDisplayGroup, testContext, testRunner, testInstance));
                delete.setTitle("Delete Log");
                header.add(delete);
            }

            Image inspect = new Image("icons2/visible-32.png");
            inspect.addStyleName("right");
//			inspect.addStyleName("iconStyle");
            inspect.addClickHandler(new LaunchInspectorClickHandler(testOverview.getTestInstance(), testContext.getTestSession(), testContext.getCurrentSiteSpec()));
            inspect.setTitle("Inspect results");
            header.add(inspect);
        }

        if (startExternally) {
            Anchor tag = new Anchor("Validate");
            tag.addClickHandler(new ValidateClickHandler());
            header.add(tag);
        }

        body.add(new HTML(testOverview.getDescription()));

        // display an interaction sequence diagram
        if (testOverview.isRun()) {
            body.add(new InteractionDiagramDisplay(testOverview));
        }

        // display sections within test
        for (String sectionName : testOverview.getSectionNames()) {
            SectionOverviewDTO sectionOverview = testOverview.getSectionOverview(sectionName);
            TestSectionComponent sectionComponent = new TestSectionComponent(testContext.getTestSession(), testOverview.getTestInstance(), sectionOverview, testRunner, allowRun);
            body.add(sectionComponent.asWidget());
        }
    }
}
