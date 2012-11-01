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

public class PatientIdSelector {
	HorizontalPanel patientIdPanel = new HorizontalPanel();
	ListBox patientIdListBox = new ListBox();
	TextBox patientIdTextBox = new TextBox();
	ToolkitServiceAsync toolkitService;
	boolean isPrivateTesting = false;
	Panel menuPanel;

	static String choose = "-- Choose --";
	static List<String> patientIdList = new ArrayList<String>();
	static String patientId = null;
	static List<PatientIdSelector> instances = new ArrayList<PatientIdSelector>();
	static String defaultAssigningAuthority = null;

	public static PatientIdSelector getInstance(ToolkitServiceAsync toolkitService, Panel menuPanel) {
		for (PatientIdSelector sel : instances) {
			if (sel.menuPanel == menuPanel) 
				return sel;
		}

		if (instances.isEmpty()) {
			toolkitService.getDefaultAssigningAuthority(new AsyncCallback<String>() {

				public void onFailure(Throwable caught) { new PopupMessage("getDefaultAssigningAuthority(): " + caught.getMessage()); }

				public void onSuccess(String result) { defaultAssigningAuthority = result; }
				
			});
		}
		
		PatientIdSelector sel = new PatientIdSelector(toolkitService, menuPanel);
		instances.add(sel);
		
		return sel;
	}
	
	public static void delete(PatientIdSelector sel) {
		instances.remove(sel);
	}
	
	public static void change(String patientIdValue) {
		patientId = patientIdValue;
		
		for (PatientIdSelector sel : instances) {
			sel.updatePatientIdListBox();
			sel.updateSelectionOnScreen();
		}
	}
	
	

	PatientIdSelector(ToolkitServiceAsync toolkitService, Panel menuPanel) {
		this.toolkitService = toolkitService;
		this.menuPanel = menuPanel;
		
		menuPanel.add(patientIdPanel);
		
		HTML testSessionLabel = new HTML();
		testSessionLabel.setText("PatientID: ");
		patientIdPanel.add(testSessionLabel);
		
		patientIdPanel.add(patientIdListBox);
		patientIdListBox.addChangeHandler(new TestSessionChangeHandler());
		updatePatientIdListBox();
		updateSelectionOnScreen();
		
		patientId = Cookies.getCookie(CookieManager.PATIENTIDCOOKIENAME);
		
		patientIdPanel.add(patientIdTextBox);
		
		Button addButton = new Button("Add");
		patientIdPanel.add(addButton);
		addButton.addClickHandler(new AddPatientIdClickHandler());
		
		Button sendButton = new Button("Send ITI-8");
		sendButton.setEnabled(false);
		patientIdPanel.add(sendButton);


	}
	
	public String getTestSession() {
		return patientId;
	}
	
	public boolean isPrivateTesting() {
		return isPrivateTesting;
	}

	public void updateCookie() {
		if ("".equals(patientId) || choose.equals(patientId))
			patientId = null;
		
		if (patientId == null || choose.equals(patientId))
			Cookies.removeCookie(CookieManager.PATIENTIDCOOKIENAME);
		else
			Cookies.setCookie(CookieManager.PATIENTIDCOOKIENAME, patientId);
		
	}
	
	void updatePatientIdListBox() {
		patientIdListBox.clear();
		patientIdListBox.addItem(choose, "");
		for (String val : patientIdList)
			patientIdListBox.addItem(val);
	}
	
	String pidWithAA(String pid) {
		if (hasAA(pid)) 
			return pid;
		return pid + "^^^" + defaultAssigningAuthority;
	}
	
	boolean hasAA(String pid) {
		return pid.indexOf("^^^") != -1;
	}
	
	void updateSelectionOnScreen() {
		String sel = patientId;
		if (patientId == null || "".equals(patientId)) 
			sel = choose;
		
		for (int i=0; i<patientIdListBox.getItemCount(); i++) {
			if (sel.equals(patientIdListBox.getItemText(i))) { 
				patientIdListBox.setSelectedIndex(i);
				break;
			}

		}

	}

	class TestSessionChangeHandler implements ChangeHandler {

		public void onChange(ChangeEvent event) {
			int selectionI = patientIdListBox.getSelectedIndex();
			change(patientIdListBox.getItemText(selectionI));
			updateCookie();
		}

		
	}
	
	class AddPatientIdClickHandler implements ClickHandler {

		public void onClick(ClickEvent event) {
			String pid = patientIdTextBox.getText();
			if (pid == null || pid.equals(""))
				return;
			if (pid.indexOf("^^^") != -1) 
				patientIdList.add(0, pid);
			else 
				pid = pid + "^^^&" + defaultAssigningAuthority + "&ISO";
			
			patientIdList.add(pid);
			change(pid);
		}
		
	}
	
	
}
