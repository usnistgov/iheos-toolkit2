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
import gov.nist.toolkit.xdstools2.client.tabs.EnvironmentState;

import java.util.List;

public class EnvironmentManager extends Composite{
	TabContainer tabContainer;
	ToolkitServiceAsync toolkitService;
	EnvironmentState environmentState;
	Panel menuPanel;
	HorizontalPanel environmentPanel = new HorizontalPanel();
	ListBox environmentListBox = new ListBox();
	static String choose = "-- Choose --";
	boolean needsUpdating = false;
	EnvironmentManager environmentManager;  // really this


	
	public EnvironmentManager(TabContainer tabContainer, ToolkitServiceAsync toolkitService/*, Panel menuPanel*/) {
		this.tabContainer = tabContainer;
		this.toolkitService = toolkitService;
		this.environmentState = tabContainer.getEnvironmentState();
//		this.menuPanel = menuPanel;
		environmentManager = this;
		
		environmentState.addManager(this);
		
		init();
		
	}
	
	public void close() {
		environmentState.deleteManager(this);
	}
	
	void init() {
		menuPanel=new Panel(environmentPanel);
//		menuPanel.add(environmentPanel);

		HTML environmentLabel = new HTML();
		environmentLabel.setText("Environment: ");
		environmentPanel.add(environmentLabel);

		environmentPanel.add(environmentListBox);
		environmentListBox.addChangeHandler(new EnvironmentChangeHandler());

		updateEnvironmentListBox();
		
		loadEnvironmentNames(null);
		getDefaultEnvironment();

		initWidget(environmentPanel);

//		loadEnvironmentNames(Cookies.getCookie(CookieManager.ENVIRONMENTCOOKIENAME));
		
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
		
	boolean updateSelectionOnScreen() {
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

	
	void updateEnvironmentListBox() {
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
	void loadEnvironmentNames(final String initialEnvironmentName) {
		toolkitService.getEnvironmentNames(new AsyncCallback<List<String>>() {

			public void onFailure(Throwable caught) {
				new PopupMessage("getEnvironmentNames: " + caught.getMessage());
			}

			public void onSuccess(List<String> result) {
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

		});
		
		toolkitService.setEnvironment(initialEnvironmentName, new AsyncCallback() {

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
			toolkitService.getDefaultEnvironment(getDefaultEnvironmentCallback);
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
//		if (environmentState.isValid())
//			Cookies.setCookie(CookieManager.ENVIRONMENTCOOKIENAME, environmentState.getEnvironmentName());
//		else
			Cookies.removeCookie(CookieManager.ENVIRONMENTCOOKIENAME);		
	}
	
	void updateServer() {
		String envName = environmentState.getEnvironmentName();
		if (envName == null || envName.equals(""))
			return;
		toolkitService.setEnvironment(envName, setEnvironmentCallback);
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

			toolkitService.setEnvironment(value, setEnvironmentCallback);
		}

	}

	boolean isEmpty(String x) { return x == null || x.equals(""); }

	public String getSelectedEnvironment(){
		return environmentListBox.getItemText(environmentListBox.getSelectedIndex());
	}
	
}
