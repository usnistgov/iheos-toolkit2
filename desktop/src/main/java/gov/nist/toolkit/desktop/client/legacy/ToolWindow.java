package gov.nist.toolkit.desktop.client.legacy;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.desktop.client.ClientUtils;
import gov.nist.toolkit.desktop.client.TabContainer;
import gov.nist.toolkit.desktop.client.abstracts.AbstractToolkitActivity;
import gov.nist.toolkit.server.shared.command.CommandContext;
import gov.nist.toolkit.desktop.client.injection.ToolkitGinInjector;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;

import javax.inject.Inject;
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

	// Tab content goes here
	public FlowPanel tabTopPanel = new FlowPanel();
	private FlowPanel eastPanel = new FlowPanel();
	private FlowPanel westPanel = new FlowPanel();

	public  HorizontalPanel menuPanel = new HorizontalPanel();
	private String helpHTML;
	private String topMessage = null;

	protected  String tabName=new String();

	public abstract Widget buildUI();
	public abstract void bindUI();
	public abstract void onTabLoad(boolean select, String eventName);
	// getWindowShortName() + ".html"is documentation file in /doc
	abstract public String getWindowShortName();

	AbstractToolkitActivity activity = null;

	public void setActivity(AbstractToolkitActivity activity) { this.activity = activity; }

	@Inject
	private TabContainer tabContainer;

	@Inject
	public ToolWindow() {
		assert(tabContainer != null);

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
		String title = getTitle();
		// .addNorth MUST come before .display - a condition of DockLayoutPanel
		if (title != null)
			tabTopRawPanel.addNorth(new HTML("<h1>" + title + "</h1>"), 4.0);
		tabTopRawPanel.addEast(eastPanel, east);
		tabTopRawPanel.addWest(westPanel, west);
		tabTopRawPanel.add(innerPanel);
		innerPanel.setWidget(tabTopPanel);
	}

	protected void addEast(Widget w) { eastPanel.add(w); }

	protected void addWest(Widget w) { westPanel.add(w); }

	public TabContainer getTabContainer() { return tabContainer; }

	public DockLayoutPanel getRawPanel() { return tabTopRawPanel; }

	public void useRawPanel(Widget windowRoot) {
		innerPanel.setWidget(windowRoot);
	}

	public CommandContext getCommandContext() {
		return ClientUtils.INSTANCE.getCurrentCommandContext();
	}

	/**
	 * Override when using Raw Mode - only way to set title in raw mode
	 * @return
	 */
	public String getTitle() { return null; }

	// Used to be protected but impractical for use with the new widget-based architecture in for ex. TestsOverviewTab
	public String getCurrentTestSession() {
		return ToolkitGinInjector.INSTANCE.getTestSessionMVP().getPresenter().getTestSessionName();
	}

//	public void setCurrentTestSession(String testSession) { testSessionManager.setCurrentTestSession(testSession);}

	public void registerTab(boolean select, String tabName) {
		assert(activity != null);
		this.tabName=tabName;
		tabContainer.addTab(tabTopRawPanel, tabName, activity);
	}

	// access to params shared between tabs
	// delegate to proper model
	public SiteSpec getCommonSiteSpec() { return ClientUtils.INSTANCE.getQueryState().getSiteSpec(); }
	public void setCommonSiteSpec(SiteSpec s) { ClientUtils.INSTANCE.getQueryState().setSiteSpec(s); }
	public String getCommonPatientId() { return ClientUtils.INSTANCE.getQueryState().getPatientId(); }
	public void setCommonPatientId(String p) { ClientUtils.INSTANCE.getQueryState().setPatientId(p); }

//	public String getEnvironmentSelection() { return XdsTools2Presenter.data().getEnvironmentState().getEnvironmentName(); }
//	public void setEnvironmentSelection(String envName) { ClientUtils.INSTANCE.getEnvironmentState().setEnvironmentName(envName); }


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

//		environmentManager.update();
	}

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
