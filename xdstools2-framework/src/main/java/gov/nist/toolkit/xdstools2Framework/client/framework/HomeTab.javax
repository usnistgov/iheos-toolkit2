package gov.nist.toolkit.xdstools2Framework.client.framework;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.toolkitFramework.client.commands.GetAdminPasswordCommand;
import gov.nist.toolkit.toolkitFramework.client.commands.GetImplementationVersionCommand;
import gov.nist.toolkit.toolkitFramework.client.toolSupport.TabContainer;
import gov.nist.toolkit.toolkitFramework.client.toolSupport.ToolWindow;
import gov.nist.toolkit.toolkitFramework.client.util.MainMenu;
import gov.nist.toolkit.toolkitFramework.client.util.PasswordManagement;
import gov.nist.toolkit.toolkitFramework.client.widgets.HorizontalFlowPanel;

import javax.inject.Inject;

public class HomeTab extends ToolWindow {
	private String aboutMessage = null;
	private HorizontalFlowPanel menubar = new HorizontalFlowPanel();
	
	@Inject
	XdsTools2AppView xdsTools2AppView;

	@Inject
	MainMenu mainMenu;

	public HomeTab() {
		super();
	}

	@Override
	public Widget buildUI() {
		FlowPanel panel = new FlowPanel();
		panel.add(menubar);

//		menubar.addTest(
//				HyperlinkFactory.launchTool("&nbsp;&nbsp;[" + ToolLauncher.toolConfigTabLabel + "]&nbsp;&nbsp;", new ToolLauncher(ToolLauncher.toolConfigTabLabel))
//		);

		Frame frame = new Frame("site/index.html");
		frame.setSize("100em", "100em");
		panel.add(frame);
		return panel;
	}

	@Override
	public void bindUI() {
		String th = "";

//		mainGrid = new FlexTable();
//		mainGrid.setCellSpacing(20);

		loadIHEGrid();
		new GetAdminPasswordCommand(){
			@Override
			public void onComplete(String result) {
				PasswordManagement.adminPassword = result;
			}
		}.run(getCommandContext());
		loadVersion();
	}

//	@Override
//	protected void configureTabView() {
//		addActorReloader();
//	}

//	@Override
////	public void onTabLoad(final xdsTools2AppView container, boolean select, String eventName) {
//	public void onTabLoad(boolean select, String eventName) {
//
//
//
//		select = true;
//		registerTab(select, eventName);
////		tabTopPanel.addTest(new HTML("Menu Bar"));
////		tabTopPanel.
//
//
//	}

	boolean forDirect = false;
	boolean forIHE = false;
	boolean forNwHIN = false;
	boolean displayTab = true;

	public void setDisplayTab(boolean displayTab) {
		this.displayTab = displayTab;
	}

	class MainGridLoader {

		//@Override
		public void featuresLoadedCallback() {

//				tabTopPanel.addTest(mainGrid);


		}

	}

	public void loadIHEGrid() {
		mainMenu.loadMenu(xdsTools2AppView);
	}

	int getIndex(ListBox lb, String value) {
		for (int i=0; i<lb.getItemCount(); i++) {
			String lbVal = lb.getItemText(i);
			if (value.equals(lbVal))
				return i;
		}
		return -1;
	}


	@Override
	public void onTabLoad(boolean select, String eventName) {

	}

	public String getWindowShortName() {
		return "home";
	}

	void loadVersion() {

		new GetImplementationVersionCommand(){
			@Override
			public void onComplete(String result) {
				aboutMessage =  "XDS Toolkit\n" + result;
			}
		}.run(getCommandContext());
	}


	public void onTabLoad(TabContainer container, boolean select,
						  String eventName) {

	}




}
