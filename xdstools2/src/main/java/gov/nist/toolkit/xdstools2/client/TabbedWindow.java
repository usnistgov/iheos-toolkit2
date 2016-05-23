package gov.nist.toolkit.xdstools2.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.tk.client.TkProps;
import gov.nist.toolkit.xdstools2.client.event.testSession.TestSessionManager2;
import gov.nist.toolkit.xdstools2.client.selectors.EnvironmentManager;
import gov.nist.toolkit.xdstools2.client.selectors.TestSessionSelector;
import gov.nist.toolkit.xdstools2.client.tabs.TabManager;

import java.util.logging.Level;
import java.util.logging.Logger;


public abstract class TabbedWindow {
	public VerticalPanel topPanel;
	String helpHTML;
	String topMessage = null;
	public HorizontalPanel menuPanel = new HorizontalPanel();
	EnvironmentManager environmentManager = null;
	protected TestSessionManager2 testSessionManager = Xdstools2.getInstance().getTestSessionManager();
	TabContainer tabContainer;
	Logger logger = Logger.getLogger("Tabbed window");
	final public ToolkitServiceAsync toolkitService = GWT
			.create(ToolkitService.class);

	public TabbedWindow() {
	}

	// Used to be protected but impractical for use with the new widget-based architecture in for ex. TestsOverviewTab
	public String getCurrentTestSession() { return testSessionManager.getCurrentTestSession(); }

	abstract public void onTabLoad(TabContainer container, boolean select, String eventName);

	// getWindowShortName() + ".html"is documentation file in /doc
	abstract public String getWindowShortName();

	public void onAbstractTabLoad(TabContainer container, boolean select, String eventName) {
		tabContainer = container;
		logger.log(Level.FINE, "onAbstractTabLoad");
		onTabLoad(container, select, eventName);
		registerTab(container);
		onTabSelection();

		environmentManager = new EnvironmentManager(tabContainer, toolkitService/*, new Panel(menuPanel)*/);
		menuPanel.add(environmentManager);
		menuPanel.add(new TestSessionSelector(testSessionManager.getTestSessions(), testSessionManager.getCurrentTestSession()).asWidget());
	}
	
	public TkProps tkProps() {
		return Xdstools2.tkProps();
	}

	void registerTab(TabContainer container) {
		TabPanel tabPanel = container.getTabPanel();
		int count = tabPanel.getWidgetCount();
		int lastAdded = count -1 ;  // would be count - 1 if home ever got registered
		if (lastAdded < 0) return;
		new TabManager().addTab(lastAdded, this);
	}

	// access to params shared between tabs
	// delegate to proper model
	public SiteSpec getCommonSiteSpec() { return tabContainer.getQueryState().getSiteSpec(); }
	public void setCommonSiteSpec(SiteSpec s) { tabContainer.getQueryState().setSiteSpec(s); }
	public String getCommonPatientId() { return tabContainer.getQueryState().getPatientId(); }
	public void setCommonPatientId(String p) { tabContainer.getQueryState().setPatientId(p); }

	public String getEnvironmentSelection() { return tabContainer.getEnvironmentState().getEnvironmentName(); }
	public void setEnvironmentSelection(String envName) { tabContainer.getEnvironmentState().setEnvironmentName(envName); }


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

	public void onTabSelection() {
		System.out.println("Tab " + getWindowShortName() + " selected");
	}

	protected void setTopMessage(String msg) {
		topMessage = msg;
	}

	protected void addCloseButton(TabContainer container, VerticalPanel panel, String helpHTML, SiteSpec site) {
		if (site != null) {
			String type = (site != null) ? site.getTypeName() : "site";
			String name = (site != null) ? site.name : "name";
			topMessage = "<h3>" + type + ": " + name + "</h3>";
		} else {
			topMessage = "<h3>No site</h3>";
		}
		addCloseButton(container, panel, helpHTML);
	}

	//	// must be called after addCloseButton which initializes the environment
	//	protected void addSigninButton(VerticalPanel vpanel) {
	//		final VerticalPanel panel = vpanel;
	//		
	//		Hyperlink signin = new Hyperlink();
	//		signin.setText("[Sign in]");
	//		signin.setTitle("Sign-in allows editing of configuration");
	//		menuPanel.add(signin);
	//		
	//		signin.addClickHandler(new ClickHandler() {
	//
	//			public void onClick(ClickEvent event) {
	//				new AdminPasswordDialogBox(panel);
	//			}
	//			
	//		});
	//	}

	// all panels getRetrievedDocumentsModel a close button except the home panel
	protected void addCloseButton(TabContainer container, VerticalPanel topPanel, String helpHTML) {

		final VerticalPanel myPanel = topPanel;
		final TabPanel tabPanel = container.getTabPanel();

		this.helpHTML = (helpHTML == null) ? "No Help Available" : helpHTML;

		Anchor close = new Anchor();
		close.setTitle("Close this tab");
		close.setText("[close]");
//		if (Xdstools2.getInstance().toolkitName.equals(GWT.getModuleName())) // Hide this from V3 module
			menuPanel.add(close);
			
		close.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				myPanel.getParent().removeFromParent();

				try {
					tabPanel.selectTab(tabPanel.getWidgetCount() - 1);
				} catch (Exception e) {
				}

				if (environmentManager != null)
					environmentManager.close();
				environmentManager = null;
			}

		});


		HTML help = new HTML();
		help.setHTML("<a href=\"" + "site/tools/" +  getWindowShortName()  + ".html" + "\" target=\"_blank\">" +  "[" + "help" + "]" + "</a>");
		//		topPanel.add(docLink);


		//		Hyperlink help = new Hyperlink();
		//		help.setHTML("[help]");
		//		help.setTitle("Show help for this tab");
		menuPanel.add(help);
		//		help.addClickHandler(new HelpHandler());

		if (topMessage != null && !topMessage.equals("")) {
			HTML top = new HTML();
			top.setHTML(topMessage);
			top.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
			menuPanel.add(top);
			menuPanel.setSpacing(30);

		}

		menuPanel.setSpacing(10);
		topPanel.add(menuPanel);
		HTML line = new HTML();
		line.setHTML("<hr />");
		topPanel.add(line);

		topPanel.setCellWidth(menuPanel, "100%");

		// add environment selector to top menu bar
		//		environmentSelector = EnvironmentSelector.getInstance(toolkitService, new Panel(menuPanel));

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
		topPanel.add(msgBox);		
	}

}
