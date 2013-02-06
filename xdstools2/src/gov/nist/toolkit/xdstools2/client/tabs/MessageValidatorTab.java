package gov.nist.toolkit.xdstools2.client.tabs;


import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.tk.client.PropertyNotFoundException;
import gov.nist.toolkit.tk.client.TkProps;
import gov.nist.toolkit.valsupport.client.MessageValidationResults;
import gov.nist.toolkit.valsupport.client.MessageValidatorDisplay;
import gov.nist.toolkit.valsupport.client.ValFormatter;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.RenameSimFileDialogBox;
import gov.nist.toolkit.xdstools2.client.TabContainer;
import gov.nist.toolkit.xdstools2.client.TabbedWindow;
import gov.nist.toolkit.xdstools2.client.ToolkitService;
import gov.nist.toolkit.xdstools2.client.ToolkitServiceAsync;
import gov.nist.toolkit.xdstools2.client.inspector.MetadataInspectorTab;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MessageValidatorTab extends TabbedWindow {
	protected TabContainer myContainer;
	VerticalPanel resultsContainer = new VerticalPanel();
	FlexTable resultsTable = new FlexTable();
	int row = 0;
	CheckBox lessdetail;
	CheckBox crossCommunity;
	CheckBox soapWrapper;
	CheckBox samlWrapper;
	CheckBox httpWrapper;
	Button inspectButton;
	String filename = null;
	boolean enableCertificateUpload = false;
	VerticalPanel fileUploadPanel = new VerticalPanel();

	final RadioButton fromFileRadioButton = new RadioButton("InputType", "From File");
	final RadioButton fromEndpointRadioButton = new RadioButton("InputType", "From Endpoint");
	List<RadioButton> inputTypeButtons = 
			Arrays.asList(
					fromFileRadioButton,
					fromEndpointRadioButton
					);
	final VerticalPanel chooseFromEndpointArea = new VerticalPanel();
	final FormPanel uploadForm = new FormPanel();
	final ListBox simFilesListBox = new ListBox();



	final protected ToolkitServiceAsync toolkitService = GWT
			.create(ToolkitService.class);

	static final String ValidationType_PnR_b = "ProvideAndRegister.b";
	static final String ValidationTypeR_b = "Register.b";
	static final String ValidationTypeXDM = "XDM (zip)";
	static final String ValidationTypeDirectXDM = "XDM (zip) with MU 2 CCDA";
	static final String ValidationTypeXDR = "XDR (Metadata-Limited or Original Recipe)";
	static final String ValidationTypeDirectXDR = "XDR (Minimal Metadata with MU 2 CCDA)";
	static final String ValidationType_SQ = "Stored Query";
	static final String ValidationType_Ret = "Retrieve";
	static final String ValidationType_guess = "Guess based on content";
	//NHIN xcpd
	static final String ValidationType_xcpd = "XCPD";
	static final String ValidationType_NwHINxcpd = "NwHIN XCPD";
	static final String ValidationType_C32 = "MU_HITSP_C32";
	static final String validationGroupName = "ValidationTypesGroup";
	static final String inOutGroupName = "InOutGroup";
	//NCIDP
	static final String ValidationType_ncpdp = "NCPDP";
	//DIRECT
	static final String ValidationType_direct = "DIRECT (with MU 2 CCDA)";
	static final String ValidationType_CCDA = "CCDA";

	//private static final VerticalAlignmentConstant ALIGN_TOP = null;

	static List<String> msgValidationTypes = 
			Arrays.asList(
					ValidationType_PnR_b,
					ValidationTypeR_b,
					ValidationTypeXDR,
					ValidationTypeDirectXDR,
					ValidationType_SQ,
					ValidationType_Ret,
					ValidationType_guess,
					ValidationType_xcpd,
					ValidationType_NwHINxcpd,
					ValidationType_ncpdp,
					ValidationType_direct
					);

	static List<String> docTypeValidationTypes = 
			Arrays.asList(
					ValidationTypeXDM,
					ValidationTypeDirectXDM,
					ValidationType_C32,
					ValidationType_CCDA
					);

	List<RadioButton> messageTypeButtons;
	Map<String, RadioButton> messageTypeButtonMap = new HashMap<String, RadioButton>();

	void addValidationTypesRadioGroup(VerticalPanel panel, boolean enable) {

		//Message Types
		messageTypeButtons = new ArrayList<RadioButton>();
		panel.add(html("<hr />"));
		panel.add(html(bold("Message Types")));
		for (String type : msgValidationTypes) {
			if (type.equals(ValidationType_ncpdp)) 
				continue;
			addValidationTypeRadioButton(panel, type, enable);
		}
		panel.add(html("<hr />"));
		panel.add(html(bold("Document Types")));
		for (String type : docTypeValidationTypes) {
			if (type.equals(ValidationType_ncpdp)) 
				continue;
			addValidationTypeRadioButton(panel, type, enable);
		}
	}
	
	void addValidationTypeRadioButton(VerticalPanel panel, String type, boolean enable) {
		boolean addToPanel = true;

		RadioButton rb = new RadioButton(validationGroupName, type);
		if (type.equals(ValidationType_guess))
			rb.setValue(true);
		if (type.equals(ValidationTypeXDM))
			rb.setEnabled(true);
		else
			rb.setEnabled(enable);

		if (type.equals(ValidationType_xcpd)) {
			//panel.add(html("<hr />"));
//			panel.add(html(bold("IHE XCPD")));
		}

		if (type.equals(ValidationType_NwHINxcpd)) {
			//panel.add(html("<hr />"));
			//panel.add(html(bold("NwHIN Patient Discovery Message Types")));
		}
		if (type.equals(ValidationType_ncpdp)) {
			panel.add(html("<hr />"));
			panel.add(html(bold("E-Prescription")));
		}
		if (type.equals(ValidationType_C32)) {
			//panel.add(html("<hr />"));
			//panel.add(html(bold("CDA Document Validator")));
		}
		if (type.equals(ValidationType_direct)) {
		//	panel.add(html("<hr />"));
		//	panel.add(html(bold("DIRECT Message")));
		}
		if (type.equals(ValidationType_CCDA)) {
			panel.add(html("<hr />"));
			panel.add(html(bold("CCDA Document Validator (CCDA validation may take more than a minute to run)")));
			
			List<String> ccdaTypes = ccdaTypes();
			for (String ctype : ccdaTypes) {
				RadioButton rb1 = new RadioButton(validationGroupName, ctype);
				rb1.addClickHandler(msgTypeClickHandler);
				messageTypeButtons.add(rb1);
				panel.add(rb1);
				messageTypeButtonMap.put(ctype, rb1);
			}
			addToPanel = false;
		}
		if (addToPanel) {
			panel.add(rb);
			rb.addClickHandler(msgTypeClickHandler);
			messageTypeButtons.add(rb);
			messageTypeButtonMap.put(type, rb);
		}

	}
	
	ClickHandler msgTypeClickHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			boolean enableContentType = false;
			for (String type : messageTypeButtonMap.keySet()) {
				RadioButton r = messageTypeButtonMap.get(type);
				if (r.getValue() && type.indexOf("MU 2 CCDA") != -1) {
					enableContentType = true;
				}
			}
			enableCcdaTypesRadioGroup(enableContentType);
		}
		
	};
	
	List<String> ccdaTypes() {
		List<String> types = new ArrayList<String>();
		
		TkProps ccdaProps = tkProps().withPrefixRemoved("direct.reporting.ccdatype");
		for (int i=1; i<30; i++) {
			String en = Integer.toString(i);
			
			String ctype = null;
			String display = null;
			try {
				ctype = ccdaProps.get("type" + en);
				display = ccdaProps.get("display" + en);
			} catch (PropertyNotFoundException e) {
			}
			if (ctype == null || display == null)
				break;
			types.add(ctype + " - " + display);
		}
		return types;
	}
	
	String simpleCcdaType(String type) {
		String[] parts = type.split("-");
		if (parts.length == 0)
			return null;
		return parts[0].trim();
	}

	String getMessageType() {
		for (RadioButton rb : messageTypeButtons) {
			if (rb.getValue() == true)
				return rb.getText();
		}
		return "";
	}


	RadioButton requestMessage;
	RadioButton responseMessage;

	void addInOutTypesRadioGroup(VerticalPanel panel, boolean enable) {
		panel.add(html("<hr />"));
		panel.add(html(bold("In/Out Message Types")));
		requestMessage = new RadioButton(inOutGroupName, "Request Message");
		responseMessage = new RadioButton(inOutGroupName, "Response Message");

		requestMessage.setEnabled(enable);
		responseMessage.setEnabled(enable);
		//		requestMessage.setValue(true);
		panel.add(requestMessage);
		panel.add(responseMessage);
	}
	
	List<RadioButton> ccdaTypes;
	String ccdaTypesGroupName = "CCDATypesGroupName";
	
	void addCcdaTypesRadioGroup(VerticalPanel panel, List<String> ccdaTypeNames) {
		panel.add(html(bold("CCDA Types for XDM or XDR content (CCDA validation may take a minute or more to run)")));
		ccdaTypes = new ArrayList<RadioButton>();
		for (String name : ccdaTypeNames) {
			RadioButton r = new RadioButton(ccdaTypesGroupName, name); 
			ccdaTypes.add(r);
			panel.add(r);
		}
		RadioButton r = new RadioButton(ccdaTypesGroupName, "Non-CCDA content");
		ccdaTypes.add(r);
		panel.add(r);
	}
	
	void enableCcdaTypesRadioGroup(boolean enable) {
		for (RadioButton r : ccdaTypes) {
			r.setEnabled(enable);
		}
	}
	
	String getCcdaContentType() {
		for (RadioButton r : ccdaTypes) {
			if (r.getValue())
				return r.getText();
		}
		return "";
	}

	boolean isRequestType() {
		return requestMessage.getValue();
	}

	boolean isResponseType() {
		return responseMessage.getValue();
	}

	boolean hasSoapWrapper() {
		return soapWrapper.getValue();
	}

	boolean hasSamlWrapper() {
		return samlWrapper.getValue();
	}

	boolean hasHttpWrapper() {
		return httpWrapper.getValue();
	}

	void addDocType(ValidationContext vc, String ccdaContentType) {
		ValidationContext vc2 = new ValidationContext();
		vc2.ccdaType = ccdaContentType;
		vc.addInnerContext(vc2);
	}

	void loadValidationContext(ValidationContext vc) {
		String msgType = getMessageType();
		if (msgType.equals(ValidationTypeR_b))
			vc.isR = true;
		else if (msgType.equals(ValidationType_PnR_b))
			vc.isPnR = true;
		else if (msgType.equals(ValidationTypeXDM))
			vc.isXDM = true;
		else if (msgType.equals(ValidationTypeDirectXDM)) {
			vc.isXDM = true;
			addDocType(vc, getCcdaContentType());
		}
		else if (msgType.equals(ValidationTypeXDR)) {
			vc.isXDR = true;
			vc.isPnR = true;
		}
		else if (msgType.equals(ValidationTypeDirectXDR)) {
			vc.isXDR = true;
			vc.isPnR = true;
			addDocType(vc, getCcdaContentType());
		}
		else if (msgType.equals(ValidationType_Ret))
			vc.isRet = true;
		else if (msgType.equals(ValidationType_SQ))
			vc.isSQ = true;
		//xcpd
		else if (msgType.equals(ValidationType_xcpd))
			vc.isXcpd = true;
		else if (msgType.equals(ValidationType_NwHINxcpd))
			vc.isNwHINxcpd = true;
		else if (msgType.equals(ValidationType_ncpdp))
			vc.isNcpdp = true;
		else if (msgType.equals(ValidationType_C32))
			vc.isC32 = true;
		else if (msgType.equals(ValidationType_direct)) {
			vc.isDIRECT = true;
			addDocType(vc, getCcdaContentType());
		}
		else if (msgType.equals(ValidationType_CCDA))
			vc.isCCDA = true;
		else {
			// must be some type of CCDA
			vc.ccdaType = simpleCcdaType(msgType);
			vc.isCCDA = true;
		}
		//------------

		if (isRequestType()) 
			vc.isRequest = true;
		if (isResponseType())
			vc.isResponse = true;

		if (crossCommunity.getValue())
			vc.isXC = true;

		vc.hasSoap = hasSoapWrapper();
		vc.hasSaml = hasSamlWrapper();
		vc.hasHttp = hasHttpWrapper();
		
	}

	public void onTabLoad(TabContainer container, boolean select, String eventName) {
		myContainer = container;
		topPanel = new VerticalPanel();
//		disableEnvMgr();
		disableTestSesMgr();

		container.addTab(topPanel, "Message Validator", select);
		addCloseButton(container,topPanel, null);

		topPanel.add(html(h2("Message Validator")));

//		FlexTable mainGrid = new FlexTable();
//		mainGrid.setCellSpacing(40);

//		int row = 0;

//		topPanel.add(mainGrid);

		// File Chooser
//		VerticalPanel fileChooserArea = new VerticalPanel();
		//topPanel.add(fileChooserArea);	
		topPanel.add(html("<hr />"));
//		mainGrid.setWidget(row,3, fileChooserArea);

		VerticalPanel messageTypeArea = new VerticalPanel();	
		VerticalPanel validationCheckBoxes = new VerticalPanel();
		VerticalPanel inOutTypeArea = new VerticalPanel();
		HorizontalPanel topH = new HorizontalPanel();
		VerticalPanel rightSideVert = new VerticalPanel();
		HorizontalPanel typesAndWrappers = new HorizontalPanel();

		// build structure
		topPanel.add(topH);
		topH.add(messageTypeArea);
		topH.add(rightSideVert);
		rightSideVert.add(typesAndWrappers);
		addCcdaTypesRadioGroup(rightSideVert, ccdaTypes());
		enableCcdaTypesRadioGroup(false);
		typesAndWrappers.add(inOutTypeArea);
		typesAndWrappers.add(validationCheckBoxes);
		
		

		//mainGrid.setBorderWidth(10);
//		mainGrid.getFlexCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);
//		mainGrid.getFlexCellFormatter().setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_TOP);
//		mainGrid.getFlexCellFormatter().setVerticalAlignment(0, 2, HasVerticalAlignment.ALIGN_TOP);
//
//		mainGrid.setWidget(row,0, messageTypeArea);
//		mainGrid.setWidget(row,1, inOutTypeArea);
//		mainGrid.setWidget(row,2, validationCheckBoxes);		

		// Message type radio buttons
		addValidationTypesRadioGroup(messageTypeArea, true);

		// InOut radio Buttons
		addInOutTypesRadioGroup(inOutTypeArea, false);

		// Validation Check Boxes
		validationCheckBoxes.add(html("<hr />"));
		validationCheckBoxes.add(html(bold("Message Structure Validators")));
		crossCommunity = new CheckBox();
		crossCommunity.setText("Cross-Community");
		crossCommunity.setValue(false);
		crossCommunity.setEnabled(false);
		validationCheckBoxes.add(crossCommunity);

		soapWrapper = new CheckBox();
		soapWrapper.setText("with SOAP Wrapper");
		soapWrapper.setValue(false);
		soapWrapper.setEnabled(false);
		validationCheckBoxes.add(soapWrapper);

		samlWrapper = new CheckBox();
		samlWrapper.setText("with SAML Wrapper");
		samlWrapper.setValue(false);
		samlWrapper.setEnabled(false);
		validationCheckBoxes.add(samlWrapper);

		httpWrapper = new CheckBox();
		httpWrapper.setText("with HTTP Wrapper");
		httpWrapper.setValue(false);
		httpWrapper.setEnabled(false);
		validationCheckBoxes.add(httpWrapper);

		lessdetail = new CheckBox();
		lessdetail.setText("Show less detail");
		lessdetail.setValue(false);
		lessdetail.setVisible(false);
		validationCheckBoxes.add(lessdetail);

		// UPLOAD FORM
		uploadForm.setAction(GWT.getModuleBaseURL() + "upload");
		uploadForm.setEncoding(FormPanel.ENCODING_MULTIPART);
		uploadForm.setMethod(FormPanel.METHOD_POST);

		uploadForm.setWidget(fileUploadPanel);

		refreshFileUploadPanel();

		//


		topPanel.add(html("<hr />"));
		VerticalPanel fromWhereArea = new VerticalPanel();
		HorizontalPanel inputTypeArea = new HorizontalPanel();
		//		inputTypeArea.add(fromFileRadioButton);
		//		inputTypeArea.add(fromEndpointRadioButton);
		fromFileRadioButton.setValue(true);
		fromEndpointRadioButton.setValue(false);

		fromWhereArea.add(inputTypeArea);
		fromWhereArea.add(uploadForm);

		topPanel.add(fromWhereArea);

		VerticalPanel simArea = new VerticalPanel();

		simEndpointMessage = html("My Simulator");
		simArea.add(simEndpointMessage);
		requestSimEndpoint();

		Button reloadSimFileList = new Button("Reload");
		reloadSimFileList.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				reloadSimFileList();
			}
		});

		simArea.add(reloadSimFileList);

		HorizontalPanel endpointPartsArea = new HorizontalPanel();
		endpointPartsArea.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		endpointPartsArea.add(html("Captured Messages"));

		simArea.add(endpointPartsArea);

		chooseFromEndpointArea.add(simArea);
		endpointPartsArea.add(simFilesListBox);
		simFilesListBox.addItem("My File");
		simFilesListBox.setVisibleItemCount(15);

		FlexTable endpointAreaButtonPanel = new FlexTable();

		Button validateFromEndpointButton = new Button("Validate Request");
		validateFromEndpointButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				new GwtValFormatter().clearResults();
				final ValidationContext vc = new ValidationContext();
				loadValidationContext(vc);
				System.out.println("vs is " + vc.toString());
				vc.hasSoap = true;
				int sel = simFilesListBox.getSelectedIndex();
				if (sel == -1) {
					new PopupMessage("Select message first");
					return;
				}
				filename = simFilesListBox.getValue(sel); 
				initiateValidation();
			}
		});

		endpointAreaButtonPanel.setWidget(0, 0, validateFromEndpointButton);
		//		endpointPartsArea.add(validateFromEndpointButton);

		Button validateRespFromEndpointButton = new Button("Validate Response");
		validateRespFromEndpointButton.setEnabled(false);
		endpointAreaButtonPanel.setWidget(0, 1, validateRespFromEndpointButton);

		Button viewFromEndpointButton = new Button("View Request");
		viewFromEndpointButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				int sel = simFilesListBox.getSelectedIndex();
				if (sel == -1) {
					new PopupMessage("Select message first");
					return;
				}
				filename = simFilesListBox.getValue(sel);
				toolkitService.getSelectedMessage(filename, getTextCallback);
			}
		});

		endpointAreaButtonPanel.setWidget(1, 0, viewFromEndpointButton);
		//		endpointPartsArea.add(viewFromEndpointButton);

		Button viewRespFromEndpointButton = new Button("View Response");
		viewRespFromEndpointButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				int sel = simFilesListBox.getSelectedIndex();
				if (sel == -1) {
					new PopupMessage("Select message first");
					return;
				}
				filename = simFilesListBox.getValue(sel);
				toolkitService.getSelectedMessageResponse(filename, getTextCallback);
			}
		});
		endpointAreaButtonPanel.setWidget(1, 1, viewRespFromEndpointButton);

		Button reExecuteMessageButton = new Button("Re-Execute");
		reExecuteMessageButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				new GwtValFormatter().clearResults();
				int sel = simFilesListBox.getSelectedIndex();
				if (sel == -1) {
					new PopupMessage("Select message first");
					return;
				}
				filename = simFilesListBox.getValue(sel);
				toolkitService.executeSimMessage(filename, messageValidationCallback);
			}
		});

		endpointAreaButtonPanel.setWidget(2, 0, reExecuteMessageButton);

		Button renameMessageButton = new Button("Rename");
		renameMessageButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				int sel = simFilesListBox.getSelectedIndex();
				if (sel == -1) {
					new PopupMessage("Select message first");
					return;
				}
				filename = simFilesListBox.getValue(sel);
				new RenameSimFileDialogBox(topPanel, filename, reloadSimMessages);
			}
		});

		endpointAreaButtonPanel.setWidget(3, 0, renameMessageButton);
		//		endpointPartsArea.add(renameMessageButton);

		Button deleteFromEndpointButton = new Button("Delete");
		deleteFromEndpointButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				int sel = simFilesListBox.getSelectedIndex();
				if (sel == -1) {
					new PopupMessage("Select message first");
					return;
				}
				filename = simFilesListBox.getValue(sel);
				toolkitService.deleteSimFile(filename, reloadSimMessages );
			}
		});

		endpointAreaButtonPanel.setWidget(4, 0, deleteFromEndpointButton);
		//		endpointPartsArea.add(deleteFromEndpointButton);

		endpointPartsArea.add(endpointAreaButtonPanel);

		chooseFromEndpointArea.setVisible(false);
		fromWhereArea.add(chooseFromEndpointArea);

		for (RadioButton b : inputTypeButtons) {
			b.addValueChangeHandler(inputTypeChangedHandler);
		}

		for (RadioButton b : messageTypeButtons) {
			b.addValueChangeHandler(messageTypeValueChangedHandler);
		}


		uploadForm.addSubmitCompleteHandler(new SubmitCompleteHandler() {
			public void onSubmitComplete(SubmitCompleteEvent event) {
				uploadFilename = null;
				timeAndDate = null;
				requestFilename();
				initiateValidation();
			}
		});

		topPanel.add(html("<hr/>"));
	} //end onTabLoad

	private void refreshFileUploadPanel() {

		fileUploadPanel.clear();

		HorizontalPanel upload1Panel = new HorizontalPanel();
		fileUploadPanel.add(upload1Panel);
		upload1Panel.add(new HTML("Message File"));
		FileUpload messageUpload1 = new FileUpload();
		messageUpload1.setName("upload1FormElement");
		upload1Panel.add(messageUpload1);

		if (enableCertificateUpload) {
			HorizontalPanel upload2Panel = new HorizontalPanel();
			fileUploadPanel.add(upload2Panel);
			upload2Panel.add(new HTML("Certificate File"));
			FileUpload messageUpload2 = new FileUpload();
			messageUpload2.setName("upload2FormElement");
			upload2Panel.add(messageUpload2);
			
			upload2Panel.add(new HTML("Password"));
			PasswordTextBox tb = new PasswordTextBox();
			tb.setName("password2");
			upload2Panel.add(tb);

		}

		Button submitButton = new Button("Validate");
		submitButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				new GwtValFormatter().clearResults();
				uploadForm.submit();
			}
		});

		HorizontalPanel runButtonsPanel = new HorizontalPanel();
		fileUploadPanel.add(runButtonsPanel);
		runButtonsPanel.add(submitButton);

		inspectButton = new Button("Inspect Metadata");
		inspectButton.setEnabled(false);
		inspectButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				toolkitService.getLastMetadata(getLastMetadataCallback);
			}
		});

		runButtonsPanel.add(inspectButton);
		
		HTML runWarning = new HTML("<b>ONCE A DOCUMENT is SUBMITTED FOR VALIDATION, PLEASE WAIT for up " + 
		"to a MINUTE for the validation to complete.</b> Submitting another validation request " + "" +
				"causes a known error that will be addressed in the next release.");
		runButtonsPanel.add(runWarning);
	}

	boolean isFileUpload() {
		return fromFileRadioButton.getValue();
	}

	void initiateValidation() {
		timeAndDate = null;
		clientIP = null;
		requestTimeAndDate();
		requestClientIP();
	}

	boolean isReadyForValidation() {
		return (uploadFilename != null || filename != null) && timeAndDate != null && clientIP != null;
	}

	HTML simEndpointMessage;
	void requestSimEndpoint() {
		toolkitService.getSimulatorEndpoint(new AsyncCallback<String>() {

			public void onFailure(Throwable caught) {
				new PopupMessage(caught.getMessage());
			}

			public void onSuccess(String result) {
				try {
					simEndpointMessage.setText("Simulator endpoint is " + result);
				} catch (Exception e) {
					new PopupMessage(e.getMessage());
				}
			}

		});
	}

	String uploadFilename;
	void requestFilename() {
		toolkitService.getLastFilename(new AsyncCallback<String>() {

			public void onFailure(Throwable caught) {
				new PopupMessage(caught.getMessage());			
			}

			public void onSuccess(String result) {
				try {
					uploadFilename = result;
					if (isReadyForValidation())
						requestValidation();
				} catch (Exception e) {
					new PopupMessage(e.getMessage());
				}

			}

		});
	}


	String timeAndDate;
	void requestTimeAndDate() {
		toolkitService.getTimeAndDate(new AsyncCallback<String>() {

			public void onFailure(Throwable caught) {
				new PopupMessage(caught.getMessage());			
			}

			public void onSuccess(String result) {
				try {
					timeAndDate = result;
					if (isReadyForValidation())
						requestValidation();
				} catch (Exception e) {
					new PopupMessage(e.getMessage());
				}

			}

		});
	}

	String clientIP = null;
	@SuppressWarnings("deprecation")
	void requestClientIP() {
		toolkitService.getClientIPAddress(new AsyncCallback<String>() {

			public void onFailure(Throwable caught) {
				new PopupMessage(caught.getMessage());			
			}

			public void onSuccess(String result) {
				try {
					clientIP = result;
					if (isReadyForValidation())
						requestValidation();
				} catch (Exception e) {
					new PopupMessage(e.getMessage());
				}

			}

		});
	}

	void requestValidation() {
		ValidationContext vc = new ValidationContext();
		loadValidationContext(vc);

		System.out.println(vc);
		
		if (isFileUpload()) {
			toolkitService.validateMessage(vc, messageValidationCallback);
		} else {
			toolkitService.validateMessage(vc, filename, messageValidationCallback);
		}
	}

	final AsyncCallback<List<String>> getSimFileNamesCallback = new AsyncCallback<List<String>>() {

		public void onFailure(Throwable caught) {
			new PopupMessage(caught.getMessage());
		}

		public void onSuccess(List<String> result) {
			try {
				simFilesListBox.clear();
				for (String name : result) {
					simFilesListBox.addItem(name);
				}

				chooseFromEndpointArea.setVisible(true);
				uploadForm.setVisible(false);
			} catch (Exception e) {
				new PopupMessage(e.getMessage());
			}

		}

	};

	ValueChangeHandler<Boolean> inputTypeChangedHandler = new ValueChangeHandler<Boolean>() {

		public void onValueChange(ValueChangeEvent<Boolean> event) {
			new GwtValFormatter().clearResults();
			if (fromFileRadioButton.getValue()) {
				chooseFromEndpointArea.setVisible(false);
				uploadForm.setVisible(true);
				inspectButton.setEnabled(false);
			}
			if (fromEndpointRadioButton.getValue()) {
				//				toolkitService.getSimFileSpecs(getSimFileNamesCallback);
			}

			messageTypeValueChangedHandler.onValueChange(null);
		}

	};

	void reloadSimFileList() {
		new GwtValFormatter().clearResults();
		//		toolkitService.getSimFileSpecs(getSimFileNamesCallback);
	}

	ValueChangeHandler<Boolean> messageTypeValueChangedHandler = new ValueChangeHandler<Boolean>() {

		public void onValueChange(ValueChangeEvent<Boolean> ignored) {

			messageTypeButtonMap.get(ValidationTypeXDM).setEnabled(true);

			boolean showCert = false;

			for (RadioButton rb : messageTypeButtons) {
				if (rb.getValue()) {
					String msgType = rb.getText();

					if (msgType.equals(ValidationType_Ret) ||
							msgType.equals(ValidationType_SQ) ||
							msgType.equals(ValidationType_xcpd) ||
							msgType.equals(ValidationType_NwHINxcpd)){
						requestMessage.setValue(true);
						responseMessage.setValue(false);
						crossCommunity.setValue(false);
						soapWrapper.setValue(true);
						samlWrapper.setValue(true);
						httpWrapper.setValue(true);

						requestMessage.setEnabled(true);
						responseMessage.setEnabled(true);
						crossCommunity.setEnabled(true);
						soapWrapper.setEnabled(true);
						samlWrapper.setEnabled(true);
						httpWrapper.setEnabled(true);
					}

					else if (msgType.equals(ValidationType_guess) ||
							msgType.equals(ValidationType_C32) ||
							msgType.equals(ValidationTypeXDM)) {
						requestMessage.setValue(false);
						responseMessage.setValue(false);
						crossCommunity.setValue(false);
						soapWrapper.setValue(false);
						samlWrapper.setValue(false);
						httpWrapper.setValue(false);

						requestMessage.setEnabled(false);
						responseMessage.setEnabled(false);
						crossCommunity.setEnabled(false);
						soapWrapper.setEnabled(false);
						samlWrapper.setEnabled(false);
						httpWrapper.setEnabled(false);
					}

					else if (msgType.equals(ValidationTypeR_b) ||
							msgType.equals(ValidationTypeXDR) ||
							msgType.equals(ValidationType_PnR_b) ) {
						requestMessage.setValue(true);
						responseMessage.setValue(false);
						crossCommunity.setValue(false);
						soapWrapper.setValue(true);
						samlWrapper.setValue(true);
						httpWrapper.setValue(true);

						requestMessage.setEnabled(true);
						responseMessage.setEnabled(true);
						crossCommunity.setEnabled(false);
						soapWrapper.setEnabled(true);
						samlWrapper.setEnabled(true);
						httpWrapper.setEnabled(true);
					}
					else if(msgType.equals(ValidationType_ncpdp)){
						requestMessage.setValue(true);
						responseMessage.setValue(false);
						crossCommunity.setValue(false);
						soapWrapper.setValue(false);
						samlWrapper.setValue(false);
						httpWrapper.setValue(false);

						requestMessage.setEnabled(false);
						responseMessage.setEnabled(false);
						crossCommunity.setEnabled(false);
						soapWrapper.setEnabled(false);
						samlWrapper.setEnabled(false);
						httpWrapper.setEnabled(false);	
					}
					else if(msgType.equals(ValidationType_direct)){
						requestMessage.setValue(true);
						responseMessage.setValue(false);
						crossCommunity.setValue(false);
						soapWrapper.setValue(false);
						samlWrapper.setValue(false);
						httpWrapper.setValue(false);

						requestMessage.setEnabled(false);
						responseMessage.setEnabled(false);
						crossCommunity.setEnabled(false);
						soapWrapper.setEnabled(false);
						samlWrapper.setEnabled(false);
						httpWrapper.setEnabled(false);	

						showCert = true;
					} else {
						requestMessage.setValue(true);
						responseMessage.setValue(false);
						crossCommunity.setValue(false);
						soapWrapper.setValue(false);
						samlWrapper.setValue(false);
						httpWrapper.setValue(false);

						requestMessage.setEnabled(false);
						responseMessage.setEnabled(false);
						crossCommunity.setEnabled(false);
						soapWrapper.setEnabled(false);
						samlWrapper.setEnabled(false);
						httpWrapper.setEnabled(false);	
					}
				}
			}

			if (showCert && !enableCertificateUpload) {
				enableCertificateUpload = true;
				refreshFileUploadPanel();
			} else if (!showCert && enableCertificateUpload) {
				enableCertificateUpload = false;
				refreshFileUploadPanel();
			}

			// this reaches out to the from file / from endpoint  radio buttons
			if (fromEndpointRadioButton.getValue()) {
				if (messageTypeButtonMap.get(ValidationTypeXDM).getValue()) 
					messageTypeButtonMap.get(ValidationType_guess).setValue(true);

				messageTypeButtonMap.get(ValidationTypeXDM).setEnabled(false);

				soapWrapper.setValue(true);
				samlWrapper.setValue(true);
				httpWrapper.setValue(true);
				requestMessage.setValue(true);

				soapWrapper.setEnabled(false);
				samlWrapper.setEnabled(false);
				httpWrapper.setEnabled(false);
				requestMessage.setEnabled(false);
				responseMessage.setEnabled(false);

			}

		}

	};

	protected AsyncCallback reloadSimMessages = new AsyncCallback () {

		public void onFailure(Throwable caught) {
			new PopupMessage(caught.getMessage());			
		}

		public void onSuccess(Object result) {
			try {
				reloadSimFileList();
			} catch (Exception e) {
				new PopupMessage(e.getMessage());
			}

		}

	};

	protected AsyncCallback<List<Result>> getLastMetadataCallback = new AsyncCallback<List<Result>> () {

		public void onFailure(Throwable caught) {
			new GwtValFormatter().addCell(caught.getMessage(), 0);
			topPanel.add(resultsTable);
		}

		public void onSuccess(List<Result> results) {
			try {
				inspect(results);
				//			InspectorTab itab = new InspectorTab();
				//			itab.onTabLoad(myContainer, true, toolkitService, results, null);
			} catch (Exception e) {
				new PopupMessage(e.getMessage());
			}

		}

	};

	protected AsyncCallback<List<Result>> getTextCallback = new AsyncCallback<List<Result>> () {

		public void onFailure(Throwable caught) {
			new GwtValFormatter().addCell(caught.getMessage(), 0);
			topPanel.add(resultsTable);
		}

		public void onSuccess(List<Result> result) {
			try {
				viewText(result);
			} catch (Exception e) {
				new PopupMessage(e.getMessage());
			}

		}

	};

	void inspect(List<Result> results) {
		MetadataInspectorTab it = new MetadataInspectorTab();
		it.setResults(results);
		it.setSiteSpec(null);
		it.setToolkitService(toolkitService);
		it.onTabLoad(myContainer, true, null);
	}

	void viewText(List<Result> results) {
		TextViewerTab v = new TextViewerTab();
		v.setResult(results);
		v.onTabLoad(myContainer, true, null);
	}

	protected AsyncCallback<MessageValidationResults> messageValidationCallback = new AsyncCallback<MessageValidationResults> () {

		public void onFailure(Throwable caught) {
			new GwtValFormatter().addCell(caught.getMessage(), 0);
			topPanel.add(resultsTable);
		}

		public void onSuccess(MessageValidationResults result) {
			try {
				displayResults(result);
			} catch (Exception e) {
				new PopupMessage(e.getMessage());
			}

		}

	};

	class GwtValFormatter implements ValFormatter {

		public void addCell(String msg, int col) {
			HTML main = new HTML();
			main.setHTML(msg);
			resultsTable.setWidget(row, col, main);
		}

		public void hr() {
			for (int i=0; i<3; i++) {
				addCell("<hr/>", i);
			}
			row++;
		}

		public void clearResults() {
			resultsTable.clear();
			row=0;
		}

		public void setDetail(String detail) {
			addCell(detail, 0);
		}

		public void setReference(String ref) {
			addCell(ref, 2);
		}

		public void setStatus(String status) {
			addCell(status, 1);
		}

		public String red(String msg) {
			return "<font color=\"#FF0000\">" + msg  + "</font>";
		}

		public String blue(String msg) {
			return "<font color=\"#0000FF\">" + msg  + "</font>";
		}

		public String bold(String msg) {
			return "<b>" + msg + "</b>";
		}

		public String h2(String msg) {
			return "<h2>" + msg + "</h2>";
		}

		public void setCell(String msg, int row, int col) {
			HTML main = new HTML();
			main.setHTML(msg);
			resultsTable.setWidget(row, col, main);
		}

		public String h3(String msg) {
			return "<h3>" + msg + "</h3>";
		}

		public void incRow() {
			row++;
		}

		public int getRow() {
			return row;
		}

	}

	void displayResults(MessageValidationResults results) {
		//		int summaryRow;
		//		boolean foundErrors = false;
		GwtValFormatter f = new GwtValFormatter();

		MessageValidatorDisplay mvd = new MessageValidatorDisplay(f);
		mvd.setClientIP(clientIP);
		mvd.setTimeAndDate(timeAndDate);
		mvd.setUploadFilename(uploadFilename);
		mvd.setLessDetail(lessdetail.getValue());

		mvd.displayResults(results);

		//		// leave as summary row (plus a blank for separation)
		//		summaryRow = row;
		//		f.setDetail("   ");
		//		row++;
		//
		//		f.setDetail("Time of validation: " + timeAndDate);
		//		row++;
		//
		//		f.setDetail("Client IP Address: " + clientIP);
		//		row++;
		//
		//		if (uploadFilename != null) {
		//			f.setDetail("File validated: " + uploadFilename);
		//			row++;
		//		}
		//		f.hr();
		//
		//		f.setDetail(f.h2("Detail"));
		//		f.setReference(f.h2("Reference"));
		//		f.setStatus(f.h2("Status"));
		//		row++;
		//
		//		for (ValidationStepResult result : results.getResults()) {
		//			f.hr();
		//			f.addCell(h3(result.stepName), 0);
		//			row++;
		//
		//			List<ValidatorErrorItem> ers = result.er;
		//			for (ValidatorErrorItem er : ers)  {
		//				boolean row_advance = true;
		//				switch (er.level) {
		//				case SECTIONHEADING:
		//					f.setDetail(f.bold(er.msg));
		//					break;
		//
		//				case CHALLENGE:
		//					if (!lessdetail.getValue()) 
		//						f.setDetail(er.msg);
		//					else
		//						row_advance = false;
		//					break;
		//
		//				case EXTERNALCHALLENGE:
		//					f.setDetail(er.msg);
		//					break;
		//
		//				case DETAIL:
		//					f.setDetail(er.msg);
		//					break;
		//
		//				case ERROR:
		//					f.setDetail(f.red(er.msg));
		//					f.setReference(f.red(er.resource));
		//					foundErrors = true;
		//					f.setStatus(f.red("error"));
		//				}
		//
		//				if (!lessdetail.getValue()) {
		//					if (er.completion == ReportingCompletionType.OK)
		//						f.setStatus("ok");
		//					else if (ReportingLevel.CHALLENGE == er.level && er.completion == ReportingCompletionType.ERROR)
		//						f.setStatus(f.red("error"));
		//				}
		//				if (row_advance)
		//					row++;
		//			}
		//		}
		//
		//		if (foundErrors)
		//			resultsTable.setWidget(summaryRow, 0, html(f.red("Summary: Errors were found")));
		//		else
		//			resultsTable.setWidget(summaryRow, 0, html("Summary: No error were found"));

		topPanel.add(resultsTable);

		inspectButton.setEnabled(true);
	}


	public String getWindowShortName() {
		return "messagevalidator";
	}



}
