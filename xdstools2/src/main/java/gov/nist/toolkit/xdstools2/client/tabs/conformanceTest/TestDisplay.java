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
    private TestContext testContext;
    private TestRunner testRunner;
    private TestContextView testContextView;
    private TestInstance testInstance;
    private boolean allowDelete= true;
    private boolean allowRun = true;
    private boolean allowValidate = false;
    private TestDisplayView view = new TestDisplayView();
    private Controller controller;

    public TestDisplay(TestInstance testInstance, TestRunner testRunner, TestContext testContext, TestContextView testContextView, Controller controller) {
        this.testInstance = testInstance;
        this.testRunner = testRunner;
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

    public void display(TestOverviewDTO testOverview, InteractionDiagramDisplay diagramDisplay) {
        if (testOverview.isRun()) {
            if (testOverview.isPass()) view.labelSuccess();
            else view.labelFailure();
        } else view.labelNotRun();

       view.setTestKitSourceIcon(testOverview.getTestKitSource(), testOverview.getTestKitSection());

        view.labelTls(testOverview.isTls());
        view.setTestTitle("Test: " + testOverview.getName() + " - " +testOverview.getTitle());
        view.setTime(testOverview.getLatestSectionTime());

        if (allowRun) view.setPlay("Run", new RunClickHandler(testRunner, testInstance, testContext, testContextView, controller, new OnTestRunComplete() {
            @Override
            void updateDisplay(TestOverviewDTO testOverviewDTO, InteractionDiagramDisplay diagramDisplay) {
                TestDisplay.this.display(testOverviewDTO, diagramDisplay);
            }
        }));
        if (allowValidate) view.setValidate("Validate", new RunClickHandler(testRunner, testInstance, testContext, testContextView, controller,
                true, new OnTestRunComplete() {
            @Override
            void updateDisplay(TestOverviewDTO testOverviewDTO, InteractionDiagramDisplay diagramDisplay) {
                TestDisplay.this.display(testOverviewDTO, diagramDisplay);
            }
        }));

        if (testOverview.isRun()) {
            if (allowDelete) view.setDelete("Delete Log", new DeleteClickHandler(this, testContext, testRunner, testInstance));
            view.setInspect("Inspect results", new LaunchInspectorClickHandler(testOverview.getTestInstance(), testContext.getTestSession(), testContext.getCurrentSiteSpec()));
        }

        view.setDescription(testOverview.getDescription());

        // Test level diagram -- normally comes from the TestDisplayView InteractionDiagram member
        boolean firstTransactionRepeatingTooManyTimes = false;
        if (diagramDisplay!=null) {
            firstTransactionRepeatingTooManyTimes = InteractionDiagram.isFirstTransactionRepeatingTooManyTimes(testOverview);
            if (diagramDisplay != null && !firstTransactionRepeatingTooManyTimes) {
                // Display all sections in the diagram at once
                view.setInteractionDiagram(diagramDisplay.render());
            }
        }

        // build sections within test
        view.clearSections();
        for (String sectionName : testOverview.getSectionNames()) {
            SectionOverviewDTO sectionOverview = testOverview.getSectionOverview(sectionName);
            if (diagramDisplay!=null && firstTransactionRepeatingTooManyTimes) {
               InteractionDiagramDisplay sectionDiagramDisplay = diagramDisplay.copy();
               sectionDiagramDisplay.getTestOverviewDTO().getSectionNames().add(sectionName);
               sectionDiagramDisplay.getTestOverviewDTO().getSections().put(sectionName,sectionOverview);
                // Display individual section level diagram instead of one big diagram
                TestSectionDisplay sectionComponent = new TestSectionDisplay(testContext.getTestSession(), testOverview.getTestInstance(), testRunner, allowRun, new OnTestRunComplete() {
                    @Override
                    void updateDisplay(TestOverviewDTO testOverviewDTO, InteractionDiagramDisplay diagramDisplay) {
                       TestDisplay.this.display(testOverviewDTO, diagramDisplay);
                    }
                });
                sectionComponent.display(sectionOverview, sectionDiagramDisplay);
                view.addSection(sectionComponent);
            } else {
                // Cumulative diagram already displayed at the Test level assuming if firstTransactionRepeatingTooManyTimes is false
                TestSectionDisplay sectionComponent = new TestSectionDisplay(testContext.getTestSession(), testOverview.getTestInstance(), testRunner, allowRun, new OnTestRunComplete() {
                    @Override
                    void updateDisplay(TestOverviewDTO testOverviewDTO, InteractionDiagramDisplay diagramDisplay) {
                        TestDisplay.this.display(testOverviewDTO, diagramDisplay);
                    }
                });
                sectionComponent.display(sectionOverview,  null);
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

}
