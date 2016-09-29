package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.configDatatypes.SimulatorProperties;
import gov.nist.toolkit.services.client.PifType;
import gov.nist.toolkit.services.client.RawResponse;
import gov.nist.toolkit.services.client.RgOrchestrationRequest;
import gov.nist.toolkit.services.client.RgOrchestrationResponse;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.widgets.buttons.OrchestrationButton;

/**
 *
 */
public class BuildRgTestOrchestrationButton extends OrchestrationButton {
    private ConformanceTestTab testTab;
    private Panel initializationPanel;
    private FlowPanel initializationResultsPanel = new FlowPanel();
    private RadioButton noFeed = new RadioButton("pidFeedGroup", "No Patient Identity Feed");
    private RadioButton v2Feed = new RadioButton("pidFeedGroup", "V2 Patient Identitfy Feed");

    private String systemTypeGroup = "System Type Group";
    private RadioButton exposed = new RadioButton(systemTypeGroup, "Exposed Registry/Repository");
    private RadioButton external = new RadioButton(systemTypeGroup, "External Registry/Repository");
    boolean isExposed() { return exposed.getValue(); }
    boolean isExternal() { return external.getValue(); }
    boolean usingExposedRR() { return exposed.getValue(); }


    BuildRgTestOrchestrationButton(ConformanceTestTab testTab, Panel initializationPanel, String label) {
        this.initializationPanel = initializationPanel;
        this.testTab = testTab;

        setParentPanel(initializationPanel);
        setLabel(label);
        setResetLabel("Reset");

        FlowPanel customPanel = new FlowPanel();

        HTML instructions = new HTML(
                "<p>" +
                        "The system under test is a Responding Gateway. For the testing to be fully automated by this tool one of the following " +
                        "configurations must be supported by your implementation. " +
                        "<ul>" +
                        "<li>Exposed Registry/Repository endpoints - your implementation includes Registry/Repository " +
                        "functionality and you expose the required endpoints for these actors. " +
                        "A single site (system configuration in toolkit) must contain the Responding Gateway " +
                        "(system under test), and the related Registry and Repository configurations." +
                        "<li>External Registry/Repository - your implementation can be configured to work with an " +
                        "external Registry and Repository which will be selected below. This tool will provide " +
                        "these actors." +
                        "</ul>" +

                        "<p>If your Responding Gateway does not meet the above requirement it must be initialized " +
                        "manually.  See <a href=\"site/testkit/tests/RG/testdata.html\"  target=\"_blank\">here</a> for details.</p>"  +

                        "<p>When the test is run a Cross Gateway Query or Retrieve transaction will be sent to the " +
                        "Responding Gateway " +
                        "selected below. This will start the test. Before running a test, make sure your " +
                        "Responding Gateway is configured to forward requests to the Document Repository and Document Registry above.  This " +
                        "test only uses non-TLS endpoints (for now). TLS selection is disabled.</p>"
        );
        customPanel.add(instructions);

        Panel systemTypePanel = new HorizontalPanel();
        systemTypePanel.add(exposed);
        systemTypePanel.add(external);
        Panel editExposedSystemConfigPanel = new HorizontalPanel();
        systemTypePanel.add(editExposedSystemConfigPanel);
        final Button editExposedSiteButton = new Button("Edit Site Configuration");
        editExposedSystemConfigPanel.add(editExposedSiteButton);
        editExposedSiteButton.setVisible(false);
        editExposedSiteButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {

            }
        });

        customPanel.add(systemTypePanel);
        customPanel.add(new HTML("<br />"));

        // Patient Identity feed to registry
        customPanel.add(noFeed);
        customPanel.add(v2Feed);
        v2Feed.setChecked(true);
        customPanel.add(new HTML("<br />"));

        setCustomPanel(customPanel);

        build();
        panel().add(initializationResultsPanel);

    }

    @Override
    public void handleClick(ClickEvent clickEvent) {
        if (!isExposed() && !isExternal()) {
            new PopupMessage("Must select Exposed or External Registry/Repository");
            return;
        }

        RgOrchestrationRequest request = new RgOrchestrationRequest();
        request.setUserName(testTab.getCurrentTestSession());
        request.setUseExposedRR(usingExposedRR());
        request.setUseSimAsSUT(false);

        request.setPifType((v2Feed.isChecked()) ? PifType.V2 : PifType.NONE);
        request.setUserName(testTab.getCurrentTestSession());
        request.setEnvironmentName(testTab.getEnvironmentSelection());
        request.setUseExistingSimulator(!isResetRequested());
        SiteSpec siteSpec = new SiteSpec(testTab.getSiteName());
        request.setSiteUnderTest(siteSpec);

        testTab.setSitetoIssueTestAgainst(siteSpec);

        ClientUtils.INSTANCE.getToolkitServices().buildRgTestOrchestration(request, new AsyncCallback<RawResponse>() {
            @Override
            public void onFailure(Throwable throwable) {
                handleError(throwable);
            }

            @Override
            public void onSuccess(RawResponse rawResponse) {
                if (handleError(rawResponse, RgOrchestrationResponse.class)) return;
                RgOrchestrationResponse orchResponse = (RgOrchestrationResponse) rawResponse;
                testTab.setRgOrchestrationResponse(orchResponse);
                panel().add(new HTML("<h2>Generated Environment</h2>"));

                if (orchResponse.getMessage().length() > 0) {
                    panel().add(new HTML("<h3>" + orchResponse.getMessage().replaceAll("\n", "<br />")  + "</h3>"));
                }

                FlexTable table = new FlexTable();
                panel().add(table);
                if (usingExposedRR()) {
                    exposedRRReport(table, orchResponse);
                } else {
                    HTML instructions = externalRRReport(table, orchResponse);
                    panel().add(instructions);
                }
            }
        });
    }

    private void exposedRRReport(FlexTable table, RgOrchestrationResponse response) {
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

    private HTML externalRRReport(FlexTable table, RgOrchestrationResponse response) {
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

    private int displayPIDs(FlexTable table, RgOrchestrationResponse response, int row) {
        table.setHTML(row++, 0, "<h3>Patient IDs</h3>");
        table.setText(row, 0, "Single document Patient ID");
        table.setText(row++, 1, response.getOneDocPid().asString());
        table.setText(row, 0, "Two document Patient ID");
        table.setText(row++, 1, response.getTwoDocPid().asString());

        return row;
    }


}
