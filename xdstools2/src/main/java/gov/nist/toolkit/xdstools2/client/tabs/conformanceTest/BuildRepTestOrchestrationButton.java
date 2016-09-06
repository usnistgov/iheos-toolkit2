package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.configDatatypes.SimulatorProperties;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.services.client.RawResponse;
import gov.nist.toolkit.services.client.RepOrchestrationRequest;
import gov.nist.toolkit.services.client.RepOrchestrationResponse;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.sitemanagement.client.TransactionBean;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.widgets.buttons.ReportableButton;

import static gov.nist.toolkit.xdstools2.client.ToolWindow.toolkitService;

/**
 *
 */
class BuildRepTestOrchestrationButton extends ReportableButton {
    private ConformanceTestTab testTab;
    private Panel initializationPanel;
    private FlowPanel initializationResultsPanel = new FlowPanel();

    BuildRepTestOrchestrationButton(ConformanceTestTab testTab, Panel initializationPanel, String label) {
        super(new VerticalPanel(), label);
        this.initializationPanel = initializationPanel;
        this.testTab = testTab;
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

        RepOrchestrationRequest request = new RepOrchestrationRequest();
        request.setSutSite(new SiteSpec(testTab.getSiteName()));
        request.setUserName(testTab.getCurrentTestSession());
        request.setEnvironmentName(testTab.getEnvironmentSelection());

        toolkitService.buildRepTestOrchestration(request, new AsyncCallback<RawResponse>() {
            @Override
            public void onFailure(Throwable throwable) {
                handleError(throwable);
            }

            @Override
            public void onSuccess(RawResponse rawResponse) {
                if (handleError(rawResponse, RepOrchestrationResponse.class)) return;
                RepOrchestrationResponse orchResponse = (RepOrchestrationResponse) rawResponse;
                testTab.setRepOrchestrationResponse(orchResponse);

                if (testTab.getSiteUnderTest() != null) {
                    initializationResultsPanel.add(new HTML("<h2>System Under Test Configuration</h2>"));
                    initializationResultsPanel.add(new HTML("Site: " + testTab.getSiteUnderTest().getName()));
                    FlexTable table = new FlexTable();
                    int row = 0;
                    table.setText(row, 0, "Provide and Register");
                    try {
                        table.setText(row++, 1, testTab.getSiteUnderTest().getRawEndpoint(TransactionType.PROVIDE_AND_REGISTER, false, false));
                    } catch (Exception e) {
                    }

                    table.setText(row, 0, "Retrieve");
                    try {
                        String repUid = testTab.getSiteUnderTest().getRepositoryUniqueId(TransactionBean.RepositoryType.REPOSITORY);
                        table.setText(row++, 1, testTab.getSiteUnderTest().getRetrieveEndpoint(repUid, false, false));
                    } catch (Exception e) {
                        new PopupMessage("oops");
                    }

                    initializationResultsPanel.add(table);
                }


                initializationResultsPanel.add(new HTML("<h2>Generated Environment</h2>"));

                if (orchResponse.getMessage().length() > 0) {
                    initializationResultsPanel.add(new HTML("<h3>" + orchResponse.getMessage().replaceAll("\n", "<br />")  + "</h3>"));
                }

                initializationResultsPanel.add(new HTML("<h3>Supporting Registry Configuration</h3>"));
                initializationResultsPanel.add(new HTML("Site: " + orchResponse.getRepSite().getName()));

                FlexTable table = new FlexTable();

                int row = 0;

                table.setText(row, 0, "Register");
                table.setText(row++, 1, orchResponse.getRegConfig().getConfigEle(SimulatorProperties.registerEndpoint).asString());
                table.setText(row, 0, "Query");
                table.setText(row++, 1, orchResponse.getRegConfig().getConfigEle(SimulatorProperties.storedQueryEndpoint).asString());

                initializationResultsPanel.add(table);

                initializationResultsPanel.add(new HTML("<p>Configure your Repository to forward Register transactions to the above endpoint.<hr />"));


                // test will be run out of support site so pass it back to conformance test tab
                testTab.setSupportSiteName(orchResponse.getRepSite().getName());
            }


        });
    }


}
