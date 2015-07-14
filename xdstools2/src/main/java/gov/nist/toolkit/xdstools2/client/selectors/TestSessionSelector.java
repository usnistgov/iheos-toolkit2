package gov.nist.toolkit.xdstools2.client.selectors;

import gov.nist.toolkit.xdstools2.client.CookieManager;
import gov.nist.toolkit.xdstools2.client.Panel;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.ToolkitServiceAsync;

import java.util.ArrayList;
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

public class TestSessionSelector {
	HorizontalPanel testSessionPanel = new HorizontalPanel();
	ListBox testSessionListBox = new ListBox();
	TextBox testSessionTextBox = new TextBox();
	ToolkitServiceAsync toolkitService;
	boolean isPrivateTesting = false;
	Panel menuPanel;

	static String choose = "-- Choose --";
	static List<String> testSessionList = new ArrayList<String>();
	static String testSession = null;
	static List<TestSessionSelector> instances = new ArrayList<TestSessionSelector>();

	public static TestSessionSelector getInstance(ToolkitServiceAsync toolkitService, Panel menuPanel) {
		for (TestSessionSelector sel : instances) {
			if (sel.menuPanel == menuPanel) 
				return sel;
		}
		TestSessionSelector sel = new TestSessionSelector(toolkitService, menuPanel);
		instances.add(sel);
		return sel;
	}

	// only to get to static vars
	public TestSessionSelector() {}
	
	public static void delete(TestSessionSelector sel) {
		instances.remove(sel);
	}
	
	public static void change(String testSessionName) {
		testSession = testSessionName;
		
		for (TestSessionSelector sel : instances) {
			sel.updateTestSessionListBox();
			sel.updateSelectionOnScreen();
		}
	}
	
	

	TestSessionSelector(ToolkitServiceAsync toolkitService, Panel menuPanel) {
		this.toolkitService = toolkitService;
		this.menuPanel = menuPanel;
		
		testSessionPanel.setVisible(false);
		menuPanel.add(testSessionPanel);
		
		HTML testSessionLabel = new HTML();
		testSessionLabel.setText("Test Session: ");
		testSessionPanel.add(testSessionLabel);
		
		testSessionPanel.add(testSessionListBox);
		testSessionListBox.addChangeHandler(new TestSessionChangeHandler());
		loadTestSessionEnabled();
		
		testSession = Cookies.getCookie(CookieManager.TESTSESSIONCOOKIENAME);

		testSessionPanel.add(testSessionTextBox);

		setMesaTestSession();

		
		
		Button addTestSessionButton = new Button("Add");
		testSessionPanel.add(addTestSessionButton);
		addTestSessionButton.addClickHandler(new AddTestSessionClickHandler());

	}
	
	void setMesaTestSession() {
		toolkitService.setMesaTestSession(testSession, new TestSessionChangeHandler().setTestSessionCallback);
	}
	
	public String getTestSession() {
		return testSession;
	}
	
	public boolean isPrivateTesting() {
		return isPrivateTesting;
	}

	public void updateCookie() {
		if ("".equals(testSession) || choose.equals(testSession))
			testSession = null;
		
		if (testSession == null || choose.equals(testSession))
			Cookies.removeCookie(CookieManager.TESTSESSIONCOOKIENAME);
		else {
			Cookies.setCookie(CookieManager.TESTSESSIONCOOKIENAME, testSession);
			setMesaTestSession();
		}
		
	}
	
	void updateTestSessionListBox() {
		testSessionListBox.clear();
		testSessionListBox.addItem(choose, "");
		for (String val : testSessionList)
			testSessionListBox.addItem(val);
	}
	
	void updateSelectionOnScreen() {
		String sel = testSession;
		if (testSession == null || "".equals(testSession)) 
			sel = choose;
		
		for (int i=0; i<testSessionListBox.getItemCount(); i++) {
			if (sel.equals(testSessionListBox.getItemText(i))) { 
				testSessionListBox.setSelectedIndex(i);
				break;
			}

		}

	}

	class TestSessionChangeHandler implements ChangeHandler {

		public void onChange(ChangeEvent event) {
			int selectionI = testSessionListBox.getSelectedIndex();
			String sessionName = testSessionListBox.getItemText(selectionI); 
			change(sessionName);
			updateCookie();
			
			testSession = sessionName;
			
			setMesaTestSession();
		}

		protected AsyncCallback<String> setTestSessionCallback = new AsyncCallback<String> () {

			public void onFailure(Throwable caught) {
				new PopupMessage(caught.getMessage());
			}

			public void onSuccess(String x) {
			}
			
		};

		
	}
	
	class AddTestSessionClickHandler implements ClickHandler {

		public void onClick(ClickEvent event) {
			toolkitService.addMesaTestSession(testSessionTextBox.getText(), new AsyncCallback<Boolean>() {

				public void onFailure(Throwable caught) {
					new PopupMessage("addMesaTestSession: " + caught.getMessage());
				}

				public void onSuccess(Boolean result) {
					testSession = testSessionTextBox.getText();
					loadTestSessionNames();
					testSession = testSessionTextBox.getText();
				}
				
			});
		}
		
	}
	
	void loadTestSessionEnabled() {
		toolkitService.isPrivateMesaTesting(new AsyncCallback<Boolean>() {

			public void onFailure(Throwable caught) {
				new PopupMessage("isPrivateMesaTesting: " + caught.getMessage());
			}

			public void onSuccess(Boolean result) {
				isPrivateTesting = result;
				if (!result)
					return;
				loadTestSessionNames();
			}
			
		});
	}
	
	
	void loadTestSessionNames() {
		toolkitService.getMesaTestSessionNames(new AsyncCallback<List<String>>() {

			public void onFailure(Throwable caught) {
				new PopupMessage("getMesaTestSessionNames: " + caught.getMessage());
			}

			public void onSuccess(List<String> result) {
				testSessionList.clear();
				testSessionList.addAll(result);
				updateTestSessionListBox();
				
				change(testSession);
		
				testSessionPanel.setVisible(true);
				testSessionTextBox.setText("");
			}
			
		});
	}
	
}
