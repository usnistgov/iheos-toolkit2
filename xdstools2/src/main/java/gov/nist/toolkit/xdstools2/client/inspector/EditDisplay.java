package gov.nist.toolkit.xdstools2.client.inspector;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import gov.nist.toolkit.http.client.HtmlMarkup;
import gov.nist.toolkit.registrymetadata.client.DocumentEntry;
import gov.nist.toolkit.results.client.CodesConfiguration;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.valsupport.client.MessageValidationResults;
import gov.nist.toolkit.xdsexception.client.NoDifferencesException;
import gov.nist.toolkit.xdsexception.client.ToolkitRuntimeException;
import gov.nist.toolkit.xdstools2.client.command.command.UpdateDocumentEntryCommand;
import gov.nist.toolkit.xdstools2.client.command.command.ValidateDocumentEntryCommand;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;
import gov.nist.toolkit.xdstools2.client.widgets.queryFilter.CodeFilter;
import gov.nist.toolkit.xdstools2.client.widgets.queryFilter.CodeFilterBank;
import gov.nist.toolkit.xdstools2.client.widgets.queryFilter.StatusDisplay;
import gov.nist.toolkit.xdstools2.shared.command.request.UpdateDocumentEntryRequest;
import gov.nist.toolkit.xdstools2.shared.command.request.ValidateDocumentEntryRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditDisplay extends CommonDisplay {
    private Button validateMuBtn = new Button("Validate");
    private Button updateBtn = new Button("Update");
    private DocumentEntry de;
    private TestInstance logId;
    Map<String, List<String>> codeSpecMap = new HashMap<String, List<String>>();

    // Edit controls
    private TextBox titleTxt = new TextBox();
    private TextBox commentsTxt = new TextBox();
    CodeFilterBank codeFilterBank;
    HTML statusBox = new HTML();
    VerticalPanel resultPanel = new VerticalPanel();
    StatusDisplay statusDisplay = new StatusDisplay() {
        @Override
        public VerticalPanel getResultPanel() {
            return resultPanel;
        }

        @Override
        public void setStatus(String message, boolean status) {
            statusBox.setHTML(HtmlMarkup.bold(HtmlMarkup.red(message, status)));
        }
    };


//    HTML errorMsgs = new HTML();

    void applyChanges() {
        if (de==null) throw new ToolkitRuntimeException("Unexpected null documentEntry");

        de.title = titleTxt.getText();
        de.titleX = "";
        de.titleDoc = "";
        de.comments = commentsTxt.getText();
        de.commentsX = "";
        de.commentsDoc = "";

        codeSpecMap.clear();
        addToCodeSpec(codeSpecMap);

        if (de.classCode!=null) {
            de.classCode.clear();
            if (codeSpecMap.containsKey(CodesConfiguration.ClassCode)) {
                de.classCode.addAll(codeSpecMap.get(CodesConfiguration.ClassCode));
            }
           if (de.classCodeX!=null)
               de.classCodeX.clear();
           if (de.classCodeDoc!=null)
               de.classCodeDoc.clear();
        }

        if (de.confCodes!=null) {
            de.confCodes.clear();
            if (codeSpecMap.containsKey(CodesConfiguration.ConfidentialityCode)) {
                de.confCodes.addAll(codeSpecMap.get(CodesConfiguration.ConfidentialityCode));
            }
            if (de.confCodesX!=null)
                de.confCodesX.clear();
            if (de.confCodesDoc!=null)
                de.confCodesDoc.clear();
        }

        if (de.eventCodeList!=null) {
            de.eventCodeList.clear();
            if (codeSpecMap.containsKey(CodesConfiguration.EventCodeList)) {
                de.eventCodeList.addAll(codeSpecMap.get(CodesConfiguration.EventCodeList));
            }
            if (de.eventCodeListX!=null)
                de.eventCodeListX.clear();
            if (de.eventCodeListDoc!=null)
                de.eventCodeListDoc.clear();
        }

        if (de.formatCode!=null) {
            de.formatCode.clear();
            if (codeSpecMap.containsKey(CodesConfiguration.FormatCode)) {
                de.formatCode.addAll(codeSpecMap.get(CodesConfiguration.FormatCode));
            }
            if (de.formatCodeX!=null)
                de.formatCodeX.clear();
            if (de.formatCodeDoc!=null)
                de.formatCodeDoc.clear();
        }

        if (de.hcftc!=null) {
            de.hcftc.clear();
            if (codeSpecMap.containsKey(CodesConfiguration.HealthcareFacilityTypeCode)) {
                de.hcftc.addAll(codeSpecMap.get(CodesConfiguration.HealthcareFacilityTypeCode));
            }
            if (de.hcftcX!=null)
                de.hcftcX.clear();
            if (de.hcftcDoc!=null)
                de.hcftcDoc.clear();
        }

        if (de.pracSetCode!=null) {
            de.pracSetCode.clear();
            if (codeSpecMap.containsKey(CodesConfiguration.PracticeSettingCode)) {
                de.pracSetCode.addAll(codeSpecMap.get(CodesConfiguration.PracticeSettingCode));
            }
            if (de.pracSetCodeX!=null)
                de.pracSetCodeX.clear();
            if (de.pracSetCodeDoc!=null)
                de.pracSetCodeDoc.clear();
        }


        if (de.typeCode!=null) {
            de.typeCode.clear();
            if (codeSpecMap.containsKey(CodesConfiguration.TypeCode)) {
                de.typeCode.addAll(codeSpecMap.get(CodesConfiguration.TypeCode));
            }
            if (de.typeCodeX!=null)
                de.typeCodeX.clear();
            if (de.typeCodeDoc!=null)
                de.typeCodeDoc.clear();
        }

    }


    public void addToCodeSpec(Map<String, List<String>> codeSpec) {
//        deStatusFilter.addToCodeSpec(codeSpec, CodesConfiguration.DocumentEntryStatus);
//        creationTimeFromFilter.addToCodeSpec(codeSpec, CodesConfiguration.CreationTimeFrom);
//        creationTimeToFilter.addToCodeSpec(codeSpec, CodesConfiguration.CreationTimeTo);
//        serviceStartTimeFromFilter.addToCodeSpec(codeSpec, CodesConfiguration.ServiceStartTimeFrom);
//        serviceStartTimeToFilter.addToCodeSpec(codeSpec, CodesConfiguration.ServiceStartTimeTo);
//        serviceStopTimeFromFilter.addToCodeSpec(codeSpec, CodesConfiguration.ServiceStopTimeFrom);
//        serviceStopTimeToFilter.addToCodeSpec(codeSpec, CodesConfiguration.ServiceStopTimeTo);
//        authorFilter.addToCodeSpec(codeSpec, CodesConfiguration.AuthorPerson);
//        onDemandFilter.addToCodeSpec(codeSpec, CodesConfiguration.DocumentEntryType);
//        returnFilter.addToCodeSpec(codeSpec, CodesConfiguration.ReturnsType);
        codeFilterBank.addToCodeSpec(codeSpec);

    }

    private class ValidateClickHandler implements ClickHandler {
        @Override
        public void onClick(ClickEvent clickEvent) {
            applyChanges();
            new ValidateDocumentEntryCommand() {
                @Override
                public void onComplete(MessageValidationResults result) {
                    if (result!=null) {
                        new PopupMessage(result.getHtmlResults());
                    }
                    else new PopupMessage("result is null");
                }
            }.run(new ValidateDocumentEntryRequest(ClientUtils.INSTANCE.getCommandContext(), de));
        }
    }

    private class UpdateClickHandler implements ClickHandler {
        @Override
        public void onClick(ClickEvent clickEvent) {
            applyChanges();
            new UpdateDocumentEntryCommand() {
                @Override
                public void onFailure(Throwable throwable) {
                    if (throwable instanceof NoDifferencesException) {
                       new PopupMessage("No differences found. Do you want to continue?");
                    } else
                        new PopupMessage(throwable.toString());
                }

                @Override
                public void onComplete(List<Result> result) {
                    // TODO: should we append the result to the Inspector?
                    if (result!=null) {
                        if (result.size() > 0)
                            new PopupMessage("got " + result.get(0).passed() + " " + result.size());
                        else
                            new PopupMessage("0 result");
                    } else {
                        new PopupMessage("null result!");
                    }
                }
            }.run(new UpdateDocumentEntryRequest(ClientUtils.INSTANCE.getCommandContext(), it.data.siteSpec, it.data.combinedMetadata, de, logId));
        }
    }


    public EditDisplay(MetadataInspectorTab it, final DocumentEntry de, final TestInstance logId) {
        this.detailPanel = it.detailPanel;
        this.metadataCollection = it.data.combinedMetadata;
        this.it = it;
        this.logId = logId;
        this.de = DocumentEntry.clone(de);
        validateMuBtn.addClickHandler(new ValidateClickHandler());
        updateBtn.addClickHandler(new UpdateClickHandler());

        // The collective validate bank being assembled
        codeFilterBank = new CodeFilterBank(statusDisplay);

        editDetail();
    }

   private void editDetail() {
       detailPanel.clear();
//		detailPanel.add(HyperlinkFactory.addHTML("<h4>Document Entry</h4>"));
        String title = (de.isFhir) ? "<h4>Document Entry (translated from DocumentReference)</h4>" : "<h4>Metadata Update - Document Entry</h4>";
        addTitle(HyperlinkFactory.addHTML(title));
        FlexTable ft = new FlexTable();
        int row=0;
        boolean b = false;


        // TODO:
       // MU Suppl
       // 4.2.3.2.32 DocumentEntry.version
       //  "The first version of a DocumentEntry shall have a value of 1."
       // Check this value for an Integer. If not, disable update if value is null.
       // Use Metadata.MU_NOT_SUPPORTED

       // TODO:
       // Call documententry validators on 'Validate' button onClick

       ft.setWidget(row, 0, validateMuBtn);
       ft.setWidget(row, 1, updateBtn);
       row++;

       /**
        * See ITI TF Vol 3. Table 4.2.3.2-1: DocumentEntry Metadata Attribute Definition (previously Table 4.1-5)
        */
        try {
            if (!de.isFhir) {
                ft.setHTML(row, 0, bold("objectType", b));
                ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, de.objectType, de.objectTypeX));
                row++;
            }

            ft.setHTML(row, 0, bold("title", b));
//            ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, de.title, de.titleX));
            titleTxt.setText(de.title);
            ft.setWidget(row, 1, titleTxt);
            row++;

            ft.setHTML(row, 0, bold("comments", b));
//            ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, de.comments, de.commentsX));
            commentsTxt.setText(de.comments);
            ft.setWidget(row, 1, commentsTxt);
            row++;

            ft.setHTML(row, 0, bold("id", b));
            ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, de.id, de.idX));
            row++;

            if (!de.isFhir) {
                ft.setHTML(row, 0, bold("lid", b));
                ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, de.lid, de.lidX));
                row++;
            }

            if (!de.isFhir) {
                ft.setHTML(row, 0, bold("version", b));
                ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, de.version, de.versionX));
                row++;
            }

            ft.setHTML(row, 0, bold("uniqueId", b));
            ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, de.uniqueId, de.uniqueIdX));
            row++;

            ft.setHTML(row, 0, bold("patientId", b));
            ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, de.patientId, de.patientIdX));
            row++;

            ft.setHTML(row, 0, bold("availabilityStatus", b));
            ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, de.status, de.statusX));
            row++;

            if (!de.isFhir) {
                ft.setHTML(row, 0, bold("homeCommunityId", b));
                ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, de.home, de.homeX));
                row++;
            }

            ft.setHTML(row, 0, bold("mimeType", b));
            ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, de.mimeType, de.mimeTypeX));
            row++;

            ft.setHTML(row, 0, bold("hash", b));
            ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, de.hash, de.hashX));
            row++;

            ft.setHTML(row, 0, bold("size", b));
            ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, de.size, de.sizeX));
            row++;

            title = (de.isFhir) ? "content.url" : "repositoryUniqueId";
            ft.setHTML(row, 0, bold(title, b));
            ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, de.repositoryUniqueId, de.repositoryUniqueIdX));
            row++;

            ft.setHTML(row, 0, bold("lang", b));
            ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, de.lang, de.langX));
            row++;

            ft.setHTML(row, 0, bold("legalAuthenticator", b));
            ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, de.legalAuth, de.legalAuthX));
            row++;

            ft.setHTML(row, 0, bold("serviceStartTime", b));
            ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, de.serviceStartTime, de.serviceStartTimeX));
            row++;

            ft.setHTML(row, 0, bold("serviceStopTime", b));
            ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, de.serviceStopTime, de.serviceStopTimeX));
            row++;

            ft.setHTML(row, 0, bold("creationTime", b));
            ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, de.creationTime, de.creationTimeX));
            row++;

            ft.setHTML(row, 0, bold("sourcePatientId", b));
            ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, de.sourcePatientId, de.sourcePatientIdX));
            row++;

            row = displayDetail(ft, row, b, "sourcePatientInfo", de.sourcePatientInfo, de.sourcePatientInfoX);

            // don't know how to handle diffs yet on extra metadata
            row = displayDetail(ft, row, b, de.extra, de.extraX);

            // TODO: pre-select codes in CodeFilter upon initial load
            // TODO: add a new code that is not in the configuration
//            See classificationDescription.requiredSchemes

//            row = displayDetail(ft, row, b, "classCode", de.classCode, de.classCodeX);
            // XDS Codes
            CodeFilter classCodeSelector = codeFilterBank.addFilter(ft, row, 0, CodesConfiguration.ClassCode);
            row++;
            // pre-load classCode
            if (de.classCode!=null) {
                for (String classCodeStr : de.classCode) {
                    classCodeSelector.selectedCodes.addItem(classCodeStr);
                }
            }

//            row = displayDetail(ft, row, b, "confCodes", de.confCodes, de.confCodesX);
            CodeFilter confCodeSelector = codeFilterBank.addFilter(ft, row, 0, CodesConfiguration.ConfidentialityCode);
            row++;
            // pre-load confCode
            if (de.confCodes!=null) {
               for (String confCodeStr : de.confCodes) {
                   confCodeSelector.selectedCodes.addItem(confCodeStr);
               }
            }

//          row = displayDetail(ft, row, b, "eventCodeList", de.eventCodeList, de.eventCodeListX);
            CodeFilter eventCodeListSel = codeFilterBank.addFilter(ft, row, 0, CodesConfiguration.EventCodeList);
            row++;
            // pre-load eventCodeList
            if (de.eventCodeList!=null) {
                for (String eventCodeListStr : de.eventCodeList) {
                    eventCodeListSel.selectedCodes.addItem(eventCodeListStr);
                }
            }

//            row = displayDetail(ft, row, b, "formatCode", de.formatCode, de.formatCodeX);
            CodeFilter formatCodeSel = codeFilterBank.addFilter(ft, row, 0, CodesConfiguration.FormatCode);
            row++;
            // pre-load formatCode
            if (de.formatCode!=null) {
               for (String formatCodeStr : de.formatCode) {
                   formatCodeSel.selectedCodes.addItem(formatCodeStr);
               }
            }

//            row = displayDetail(ft, row, b, "healthcareFacilityType", de.hcftc, de.hcftcX);
            CodeFilter hcftcSel = codeFilterBank.addFilter(ft, row, 0, CodesConfiguration.HealthcareFacilityTypeCode);
            row++;
            // pre-load formatCode
            if (de.hcftc!=null) {
                for (String hcftcCodeStr : de.hcftc) {
                    hcftcSel.selectedCodes.addItem(hcftcCodeStr);
                }
            }

//            row = displayDetail(ft, row, b, "practiceSetting", de.pracSetCode, de.pracSetCodeX);
            CodeFilter pracSel = codeFilterBank.addFilter(ft, row, 0, CodesConfiguration.PracticeSettingCode);
            row++;
            // pre-load
            if (de.pracSetCode!=null) {
                for (String pracCodeStr : de.pracSetCode) {
                    pracSel.selectedCodes.addItem(pracCodeStr);
                }
            }
//
//            row = displayDetail(ft, row, b, "typeCode", de.typeCode, de.typeCodeX);
            CodeFilter typeCodeSel = codeFilterBank.addFilter(ft, row, 0, CodesConfiguration.TypeCode);
            row++;
            if (de.typeCode!=null) {
                for (String typeCodeStr : de.typeCode) {
                    typeCodeSel.selectedCodes.addItem(typeCodeStr);
                }
            }
//
//            row = displayDetail(ft, row, b, de.authors, de.authorsX);


            // TODO: configure addToCodeSpec

//            codeFilterBank.addFilter(paramGrid, prow, 1, CodesConfiguration.TypeCode);
//            prow++;
//            codeFilterBank.addFilter(paramGrid, prow, 1, CodesConfiguration.FormatCode);
//            prow++;
//            codeFilterBank.addFilter(paramGrid, prow, 1, CodesConfiguration.HealthcareFacilityTypeCode);
//            prow++;
//            codeFilterBank.addFilter(paramGrid, prow, 1, CodesConfiguration.PracticeSettingCode);
//            prow++;
//            codeFilterBank.addFilter(paramGrid, prow, 1, CodesConfiguration.ConfidentialityCode);
//            prow++;
//            codeFilterBank.addFilter(paramGrid, prow, 1, CodesConfiguration.EventCodeList);
//            prow++;

        } catch (Exception ex) {
            new PopupMessage(ex.toString());
        } finally {
            ft.setWidget(row, 0, validateMuBtn);
            ft.setWidget(row, 1, updateBtn);
            row++;
            ft.setWidget(row, 0, statusBox);
            row++;
            ft.setWidget(row, 0, resultPanel);
            row++;
            detailPanel.add(ft);
        }

    }
}
