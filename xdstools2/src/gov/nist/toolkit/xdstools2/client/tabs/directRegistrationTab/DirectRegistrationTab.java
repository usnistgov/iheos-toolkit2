package gov.nist.toolkit.xdstools2.client.tabs.directRegistrationTab;


import gov.nist.toolkit.directsim.client.ContactRegistrationData;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.TabContainer;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.BaseSiteActorManager;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.NullSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class DirectRegistrationTab extends GenericQueryTab {
	FlexTable msgs = new FlexTable();
	FlexTable grid = new FlexTable();
	FlexTable existingGrid = new FlexTable();

	String required        = new String("*Required fields");

	Button continueButton  = new Button("Continue");
	Button deleteDirectButton = new Button("Delete");
//	Button updateDirectButton = new Button("Update");

	TextBox addDirectFrom = new TextBox();
	TextBox cert = new TextBox();

//	public ListBox directFromForUpdating = new ListBox();
	public ListBox directFromForDeletion = new ListBox();

	static String DEFAULTWIDTH = "30em";
//	static String DEFAULTHEIGHT = "1em";
	static String DEFAULTTITLEWIDTH = "15em";


	public DirectRegistrationTab(BaseSiteActorManager siteActorManager) {
		super(new NullSiteActorManager());
	}

	public DirectRegistrationTab() {
		super(new NullSiteActorManager());
	}

	TextBox contactEmail = new TextBox();
	Label contactStatus = new Label();
	//	ListBox directFromAddrs;
	Button loadContactButton = new Button("Load/Create Contact");
	Button addDirectFromButton = new Button("Add");

	List<String> directToAddrs = new ArrayList<String>();

	ContactRegistrationData currentRegistration = null;  // none loaded yet

	boolean hasContactEmail() {
		return currentRegistration != null;
	}

	@Override
	public void onTabLoad(TabContainer container, boolean select,
			String eventName) {
		myContainer = container;
		topPanel = new VerticalPanel();
		
		container.addTab(topPanel, "DirectRegistration", select);
		addCloseButton(container,topPanel, null);

		directToAddrs.add("Type 1");
		directToAddrs.add("Type 2");
		directToAddrs.add("Type 3");

		HTML title = new HTML();
		title.setHTML("<h2>Registration for Transport Testing Tool</h2>");
		topPanel.add(title);

		topPanel.add(new HTML(required));

		grid.setCellSpacing(20);

		/////////////////////////////////////////////////////////////////////////////
		// Contact
		/////////////////////////////////////////////////////////////////////////////

		topPanel.add(newContact());

		loadContactButton.addClickHandler(new LoadContactHandler(this));
		contactEmail.addChangeHandler(new LoadContactHandler(this));

		/////////////////////////////////////////////////////////////////////////////
		// New Direct
		/////////////////////////////////////////////////////////////////////////////

		topPanel.add(new HTML("<hr />"));

		topPanel.add(manageDirectRegistration());

		/////////////////////////////////////////////////////////////////////////////
		// Delete Direct
		/////////////////////////////////////////////////////////////////////////////

		topPanel.add(new HTML("<hr />"));

//		topPanel.add(delExistingDirectRegistrations());

	}

	VerticalPanel newContact() {
		VerticalPanel panel = new VerticalPanel();
		FlexTable grid = new FlexTable();

		panel.add(new HTML("<h3>Register a Contact Email Address</h3>" + 
				"<p>This is used to send feedback to the user.  It is not part of the Direct specification. " + 
				"A Direct message sent to this tool results in a validation report sent to this Contact Email Address. " +
				"Other parts of the user registration (below) are maintained for this user as identified by this Contact Email Adddress."));

		panel.add(grid);

		HTML h = new HTML("Contact Email Addr*:");
		h.setWidth(DEFAULTTITLEWIDTH);

		int row = 0;
		grid.setWidget(row, 0, h);
		contactEmail.setWidth(DEFAULTWIDTH);
		grid.setWidget(row, 1, contactEmail);
		grid.setWidget(row, 2, loadContactButton);
		grid.setWidget(row, 3, contactStatus);

		return panel;
	}

//	public VerticalPanel delExistingDirectRegistrations() {
//		VerticalPanel panel = new VerticalPanel();
//		FlexTable grid = new FlexTable();
//
//		panel.add(new HTML("<h3>Upload </h3>"));
//		panel.add(new HTML("Remove a Direct (From) email from the accepted list above or."));
//
//		panel.add(grid);
//
//		HTML h;
//		int row = 0;;
//		h = new HTML("Existing Direct (From) Email Addrs:");
//		h.setWidth(DEFAULTTITLEWIDTH);
//		grid.setWidget(row, 0, h);
//		grid.setWidget(row, 1, directFromForDeletion);
////		grid.setWidget(row, 2, deleteDirectButton);
//		grid.setWidget(row, 3, directDeleteExistingFromMessage);
//
//		directDeleteExistingFromMessage("");
//		deleteDirectButton.addClickHandler(new DeleteDirectHandler(this));
//		
//		panel.add(new HTML("<h3>Public Certificate</h>"));
//		panel.add(new HTML("The clinical content of the Direct message must be encrypted"));
//
//		h = new HTML("Copy/Paste for BASE64 encoded certs (PEM)");
//		h.setWidth(DEFAULTTITLEWIDTH);
//		grid.setWidget(row, 0, h);
//		cert.setWidth(DEFAULTWIDTH);
//		cert.setHeight("20em");
//		grid.setWidget(row, 1, cert);
//		Button saveCertButton = new Button("Upload");
//		grid.setWidget(row, 2, saveCertButton);
//		grid.setWidget(row, 3, new HTML("Cert associated with Direct (From) Email address above"));
//
//		saveCertButton.addClickHandler(new SaveDirectAddrHandler(this, false));
//
//		row++;
//		buildFileUploadPanelForCert(grid, row);
//
//		return panel;
//	}

	// Status messages that can popup
	Label directFromMessage = new Label("");

	public void directFromMessage(String text) {
		clearAllMessages();
		directFromMessage.setText(text);
	}

	Label directExistingFromMessage = new Label("");

	public void directExistingFromMessage(String text) {
		clearAllMessages();
		directExistingFromMessage.setText(text);
	}

	Label directDeleteExistingFromMessage = new Label("");

	public void directDeleteExistingFromMessage(String text) {
		clearAllMessages();
		directDeleteExistingFromMessage.setText(text);
	}

	public void contactMessage(String message) {
		clearAllMessages();
		contactStatus.setText(message);
	}
	
	public void clearAllMessages() {
		directFromMessage.setText("");
		directExistingFromMessage.setText("");
		directDeleteExistingFromMessage.setText("");	
		contactStatus.setText("");
	}

	// End of status messages


	// This load serves both the updating and deletion so data is used
	// to populate on screen widgets twice
	public void loadExistingDirectRegistrations() {
//		directFromForUpdating.clear();
		directFromForDeletion.clear();
//		directFromForUpdating.addItem("-- Choose --");
//		directFromForDeletion.addItem("-- Choose --");
		for (String direct : currentRegistration.directToCertMap.keySet()) {
//			directFromForUpdating.addItem(direct);
			directFromForDeletion.addItem(direct);
		}
	}

	public VerticalPanel manageDirectRegistration() {
		VerticalPanel panel = new VerticalPanel();
		FlexTable grid = new FlexTable();

		panel.add(new HTML("<h3>Manage Direct (From) Email Addresses</h3>"));
		panel.add(new HTML("Direct messages will be accepted for validation only when the Direct (From) address is registered here. " +
		""
//				"The Public Certificate available from this tool will be used to decrypt messages. "  +
//				"This Public Certificate can be downloaded from the home page. <p>" +
//				" The resulting validation report will go back to the Contact email address above. "
				));

		panel.add(grid);

		int row = 0;
		HTML h;
		h = new HTML("Direct (From) Email Addr*:");
		h.setWidth(DEFAULTTITLEWIDTH);

		// For type in
		grid.setWidget(row, 0, h);
		addDirectFrom.setWidth(DEFAULTWIDTH);
		grid.setWidget(row, 1, addDirectFrom);
		grid.setWidget(row, 2, addDirectFromButton);
		grid.setWidget(row, 3, directFromMessage);

		addDirectFrom.addChangeHandler(new AddDirectAddrHandler(this, true));
		
		grid = new FlexTable();
		panel.add(grid);
		row=0;
		
		// For selection
		row++;
		h = new HTML("Select from existing:");
		h.setWidth(DEFAULTTITLEWIDTH);
		grid.setWidget(row, 0, h);
		directFromForDeletion.setVisibleItemCount(5);
		directFromForDeletion.setWidth(DEFAULTWIDTH);
		grid.setWidget(row, 1, directFromForDeletion);
		grid.setWidget(row, 3, deleteDirectButton);
		grid.setWidget(row, 4, directExistingFromMessage);
		deleteDirectButton.addClickHandler(new DeleteDirectHandler(this));

		panel.add(new HTML("<hr /><h3>Direct (To) Email Address</h3>" +
		"<p> The Direct (To) address controls the Meaningful Use Stage 2 CCDA validation. " +
				"See the Home page for document types and the required Direct (To) addresses."
				));

		panel.add(new HTML("<hr /><h3>Signing certificate</h3>" +
		"<p>The sender signs the message content with their private key. The associated public key is included in the message and is " +
				"used by TTT to validate the content against the signature." 
				));

		panel.add(new HTML("<hr /><h3>Encryption certificate</h3>" +
		"<p>The sender encrypts the message content with the public key belonging to the recipient (associated with the Direct (To) address), " +
				"in this case the TTT toolkit. This public key can be downloaded from the home tab of this tool. The Direct recipient (TTT toolkit) " +
		" decrypts the message using its private key."
				));

		directExistingFromMessage("");

		directFromForDeletion.addChangeHandler(new UpdateDirectSelectionChangeHandler(this));

		grid = new FlexTable();
		panel.add(grid);
		
		return panel;
	}

	private void buildFileUploadPanelForCert(FlexTable grid, int row) {
		VerticalPanel fileUploadPanel = new VerticalPanel();

		final FormPanel uploadForm = new FormPanel();

		HorizontalPanel upload2Panel = new HorizontalPanel();
		fileUploadPanel.add(upload2Panel);
		FileUpload messageUpload2 = new FileUpload();
		messageUpload2.setName("upload2FormElement");
		upload2Panel.add(messageUpload2);

		Button submitButton = new Button("Upload");
		submitButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				uploadForm.submit();
			}
		});
		
		uploadForm.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
			@Override
		      public void onSubmitComplete(SubmitCompleteEvent event) {
		        // When the form submission is successfully completed, this event is
		        // fired. Assuming the service returned a response of type text/html,
		        // we can get the result text here (see the FormPanel documentation for
		        // further explanation).
				ContactRegistrationData reg = currentRegistration;
				String directAddr = addDirectFrom.getText();
		        toolkitService.saveCertFromUpload(reg, directAddr, saveCertCallback);
		      }

		    });

		// UPLOAD FORM
		uploadForm.setAction(GWT.getModuleBaseURL() + "upload");
		uploadForm.setEncoding(FormPanel.ENCODING_MULTIPART);
		uploadForm.setMethod(FormPanel.METHOD_POST);

		uploadForm.setWidget(fileUploadPanel);

		grid.setWidget(row, 0, new HTML("File upload for binary format certificates (all others)"));
		grid.setWidget(row, 1, uploadForm);
		grid.setWidget(row, 2, submitButton);

	}
	
	AsyncCallback<ContactRegistrationData> saveCertCallback = new AsyncCallback<ContactRegistrationData> () {

		@Override
		public void onFailure(Throwable arg0) {
			new PopupMessage("Certificate could not be saved: " + arg0.getMessage());
		}

		@Override
		public void onSuccess(ContactRegistrationData arg0) {
		}
	};

//	public String currentDirectForUpdating() {
//		int select = directFromForUpdating.getSelectedIndex();
//		if (select == -1)
//			return null;
//		return directFromForUpdating.getItemText(select);
//	}

	public String currentDirectForDeletion() {
		int select = directFromForDeletion.getSelectedIndex();
		if (select == -1)
			return null;
		return directFromForDeletion.getItemText(select);
	}

//	public int currentDirectIForUpdating() {
//		return directFromForUpdating.getSelectedIndex();
//	}

	public int currentDirectIForDeletion() {
		return directFromForDeletion.getSelectedIndex();
	}

	public void refreshContact() {
		contactEmail.setText(currentRegistration.contactAddr);
		directFromForDeletion.clear();
//		directFromForUpdating.addItem("-- Choose --");
		for (String direct : currentRegistration.directToCertMap.keySet()) {
			directFromForDeletion.addItem(direct);
		}
		directFromForDeletion.clear();
//		directFromForDeletion.addItem("-- Choose --");
		for (String direct : currentRegistration.directToCertMap.keySet()) {
			if (direct == null || direct.equals(""))
				continue;
			directFromForDeletion.addItem(direct);
		}
		addDirectFrom.setValue("");
		cert.setValue("");
	}

	////////////////////////////////////////////////////////////


//	AsyncCallback<Map<String, String>> validationEndpointsCallback = new AsyncCallback<Map<String, String>> () {
//
//		@Override
//		public void onFailure(Throwable arg0) {
//			new PopupMessage("Failed to load Validation Endpoints");
//
//		}
//
//		@Override
//		public void onSuccess(Map<String, String> arg0) {
//			for (String purpose : arg0.keySet()) {
//				int row = directTable.getRowCount();
//				String endpoint = arg0.get(purpose);
//				directTable.setWidget(row, 0, new HTML(endpoint));
//				directTable.setWidget(row, 1, new HTML(purpose));
//			}
//			int row = directTable.getRowCount();
//			Anchor download = new Anchor();
//			download.setText("Download toolkit public certificate");
//			directTable.setWidget(row, 0, download);
//			download.setHref("pubcert/cert.p12/mykey.pub ");
//			download.setTarget("target=\"_blank\"");
//		}
//
//	};

	public void errorMsg(String msg) {
		if (msg == null || msg.equals("")) {
			try {
				msgs.removeCell(0, 1);
			} catch (Exception e) {}
		} else
			msgs.setHTML(0, 1, Display.asRed(msg));
	}

	public void add(String direct, byte[] cert) {
		currentRegistration.add(direct, cert);
	}

	public ContactRegistrationData registrationData() throws Exception {
		String contactAddr = contactEmail.getText();
		if (contactAddr == null || contactAddr.equals("")) throw new Exception("Contact Address Not Filled In");
		return currentRegistration;
	}

	@Override
	public String getWindowShortName() {
		return "DirectRegistrationTab";
	}
}
