package gov.nist.toolkit.xdstools2.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.tk.client.TkProps;
import gov.nist.toolkit.xdstools2.client.event.testSession.TestSessionManager2;
import gov.nist.toolkit.xdstools2.client.selectors.EnvironmentManager;

import java.util.logging.Logger;

/**
 * This supports two display modes for tabs/tools
 * 1. Normal mode - tab class (which extends GenericQueryTab) adds content directly
 * to tabTopPanel.  This accepts any number of widgets.  This is kept around for
 * backwards compatibility with all the existing tools.
 *
 * 2. Raw mode - tab class (which extends GenericQueryTab) declares a single top
 * level widget of type *LayoutPanel and expects the window management that comes
 * from *LayoutPanels (resize from out to in) which is the opposite of non-*LayoutPanels
 * - from inside to out.
 *
 * To use: Set the title by overriding the getTitle method and call useRawPanel(*LayoutPanel)
 * passing the top level *LayoutPanel.
 *
 * In either style, the title shown in the little tab at the top is controlled by
 * registerTab(boolean select, String eventName)
 */
public abstract class ToolWindow {
	private DockLayoutPanel tabTopRawPanel = new DockLayoutPanel(Style.Unit.EM);
//	private SimpleLayoutPanel innerPanel = new SimpleLayoutPanel();
	private ScrollPanel innerPanel = new ScrollPanel();
	public FlowPanel tabTopPanel = new FlowPanel();
	private FlowPanel eastPanel = new FlowPanel();
	private FlowPanel westPanel = new FlowPanel();
	String helpHTML;
	String topMessage = null;
	public HorizontalPanel menuPanel = new HorizontalPanel();
	EnvironmentManager environmentManager = null;
	protected TestSessionManager2 testSessionManager = Xdstools2.getTestSessionManager();
	protected TabContainer tabContainer;
	Logger logger = Logger.getLogger("Tabbed window");
	final static public ToolkitServiceAsync toolkitService = GWT
			.create(ToolkitService.class);

	public ToolWindow() {
		String title = getTitle();
		// .addNorth MUST come before .add - a condition of DockLayoutPanel
		if (title != null)
			tabTopRawPanel.addNorth(new HTML("<h1>" + title + "</h1>"), 4.0);
		tabTopRawPanel.addEast(eastPanel, 0);
		tabTopRawPanel.addWest(westPanel, 0);
		tabTopRawPanel.add(innerPanel);
		innerPanel.setWidget(tabTopPanel);
	}

	public void addEast(Widget w) { eastPanel.add(w); }

	public TabContainer getTabContainer() { return tabContainer; }

	public DockLayoutPanel getRawPanel() { return tabTopRawPanel; }

	public void useRawPanel(Widget windowRoot) {
		innerPanel.setWidget(windowRoot);
	}

	/**
	 * Override when using Raw Mode - only way to set title in raw mode
	 * @return
     */
	public String getTitle() { return null; }

	// Used to be protected but impractical for use with the new widget-based architecture in for ex. TestsOverviewTab
	public String getCurrentTestSession() { return testSessionManager.getCurrentTestSession(); }

	abstract public void onTabLoad(boolean select, String eventName);

	// getWindowShortName() + ".html"is documentation file in /doc
	abstract public String getWindowShortName();

	public void onAbstractTabLoad(boolean select, String eventName) {
		onTabLoad(select, eventName);
//		registerTab(container);
//		onTabSelection();

//		environmentManager = new EnvironmentManager(tabContainer, toolkitService/*, new Panel1(menuPanel)*/);
//		menuPanel.add(environmentManager);
//		menuPanel.add(new TestSessionSelector(testSessionManager.getTestSessions(), testSessionManager.getCurrentTestSession()).asWidget());
	}

	public void registerTab(boolean select, String tabName) {
		TabContainer.instance().addTab(tabTopRawPanel, tabName, select);
	}
	
	public TkProps tkProps() {
		return Xdstools2.tkProps();
	}

//	void registerTab(TabContainer container) {
//		TabPanel tabPanel = container.getTabPanel();
//		int count = tabPanel.getWidgetCount();
//		int lastAdded = count -1 ;  // would be count - 1 if home ever got registered
//		if (lastAdded < 0) return;
//		TabManager.addTab(lastAdded, this);
//	}

	// access to params shared between tabs
	// delegate to proper model
	public SiteSpec getCommonSiteSpec() { return Xdstools2.getInstance().getQueryState().getSiteSpec(); }
	public void setCommonSiteSpec(SiteSpec s) { Xdstools2.getInstance().getQueryState().setSiteSpec(s); }
	public String getCommonPatientId() { return Xdstools2.getInstance().getQueryState().getPatientId(); }
	public void setCommonPatientId(String p) { Xdstools2.getInstance().getQueryState().setPatientId(p); }

	public String getEnvironmentSelection() { return Xdstools2.getInstance().getEnvironmentState().getEnvironmentName(); }
	public void setEnvironmentSelection(String envName) { Xdstools2.getInstance().getEnvironmentState().setEnvironmentName(envName); }


	/**
	 * This is meant to be overridden by any tab that wants to update
	 * its view when it is selected (redisplayed)
	 * Called by TabManager
	 */
	public void tabIsSelected() { }

	public void globalTabIsSelected() {
		System.out.println("Tab " + this.getClass().getName() + " selected");

		try {
			tabIsSelected();
		} 
		catch (RuntimeException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("calling envMgr.update");

		environmentManager.update();
	}

//	public void onTabSelection() {
//		System.out.println("Tab " + getWindowShortName() + " selected");
//	}

	protected void setTopMessage(String msg) {
		topMessage = msg;
	}

	protected void addToolHeader(DockLayoutPanel panel, String helpHTML, SiteSpec site) {
		if (site != null) {
			String type = (site != null) ? site.getTypeName() : "site";
			String name = (site != null) ? site.name : "name";
			topMessage = "<h3>" + type + ": " + name + "</h3>";
		} else {
			topMessage = "<h3>No site</h3>";
		}
		addToolHeader(panel, helpHTML);
	}

	// all panels getRetrievedDocumentsModel a close button except the home panel
//	protected void addToolHeader(TabContainer container, LayoutPanel topPanel, String helpHTML) {
//
//		this.helpHTML = (helpHTML == null) ? "No Help Available" : helpHTML;
//
//		HTML help = new HTML();
//		help.setHTML("<a href=\"" + "site/tools/" +  getWindowShortName()  + ".html" + "\" target=\"_blank\">" +  "[" + "help" + "]" + "</a>");
//		menuPanel.add(help);
//
//		if (topMessage != null && !topMessage.equals("")) {
//			HTML top = new HTML();
//			top.setHTML(topMessage);
//			top.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
//			menuPanel.add(top);
//			menuPanel.setSpacing(30);
//
//		}
//
//		menuPanel.setSpacing(10);
//		topPanel.add(menuPanel);
//		HTML line = new HTML();
//		line.setHTML("<hr />");
//		topPanel.add(line);
//
////		topPanel.setCellWidth(menuPanel, "100%");
//
//	}

	protected void addToolHeader(DockLayoutPanel topPanel, String helpHTML) {

		this.helpHTML = (helpHTML == null) ? "No Help Available" : helpHTML;

//		menuPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		HTML help = new HTML();
		help.setHTML("<a href=\"" + "site/tools/" +  getWindowShortName()  + ".html" + "\" target=\"_blank\">" +  "[" + "help" + "]" + "</a>");
		menuPanel.add(help);

		if (topMessage != null && !topMessage.equals("")) {
			HTML top = new HTML();
			top.setHTML(topMessage);
			menuPanel.add(top);
		}
//		menuPanel.setSpacing(30);
//		menuPanel.setWidth("100%");

		topPanel.addNorth(new HTML("<hr />"), 4);
		menuPanel.setSpacing(10);
		topPanel.addNorth(menuPanel, 4);

//		tabTopPanel.setCellWidth(menuPanel, "100%");
	}

	public void addToMenu(Anchor anchor) {
		menuPanel.add(anchor);
		//		else
		//			menuPanel.insert(anchor, menuPanel.getWidgetIndex(environmentSelector.getPanel()));
	}

	protected void showMessage(Throwable caught) {
		showMessage(caught.getMessage());
	}

	protected void showMessage(String message) {		
		HTML msgBox = new HTML();
		msgBox.setHTML("<b>" + message + "</b>");
		tabTopPanel.add(msgBox);
	}

	public FlowPanel getTabTopPanel() {
		return tabTopPanel;
	}
}
