package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.services.client.*;
import gov.nist.toolkit.session.client.TestOverviewDTO;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.HorizontalFlowPanel;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.widgets.buttons.OrchestrationButton;

import java.util.List;


/**
 *
 */
public class BuildRegTestOrchestrationButton extends OrchestrationButton {
    private ConformanceTestTab testTab;
    private Panel initializationPanel;
    private FlowPanel initializationResultsPanel = new FlowPanel();
    private RadioButton noFeed = new RadioButton("pidFeedGroup", "No Patient Identity Feed");
    private RadioButton v2Feed = new RadioButton("pidFeedGroup", "V2 Patient Identitfy Feed");

    BuildRegTestOrchestrationButton(ConformanceTestTab testTab, Panel initializationPanel, String label) {
        this.initializationPanel = initializationPanel;
        this.testTab = testTab;

        setParentPanel(initializationPanel);
        setLabel(label);
        setResetLabel("Reset");

        FlowPanel pidFeedPanel = new FlowPanel();
        pidFeedPanel.add(noFeed);
        pidFeedPanel.add(v2Feed);
        v2Feed.setChecked(true);

        setCustomPanel(pidFeedPanel);
        build();
        panel().add(initializationResultsPanel);

    }

    @Override
    public void handleClick(ClickEvent clickEvent) {
        String msg = testTab.verifyConformanceTestEnvironment();
        if (msg != null) {
            testTab.launchTestEnvironmentDialog(msg);
            return;
        }

        initializationResultsPanel.clear();

        RegOrchestrationRequest request = new RegOrchestrationRequest();
        request.setPifType((v2Feed.isChecked()) ? PifType.V2 : PifType.NONE);
        request.setUserName(testTab.getCurrentTestSession());
        request.setEnvironmentName(testTab.getEnvironmentSelection());
        request.setUseExistingSimulator(!isResetRequested());
        SiteSpec siteSpec = new SiteSpec(testTab.getSiteName());
        request.setRegistrySut(siteSpec);

        testTab.setSitetoIssueTestAgainst(siteSpec);


        ClientUtils.INSTANCE.getToolkitServices().buildRegTestOrchestration(request, new AsyncCallback<RawResponse>() {
            @Override
            public void onFailure(Throwable throwable) {
                handleError(throwable);
            }

            @Override
            public void onSuccess(RawResponse rawResponse) {
                if (handleError(rawResponse, RegOrchestrationResponse.class)) return;
                final RegOrchestrationResponse orchResponse = (RegOrchestrationResponse) rawResponse;
                testTab.setRegOrchestrationResponse(orchResponse);

                initializationResultsPanel.add(new HTML("Initialization Complete"));

                initializationResultsPanel.add(new HTML("<h2>Generated Environment</h2>"));

                if (orchResponse.getMessage().length() > 0) {
                    HTML h = new HTML("<p>" + orchResponse.getMessage().replaceAll("\n", "<br />")  + "</p>");
                    h.setStyleName("serverResponseLabelError");
                    initializationResultsPanel.add(h);
                }

                // Display tests run as part of orchestration - so links to their logs are available
                testTab.getToolkitServices().getTestsOverview(testTab.getCurrentTestSession(), orchResponse.getTestInstances(), new AsyncCallback<List<TestOverviewDTO>>() {

                    public void onFailure(Throwable caught) {
                        new PopupMessage("getTestOverview: " + caught.getMessage());
                    }

                    public void onSuccess(List<TestOverviewDTO> testOverviews) {
                        initializationResultsPanel.add(new HTML("Utilities run to initialize environment"));
                        for (TestOverviewDTO testOverview : testOverviews) {
                            MessageItem item = orchResponse.getItemForTest(testOverview.getTestInstance());
                            HorizontalFlowPanel orchTest = new HorizontalFlowPanel();
                            orchTest.getElement().getStyle().setHeight(32, Style.Unit.PX);
                            orchTest.add(new HTML(testOverview.getName() + " - " + testOverview.getTitle()));
                            Image inspect = new Image("icons2/visible-32.png");
                            inspect.addStyleName("right");
                            inspect.addClickHandler(testTab.getInspectClickHandler(testOverview.getTestInstance()));
                            inspect.setTitle("Inspect results");

                            if (item.isSuccess()) {
                                Image status = new Image("icons2/correct-16.png");
                                status.addStyleName("right");
                                status.setTitle("Success");
                                orchTest.add(status);
                            } else {
                                Image status = new Image("icons/ic_warning_black_24dp_1x.png");
                                status.addStyleName("right");
                                status.setTitle("Failure");
                                orchTest.add(status);
                            }

                            if (item.isSuccess())
                                orchTest.setStyleName("testOverviewHeaderSuccess");
                            else
                                orchTest.setStyleName("testOverviewHeaderFail");
                            orchTest.add(inspect);

                            initializationResultsPanel.add(orchTest);
                        }

                    }

                });

                initializationResultsPanel.add(new HTML("Patient ID for Register tests: " + orchResponse.getRegisterPid().toString()));
                initializationResultsPanel.add(new HTML("Patient ID for Stored Query tests: " + orchResponse.getSqPid().toString()));
                initializationResultsPanel.add(new HTML("Patient ID for MPQ tests: " + orchResponse.getMpq1Pid().toString()));
                initializationResultsPanel.add(new HTML("Patient ID for MPQ tests: " + orchResponse.getMpq2Pid().toString()));
                initializationResultsPanel.add(new HTML("<br />"));

            }



        });

    }
}
