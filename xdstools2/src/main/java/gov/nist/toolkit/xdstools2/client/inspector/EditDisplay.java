package gov.nist.toolkit.xdstools2.client.inspector;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.TextBox;
import gov.nist.toolkit.registrymetadata.client.DocumentEntry;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.valsupport.client.MessageValidationResults;
import gov.nist.toolkit.xdsexception.client.ToolkitRuntimeException;
import gov.nist.toolkit.xdstools2.client.command.command.UpdateDocumentEntryCommand;
import gov.nist.toolkit.xdstools2.client.command.command.ValidateDocumentEntryCommand;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;
import gov.nist.toolkit.xdstools2.shared.command.request.UpdateDocumentEntryRequest;
import gov.nist.toolkit.xdstools2.shared.command.request.ValidateDocumentEntryRequest;

import java.util.List;

public class EditDisplay extends CommonDisplay {
    private Button validateMuBtn = new Button("Validate");
    private Button updateBtn = new Button("Update");
    private DocumentEntry de;
    private TestInstance logId;

    // Edit controls
    private TextBox titleTxt = new TextBox();

//    HTML errorMsgs = new HTML();

    void applyChanges() {
        if (de==null) throw new ToolkitRuntimeException("Unexpected null documentEntry");

        de.title = titleTxt.getText();
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
                    new PopupMessage(throwable.toString());
                }

                @Override
                public void onComplete(List<Result> result) {
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
            ft.setWidget(row, 1, HyperlinkFactory.linkXMLView(it, de.comments, de.commentsX));
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

            row = displayDetail(ft, row, b, "classCode", de.classCode, de.classCodeX);

            row = displayDetail(ft, row, b, "confCodes", de.confCodes, de.confCodesX);

            row = displayDetail(ft, row, b, "eventCodeList", de.eventCodeList, de.eventCodeListX);

            row = displayDetail(ft, row, b, "formatCode", de.formatCode, de.formatCodeX);

            row = displayDetail(ft, row, b, "healthcareFacilityType", de.hcftc, de.hcftcX);

            row = displayDetail(ft, row, b, "practiceSetting", de.pracSetCode, de.pracSetCodeX);

            row = displayDetail(ft, row, b, "typeCode", de.typeCode, de.typeCodeX);

            row = displayDetail(ft, row, b, de.authors, de.authorsX);
        } catch (Exception ex) {
            new PopupMessage(ex.toString());
        } finally {
            ft.setWidget(row, 0, validateMuBtn);
            ft.setWidget(row, 1, updateBtn);
            row++;
            detailPanel.add(ft);
        }

    }

}
