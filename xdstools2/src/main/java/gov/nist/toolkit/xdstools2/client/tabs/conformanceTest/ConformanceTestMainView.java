package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
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
    private final FlowPanel tabBarPanel = new FlowPanel();
    private final TabBar actorTabBar = new TabBar();            // tab bar at the top for selecting actor types
    private final UserDefinedTabBar profileTabBar = new UserDefinedTabBar();
    private final UserDefinedTabBar optionsTabBar = new UserDefinedTabBar();
    private final Anchor indexAnchor = new Anchor("Index");

    private HTML testSessionDescription = new HTML();
    private FlowPanel testSessionDescriptionPanel = new FlowPanel();
    private HTML loadingMessage = new HTML();

    ConformanceTestMainView(ToolWindow toolWindow) {
        toolPanel.getElement().getStyle().setMargin(4, Style.Unit.PX);
        toolPanel.getElement().getStyle().setMarginLeft(0, Style.Unit.PX);
        testsPanel.getElement().getStyle().setMarginRight(4, Style.Unit.PX);

        FlowPanel sitesPanel = new FlowPanel();
        toolPanel.add(sitesPanel);

        HorizontalFlowPanel actorpanel = new HorizontalFlowPanel();
        indexAnchor.getElement().getStyle().setMarginLeft(0, Style.Unit.PX);
        toolPanel.add(indexAnchor);
        HTML actorToTest = new HTML("Actor to test");
//        actorToTest.addStyleName("section-title");
        actorpanel.add(actorToTest);
        actorpanel.add(new InformationLink("Help with Conformance Test tool", "Conformance-Test-Tool").asWidget());
        toolPanel.add(actorpanel);
        tabBarPanel.add(actorTabBar);
        tabBarPanel.add(new HTML("Profile"));
        tabBarPanel.add(profileTabBar);
        tabBarPanel.add(new HTML("Option"));
        tabBarPanel.add(optionsTabBar);
        tabBarPanel.setVisible(false);
        toolPanel.add(tabBarPanel);

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

    public UserDefinedTabBar getProfileTabBar() {
        return profileTabBar;
    }

    public UserDefinedTabBar getOptionsTabBar() {
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

    public FlowPanel getTabBarPanel() {
        return tabBarPanel;
    }

    public Anchor getIndexAnchor() {
        return indexAnchor;
    }
}
