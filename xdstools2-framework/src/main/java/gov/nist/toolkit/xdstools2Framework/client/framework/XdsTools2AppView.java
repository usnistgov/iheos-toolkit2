package gov.nist.toolkit.xdstools2Framework.client.framework;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.toolkitFramework.client.environment.EnvironmentManager;
import gov.nist.toolkit.toolkitFramework.client.events.ResizeToolkitEvent;
import gov.nist.toolkit.toolkitFramework.client.testSession.TestSessionManager;
import gov.nist.toolkit.toolkitFramework.client.testSession.TestSessionSelector;
import gov.nist.toolkit.toolkitFramework.client.toolSupport.TabContainer;
import gov.nist.toolkit.toolkitFramework.client.util.MenuManagement;

import javax.inject.Inject;
import java.util.logging.Logger;


public class XdsTools2AppView implements MenuManagement {
	private SplitLayoutPanel mainSplitPanel = new SplitLayoutPanel(3);
	private FlowPanel mainMenuPanel = new FlowPanel();
	private static final int TRAY_SIZE = 190;
	private static final int TRAY_CTL_BTN_SIZE = 9; // 23

	private static XdsTools2AppView ME;

	private final static Logger logger=Logger.getLogger(XdsTools2AppView.class.getName());

	private TestSessionManager testSessionManager;

	private EventBus eventBus;

	private HorizontalPanel uiDebugPanel = new HorizontalPanel();
	boolean UIDebug = false;

	@Inject
	public XdsTools2AppView(TestSessionManager testSessionManager, EventBus eventBus) {
		ME = this;
		this.testSessionManager = testSessionManager;
		this.eventBus = eventBus;
		GWT.log("In XdsTools2AppView");
		assert(testSessionManager != null);
		assert(eventBus != null);
	}

//	static public XdsTools2AppView getInstance() {
//		if (ME==null){
//			ME=new XdsTools2AppView();
//		}
//		return ME;
//	}

	public void setWidget(IsWidget isWidget) {
		mainSplitPanel.add(isWidget.asWidget());
	}

	public Widget asWidget() {
		return mainSplitPanel;
	}

	public void buildTabsWrapper() {
		HorizontalPanel menuPanel = new HorizontalPanel();
		EnvironmentManager environmentManager = new EnvironmentManager();

		Widget decoratedTray = decorateMenuContainer();

		mainSplitPanel.addWest(decoratedTray, TRAY_SIZE);
		mainSplitPanel.setWidgetToggleDisplayAllowed(decoratedTray,true);


		TabContainer.setWidth("100%");
		TabContainer.setHeight("100%");

		menuPanel.add(environmentManager);
		menuPanel.setSpacing(10);
		menuPanel.add(new TestSessionSelector(testSessionManager.getTestSessions(), testSessionManager.getCurrentTestSession()).asWidget());

		DockLayoutPanel mainPanel = new DockLayoutPanel(Style.Unit.EM);
		mainPanel.addNorth(menuPanel, 4);
		mainPanel.add(TabContainer.getTabPanel());
		mainSplitPanel.add(mainPanel);

		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				eventBus.fireEvent(new ResizeToolkitEvent());
			}

		});
	}

	private Widget decorateMenuContainer() {
		final FlowPanel vpCollapsible =  new FlowPanel();

		// Set margins
		mainMenuPanel.getElement().getStyle().setMargin(3, Style.Unit.PX);

		// Make it Collapsible

		final HTML menuTrayStateToBe = new HTML("&laquo;");
		menuTrayStateToBe.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT); // Right alignment causes a problem where it is cut off (non-intuitively hidden over current view but still accessible by the horizontal scrollbar) by longer menu item labels that wrap in a smaller-sized browser window
		menuTrayStateToBe.getElement().getStyle().setFontSize(14, Style.Unit.PX);
		menuTrayStateToBe.setTitle("Minimize Toolkit Menu");

		// Make it rounded
		menuTrayStateToBe.setStyleName("menuCollapse");
		menuTrayStateToBe.setWidth("15px");
		menuTrayStateToBe.setWidth("100%");

		vpCollapsible.add(menuTrayStateToBe);
		vpCollapsible.add(mainMenuPanel);

		// Wrap menu in a scroll panel
		final ScrollPanel spMenu = new ScrollPanel(vpCollapsible);

		menuTrayStateToBe.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (menuTrayStateToBe.getHTML().hashCode()==171) {
					menuTrayStateToBe.setHTML("&raquo;");
					menuTrayStateToBe.setTitle("Expand Toolkit Menu");
					mainMenuPanel.setVisible(false);
					menuTrayStateToBe.setWidth("");
					menuTrayStateToBe.setHeight(mainSplitPanel.getElement().getClientHeight()+"px");
					mainSplitPanel.setWidgetSize(spMenu, TRAY_CTL_BTN_SIZE);
				} else {
					menuTrayStateToBe.setHTML("&laquo;");
					menuTrayStateToBe.setTitle("Collapse");
					mainMenuPanel.setVisible(true);
					menuTrayStateToBe.setWidth("100%");
					menuTrayStateToBe.setHeight("");
					mainSplitPanel.setWidgetSize(spMenu,TRAY_SIZE);
				}
			}
		});

		return spMenu;
	}

	public void clearMainMenu() {
		mainMenuPanel.clear();
	}


	public void addtoMainMenu(Widget w) {
		mainMenuPanel.add(w);
	}

}
