package gov.nist.toolkit.xdstools2.client.tabs.directSenderTab;

import gov.nist.toolkit.results.client.AssertionResult;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.TabContainer;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.BaseSiteActorManager;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.NullSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;

public class DirectSenderTab extends GenericQueryTab {
	static String CHOOSE = "-- Choose --";
	TextBox directServerNameTextBox = new TextBox();
//	TextBox directServerPortTextBox = new TextBox();
	TextBox directFromAddressTextBox = new TextBox();
	TextBox directToAddressTextBox = new TextBox();
	ListBox messageSelectionListBox = new ListBox();

	public DirectSenderTab(BaseSiteActorManager siteActorManager) {
		super(new NullSiteActorManager());
	}

	public DirectSenderTab() {
		super(new NullSiteActorManager());
	}

	@Override
	public void onTabLoad(TabContainer container, boolean select,
			String eventName) {
		myContainer = container;
		topPanel = new VerticalPanel();


		container.addTab(topPanel, "DirectSender", select);
		addCloseButton(container,topPanel, null);

		HTML title = new HTML();
		title.setHTML("<h2>Send Direct Message</h2>");
		topPanel.add(title);

		mainGrid = new FlexTable();

		String textBoxWidth = "25em";
		String listBoxWidth = "25em";

		directServerNameTextBox.setWidth(textBoxWidth);
		directServerNameTextBox.setText("");

//		directServerPortTextBox.setWidth(textBoxWidth);
//		directServerPortTextBox = new TextBox();
//		directServerPortTextBox.setWidth(textBoxWidth);
//		directServerPortTextBox.setText("25");

		directFromAddressTextBox.setWidth(textBoxWidth);

		directToAddressTextBox.setWidth(textBoxWidth);

		messageSelectionListBox.setWidth(listBoxWidth);

		int row = 0;
		mainGrid.setWidget(row, 0, new HTML("Direct System"));
		mainGrid.setWidget(row, 1, new HTML("Server Name"));
		mainGrid.setWidget(row, 2, directServerNameTextBox);
		mainGrid.setWidget(row, 3, new HTML("Direct system to send message to on port 25"));
//		row++;
//		mainGrid.setWidget(row, 1, new HTML("Port"));
//		mainGrid.setWidget(row, 2, directServerPortTextBox);

		row++;
		mainGrid.setWidget(row, 0, new HTML("Direct"));
		mainGrid.setWidget(row, 1, new HTML("From Address"));
		mainGrid.setWidget(row, 2, directFromAddressTextBox);
		mainGrid.setWidget(row, 3, new HTML("From: field of message header"));

		row++;
		mainGrid.setWidget(row, 1, new HTML("To Address"));
		mainGrid.setWidget(row, 2, directToAddressTextBox);
		mainGrid.setWidget(row, 3, new HTML("To: field of message header"));

		row++;

		row++;
		mainGrid.setWidget(row, 0, new HTML("Message Selection"));
		mainGrid.setWidget(row, 2, messageSelectionListBox);
		mainGrid.setWidget(row, 3, new HTML("Docment to be sent as attachment"));
		row++;

		new LoadTestdataList(toolkitService, "direct-messages", messageSelectionListBox, CHOOSE).run();
		
		topPanel.add(mainGrid);
		
		topPanel.add(new HTML("<br /><hr /><br />"));
		topPanel.add(new HTML("<h3>Certificates to be used</h3>"));
		
		addCertUpload(false, 
				"Signing Cert",
				"This cert will be used to sign the message. It must be in PKCS12 format and include the private key. The password field should be left blank if the key is not password protected", 
				"1", 
				true);
		
		addCertUpload(true, 
				"Encryption Cert", 
				"This cert is the public key of the receiver that will be used to encrypt the message. It must be in DER format.", 
				"2", 
				false);
		
		addQueryBoilerplate(new Runner(), null, null, false);
		queryBoilerplate.enableRun(false);
		queryBoilerplate.enableSaml(false);
		queryBoilerplate.enableTls(false);
		queryBoilerplate.enableInspectResults(false);
	}

	FileUpload upload = null;
	FormPanel form = null;
	VerticalPanel vpan = new VerticalPanel();
	boolean submitComplete = true;

	
	void addCertUpload(boolean submit, String label, String doc, String fieldSuffix, boolean showPassword) {
		//
		// Upload signing cert
		//
		
		if (form == null) {
			form = new FormPanel();
			form.setAction(GWT.getModuleBaseURL() + "upload");
			form.setEncoding(FormPanel.ENCODING_MULTIPART);
			form.setMethod(FormPanel.METHOD_POST);
		}
		
		HorizontalPanel hpan = new HorizontalPanel();
		vpan.add(hpan);
		form.setWidget(vpan);
		
		hpan.add(new HTML(label));
		upload = new FileUpload();
		upload.setName("upload" + fieldSuffix + "FormElement");
		hpan.add(upload);
		
		if (showPassword) {
			hpan.add(new HTML("Password"));
			TextBox tb = new TextBox();
			tb.setName("password" + fieldSuffix);
			hpan.add(tb);
		}
		
		if (submit) {
			vpan.add(new HTML("<br /><hr /><br />"));
			vpan.add(new Button("Submit", new ClickHandler() {
				public void onClick(ClickEvent event) {
					form.submit();
				}
			}));
		}
		hpan.add(new HTML(doc));
		
		form.addSubmitHandler(new FormPanel.SubmitHandler() {
			// fired just before the form is submitted.  Validation
			// can be done here. The submit can be canceled via
			// event.cancel();
			@Override
			public void onSubmit(SubmitEvent event) {
				if (!checkInputs()) 
					event.cancel();
				else
					submitComplete = false;
			}
		});
		form.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
			// When the form submission is successfully completed, this event
			// is fired.
			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
				if (submitComplete)
					return;
				submitComplete = true;
				Map<String, String> parms = new HashMap<String, String>();
				resultPanel.clear();

				if (!checkInputs()) 
					return;
				
				parms.put("$direct_server_name$", directServerNameTextBox.getText());
				parms.put("$direct_from_address$", directFromAddressTextBox.getText());
				parms.put("$direct_to_address$", directToAddressTextBox.getText());
				int selected = messageSelectionListBox.getSelectedIndex();
				parms.put("$ccda_attachment_file$", messageSelectionListBox.getItemText(selected));

				addStatusBox();
				try {
					getGoButton().setVisible(false);
				} catch (Exception e) {}
				if (getInspectButton() != null)
					getInspectButton().setEnabled(false);

				toolkitService.directSend(parms, new AsyncCallback<List<Result>> () {
					public void onFailure(Throwable caught) {
						resultPanel.clear();
						resultPanel.add(addHTML("<font color=\"#FF0000\">" + "Error running validation: " + caught.getMessage() + "</font>"));
					}

					public void onSuccess(List<Result> results) {
						boolean status = true;
						for (Result result : results) {
							for (AssertionResult ar : result.assertions.assertions) {
								String assertion = ar.assertion;
								if (assertion != null)
									assertion = assertion.replaceAll("\n", "<br />");
								if (ar.status) {
									resultPanel.add(addHTML(assertion));
								} else {
									if (assertion.contains("EnvironmentNotSelectedException"))
										resultPanel.add(addHTML("<font color=\"#FF0000\">" + "Environment Not Selected" + "</font>"));
									else
										resultPanel.add(addHTML("<font color=\"#FF0000\">" + assertion + "</font>"));
									status = false;
								}
							}
						}
						if (status)
							setStatus("Status: Success", true);
						else
							setStatus("Status: Failure", false);
						//getGoButton().setEnabled(true);
					}});
			}
		});
		
		vpan.add(new HTML("<br />"));
				
		topPanel.add(form);
		
	}
	
	boolean checkInputs() {
		if ("".equals(directServerNameTextBox.getText())) {
			new PopupMessage("Server Name is required");
			return false;
		}
		if ("".equals(directFromAddressTextBox.getText())) {
			new PopupMessage("From Address is required");
			return false;
		}
		if ("".equals(directToAddressTextBox.getText())) {
			new PopupMessage("To Address is required");
			return false;
		}
		int selected = messageSelectionListBox.getSelectedIndex();
		if (selected == -1 || selected == 0) {
			new PopupMessage("Message Selection is required");
			return false;
		}
		String fn = upload.getFilename();
		if (fn == null || fn.equals("")) {
			new PopupMessage("Signing Cert is required");
			return false;
		}
		return true;
	}

	class Runner implements ClickHandler {

		@Override
		public void onClick(ClickEvent arg0) {
			// TODO Auto-generated method stub
			
		}
	}


	@Override
	public String getWindowShortName() {
		return "DirectSenderTab";
	}

}
