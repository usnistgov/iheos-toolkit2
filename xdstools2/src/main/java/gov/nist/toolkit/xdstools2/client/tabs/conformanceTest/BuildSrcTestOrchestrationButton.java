package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import gov.nist.toolkit.actortransaction.shared.ActorOption;
import gov.nist.toolkit.installation.shared.TestSession;
import gov.nist.toolkit.services.client.RawResponse;
import gov.nist.toolkit.services.client.SrcOrchestrationRequest;
import gov.nist.toolkit.services.client.SrcOrchestrationResponse;
import gov.nist.toolkit.xdstools2.client.command.command.BuildSrcTestOrchestrationCommand;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.widgets.OrchestrationSupportTestsDisplay;
import gov.nist.toolkit.xdstools2.client.widgets.buttons.AbstractOrchestrationButton;
import gov.nist.toolkit.xdstools2.shared.command.request.BuildSrcTestOrchestrationRequest;

/**
 *
 */
public abstract class BuildSrcTestOrchestrationButton extends AbstractOrchestrationButton {
    protected ConformanceTestTab testTab;
    protected Panel initializationPanel;
    protected TestContext testContext;
    protected ActorOption actorOption;
    protected TestContextView testContextView;
    protected FlowPanel initializationResultsPanel = new FlowPanel();
    protected boolean testingAClient = true;

    BuildSrcTestOrchestrationButton(ConformanceTestTab testTab, TestContext testContext, TestContextView testContextView, Panel initializationPanel, String label, ActorOption actorOption) {
        this.initializationPanel = initializationPanel;
        this.testTab = testTab;
        this.testContext = testContext;
        this.testContextView = testContextView;
        this.actorOption = actorOption;

        setParentPanel(initializationPanel);

        build();
        panel().add(initializationResultsPanel);
    }

    @Override
    public void orchestrate() {
        String msg = testContext.verifyTestContext(testingAClient);
        if (msg != null) {
            testContextView.launchDialog(msg);
            return;
        }

        initializationResultsPanel.clear();

        SrcOrchestrationRequest request = new SrcOrchestrationRequest(actorOption);
        request.setTestSession(new TestSession(testTab.getCurrentTestSession()));
        request.setEnvironmentName(testTab.getEnvironmentSelection());
        request.setUseExistingState(!isResetRequested());
        request.setUseTls(isTls());
        request.getActorOption().copyFrom(testTab.getCurrentActorOption());

        testTab.setSiteToIssueTestAgainst(null);

        orchestrate(request);
    }

    abstract void orchestrate(SrcOrchestrationRequest request);

}
