package gov.nist.toolkit.xdstools2.client.tabs.GatewayTestsTabs;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.configDatatypes.client.Pid;
import gov.nist.toolkit.services.client.IgOrchestrationRequest;
import gov.nist.toolkit.services.client.IgOrchestrationResponse;
import gov.nist.toolkit.services.client.RawResponse;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;
import gov.nist.toolkit.xdstools2.client.tabs.FindDocumentsLauncher;
import gov.nist.toolkit.xdstools2.client.tabs.conformanceTest.*;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.widgets.OrchestrationSupportTestsDisplay;
import gov.nist.toolkit.xdstools2.client.widgets.buttons.AbstractOrchestrationButton;

/**
 * Build Orchestration for testing an Inititating Gateway.
 * This code id tied to the button that launches it.
 */
public class BuildIGTestOrchestrationButton extends AbstractOrchestrationButton {
    private ConformanceTestTab testTab;
    private boolean includeIG;
    private TestContext testContext;
    private TestContextDisplay testContextDisplay;
    private TestRunner testRunner;
    private Panel initializationPanel;
    private FlowPanel initializationResultsPanel = new FlowPanel();

    public BuildIGTestOrchestrationButton(ConformanceTestTab testTab, Panel initializationPanel, String label, TestContext testContext, TestContextDisplay testContextDisplay, TestRunner testRunner, boolean includeIG) {
        this.initializationPanel = initializationPanel;
        this.testTab = testTab;
        this.testContext = testContext;
        this.testContextDisplay = testContextDisplay;
        this.testRunner = testRunner;

        setParentPanel(initializationPanel);
        setLabel(label);
        setResetLabel("Reset");
        this.includeIG = includeIG;

        FlowPanel customPanel = new FlowPanel();

        HTML instructions = new HTML();

        customPanel.add(instructions);

        build();
        panel().add(initializationResultsPanel);
    }

    public void handleClick(ClickEvent event) {
        if (GenericQueryTab.empty(testTab.getCurrentTestSession())) {
            new PopupMessage("Must select test session first");
            return;
        }
        IgOrchestrationRequest request = new IgOrchestrationRequest();
        request.setUseExistingState(!isResetRequested());
        request.setUserName(testTab.getCurrentTestSession());
        request.setIncludeLinkedIG(includeIG);

        initializationResultsPanel.clear();

        ClientUtils.INSTANCE.getToolkitServices().buildIgTestOrchestration(request, new AsyncCallback<RawResponse>() {
            @Override
            public void onFailure(Throwable throwable) {
                handleError(throwable);
            }

            @Override
            public void onSuccess(RawResponse rawResponse) {
                if (handleError(rawResponse, IgOrchestrationResponse.class)) return;
                IgOrchestrationResponse orchResponse = (IgOrchestrationResponse) rawResponse;
                testTab.setOrchestrationResponse(orchResponse);
                testTab.setSitetoIssueTestAgainst(testContext.getSiteUnderTest().siteSpec());

                initializationResultsPanel.add(new HTML("Initialization Complete"));

                if (testContext.getSiteUnderTest() != null) {
                    initializationResultsPanel.add(new SiteDisplay("System Under Test Configuration", testContext.getSiteUnderTest()));
                }

                if (orchResponse.getSupportRG1() != null) {
                    initializationResultsPanel.add(new SiteDisplay("Supporting Environment Configuration", orchResponse.getSupportRG1()));
                }
                if (orchResponse.getSupportRG2() != null) {
                    initializationResultsPanel.add(new SiteDisplay("", orchResponse.getSupportRG2()));
                }

                handleMessages(initializationResultsPanel, orchResponse);

                initializationResultsPanel.add(new HTML("<br />"));

                initializationResultsPanel.add(new OrchestrationSupportTestsDisplay(orchResponse, testContext, testContextDisplay, testRunner ));

                initializationResultsPanel.add(new HTML("<br />"));


//                initializationResultsPanel.add(new HTML("<h2>Test Environment</h2>"));
                FlexTable table = new FlexTable();
                panel().add(table);
                int row = 0;

                Widget w;

                table.setWidget(row, 0, new HTML("<h3>Test data pattern</h3>"));
                table.setWidget(row++, 1, new HTML("<h3>Patient ID</h3>"));

//                table.setWidget(row++, 0, new HTML("Each Patient is configured with records to support a different test environment."));

                // FindDocuments launcher need actor type
                SiteSpec siteSpecRg1 = orchResponse.getSupportRG1().siteSpec();
                siteSpecRg1.setActorType(ActorType.RESPONDING_GATEWAY);
                SiteSpec siteSpecRg2 = orchResponse.getSupportRG2().siteSpec();
                siteSpecRg2.setActorType(ActorType.RESPONDING_GATEWAY);

//                Anchor a;
//                a = new Anchor("Single document in Community 1");
//                try {
//                    a.addClickHandler(new FindDocumentsLauncher(orchResponse.getOneDocPid(), siteSpecRg1, false));
//                } catch (Exception e) {
//                    new PopupMessage(e.getMessage() + " for " + "Single document in Community 1");
//                }
                table.setWidget(row, 0, buildFindDocumentsLauncher(siteSpecRg1, orchResponse.getOneDocPid(), "Single document in Community 1"));
                table.setWidget(row++, 1, new HTML(orchResponse.getOneDocPid().asString()));

                table.setWidget(row, 0, buildFindDocumentsLauncher(siteSpecRg1, orchResponse.getTwoDocPid(), "Two documents in Community 1"));
                table.setWidget(row++, 1, new HTML(orchResponse.getTwoDocPid().asString()));



                table.setWidget(row, 0, new HTML("Both Communities have single document"));
                table.setWidget(row++, 1, new HTML(orchResponse.getTwoRgPid().asString()));
                table.setWidget(row++, 0, buildFindDocumentsLauncher(siteSpecRg1, orchResponse.getTwoRgPid(), "Community 1"));
                table.setWidget(row++, 0, buildFindDocumentsLauncher(siteSpecRg2, orchResponse.getTwoRgPid(), "Community 2"));

                table.setWidget(row, 0, new HTML("Error management Patient ID"));
                table.setWidget(row++, 1, new HTML(orchResponse.getUnknownPid().asString()));
                table.setWidget(row++, 0, buildFindDocumentsLauncher(siteSpecRg1, orchResponse.getUnknownPid(), "Community 1 returns Registry Error"));
                table.setWidget(row++, 0, buildFindDocumentsLauncher(siteSpecRg2, orchResponse.getUnknownPid(), "Community 2 returns single document"));

                initializationResultsPanel.add(new HTML("<h3>Configure your Initiating Gateway to forward requests to both of the above Responding Gateways.</h3><hr />"));

            }
        });
    }

    Anchor buildFindDocumentsLauncher(SiteSpec siteSpec, Pid pid, String title) {
        Anchor a = new Anchor(title);
        try {
            a.addClickHandler(new FindDocumentsLauncher(pid, siteSpec, false));
        } catch (Exception e) {
            new PopupMessage(e.getMessage() + " for " + title);
        }
        return a;
    }

    Widget light(Widget w) {
        w.getElement().getStyle().setProperty("backgroundColor", "#f0f0f0");
        return w;
    }

    Widget dark(Widget w) {
        w.getElement().getStyle().setProperty("backgroundColor", "#d3d3d3");
        return w;
    }
}
