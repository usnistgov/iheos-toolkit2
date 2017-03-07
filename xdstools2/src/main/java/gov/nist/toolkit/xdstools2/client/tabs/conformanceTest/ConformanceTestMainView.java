package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TabBar;
import gov.nist.toolkit.xdstools2.client.ToolWindow;
import gov.nist.toolkit.xdstools2.client.util.InformationLink;
import gov.nist.toolkit.xdstools2.client.widgets.HorizontalFlowPanel;

/**
 *
 */
public class ConformanceTestMainView {
    private final FlowPanel toolPanel = new FlowPanel();   // Outer-most panel for the tool
    private final FlowPanel initializationPanel = new FlowPanel();
    private final FlowPanel testsPanel = new FlowPanel();  // panel for displaying tests
    private final TabBar actorTabBar = new TabBar();            // tab bar at the top for selecting actor types
    private final OptionsTabBar optionsTabBar;

    private HTML testSessionDescription = new HTML();
    private FlowPanel testSessionDescriptionPanel = new FlowPanel();
    private HTML loadingMessage = new HTML();

    ConformanceTestMainView(ToolWindow toolWindow, OptionsTabBar optionsTabBar) {
        this.optionsTabBar = optionsTabBar;
        toolPanel.getElement().getStyle().setMargin(4, Style.Unit.PX);
        toolPanel.getElement().getStyle().setMarginLeft(0, Style.Unit.PX);
        testsPanel.getElement().getStyle().setMarginRight(4, Style.Unit.PX);

        FlowPanel sitesPanel = new FlowPanel();
        toolPanel.add(sitesPanel);
        HorizontalFlowPanel actorpanel = new HorizontalFlowPanel();
        actorpanel.add(new HTML("Actor"));
        actorpanel.add(new InformationLink("Help with Conformance Test tool", "Conformance-Test-Tool").asWidget());
        toolPanel.add(actorpanel);
        toolPanel.add(actorTabBar);
        toolPanel.add(new HTML("Option"));
        toolPanel.add(optionsTabBar);
        toolPanel.add(loadingMessage);
        toolPanel.add(initializationPanel);
        toolPanel.add(testsPanel);
        toolPanel.add(new HTML("<br /><br />"));

        testSessionDescriptionPanel.setStyleName("with-rounded-border");
        testSessionDescriptionPanel.add(testSessionDescription);
        testSessionDescriptionPanel.getElement().getStyle().setMarginLeft(2, Style.Unit.PX);
    }

    public HTML getTestSessionDescription() {
        return testSessionDescription;
    }

    public Panel getTestSessionDescriptionPanel() {
        return testSessionDescriptionPanel;
    }

    public FlowPanel getToolPanel() {
        return toolPanel;
    }

    public TabBar getActorTabBar() {
        return actorTabBar;
    }

    public OptionsTabBar getOptionsTabBar() {
        return optionsTabBar;
    }

    public Panel getTestsPanel() {
        return testsPanel;
    }

    public FlowPanel getInitializationPanel() {
        return initializationPanel;
    }

    public void showLoadingMessage(String loadingMessage) {
        this.loadingMessage.setStyleName("loadingMessage");
        this.loadingMessage.setHTML(loadingMessage);
    }

    public void clearLoadingMessage() {
        this.loadingMessage.setHTML("");
        this.loadingMessage.setStyleName("hiddenMessage");
    }
}
