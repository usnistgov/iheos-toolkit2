package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.session.client.logtypes.SectionOverviewDTO;
import gov.nist.toolkit.session.client.logtypes.TestOverviewDTO;
import gov.nist.toolkit.xdstools2.client.widgets.LaunchInspectorClickHandler;

/**
 * Display of a single test including its sections.
 * This is the View and Presentation.
 */
public class TestDisplay  implements IsWidget {
    private TestDisplayGroup testDisplayGroup;
    private TestContext testContext;
    private TestRunner testRunner;
    private TestContextView testContextView;
    private TestInstance testInstance;
    private boolean allowDelete= true;
    private boolean allowRun = true;
    private boolean showValidate = false;
    private TestDisplayView view = new TestDisplayView();

    public TestDisplay(TestInstance testInstance, TestDisplayGroup testDisplayGroup, TestRunner testRunner, TestContext testContext, TestContextView testContextView) {
        this.testInstance = testInstance;
        this.testRunner = testRunner;
        this.testDisplayGroup = testDisplayGroup;
        this.testContext = testContext;
        this.testContextView = testContextView;
    }

    public void allowDelete(boolean allowDelete) {
        this.allowDelete = allowDelete;
    }

    public void allowRun(boolean allowRun) {
        this.allowRun = allowRun;
    }

    public void display(TestOverviewDTO testOverview) {

        if (testOverview.isRun()) {
            if (testOverview.isPass()) view.labelSuccess();
            else view.labelFailure();
        } else view.labelNotRun();

        view.setTestTitle("Test: " + testOverview.getName() + " - " +testOverview.getTitle());
        view.setTime(testOverview.getLatestSectionTime());

        if (allowRun) view.setPlay("Run", new RunClickHandler(testRunner, testInstance, testContext, testContextView));

        if (testOverview.isRun()) {
            if (allowDelete) view.setDelete("Delete Log", new DeleteClickHandler(testDisplayGroup, testContext, testRunner, testInstance));
            view.setInspect("Inspect results", new LaunchInspectorClickHandler(testOverview.getTestInstance(), testContext.getTestSession(), testContext.getCurrentSiteSpec()));
        }

        view.setDescription(testOverview.getDescription());

        // build an interaction sequence diagram
        view.setInteractionDiagram(new InteractionDiagramDisplay(testOverview));

        // build sections within test
        view.clearSections();
        for (String sectionName : testOverview.getSectionNames()) {
            SectionOverviewDTO sectionOverview = testOverview.getSectionOverview(sectionName);
            TestSectionDisplay sectionComponent = new TestSectionDisplay(testContext.getTestSession(), testOverview.getTestInstance(), sectionOverview, testRunner, allowRun);
            view.addSection(sectionComponent.asWidget());
        }

        view.display();
    }

    public void showValidate(boolean showValidate) {
        this.showValidate = showValidate;
    }

    public Widget asWidget() { return view; }
}
