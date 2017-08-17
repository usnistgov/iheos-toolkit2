package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.interactiondiagram.client.widgets.InteractionDiagram;
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
    private InteractionDiagramDisplay diagramDisplay;

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

       view.setTestKitSourceIcon(testOverview.getTestKitSource(), testOverview.getTestKitSection());

        view.setTestTitle("Test: " + testOverview.getName() + " - " +testOverview.getTitle());
        view.setTime(testOverview.getLatestSectionTime());

        if (allowRun) view.setPlay("Run", new RunClickHandler(testRunner, testInstance, testContext, testContextView));

        if (testOverview.isRun()) {
            if (allowDelete) view.setDelete("Delete Log", new DeleteClickHandler(testDisplayGroup, testContext, testRunner, testInstance));
            view.setInspect("Inspect results", new LaunchInspectorClickHandler(testOverview.getTestInstance(), testContext.getTestSession(), testContext.getCurrentSiteSpec()));
        }

        view.setDescription(testOverview.getDescription());

        // Test level diagram -- normally comes from the TestDisplayView InteractionDiagram member
        boolean firstTransactionRepeatingTooManyTimes = InteractionDiagram.isFirstTransactionRepeatingTooManyTimes(testOverview);
        if (getDiagramDisplay()!=null && !firstTransactionRepeatingTooManyTimes) {
            view.setInteractionDiagram(getDiagramDisplay().render());
        }

        // build sections within test
        view.clearSections();
        for (String sectionName : testOverview.getSectionNames()) {
            SectionOverviewDTO sectionOverview = testOverview.getSectionOverview(sectionName);
            // Section level diagram
            if (getDiagramDisplay()!=null && firstTransactionRepeatingTooManyTimes) {
               InteractionDiagramDisplay diagramDisplay = getDiagramDisplay().copy();
               diagramDisplay.getTestOverviewDTO().getSectionNames().add(sectionName);
               diagramDisplay.getTestOverviewDTO().getSections().put(sectionName,sectionOverview);

                TestSectionDisplay sectionComponent = new TestSectionDisplay(testContext.getTestSession(), testOverview.getTestInstance(), sectionOverview, testRunner, allowRun, diagramDisplay);
                view.addSection(sectionComponent.asWidget());
            } else {
                TestSectionDisplay sectionComponent = new TestSectionDisplay(testContext.getTestSession(), testOverview.getTestInstance(), sectionOverview, testRunner, allowRun, null);
                view.addSection(sectionComponent.asWidget());
            }

        }

        view.display();
    }

    public void showValidate(boolean showValidate) {
        this.showValidate = showValidate;
    }

    public Widget asWidget() { return view; }

    public TestDisplayView getView() {
        return view;
    }

    public void addExtraStyle(String name) {
        view.addExtraStyle(name);
    }
    public void removeExtraStyle(String name) {
        view.removeExtraStyle(name);
    }

    public InteractionDiagramDisplay getDiagramDisplay() {
        return diagramDisplay;
    }

    public void setDiagramDisplay(InteractionDiagramDisplay diagramDisplay) {
        this.diagramDisplay = diagramDisplay;
    }
}
