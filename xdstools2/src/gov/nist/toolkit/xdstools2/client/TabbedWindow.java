package gov.nist.toolkit.xdstools2.client;

import gov.nist.toolkit.results.client.SiteSpec;
import gov.nist.toolkit.tk.client.TkProps;
import gov.nist.toolkit.xdstools2.client.selectors.EnvironmentManager;
import gov.nist.toolkit.xdstools2.client.selectors.TestSessionManager;
import gov.nist.toolkit.xdstools2.client.tabs.TabManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;


public abstract class TabbedWindow {
	public VerticalPanel topPanel;
	String helpHTML;
	String topMessage = null;
	public HorizontalPanel menuPanel = new HorizontalPanel();
	//	EnvironmentSelector environmentSelector = null;
	EnvironmentManager environmentManager = null;
	public TestSessionManager testSessionManager = null;
	boolean envMgrEnabled = true;
	boolean testSesMgrEnabled = true;
	TabContainer tabContainer;

	final public ToolkitServiceAsync toolkitService = GWT
			.create(ToolkitService.class);

	public TabbedWindow() {
	}


	abstract public void onTabLoad(TabContainer container, boolean select, String eventName);
	
	public void disableEnvMgr() { envMgrEnabled = false; }
	public void disableTestSesMgr() { testSesMgrEnabled = false; }

	// getWindowShortName() + ".html"is documentation file in /doc
	abstract public String getWindowShortName();

	public void onAbstractTabLoad(TabContainer container, boolean select, String eventName) {
		tabContainer = container;
		onTabLoad(container, select, eventName);
		registerTab(container);
		onTabSelection();

		if (Xdstools2.showEnvironment) {
			if (envMgrEnabled)
				environmentManager = new EnvironmentManager(tabContainer, toolkitService, new Panel(menuPanel));
			if (testSesMgrEnabled && testSessionManager == null)
				testSessionManager = new TestSessionManager(tabContainer, toolkitService, new Panel(menuPanel));
		}
	}
	
	public void loadTestSessionManagerEarly() {
		testSessionManager = new TestSessionManager(tabContainer, toolkitService, new Panel(menuPanel));
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
	// delegate to proper object
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

	// all panels get a close button except the home panel
	protected void addCloseButton(TabContainer container, VerticalPanel topPanel, String helpHTML) {

		final VerticalPanel myPanel = topPanel;
		final TabPanel tabPanel = container.getTabPanel();

		this.helpHTML = (helpHTML == null) ? "No Help Available" : helpHTML;

		Anchor close = new Anchor();
		close.setTitle("Close this tab");
		close.setText("[close]");
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
				environmentManager = null;
				if (testSessionManager != null)
					testSessionManager.close();
				testSessionManager = null;

			}

		});


		HTML help = new HTML();
		help.setHTML("<a href=\"" + "doc/" +  getWindowShortName()  + ".html" + "\" target=\"_blank\">" +  "[" + "help" + "]" + "</a>");
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
		topPanel.setCellWidth(menuPanel, "100%");

		// add environment selector to top menu bar
		//		environmentSelector = EnvironmentSelector.getInstance(toolkitService, new Panel(menuPanel));

	}

	public TestSessionManager getTestSessionManager() {
		return testSessionManager;
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
