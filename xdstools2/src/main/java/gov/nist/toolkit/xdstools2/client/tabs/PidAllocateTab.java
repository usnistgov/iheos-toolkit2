package gov.nist.toolkit.xdstools2.client.tabs;

import gov.nist.toolkit.xdstools2.client.PidAllocateService;
import gov.nist.toolkit.xdstools2.client.PidAllocateServiceAsync;
import gov.nist.toolkit.xdstools2.client.TabContainer;
import gov.nist.toolkit.xdstools2.client.TabbedWindow;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;

public class PidAllocateTab extends TabbedWindow {
	
	final PidAllocateServiceAsync pidAllocateService = GWT
	.create(PidAllocateService.class);

	
	public void onTabLoad(TabContainer container, boolean select, String eventName) {
		topPanel = new VerticalPanel();
		container.addTab(topPanel, "Patient ID", select);
		addCloseButton(container, topPanel, null);
		
		HTML intro = new HTML();
		intro.setHTML(
			"<h2>Public Registry Patient ID Allocation Service</h2>" +
			
			"<p>Patient IDs allocated through this service will be fed to the Public Registry actor" +
			"as part of the Patient Identity Feed transaction.  Once delivered, the Document Registry actor" +
			"will accept Register Transactions for this Patient ID. All Patient IDs are allocated from an Assigning" +
			"Authority. Because the Public Registry supports multiple concurrent test events, it operates" +
			"like multiple virtual Registries, each with their own Assigning Authority.</p><br />" +
			"<p>Note: The Public Registry software can be installed on any machine.  This tool references " +
			"the Registry on localhost (local to where the xdstools2 package is loaded)."
		);
		topPanel.add(intro);
				
		HTML selectPID = new HTML();
		selectPID.setHTML("Select an Assigning Authority for the new Patient ID:");
		topPanel.add(selectPID);
		
		
		getAAsFromServer();
		
	}

	void getAAsFromServer() {
		pidAllocateService.getAssigningAuthorities(
				new AsyncCallback<List<String>>() {
					public void onFailure(Throwable caught) {
						showMessage(caught);
					}

					public void onSuccess(List<String> result) {
						final List<RadioButton> selections = new ArrayList<RadioButton>();
						VerticalPanel panel = new VerticalPanel();
						for (String res1 : result) {
							String[] parts = res1.split("\\&");
							String aa;
							if (parts.length == 3)
								aa = parts[1];
							else
								aa = res1;
							RadioButton rb = new RadioButton("aas", aa);
							selections.add(rb);
							panel.add(rb);
						}
						Button sendButton = new Button("Request a Patient ID");
						sendButton.addStyleName("sendButton");
						panel.add(sendButton);
						HTML h = new HTML();
						h.setHTML("<br /><br />");
						panel.add(h);
						topPanel.add(panel);
						
						sendButton.addClickHandler(new ClickHandler() {
							public void onClick(ClickEvent event) {
								for (RadioButton rb : selections) {
									if (rb.getValue()) {
										String aa = rb.getText();
										getPatientIdFromServer(aa);
									}
								}
							}
						});


					}
				});
	}

	void getPatientIdFromServer(String assigningAuthority) {
		pidAllocateService.getNewPatientId(assigningAuthority, 
				new AsyncCallback<String>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						showMessage(caught);
					}

					public void onSuccess(String result) {
						HTML resultBox = new HTML();
						resultBox.setText(result);
						
						topPanel.add(resultBox);
					}
				});
	}

	public String getWindowShortName() {
		return "pidallocate";
	}
	


}
