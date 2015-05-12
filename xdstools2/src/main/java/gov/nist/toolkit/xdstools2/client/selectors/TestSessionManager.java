package gov.nist.toolkit.xdstools2.client.selectors;

import gov.nist.toolkit.xdstools2.client.CookieManager;
import gov.nist.toolkit.xdstools2.client.Panel;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.TabContainer;
import gov.nist.toolkit.xdstools2.client.ToolkitServiceAsync;
import gov.nist.toolkit.xdstools2.client.selectors.TestSessionSelector.AddTestSessionClickHandler;
import gov.nist.toolkit.xdstools2.client.tabs.TestSessionState;

import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;

public class TestSessionManager {
	TabContainer tabContainer;
	ToolkitServiceAsync toolkitService;
	TestSessionState testSessionState;
	Panel menuPanel;
	HorizontalPanel testSessionPanel = new HorizontalPanel();
	ListBox testSessionListBox = new ListBox();
	TextBox testSessionTextBox = new TextBox();
	static String choose = "-- Choose --";
	boolean needsUpdating = false;
	TestSessionManager testSessionManager;  // really this
	static String testSession = null;
	
	public TestSessionManager(TabContainer tabContainer, ToolkitServiceAsync toolkitService, Panel menuPanel) {
		this.tabContainer = tabContainer;
		this.toolkitService = toolkitService;
		this.testSessionState = tabContainer.getTestSessionState();
		this.menuPanel = menuPanel;
		testSessionManager = this;
		
		testSessionState.addManager(this);
		
		init();
		
	}
	
	class AddTestSessionClickHandler implements ClickHandler {

		public void onClick(ClickEvent event) {
			final String newItem = testSessionTextBox.getText();
			toolkitService.addMesaTestSession(newItem, new AsyncCallback<Boolean>() {

				public void onFailure(Throwable caught) {
					new PopupMessage("addMesaTestSession: " + caught.getMessage());
				}

				public void onSuccess(Boolean result) {
					testSession = testSessionTextBox.getText();
					loadTestSessionNames(newItem);
					testSession = testSessionTextBox.getText();
				}
				
			});
		}
		
	}
	
	public void close() {
		testSessionState.deleteManager(this);
	}
	
	void init() {
		menuPanel.add(testSessionPanel);

		HTML testSessionLabel = new HTML();
		testSessionLabel.setText("TestSession: ");
		testSessionPanel.add(testSessionLabel);

		testSessionPanel.add(testSessionListBox);
		testSessionListBox.addChangeHandler(new TestSessionChangeHandler());

		testSessionPanel.add(testSessionTextBox);
		Button addTestSessionButton = new Button("Add");
		testSessionPanel.add(addTestSessionButton);
		addTestSessionButton.addClickHandler(new AddTestSessionClickHandler());

		
		updateTestSessionListBox();

		loadTestSessionNames(Cookies.getCookie(CookieManager.TESTSESSIONCOOKIENAME));
		
	}
	
	
	public void needsUpdating() {
		needsUpdating = true;
		System.out.println("TestSession Manager " + testSessionState.getManagerIndex(this) + " needs updating");
	}
	
	public void update() {
		System.out.println("Updating TestSession " + testSessionState.getManagerIndex(this) + "???");
		if (!needsUpdating) return;
		needsUpdating = false;
		System.out.println("Updating TestSession " + testSessionState.getManagerIndex(this));
		updateTestSessionListBox();
		updateSelectionOnScreen();
	}
		
	boolean updateSelectionOnScreen() {
		String sel = null;
		if (testSessionState.isValid())
			sel = testSessionState.getTestSessionName();
		if (isEmpty(sel))
			sel = choose;

		for (int i=0; i<testSessionListBox.getItemCount(); i++) {
			if (sel.equals(testSessionListBox.getItemText(i))) { 
				testSessionListBox.setSelectedIndex(i);
				return true;
			}
		}
		return false;
	}
	
	public String getCurrentSelection() {
		int selectedIndex = testSessionListBox.getSelectedIndex();
		if (selectedIndex == -1)
			return "";
		return testSessionListBox.getItemText(selectedIndex);
	}

	
	void updateTestSessionListBox() {
		testSessionListBox.clear();
		testSessionListBox.addItem(choose, "");
		for (String val : testSessionState.getTestSessionNameChoices())
			testSessionListBox.addItem(val);
	}
	
	public void change(String testSessionName) {
		testSessionState.setTestSessionName(testSessionName);
		updateSelectionOnScreen();
		updateCookie();
		updateServer();
	}

	
	void loadTestSessionNames(final String initialTestSessionName) {
		toolkitService.getMesaTestSessionNames(new AsyncCallback<List<String>>() {

			public void onFailure(Throwable caught) {
				new PopupMessage("getTestSessionNames: " + caught.getMessage());
			}

			public void onSuccess(List<String> result) {
				testSessionState.setTestSessionNameChoices(result);
				testSessionState.setTestSessionName(initialTestSessionName);

				updateServer();
				updateTestSessionListBox();
				updateSelectionOnScreen();
				updateCookie();
			}

		});
	}
	
	
	void updateCookie() {
		if (testSessionState.isValid())
			Cookies.setCookie(CookieManager.TESTSESSIONCOOKIENAME, testSessionState.getTestSessionName());
		else
			Cookies.removeCookie(CookieManager.TESTSESSIONCOOKIENAME);		
	}
	
	void updateServer() {
		toolkitService.setMesaTestSession(testSessionState.getTestSessionName(), setTestSessionCallback);
	}


	protected AsyncCallback<String> setTestSessionCallback = new AsyncCallback<String> () {

		public void onFailure(Throwable caught) {
			new PopupMessage(caught.getMessage());
		}

		public void onSuccess(String x) {
		}

	};

	class TestSessionChangeHandler implements ChangeHandler {

		public void onChange(ChangeEvent event) {
			int selectionI = testSessionListBox.getSelectedIndex();
			String value = testSessionListBox.getItemText(selectionI);
			change(value);
			
			testSessionState.updated(testSessionManager);

			toolkitService.setMesaTestSession(value, setTestSessionCallback);
		}

	}

	boolean isEmpty(String x) { return x == null || x.equals(""); }
	

}
