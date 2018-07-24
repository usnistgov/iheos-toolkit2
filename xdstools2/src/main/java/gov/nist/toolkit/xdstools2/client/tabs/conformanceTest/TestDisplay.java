package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.HandlerRegistration;
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
    private boolean allowValidate = false;
    private TestDisplayView view = new TestDisplayView();
    private InteractionDiagramDisplay diagramDisplay;
    private Controller controller;

    public TestDisplay(TestInstance testInstance, TestDisplayGroup testDisplayGroup, TestRunner testRunner, TestContext testContext, TestContextView testContextView, Controller controller) {
        this.testInstance = testInstance;
        this.testRunner = testRunner;
        this.testDisplayGroup = testDisplayGroup;
        this.testContext = testContext;
        this.testContextView = testContextView;
        this.controller = controller;
    }

    void allowDelete(boolean allowDelete) {
        this.allowDelete = allowDelete;
    }

    void allowRun(boolean allowRun) {
        this.allowRun = allowRun;
    }

    public void allowValidate(boolean allowValidate) {
        if (allowValidate) allowRun = false;
        this.allowValidate = allowValidate;
    }

    public void display(TestOverviewDTO testOverview) {

        if (testOverview.isRun()) {
            if (testOverview.isPass()) view.labelSuccess();
            else view.labelFailure();
        } else view.labelNotRun();

       view.setTestKitSourceIcon(testOverview.getTestKitSource(), testOverview.getTestKitSection());

       if (testOverview.isTls())
           view.labelTls();

        view.setTestTitle("Test: " + testOverview.getName() + " - " +testOverview.getTitle());
        view.setTime(testOverview.getLatestSectionTime());

        if (allowRun) view.setPlay("Run", new RunClickHandler(testRunner, testInstance, testContext, testContextView, controller));
        if (allowValidate) view.setValidate("Validate", new RunClickHandler(testRunner, testInstance, testContext, testContextView, controller, true));

        if (testOverview.isRun()) {
            if (allowDelete) view.setDelete("Delete Log", new DeleteClickHandler(testDisplayGroup, testContext, testRunner, testInstance));
            view.setInspect("Inspect results", new LaunchInspectorClickHandler(testOverview.getTestInstance(), testContext.getTestSession(), testContext.getCurrentSiteSpec()));
        }

        view.setDescription(testOverview.getDescription());

        // Test level diagram -- normally comes from the TestDisplayView InteractionDiagram member
        boolean firstTransactionRepeatingTooManyTimes = false;
        if (getDiagramDisplay()!=null) {
            firstTransactionRepeatingTooManyTimes = InteractionDiagram.isFirstTransactionRepeatingTooManyTimes(testOverview);
            if (getDiagramDisplay() != null && !firstTransactionRepeatingTooManyTimes) {
                view.setInteractionDiagram(getDiagramDisplay().render());
            }
        }

        // build sections within test
        view.clearSections();
        for (String sectionName : testOverview.getSectionNames()) {
            SectionOverviewDTO sectionOverview = testOverview.getSectionOverview(sectionName);
            // Section level diagram
            if (getDiagramDisplay()!=null && firstTransactionRepeatingTooManyTimes) {
               InteractionDiagramDisplay sectionDiagramDisplay = getDiagramDisplay().copy();
               sectionDiagramDisplay.getTestOverviewDTO().getSectionNames().add(sectionName);
               sectionDiagramDisplay.getTestOverviewDTO().getSections().put(sectionName,sectionOverview);

                TestSectionDisplay sectionComponent = new TestSectionDisplay(testContext.getTestSession(), testOverview.getTestInstance(), sectionOverview, testRunner, allowRun, sectionDiagramDisplay);
                view.addSection(sectionComponent);
            } else {
                TestSectionDisplay sectionComponent = new TestSectionDisplay(testContext.getTestSession(), testOverview.getTestInstance(), sectionOverview, testRunner, allowRun, null);
                view.addSection(sectionComponent);
            }

        }

        view.display();
    }

    public void showValidate(boolean showValidate) {
        this.allowValidate = showValidate;
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
