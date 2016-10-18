package gov.nist.toolkit.xdstools2.client.selectors;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import gov.nist.toolkit.xdstools2.client.*;
import gov.nist.toolkit.xdstools2.client.command.command.GetEnvironmentNamesCommand;
import gov.nist.toolkit.xdstools2.client.event.Xdstools2EventBus;
import gov.nist.toolkit.xdstools2.client.tabs.EnvironmentState;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;

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
		this.environmentState = ClientUtils.INSTANCE.getEnvironmentState();
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
		}.run(ClientUtils.INSTANCE.getCommandContext());

		ClientUtils.INSTANCE.getToolkitServices().setEnvironment(initialEnvironmentName, new AsyncCallback() {

			@Override
			public void onFailure(Throwable caught) {
				new PopupMessage("setEnvironment(" + initialEnvironmentName + ") failed");
			}

			@Override
			public void onSuccess(Object result) {

			}
			
		});
	}
	
	void getDefaultEnvironment() {
		if (environmentState.getEnvironmentName() == null)
			ClientUtils.INSTANCE.getToolkitServices().getDefaultEnvironment(getDefaultEnvironmentCallback);
	}
	
	AsyncCallback<String> getDefaultEnvironmentCallback = new AsyncCallback<String> () {

		public void onFailure(Throwable caught) {
			new PopupMessage("Call to retrieve default environment name failed: " + caught.getMessage());
		}

		public void onSuccess(String result) {
			environmentState.setEnvironmentName(result);
			
			updateEnvironmentListBox();
			updateSelectionOnScreen();
			updateCookie();
			updateServer();
		}

	};

	
	
	void updateCookie() {
			Cookies.removeCookie(CookieManager.ENVIRONMENTCOOKIENAME);
	}
	
	void updateServer() {
		String envName = environmentState.getEnvironmentName();
		if (envName == null || envName.equals(""))
			return;
		ClientUtils.INSTANCE.getToolkitServices().setEnvironment(envName, setEnvironmentCallback);
	}


	protected AsyncCallback<String> setEnvironmentCallback = new AsyncCallback<String> () {

		public void onFailure(Throwable caught) {
			new PopupMessage(caught.getMessage());
		}

		public void onSuccess(String x) {
		}

	};

	class EnvironmentChangeHandler implements ChangeHandler {

		public void onChange(ChangeEvent event) {
			int selectionI = environmentListBox.getSelectedIndex();
			String value = environmentListBox.getItemText(selectionI);
			change(value);
			
			environmentState.updated(environmentManager);
			((Xdstools2EventBus) ClientUtils.INSTANCE.getEventBus()).fireEnvironmentChangedEvent(value);
			ClientUtils.INSTANCE.getToolkitServices().setEnvironment(value, setEnvironmentCallback);
		}

	}

	boolean isEmpty(String x) { return x == null || x.equals(""); }

	public String getSelectedEnvironment(){
		return environmentListBox.getItemText(environmentListBox.getSelectedIndex());
	}
	
}
