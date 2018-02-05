package gov.nist.toolkit.xdstools2.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.tk.client.TkProps;
import gov.nist.toolkit.xdstools2.client.event.testSession.TestSessionManager2;
import gov.nist.toolkit.xdstools2.client.injector.Injector;
import gov.nist.toolkit.xdstools2.client.selectors.EnvironmentManager;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

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
	Logger logger = Logger.getLogger("Tabbed window");

	private DockLayoutPanel tabTopRawPanel = new DockLayoutPanel(Style.Unit.EM);
	private ScrollPanel innerPanel = new ScrollPanel();
	public FlowPanel tabTopPanel = new FlowPanel();
	private FlowPanel eastPanel = new FlowPanel();
	private FlowPanel westPanel = new FlowPanel();

	public HorizontalPanel menuPanel = new HorizontalPanel();
//	protected TabContainer tabContainer;
	String helpHTML;
	String topMessage = null;

	EnvironmentManager environmentManager = null;
	protected TestSessionManager2 testSessionManager = ClientUtils.INSTANCE.getTestSessionManager();
	protected String tabName=new String();

//	protected abstract Widget buildUI();
//	protected abstract void bindUI();
	public abstract void onTabLoad(boolean select, String eventName);
	// getWindowShortName() + ".html"is documentation file in /doc
	abstract public String getWindowShortName();

	public ToolWindow() {
		tabContainer = Injector.INSTANCE.getTabContainer();
		String title = getTitle();
		// .addNorth MUST come before .display - a condition of DockLayoutPanel
		if (title != null)
			tabTopRawPanel.addNorth(new HTML("<h1>" + title + "</h1>"), 4.0);
		tabTopRawPanel.addEast(eastPanel, 0.0);
		tabTopRawPanel.addWest(westPanel, 0.0);
		tabTopRawPanel.add(innerPanel);
		innerPanel.setWidget(tabTopPanel);
	}

	public ToolWindow(double east, double west) {
		tabContainer = Injector.INSTANCE.getTabContainer();
		String title = getTitle();
		// .addNorth MUST come before .display - a condition of DockLayoutPanel
		if (title != null)
			tabTopRawPanel.addNorth(new HTML("<h1>" + title + "</h1>"), 4.0);
		tabTopRawPanel.addEast(eastPanel, east);
		tabTopRawPanel.addWest(westPanel, west);
		tabTopRawPanel.add(innerPanel);
		innerPanel.setWidget(tabTopPanel);
	}

	public ToolWindow(boolean isWidget) {
	}

	protected void addEast(Widget w) { eastPanel.add(w); }

	protected void addWest(Widget w) { westPanel.add(w); }

	public TabContainer getTabContainer() { return tabContainer; }

	public DockLayoutPanel getRawPanel() { return tabTopRawPanel; }

	public void useRawPanel(Widget windowRoot) {
		innerPanel.setWidget(windowRoot);
	}

	public CommandContext getCommandContext() {
		return ClientUtils.INSTANCE.getCommandContext();
	}

	/**
	 * Override when using Raw Mode - only way to set title in raw mode
	 * @return
	 */
	public String getTitle() { return null; }

	// Used to be protected but impractical for use with the new widget-based architecture in for ex. TestsOverviewTab
	public String getCurrentTestSession() {
		return testSessionManager.getCurrentTestSession();
	}

	public void setCurrentTestSession(String testSession) { testSessionManager.setCurrentTestSession(testSession);}

	private TabContainer tabContainer;
	private int index = -1;

	public int registerTab(boolean select, String tabName) {
		this.tabName=tabName;
		assert(tabContainer != null);
		index = tabContainer.addTabWithIndex(tabTopRawPanel, null, tabName, select);
		return index;
	}

	public void registerDeletableTab(boolean select, String tabName, NotifyOnDelete notifyOnDelete) {
		this.tabName=tabName;
		assert(tabContainer != null);
		tabContainer.addDeletableTab(tabTopRawPanel, null, tabName,select, notifyOnDelete);
	}

	public void deleteMe() {
		tabContainer.rmTab(index);
	}

	public TkProps tkProps() {
		return Xdstools2.tkProps();
	}

	// access to params shared between tabs
	// delegate to proper model
	public SiteSpec getCommonSiteSpec() { return Xdstools2.getInstance().getQueryState().getSiteSpec(); }
	public void setCommonSiteSpec(SiteSpec s) { Xdstools2.getInstance().getQueryState().setSiteSpec(s); }
	public String getCommonPatientId() { return Xdstools2.getInstance().getQueryState().getPatientId(); }
	public void setCommonPatientId(String p) { Xdstools2.getInstance().getQueryState().setPatientId(p); }

	public String getEnvironmentSelection() { return ClientUtils.INSTANCE.getEnvironmentState().getEnvironmentName(); }
//	public void setEnvironmentSelection(String envName) { ClientUtils.INSTANCE.getEnvironmentState().setEnvironmentName(envName); }


	/**
	 * This is meant to be overridden by any tab that wants to update
	 * its view when it is selected (redisplayed)
	 * Called by TabManager
	 */
	public void tabIsSelected() {
	}

	public void globalTabIsSelected() {
		GWT.log("Tab " + this.getClass().getName() + " selected");

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
//		menuPanel.display(help);
//
//		if (topMessage != null && !topMessage.equals("")) {
//			HTML top = new HTML();
//			top.setHTML(topMessage);
//			top.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
//			menuPanel.display(top);
//			menuPanel.setSpacing(30);
//
//		}
//
//		menuPanel.setSpacing(10);
//		topPanel.display(menuPanel);
//		HTML line = new HTML();
//		line.setHTML("<hr />");
//		topPanel.display(line);
//
////		topPanel.setCellWidth(menuPanel, "100%");
//
//	}

	protected void addToolHeader(DockLayoutPanel topPanel, String helpHTML) {

		this.helpHTML = (helpHTML == null) ? "No Help Available" : helpHTML;

		HTML help = new HTML();
		help.setHTML("<a href=\"" + "site/tools/" +  getWindowShortName()  + ".html" + "\" target=\"_blank\">" +  "[" + "help" + "]" + "</a>");
		menuPanel.add(help);

		if (topMessage != null && !topMessage.equals("")) {
			HTML top = new HTML();
			top.setHTML(topMessage);
			menuPanel.add(top);
		}

		topPanel.addNorth(new HTML("<hr />"), 4);
		menuPanel.setSpacing(10);
		topPanel.addNorth(menuPanel, 4);
	}

	public void addToMenu(Anchor anchor) {
		menuPanel.add(anchor);
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
