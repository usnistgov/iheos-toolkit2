package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TabBar;
import gov.nist.toolkit.xdstools2.client.ToolWindow;

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

    ConformanceTestMainView(ToolWindow toolWindow, OptionsTabBar optionsTabBar) {
        this.optionsTabBar = optionsTabBar;
        toolPanel.getElement().getStyle().setMargin(4, Style.Unit.PX);
        toolPanel.getElement().getStyle().setMarginLeft(0, Style.Unit.PX);
        testsPanel.getElement().getStyle().setMarginRight(4, Style.Unit.PX);

        FlowPanel sitesPanel = new FlowPanel();
        toolPanel.add(sitesPanel);
        toolPanel.add(actorTabBar);
        toolPanel.add(optionsTabBar);
        toolPanel.add(initializationPanel);
        toolPanel.add(testsPanel);

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
}
