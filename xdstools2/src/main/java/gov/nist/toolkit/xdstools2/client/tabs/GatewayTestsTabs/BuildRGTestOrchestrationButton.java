package gov.nist.toolkit.xdstools2.client.tabs.GatewayTestsTabs;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import gov.nist.toolkit.configDatatypes.SimulatorProperties;
import gov.nist.toolkit.services.client.RawResponse;
import gov.nist.toolkit.services.client.RgOrchestrationRequest;
import gov.nist.toolkit.services.client.RgOrchestrationResponse;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.widgets.buttons.AbstractOrchestrationButton;

/**
 *
 */
class BuildRGTestOrchestrationButton extends AbstractOrchestrationButton {
    private RGTestTab testTab;
    private SiteSpec siteUnderTest;
    private boolean useExposedRR;
    private boolean useSimAsSUT;
//    private List<AbstractOrchestrationButton> linkedOrchestrationButtons = new ArrayList<>();

    BuildRGTestOrchestrationButton(RGTestTab testTab, Panel topPanel, String label, boolean useSimAsSUT) {
        super(topPanel, label);
        this.testTab = testTab;
        this.useSimAsSUT = useSimAsSUT;
    }

//    public void addLinkedOrchestrationButton(AbstractOrchestrationButton orchestrationButton) {
//        linkedOrchestrationButtons.display(orchestrationButton);
//    }

    public void handleClick(ClickEvent event) {
        if (GenericQueryTab.empty(testTab.getCurrentTestSession())) {
            new PopupMessage("Must select test session first");
            return;
        }

        if (!useSimAsSUT && !testTab.isExposed() && !testTab.isExternal()) {
            new PopupMessage("Must select Exposed or External Registry/Repository");
            return;
        }

        useExposedRR = testTab.usingExposedRR();
        siteUnderTest = testTab.getSiteSelection();

        if (!useSimAsSUT && siteUnderTest == null) {
            new PopupMessage("Select a Responding Gateway to test");
            return;
        }

        // get rid of past reports
//        for (AbstractOrchestrationButton b : linkedOrchestrationButtons) {
//            b.clean();
//        }

        RgOrchestrationRequest request = new RgOrchestrationRequest();
        request.setUserName(testTab.getCurrentTestSession());
//        request.setEnvironmentName(??????);
        request.setSiteUnderTest(siteUnderTest);
        request.setUseExposedRR(useExposedRR);
        request.setUseSimAsSUT(useSimAsSUT);

        ClientUtils.INSTANCE.getToolkitServices().buildRgTestOrchestration(request, new AsyncCallback<RawResponse>() {
            @Override
            public void onFailure(Throwable throwable) {
                handleError(throwable);
            }

            @Override
            public void onSuccess(RawResponse rawResponse) {
                if (handleError(rawResponse, RgOrchestrationResponse.class)) return;
                RgOrchestrationResponse orchResponse = (RgOrchestrationResponse) rawResponse;
                testTab.orch = orchResponse;
                panel().add(new HTML("<h2>Generated Environment</h2>"));

                if (orchResponse.getMessage().length() > 0) {
                    panel().add(new HTML("<h3>" + orchResponse.getMessage().replaceAll("\n", "<br />")  + "</h3>"));
                }

                FlexTable table = new FlexTable();
                panel().add(table);
                if (useSimAsSUT) {
                    simAsSUTReport(table, orchResponse);
                } else {
                    if (useExposedRR) {
                        exposedRRReport(table, orchResponse);
                    } else {
                        HTML instructions = externalRRReport(table, orchResponse);
                        panel().add(instructions);
                    }
                }
            }
        });
    }

    int displayPIDs(FlexTable table, RgOrchestrationResponse response, int row) {
        table.setHTML(row++, 0, "<h3>Patient IDs</h3>");
        table.setText(row, 0, "Single document Patient ID");
//        table.setText(row++, 1, response.getOneDocPid().asString());
//        table.setText(row, 0, "Two document Patient ID");
//        table.setText(row++, 1, response.getTwoDocPid().asString());

        return row;
    }

    void simAsSUTReport(FlexTable table, RgOrchestrationResponse response) {
        int row = 0;

        row = displayPIDs(table, response, row);

        table.setHTML(row++, 0, "<h3>Simulators</h3>");

        table.setWidget(row, 0, new HTML("<h3>System under test</h3>"));
        table.setText(row++, 1, response.getSiteUnderTest().getName());

    }

    void exposedRRReport(FlexTable table, RgOrchestrationResponse response) {
        int row = 0;

        row = displayPIDs(table, response, row);

        table.setHTML(row++, 0, "<h3>Simulators</h3>");
        if (response.getRegrepConfig() == null)
            table.setText(row++, 1, "None");
        else {
            table.setHTML(row++, 0, "<h3>Supporting Registry/Repository");
            table.setText(row, 0, "Query");
            table.setText(row++, 1, response.getRegrepConfig().getConfigEle(SimulatorProperties.storedQueryEndpoint).asString());
            table.setText(row, 0, "Retrieve");
            table.setText(row++, 1, response.getRegrepConfig().getConfigEle(SimulatorProperties.retrieveEndpoint).asString());
        }

        table.setWidget(row, 0, new HTML("<h3>System under test</h3>"));
        table.setText(row++, 1, response.getSiteUnderTest().getName());

    }

    HTML externalRRReport(FlexTable table, RgOrchestrationResponse response) {
        int row = 0;

        row = displayPIDs(table, response, row);

        table.setHTML(row++, 0, "<h3>Simulators</h3>");
        table.setText(row++, 1, response.getRegrepConfig().getId().toString());

        table.setWidget(row, 0, new HTML("<h3>System under test</h3>"));
        table.setText(row++, 1, response.getSiteUnderTest().getName());

        table.setHTML(row++, 0, "<h3>Supporting Registry/Repository");
        table.setText(row, 0, "Query");
        table.setText(row++, 1, response.getRegrepConfig().getConfigEle(SimulatorProperties.storedQueryEndpoint).asString());
        table.setText(row, 0, "Retrieve");
        table.setText(row++, 1, response.getRegrepConfig().getConfigEle(SimulatorProperties.retrieveEndpoint).asString());

        return new HTML(
                "Configure your Responding Gateway to use the above Registry and Repository."
        );
    }

}
