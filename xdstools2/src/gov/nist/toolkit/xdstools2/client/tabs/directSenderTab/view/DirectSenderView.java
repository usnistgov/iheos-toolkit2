package gov.nist.toolkit.xdstools2.client.tabs.directSenderTab.view;

import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.tabs.directSenderTab.DirectSenderTab;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.ValueBoxBase.TextAlignment;

public class DirectSenderView implements DirectSenderTab.Display {
	TextBox directFromAddressTextBox = new TextBox();
	TextBox directToAddressTextBox = new TextBox();
	ListBox messageSelectionListBox = new ListBox();
	VerticalPanel topPanel;
	VerticalPanel resultPanel;
	Button submitButton = new Button();
	FileUpload upload = null;
	FormPanel form = null;
	HTML certAvailableMessage;
	VerticalPanel vpan = new VerticalPanel();
	
	DirectSenderTab dsTab;
	String sendingDomain;

	static String CHOOSE = "-- Choose --";
	
	String textBoxWidth = "25em";
	String listBoxWidth = "25em";


	public DirectSenderView(VerticalPanel topPanel, DirectSenderTab dsTab) {
		this.topPanel = topPanel;
		this.resultPanel = new VerticalPanel();
		this.dsTab = dsTab;
	}
	
	public void build() {
		
		HTML title = new HTML();
		title.setHTML("<h2>Send a Direct Message</h2>");
		topPanel.add(title);
		
		topPanel.add(new HTML("<p>Send a Direct message from this tool to a HISP of your choosing"));

		topPanel.add(new HTML("<hr />"));
		topPanel.add(new HTML("<h3>Direct From Address</h3>"));
		HorizontalPanel fromAddressPanel = new HorizontalPanel();
		directFromAddressTextBox.setWidth(textBoxWidth);
		directFromAddressTextBox.setAlignment(TextAlignment.RIGHT);
		fromAddressPanel.add(directFromAddressTextBox);
		fromAddressPanel.add(new HTML("@" + sendingDomain));
		topPanel.add(fromAddressPanel);
		topPanel.add(new HTML("<p>The message content will be signed using the " +
		"private key for this sending domain which was installed in the toolkit. " +
				"The public key will be sent as part of the message payload and will be used by the " +
		" receiving HISP to validate the content."));

		topPanel.add(new HTML("<hr />"));
		topPanel.add(new HTML("<h3>Direct To Address</h3>"));
		topPanel.add(new Label("Format:  account_name@domain"));
		directToAddressTextBox.setWidth(textBoxWidth);
		topPanel.add(directToAddressTextBox);

		topPanel.add(new HTML("<hr />"));
		topPanel.add(new HTML("<h3>Choose document to be sent as the message content</h3>"));
		messageSelectionListBox.setWidth(listBoxWidth);
		topPanel.add(messageSelectionListBox);
		
		topPanel.add(new HTML("<hr />"));
		topPanel.add(new HTML("<h3>Message format</h3>"));
		HorizontalPanel formatPanel = new HorizontalPanel();
		List<RadioButton> wrappedSelection = new ArrayList<RadioButton>();
		RadioButton wrappedRadio = new RadioButton("Wrapped");
		wrappedRadio.setText("Wrapped");
		wrappedRadio.setEnabled(false);
		wrappedSelection.add(wrappedRadio);

		RadioButton unwrappedRadio = new RadioButton("Unwrapped");
		unwrappedRadio.setText("Unwrapped");
		unwrappedRadio.setValue(true);
		wrappedSelection.add(unwrappedRadio);
		formatPanel.add(unwrappedRadio);
		formatPanel.add(wrappedRadio);
		topPanel.add(formatPanel);
		topPanel.add(new HTML("<p>If wrapped format is chosen then the following header fields will " + 
		" be moved from the outer RFC 822 header into the encrypted part of the message: "));
		
		topPanel.add(new HTML("<br /><hr /><br />"));
		topPanel.add(new HTML("<h3>Encyption Certificate</h3>"));

		addCertUpload(true, 
				"Encryption Cert", 
				"This cert is the public key of the receiver that will be used to encrypt the message. It must be in DER format.", 
				"2", 
				false);
		
		certAvailableMessage = new HTML("<p>Encryption cert is available");
		certAvailableMessage.setVisible(false);
		topPanel.add(certAvailableMessage);

		submitButton = new Button("Submit");
		submitButton.setTitle("Submit");
		submitButton.setVisible(false);
		
		topPanel.add(submitButton);
		
		topPanel.add(resultPanel);
	}
	
	public void setEncryptionCertAvailable(String domain, boolean avail) {
		if (avail) {
			certAvailableMessage.setHTML("<p>Encryption cert is available for domain " + domain);
			submitButton.setVisible(true);
			vpan.setVisible(false);
		} else {
			vpan.setVisible(true);
			submitButton.setVisible(false);
		}
		certAvailableMessage.setVisible(avail);
	}
	
	void addCertUpload(boolean submit, String label, String doc, String fieldSuffix, boolean showPassword) {
		//
		// Upload cert
		//
		
		if (form == null) {
			form = new FormPanel();
			form.setAction(GWT.getModuleBaseURL() + "upload");
			form.setEncoding(FormPanel.ENCODING_MULTIPART);
			form.setMethod(FormPanel.METHOD_POST);
		}
		form.setVisible(true);
		
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
		
		form.addSubmitHandler(dsTab);
						
		form.addSubmitCompleteHandler(dsTab);
		
		vpan.add(new HTML("<br />"));
				
		topPanel.add(form);
		
	}
	
	public void displayStatus(boolean ok) {
		if (ok)
			resultPanel.add(new HTML("Status: Success"));
		else
			resultPanel.add(new HTML("<font color=\"#FF0000\">Status: Failure</font>"));
	}
	
	public boolean checkInputs() {
		submitButton.setEnabled(false);
		if ("".equals(getFromAddress())) {
			new PopupMessage("From Address is required");
			return false;
		}
		if ("".equals(getToAddress())) {
			new PopupMessage("To Address is required");
			return false;
		}
		String selected = getSelectedMessageName();
		if (selected.equals("")) {
			new PopupMessage("Message Selection is required");
			return false;
		}
//		String fn = upload.getFilename();
//		if (fn == null || fn.equals("")) {
//			new PopupMessage("Signing Cert is required");
//			return false;
//		}
		submitButton.setEnabled(true);
		return true;
	}


	@Override
	public void setMessageSelections(List<String> names) {
		messageSelectionListBox.clear();
		messageSelectionListBox.addItem(CHOOSE);
		for (String name : names)
			messageSelectionListBox.addItem(name);
	}

	@Override
	public String getFromAddress() {
		return directFromAddressTextBox.getText();
	}

	@Override
	public String getToAddress() {
		return directToAddressTextBox.getText();
	}

	@Override
	public String getSelectedMessageName() {
		int selected = messageSelectionListBox.getSelectedIndex();
		if (selected == -1 || selected == 0)
			return "";
		return messageSelectionListBox.getItemText(selected);
	}

	@Override
	public void addHTMLToResultPanel(String html) {
		resultPanel.add(new HTML(html));
	}

	@Override
	public void clearResultPanel() {
		resultPanel.clear();
	}

	@Override
	public void popupError(String msg) {
		new PopupMessage(msg);
	}

	@Override
	public void setSendingDomain(String domainName) {
		sendingDomain = domainName;
	}

	@Override
	public HasValueChangeHandlers<String> getToAddressTextBox() {
		return directToAddressTextBox;
	}

	@Override
	public HasClickHandlers getKnownCertSubmitButton() {
		return submitButton;
	}
}
