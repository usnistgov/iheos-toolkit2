package gov.nist.toolkit.xdstools2.client.tabs.messageValidator;


import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import gov.nist.toolkit.actorfactory.client.CcdaTypeSelection;
import gov.nist.toolkit.http.client.HtmlMarkup;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.valsupport.client.*;
import gov.nist.toolkit.xdstools2.client.ToolWindow;
import gov.nist.toolkit.xdstools2.client.command.command.*;
import gov.nist.toolkit.xdstools2.client.inspector.MetadataInspectorTab;
import gov.nist.toolkit.xdstools2.client.tabs.TextViewerTab;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;
import gov.nist.toolkit.xdstools2.client.widgets.RenameSimFileDialogBox;
import gov.nist.toolkit.xdstools2.shared.command.request.DeleteSimFileRequest;
import gov.nist.toolkit.xdstools2.shared.command.request.ExecuteSimMessageRequest;
import gov.nist.toolkit.xdstools2.shared.command.request.GetSelectedMessageRequest;
import gov.nist.toolkit.xdstools2.shared.command.request.ValidateMessageRequest;

import java.util.*;

public class MessageValidatorTab extends ToolWindow {
    FlexTable resultsTable = new FlexTable();
    HTML htmlReport = new HTML();
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

    final VerticalPanel chooseFromEndpointArea = new VerticalPanel();
    final FormPanel uploadForm = new FormPanel();
    final ListBox simFilesListBox = new ListBox();
    CcdaTypeSelection ccdaSel;

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

    static List<String> ccdaRequiredValidationTypes =
            Arrays.asList(
                    ValidationTypeDirectXDM,
                    ValidationTypeDirectXDR,
                    ValidationType_direct
            );

    boolean requiresCCDA(String type) {
        return ccdaRequiredValidationTypes.contains(type);
    }

    boolean isMessageValidationType(String type) {
        return msgValidationTypes.contains(type);
    }

    boolean isDocumentValidationType(String type) {
        return docTypeValidationTypes.contains(type);
    }

    List<RadioButton> messageTypeButtons;
    Map<String, RadioButton> messageTypeButtonMap = new HashMap<String, RadioButton>();

    void addValidationTypesRadioGroup(VerticalPanel panel, boolean enable) {

        //Message Types
        messageTypeButtons = new ArrayList<RadioButton>();
        panel.add(HtmlMarkup.html("<hr />"));
        panel.add(HtmlMarkup.html(HtmlMarkup.bold("Message Types")));
        for (String type : msgValidationTypes) {
            if (type.equals(ValidationType_ncpdp))
                continue;
            addValidationTypeRadioButton(panel, type, enable);
        }
        panel.add(HtmlMarkup.html("<hr />"));
        panel.add(HtmlMarkup.html(HtmlMarkup.bold("Document Types")));
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
        }

        if (type.equals(ValidationType_NwHINxcpd)) {
        }
        if (type.equals(ValidationType_ncpdp)) {
            panel.add(HtmlMarkup.html("<hr />"));
            panel.add(HtmlMarkup.html(HtmlMarkup.bold("E-Prescription")));
        }
        if (type.equals(ValidationType_C32)) {
        }
        if (type.equals(ValidationType_direct)) {
        }
        if (type.equals(ValidationType_CCDA)) {
            panel.add(HtmlMarkup.html("<hr />"));
            panel.add(HtmlMarkup.html(HtmlMarkup.bold("CCDA Document Validator (CCDA validation may take more than a minute to run)")));

            List<String> ccdaTypes = ccdaSel.ccdaTypes();
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
            selectionChanged();
        }


    };

    void selectionChanged() {
        boolean enableContentType = false;

        for (String type : messageTypeButtonMap.keySet()) {
            RadioButton r = messageTypeButtonMap.get(type);
            //type.indexOf("MU 2 CCDA") != -1) {
            if (r.getValue()) {
                boolean withHttpWrapper = httpWrapper.getValue();
                if (isMessageValidationType(type) && requiresCCDA(type) && withHttpWrapper) {
                    enableContentType = true;
                } else if (isDocumentValidationType(type) && requiresCCDA(type)) {
                    enableContentType = true;
                }
                break;
            }
        }
        ccdaSel.enableCcdaTypesRadioGroup(enableContentType);
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
        panel.add(HtmlMarkup.html("<hr />"));
        panel.add(HtmlMarkup.html(HtmlMarkup.bold("In/Out Message Types")));
        requestMessage = new RadioButton(inOutGroupName, "Request Message");
        responseMessage = new RadioButton(inOutGroupName, "Response Message");

        requestMessage.setEnabled(enable);
        responseMessage.setEnabled(enable);
        //		requestMessage.setValue(true);
        panel.add(requestMessage);
        panel.add(responseMessage);
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
            ccdaSel.addDocTypeToValidation(vc);
        }
        else if (msgType.equals(ValidationTypeXDR)) {
            vc.isXDR = true;
            vc.isPnR = true;
        }
        else if (msgType.equals(ValidationTypeDirectXDR)) {
            vc.isXDR = true;
            vc.isPnR = true;
            ccdaSel.addDocTypeToValidation(vc);
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
            ccdaSel.addDocTypeToValidation(vc);
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

    MessageValidatorTab me;

    @Override
    public void onTabLoad(boolean select, String eventName) {
        me = this;
        ccdaSel = new CcdaTypeSelection(tkProps(), null);

        registerTab(select, eventName);

        tabTopPanel.add(HtmlMarkup.html(HtmlMarkup.h2("Message Validator")));

        tabTopPanel.add(HtmlMarkup.html("<hr />"));

        VerticalPanel messageTypeArea = new VerticalPanel();
        VerticalPanel validationCheckBoxes = new VerticalPanel();
        VerticalPanel inOutTypeArea = new VerticalPanel();
        HorizontalPanel topH = new HorizontalPanel();
        VerticalPanel rightSideVert = new VerticalPanel();
        HorizontalPanel typesAndWrappers = new HorizontalPanel();

        // build structure
        tabTopPanel.add(topH);
        topH.add(messageTypeArea);
        topH.add(rightSideVert);
        rightSideVert.add(typesAndWrappers);
        ccdaSel.addCcdaTypesRadioGroup(rightSideVert, ccdaSel.ccdaTypes(), "CCDA Type for XDM or XDR content (CCDA validation may take a minute or more to run)");
        ccdaSel.enableCcdaTypesRadioGroup(false);
        typesAndWrappers.add(inOutTypeArea);
        typesAndWrappers.add(validationCheckBoxes);

        // Message type radio buttons
        addValidationTypesRadioGroup(messageTypeArea, true);

        // InOut radio Buttons
        addInOutTypesRadioGroup(inOutTypeArea, false);

        // Validation Check Boxes
        validationCheckBoxes.add(HtmlMarkup.html("<hr />"));
        validationCheckBoxes.add(HtmlMarkup.html(HtmlMarkup.bold("Message Structure Validators")));
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
        httpWrapper.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                selectionChanged();
            }
        });
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


        tabTopPanel.add(HtmlMarkup.html("<hr />"));
        VerticalPanel fromWhereArea = new VerticalPanel();
        HorizontalPanel inputTypeArea = new HorizontalPanel();
        //		inputTypeArea.addTest(fromFileRadioButton);
        //		inputTypeArea.addTest(fromEndpointRadioButton);

        fromWhereArea.add(inputTypeArea);
        fromWhereArea.add(uploadForm);

        tabTopPanel.add(fromWhereArea);

        VerticalPanel simArea = new VerticalPanel();

        simEndpointMessage = HtmlMarkup.html("My Simulator");
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
        endpointPartsArea.add(HtmlMarkup.html("Captured Messages"));

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
                final ValidationContext vc = EmptyValidationContextFactory.validationContext();
                loadValidationContext(vc);
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
        //		endpointPartsArea.addTest(validateFromEndpointButton);

        Button validateRespFromEndpointButton = new Button("Validate Response");
        validateRespFromEndpointButton.setEnabled(false);
        endpointAreaButtonPanel.setWidget(0, 1, validateRespFromEndpointButton);

        Button viewFromEndpointButton = new Button("View Request");
        viewFromEndpointButton.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                testMessageSelection();
                new GetSelectedMessageCommand(){
                    @Override
                    public void onComplete(List<Result> result) {
                        viewResults(result);
                    }
                }.run(new GetSelectedMessageRequest(getCommandContext(),filename));
            }
        });

        endpointAreaButtonPanel.setWidget(1, 0, viewFromEndpointButton);
        //		endpointPartsArea.addTest(viewFromEndpointButton);

        Button viewRespFromEndpointButton = new Button("View Response");
        viewRespFromEndpointButton.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                testMessageSelection();
                new GetSelectedMessageResponseCommand(){
                    @Override
                    public void onComplete(List<Result> result) {
                        viewResults(result);
                    }
                }.run(new GetSelectedMessageRequest(getCommandContext(),filename));
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
                new ExecuteSimMessageCommand(){
                    @Override
                    public void onComplete(MessageValidationResults result) {
                        messageValidationCallbackSuccess(result);
                    }
                }.run(new ExecuteSimMessageRequest(getCommandContext(),filename));
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
                new RenameSimFileDialogBox(me.getRawPanel(), filename, reloadSimMessages);
            }
        });

        endpointAreaButtonPanel.setWidget(3, 0, renameMessageButton);
        //		endpointPartsArea.addTest(renameMessageButton);

        Button deleteFromEndpointButton = new Button("Delete");
        deleteFromEndpointButton.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                int sel = simFilesListBox.getSelectedIndex();
                if (sel == -1) {
                    new PopupMessage("Select message first");
                    return;
                }
                filename = simFilesListBox.getValue(sel);
                new DeleteSimFileCommand(){
                    @Override
                    public void onComplete(Void result) {
                        reloadSimFileList();
                    }
                }.run(new DeleteSimFileRequest(getCommandContext(),filename));
            }
        });

        endpointAreaButtonPanel.setWidget(4, 0, deleteFromEndpointButton);
        //		endpointPartsArea.addTest(deleteFromEndpointButton);

        endpointPartsArea.add(endpointAreaButtonPanel);

        chooseFromEndpointArea.setVisible(false);
        fromWhereArea.add(chooseFromEndpointArea);

//		for (RadioButton b : inputTypeButtons) {
//			b.addValueChangeHandler(inputTypeChangedHandler);
//		}

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

        tabTopPanel.add(HtmlMarkup.html("<hr/>"));
    } //end onTabLoad

    private void testMessageSelection(){
        int sel = simFilesListBox.getSelectedIndex();
        if (sel == -1) {
            new PopupMessage("Select message first");
            return;
        }
        filename = simFilesListBox.getValue(sel);
    }

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
                new GetLastMetadataCommand(){
                    @Override
                    public void onFailure(Throwable caught) {
                        new GwtValFormatter().addCell(caught.getMessage(), 0);
                        tabTopPanel.add(resultsTable);
                    }

                    @Override
                    public void onComplete(List<Result> results) {
                        try {
                            inspect(results);
                            //			InspectorTab itab = new InspectorTab();
                            //			itab.onTabLoad(getTabContainer(), true, toolkitService, results, null);
                        } catch (Exception e) {
                            new PopupMessage(e.getMessage());
                        }

                    }
                }.run(getCommandContext());
            }
        });

        runButtonsPanel.add(inspectButton);

        HTML runWarning = new HTML("<b>ONCE A DOCUMENT is SUBMITTED FOR VALIDATION, PLEASE WAIT for up " +
                "to a MINUTE for the validation to complete.</b> Submitting another validation request " + "" +
                "causes a known error that will be addressed in the next release.");
        runButtonsPanel.add(runWarning);
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
        new GetSimulatorEndpointCommand(){
            @Override
            public void onComplete(String result) {
                try {
                    simEndpointMessage.setText("Simulator endpoint is " + result);
                } catch (Exception e) {
                    new PopupMessage(e.getMessage());
                }
            }
        }.run(getCommandContext());
    }

    String uploadFilename;
    void requestFilename() {
        new GetLastFilenameCommand(){
            @Override
            public void onComplete(String result) {
                try {
                    uploadFilename = result;
                    if (isReadyForValidation())
                        requestValidation();
                } catch (Exception e) {
                    new PopupMessage(e.getMessage());
                }
            }
        }.run(getCommandContext());
    }


    String timeAndDate;
    void requestTimeAndDate() {
        new GetTimeAndDateCommand(){
            @Override
            public void onComplete(String result) {
                try {
                    timeAndDate = result;
                    if (isReadyForValidation())
                        requestValidation();
                } catch (Exception e) {
                    new PopupMessage(e.getMessage());
                }
            }
        }.run(getCommandContext());
    }

    String clientIP = null;
    @SuppressWarnings("deprecation")
    void requestClientIP() {
        ClientUtils.INSTANCE.getToolkitServices().getClientIPAddress(new AsyncCallback<String>() {

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

    private void messageValidationCallbackSuccess(MessageValidationResults results){
        try {
            displayResults(results);
        } catch (Exception e) {
            new PopupMessage(e.getMessage());
        }
    }

    void requestValidation() {
        ValidationContext vc = EmptyValidationContextFactory.validationContext();
        loadValidationContext(vc);

        System.out.println(vc);

        new ValidateMessageCommand(){
            @Override
            public void onComplete(MessageValidationResults result) {
                messageValidationCallbackSuccess(result);
            }
        }.run(new ValidateMessageRequest(getCommandContext(),vc));
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
            chooseFromEndpointArea.setVisible(false);
            uploadForm.setVisible(true);
            inspectButton.setEnabled(false);

            messageTypeValueChangedHandler.onValueChange(null);
        }

    };

    void reloadSimFileList() {
        new GwtValFormatter().clearResults();
        //		toolkitService.getSimFileSpecs(getSimFileNamesCallback);
    }

    // this now is only used to initialize the settings
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
                            msgType.equals(ValidationType_PnR_b) ||
                            msgType.equals(ValidationTypeDirectXDR)) {
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

    private void viewResults(List<Result> results){
        try {
            viewText(results);
        } catch (Exception e) {
            new PopupMessage(e.getMessage());
        }
    }
    void inspect(List<Result> results) {
        MetadataInspectorTab it = new MetadataInspectorTab();
        it.setResults(results);
        it.setSiteSpec(null);
//		it.setToolkitService(toolkitService);
        it.onTabLoad(true, "Insp");
    }

    void viewText(List<Result> results) {
        TextViewerTab v = new TextViewerTab();
        v.setResult(results);
        v.onTabLoad(true, "Text");
    }

    class GwtValFormatter implements ValFormatter {

        public void addCell(String msg, int col) {
            HTML main = new HTML();
            main.setHTML(msg);
            resultsTable.setCellPadding(2);
            //resultsTable.setBorderWidth(2);
            resultsTable.setWidget(row, col, main);
            //resultsTable.getCellFormatter().setHorizontalAlignment(row, col, HasHorizontalAlignment.ALIGN_CENTER);
        }

        public void hr() {
            for (int i=0; i<6; i++) {
                addCell("<hr/>", i);
            }
            row++;
        }

        public void clearResults() {
            resultsTable.removeAllRows();
            row=0;
        }

        public void setColSpan(int col, int colSpan) {
            resultsTable.getFlexCellFormatter().setColSpan(row, col, colSpan);
        }

        public void setDetail(String detail) {
            addCell(detail, 0);
        }

        public void setDTS(String dts) {
            addCell(dts, 2);
        }

        public void setFound(String found) {
            addCell(found, 3);
        }

        public void setReference(String ref) {
            addCell(ref, 2);
        }

        public void setStatus(String status) {
            addCell(status, 1);
        }

        public void setName(String name) {
            addCell(name, 0);
        }

        public void setExpected(String expected) {
            addCell(expected, 4);
        }

        public void setRFC(String rfc) {
            addCell(rfc, 5);
        }

        public String red(String msg) {
            return "<font color=\"#FF0000\">" + msg  + "</font>";
        }

        public String blue(String msg) {
            return "<font color=\"#0000FF\">" + msg  + "</font>";
        }

        public String green(String msg) {
            return "<font color=\"#66CD00\">" + msg  + "</font>";
        }

        public String purple(String msg) {
            return "<font color=\"#551A8B\">" + msg  + "</font>";
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

        @Override
        public String rfc_link(String msg) {
            String res = "";
            if(msg.contains(";")) {
                String[] msgSplit = msg.split(";");
                for(int i=0 ; i < msgSplit.length ; i=i+2) {
                    if(i+1 >= msgSplit.length) {
                        break;
                    }
                    res += html_link(msgSplit[i], msgSplit[i+1]);
                }
                if(res.equals("")) {
                    res = msg;
                }
            } else {
                res = msg;
            }
            return res;
        }

        public String html_link(String msg, String url) {
            return "<a href=\"" + url + "\" target=\"_blank\">"+ msg + "</a><br>";
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

        this.htmlReport.removeFromParent();
        this.htmlReport = new HTML(results.getHtmlResults());
        tabTopPanel.add(this.htmlReport);
        //tabTopPanel.addTest(resultsTable);

        inspectButton.setEnabled(true);
    }


    public String getWindowShortName() {
        return "messagevalidator";
    }



}
