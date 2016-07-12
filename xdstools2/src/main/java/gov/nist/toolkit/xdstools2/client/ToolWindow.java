package gov.nist.toolkit.xdstools2.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.tk.client.TkProps;
import gov.nist.toolkit.xdstools2.client.event.testSession.TestSessionManager2;
import gov.nist.toolkit.xdstools2.client.selectors.EnvironmentManager;

import java.util.logging.Logger;


public abstract class ToolWindow {
	public FlowPanel tabTopPanel = new FlowPanel();
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
	}

	// Used to be protected but impractical for use with the new widget-based architecture in for ex. TestsOverviewTab
	public String getCurrentTestSession() { return testSessionManager.getCurrentTestSession(); }

	abstract public void onTabLoad(TabContainer container, boolean select, String eventName);

	// getWindowShortName() + ".html"is documentation file in /doc
	abstract public String getWindowShortName();

	public void onAbstractTabLoad(TabContainer container, boolean select, String eventName) {
		tabContainer = TabContainer.instance();
		onTabLoad(container, select, eventName);
//		registerTab(container);
//		onTabSelection();

//		environmentManager = new EnvironmentManager(tabContainer, toolkitService/*, new Panel1(menuPanel)*/);
//		menuPanel.add(environmentManager);
//		menuPanel.add(new TestSessionSelector(testSessionManager.getTestSessions(), testSessionManager.getCurrentTestSession()).asWidget());
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

	protected void addToolHeader(TabContainer container, FlowPanel panel, String helpHTML, SiteSpec site) {
		if (site != null) {
			String type = (site != null) ? site.getTypeName() : "site";
			String name = (site != null) ? site.name : "name";
			topMessage = "<h3>" + type + ": " + name + "</h3>";
		} else {
			topMessage = "<h3>No site</h3>";
		}
		addToolHeader(container, panel, helpHTML);
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

	protected void addToolHeader(TabContainer container, FlowPanel topPanel, String helpHTML) {

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

		topPanel.add(new HTML("<hr />"));
		menuPanel.setSpacing(10);
		topPanel.add(menuPanel);

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

}
