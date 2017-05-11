package gov.nist.toolkit.xdstools2.client.selectors;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import gov.nist.toolkit.xdstools2.client.CookieManager;
import gov.nist.toolkit.xdstools2.client.Panel1;
import gov.nist.toolkit.xdstools2.client.TabContainer;
import gov.nist.toolkit.xdstools2.client.command.command.GetDefaultEnvironmentCommand;
import gov.nist.toolkit.xdstools2.client.command.command.GetEnvironmentNamesCommand;
import gov.nist.toolkit.xdstools2.client.command.command.SetEnvironmentCommand;
import gov.nist.toolkit.xdstools2.client.event.Xdstools2EventBus;
import gov.nist.toolkit.xdstools2.client.initialization.FrameworkInitialization;
import gov.nist.toolkit.xdstools2.client.tabs.EnvironmentState;

import java.util.List;


public class EnvironmentManager extends Composite{
	TabContainer tabContainer;
//	ToolkitServiceAsync toolkitService;
	EnvironmentState environmentState;
	Panel1 menuPanel;
	HorizontalPanel environmentPanel = new HorizontalPanel();
	ListBox environmentListBox = new ListBox();
	static String choose = "-- Choose --";
	boolean needsUpdating = false;
	EnvironmentManager environmentManager;  // really this


	
	public EnvironmentManager(TabContainer tabContainer) {
		this.tabContainer = tabContainer;
		this.environmentState = FrameworkInitialization.data().getEnvironmentState();
		environmentManager = this;
		
		environmentState.addManager(this);
		
		init();
		
	}
	
	public void close() {
		environmentState.deleteManager(this);
	}
	
	void init() {
		menuPanel=new Panel1(environmentPanel);

		HTML environmentLabel = new HTML();
		environmentLabel.setText("Environment: ");
		environmentPanel.add(environmentLabel);

		environmentPanel.add(environmentListBox);
		environmentListBox.addChangeHandler(new EnvironmentChangeHandler());

//		updateEnvironmentListBox();
		
//		loadEnvironmentNames(null);
//		getDefaultEnvironment();

		updateEnvironmentListBox();
		updateSelectionOnScreen();
		updateCookie();

		initWidget(environmentPanel);
	}
	
	
	public void needsUpdating() {
		needsUpdating = true;
		System.out.println("Environment Manager " + environmentState.getManagerIndex(this) + " needs updating");
	}
	
	public void update() {
		System.out.println("Updating Environment " + environmentState.getManagerIndex(this) + "???");
		if (!needsUpdating) return;
		needsUpdating = false;
		System.out.println("Updating Environment " + environmentState.getManagerIndex(this));
		updateEnvironmentListBox();
		updateSelectionOnScreen();
	}
		
	private boolean updateSelectionOnScreen() {
		String sel = null;
		if (environmentState.isValid())
			sel = environmentState.getEnvironmentName();
		if (isEmpty(sel))
			sel = choose;

		for (int i=0; i<environmentListBox.getItemCount(); i++) {
			if (sel.equals(environmentListBox.getItemText(i))) { 
				environmentListBox.setSelectedIndex(i);
				return true;
			}
		}
		return false;
	}

	
	private void updateEnvironmentListBox() {
		environmentListBox.clear();
		environmentListBox.addItem(choose, "");
		for (String val : environmentState.getEnvironmentNameChoices())
			environmentListBox.addItem(val);
	}
	
	public void change(String environmentName) {
		environmentState.setEnvironmentName(environmentName);
		updateSelectionOnScreen();
		updateCookie();
		updateServer();
	}

	
	@SuppressWarnings("rawtypes")
	private void loadEnvironmentNames(final String initialEnvironmentName) {

		new GetEnvironmentNamesCommand() {

			@Override
			public void onComplete(List<String> result) {
				environmentState.setEnvironmentNameChoices(result);
				if (environmentState.getEnvironmentName() == null)
					environmentState.setEnvironmentName(initialEnvironmentName);

				if (environmentState.isFirstManager() && !environmentState.isValid())
					getDefaultEnvironment();
				else {
					updateEnvironmentListBox();
					updateSelectionOnScreen();
					updateCookie();
				}
			}
		}.run(FrameworkInitialization.data().getCommandContext());

		new SetEnvironmentCommand().run(FrameworkInitialization.data().getCommandContext().setEnvironmentName(initialEnvironmentName));
	}
	
	void getDefaultEnvironment() {
		if (environmentState.getEnvironmentName() == null)
			new GetDefaultEnvironmentCommand(){

				@Override
				public void onComplete(String result) {
					environmentState.setEnvironmentName(result);

					updateEnvironmentListBox();
					updateSelectionOnScreen();
					updateCookie();
					updateServer();
				}
			}.run(FrameworkInitialization.data().getCommandContext());
	}

	void updateCookie() {
			Cookies.removeCookie(CookieManager.ENVIRONMENTCOOKIENAME);
	}
	
	void updateServer() {
		new SetEnvironmentCommand().run(FrameworkInitialization.data().getCommandContext());
	}

	class EnvironmentChangeHandler implements ChangeHandler {

		public void onChange(ChangeEvent event) {
			int selectionI = environmentListBox.getSelectedIndex();
			String value = environmentListBox.getItemText(selectionI);
			change(value);
			
			environmentState.updated(environmentManager);
			((Xdstools2EventBus) FrameworkInitialization.data().getEventBus()).fireEnvironmentChangedEvent(value);
			new SetEnvironmentCommand().run(FrameworkInitialization.data().getCommandContext().setEnvironmentName(value));
		}

	}

	boolean isEmpty(String x) { return x == null || x.equals(""); }

	public String getSelectedEnvironment(){
		return environmentListBox.getItemText(environmentListBox.getSelectedIndex());
	}
	
}
