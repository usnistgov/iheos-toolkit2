package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.session.client.logtypes.SectionOverviewDTO;
import gov.nist.toolkit.session.client.logtypes.TestPartFileDTO;
import gov.nist.toolkit.testenginelogging.client.LogFileContentDTO;
import gov.nist.toolkit.testenginelogging.client.TestStepLogContentDTO;
import gov.nist.toolkit.xdstools2.client.HorizontalFlowPanel;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;


/**
 * Presentation and view for displaying test sections.
 */
class TestSectionDisplay implements IsWidget {
    private final String sessionName;
    private final TestInstance testInstance;
    private TestInstance fullTestInstance;
    private SectionOverviewDTO sectionOverview;
    private TestSectionView view = new TestSectionView();
    private TestRunner testRunner;
    private TestSectionDisplay me;


    TestSectionDisplay(String sessionName, TestInstance testInstance, SectionOverviewDTO sectionOverview, TestRunner testRunner, boolean allowRun) {
        me = this;
        this.sessionName = sessionName;
        this.testInstance = testInstance;
        this.testRunner = testRunner;
        this.sectionOverview = sectionOverview;
        fullTestInstance = new TestInstance(testInstance.getId(), sectionOverview.getName());

        if (sectionOverview.isRun()) {
            if (sectionOverview.isPass()) view.labelSuccess();
            else view.labelFailure();
        } else view.labelNotRun();

        view.setSectionTitle("Section: " + sectionOverview.getName(), "A section can be run independently although frequently a test section depends on the output of a previous section in the test.");
        view.setTime(sectionOverview.getDisplayableTime());

        if (sectionOverview.isRun()) {
            view.addOpenHandler(new SectionOpenHandler(new TestInstance(testInstance.getId(), sectionOverview.getName()), sessionName, sectionOverview.getName()));
        } else {
            view.addOpenHandler(new SectionNotRunOpenHandler(sessionName, testInstance, sectionOverview.getName()));
        }

        if (allowRun && !sectionOverview.isSutInitiated()) {
            view.setPlay("Run", "Play button", new RunSection(fullTestInstance));
        }

        if (sectionOverview.isSutInitiated()) {
            view.setDone("Operation done - contintue", "Operation done - contintue", null);
        }
        view.setDescription(sectionOverview.getDescription());
        view.build();
    }


    private class SectionNotRunOpenHandler implements OpenHandler<DisclosurePanel> {
        String sessionName;
        TestInstance testInstance;
        String section;

        public SectionNotRunOpenHandler(String sessionName, TestInstance testInstance, String section) {
            this.sessionName = sessionName;
            this.testInstance = testInstance;
            this.section = section;
        }

        @Override
        public void onOpen(OpenEvent<DisclosurePanel> openEvent) {
            ClientUtils.INSTANCE.getToolkitServices().getSectionTestPartFile(sessionName, testInstance, section, new AsyncCallback<TestPartFileDTO>() {
                @Override
                public void onFailure(Throwable throwable) {
                    new PopupMessage("TestSectionDisplay: SectionNotRun: getTestplanAsFile error: " + throwable.toString());
                }

                @Override
                public void onSuccess(final TestPartFileDTO sectionTp) {

                    TestPlanDisplay testPlanDisplay = new TestPlanDisplay(sectionTp.getHtlmizedContent().replace("<br/>", "\r\n"));
                    view.setTestPlanDisplay(testPlanDisplay);

                    boolean singleStep = sectionTp.getStepList().size() == 1;
                    for (final String stepName : sectionTp.getStepList()) {
                        HorizontalFlowPanel stepHeader = new HorizontalFlowPanel();

                        HTML stepHeaderTitle = new HTML("Step: " + stepName);
                        stepHeaderTitle.addStyleName("testOverviewHeaderNotRun");
                        stepHeader.add(stepHeaderTitle);

                        DisclosurePanel stepPanel = new DisclosurePanel(stepHeader);
                        stepPanel.setOpen(singleStep);

                        MetadataDisplay metadataViewerPanel = new MetadataDisplay(sectionTp.getStepTpfMap().get(stepName), sessionName, testInstance, section);

                        final FlowPanel stepResults = new FlowPanel();
                        stepResults.add(metadataViewerPanel.getLabel());
                        stepResults.add(metadataViewerPanel);

                        stepPanel.add(stepResults);
                        view.addStepPanel(stepPanel);
                    }

                }
            });
        }
    }

    private class SectionOpenHandler implements OpenHandler<DisclosurePanel> {
        TestInstance testInstance; // must include section name
        String testSession;
        String section;

        SectionOpenHandler(TestInstance testInstance, String testSession, String section) {
            this.testInstance = testInstance;
            this.testSession = testSession;
            this.section = section;
        }

        @Override
        public void onOpen(OpenEvent<DisclosurePanel> openEvent) {
            ClientUtils.INSTANCE.getToolkitServices().getTestLogDetails(sessionName, testInstance, new AsyncCallback<LogFileContentDTO>() {
                @Override
                public void onFailure(Throwable throwable) {
                    new PopupMessage("getTestLogDetails failed " + throwable.getMessage());
                }

                @Override
                public void onSuccess(final LogFileContentDTO log) {
                    if (log == null) new PopupMessage("section is " + testInstance.getSection());

                    ClientUtils.INSTANCE.getToolkitServices().getSectionTestPartFile(sessionName, testInstance, testInstance.getSection(), new AsyncCallback<TestPartFileDTO>() {
                        @Override
                        public void onFailure(Throwable throwable) {
                            new PopupMessage("TestSectionDisplay: getTestplanAsFile error: " + throwable.toString());
                        }
                        @Override
                        public void onSuccess(final TestPartFileDTO sectionTp) {
                            TestPlanDisplay testPlanDisplay = new TestPlanDisplay(sectionTp.getHtlmizedContent().replace("<br/>", "\r\n"));
                            view.setTestPlanDisplay(testPlanDisplay);

                            if (log.hasFatalError()) view.setFatalError(log.getFatalError());
                            boolean singleStep = log.getSteps().size() == 1;
                            for (TestStepLogContentDTO step : log.getSteps()) {
                                StepView stepView = new StepView(sectionTp, sectionOverview, step, singleStep, testSession, testInstance, section);

                                view.addStepPanel(stepView.asWidget());

                            }
                        }
                    });


                }
            });

        }
    }

    private class RunSection implements ClickHandler {
        TestInstance testInstance;

        RunSection(TestInstance testInstance) {
            this.testInstance = testInstance;
        }

        @Override
        public void onClick(ClickEvent clickEvent) {
            clickEvent.preventDefault();
            clickEvent.stopPropagation();

            me.testRunner.runTest(testInstance, null);
        }
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

}
