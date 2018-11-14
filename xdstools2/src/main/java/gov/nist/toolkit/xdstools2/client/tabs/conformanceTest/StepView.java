package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.session.client.logtypes.SectionOverviewDTO;
import gov.nist.toolkit.session.client.logtypes.TestPartFileDTO;
import gov.nist.toolkit.testenginelogging.client.ReportDTO;
import gov.nist.toolkit.testenginelogging.client.TestStepLogContentDTO;
import gov.nist.toolkit.testenginelogging.client.UseReportDTO;
import gov.nist.toolkit.xdstools2.client.tabs.simMsgViewerTab.SimMsgViewer;
import gov.nist.toolkit.xdstools2.client.toolLauncher.NewToolLauncher;
import gov.nist.toolkit.xdstools2.client.widgets.HorizontalFlowPanel;

import java.util.*;

/**
 *
 */
public class StepView implements IsWidget {
    // Disclosure panel header
    private HorizontalFlowPanel stepHeader = new HorizontalFlowPanel();
    // Top level widget
    private DisclosurePanel stepPanel = new DisclosurePanel(stepHeader);
    // Disclosure header body
    private FlowPanel stepBody = new FlowPanel();

    private TestPartFileDTO sectionTp;
    private SectionOverviewDTO sectionOverview;
    private TestStepLogContentDTO step;
    private String testSession;
    private TestInstance testInstance;
    private String section;
    private String stepName;

    StepView(TestPartFileDTO sectionTp, SectionOverviewDTO sectionOverview, TestStepLogContentDTO step, boolean open, String testSession, TestInstance testInstance, String section) {
        this.sectionTp = sectionTp;
        this.sectionOverview = sectionOverview;
        this.step = step;
        this.testSession = testSession;
        this.testInstance = testInstance;
        this.section = section;
        this.stepName = step.getId();
        HTML stepHeaderTitle = new HTML("Step: " + step.getId());
        if (step.isSuccess()) stepHeaderTitle.addStyleName("testOverviewHeaderSuccess");
        else stepHeaderTitle.addStyleName("testOverviewHeaderFail");
        stepHeader.add(stepHeaderTitle);
        stepPanel.add(stepBody);
        stepPanel.setOpen(open);
        build();
    }

    private int displayErrorMsg(FlexTable errTbl, int column, int startingRow, String msg) {
        String[] lines = msg.split("\n");
        for (String line : lines) {
            errTbl.setWidget(startingRow++, column, new HTML(line));
        }
        return startingRow;
    }

    private void formatErrorMessages(FlowPanel stepBody, List<String> errors ) {
        if (errors != null && !errors.isEmpty()) {
            FlexTable errTbl = new FlexTable();
            errTbl.setStyleName("with-border");
            errTbl.setCellPadding(3);
            int row = 0;
//            HTML header0 = new HTML("Step");
//            header0.addStyleName("detail-table-header");
            HTML header1 = new HTML("Message");
            header1.addStyleName("detail-table-header");
//            errTbl.setWidget(row, 0, header0);
            errTbl.setWidget(row, 0, header1);
            row++;
            String last = "";
            for (String error : errors) {
                if (error.contains("(stepId=")) {
                    String msg = substringBeforeLast(error, "(stepId=");
                    String stp = substringAfterLast(error, "(stepId=");
                    stp = substringBeforeLast(stp, ")");
                    if (stp.equals(last)) stp = "";
                    else last = stp;
//                    errTbl.setWidget(row, 0, new HTML(stp));
                    row = displayErrorMsg(errTbl, 0, row, msg);
                    row++;
                } else {
                    row = displayErrorMsg(errTbl, 0, row, error);
                }
            }
            HTML title = new HTML("Errors:");
            title.addStyleName("detail-section-header");
            stepBody.add(title);
            stepBody.add(errTbl);
        }
    }

    class Link {
        String text;
        String link;
        Anchor anchor;
        Anchor image;
    }

    class SimLogClickHandler implements ClickHandler {
        String token;

        SimLogClickHandler(String token) {
            this.token = token;
        }

        @Override
        public void onClick(ClickEvent clickEvent) {
            new NewToolLauncher().launch(new SimMsgViewer(token));
        }
    }

    // Link format is
    // #http://xxxxxxx [internal place token] (link text)
    // link (with link text) will generate new tab if internal place token is supplied
    //   otherwise it will generate new browser tab
    // Image will always link to new browser tab
    private Link parseLinkInternals(String content) {
        Link link = new Link();
        int start;
        int end;

        // parse internal place token
        String token = null;
        start = content.indexOf('[');
        if (start != -1) {
            end = content.indexOf(']', start);
            start++;
            token = content.substring(start, end);
            if (token.length() == 0)
                token = null;
        }

        String linkText = null;
        start = content.indexOf('(');
        if (start != -1) {
            start++;
            end = content.indexOf(')', start);
            linkText = content.substring(start, end);
        }
        if (linkText == null)
            return null;  // no link information found
        link.text = linkText;

        int httpStart = content.indexOf("http");
        int httpEnd = (content.contains("[")) ?
                content.indexOf("[")
                :
                content.indexOf("(");
        String hyperlink = content.substring(httpStart, httpEnd).trim();
        link.link = hyperlink;

        ClickHandler internal = null;
        if (token != null)
            internal = new SimLogClickHandler(token);

        Image openExternal = new Image("icons2/open_external-16.png");
        Anchor hiddenAnchor = new Anchor("", hyperlink, "_blank");
        hiddenAnchor.getElement().getStyle().setCursor(Style.Cursor.POINTER);
        hiddenAnchor.getElement().appendChild(openExternal.getElement());

        Anchor anchor = new Anchor();
        anchor.setText(linkText);
        if (internal != null)
            anchor.addClickHandler(internal);
        else {
            anchor.setHref(hyperlink);
            anchor.setTarget("_blank");
        }
//        link.hiddenAnchor = hiddenAnchor;
        link.anchor = anchor;
        link.image = hiddenAnchor;
        return link;
    }

    /**
     * format a single detail row
     *  example is for simlog viewer but this depends on the link values coded on the server
     * @param content #link [internalLink] (linktext) = validation message where
     *                # indicates a header (Markdown syntax)
     *                link is for opening simlogviewer in a new browser tab (throw places)
     *                internalLink is for opening simlogviewer in new toolkit tab (through places)
     *                linktext is the text to display in the link
     * @param lastLink linktext displayed last - so that repetitions can leave field blank
     * @param errTbl  table being built
     * @param row   row to update
     * @return  lastLink for next time called
     */
    private String formatDetailRow(String content, String lastLink, FlexTable errTbl, int row) {
        List<Widget> widgets = new ArrayList<>();

        // label = details
        String[] parts = content.split("=");
        String label = parts[0].trim();
        String details = parts[1].trim();

        boolean isHeader = label.startsWith("#");
        if (isHeader)
            label = label.substring(1);

        Link theLink = parseLinkInternals(label);

        if (theLink == null) {
            HTML it = new HTML(label);
            errTbl.setWidget(row, 0, it);
        } else if (!theLink.link.equals(lastLink)){
            HorizontalPanel panel = new HorizontalPanel();
            panel.add(theLink.anchor);
            panel.add(theLink.image);
            errTbl.setWidget(row, 0, panel);
        }
        HTML it = new HTML(details);
        if (isHeader)
            it.addStyleName("detail-table-header");
        errTbl.setWidget(row, 1, it);

        if (isHeader) {
            errTbl.getWidget(row, 0).addStyleName("detail-table-header");
            if (details.equals(""))
            errTbl.getFlexCellFormatter().setColSpan(row, 0, 2);
        }

        if (theLink != null)
            return theLink.text;
        return "";
    }

    private void build() {
        if (sectionTp.getStepTpfMap()!=null && sectionTp.getStepTpfMap().get(stepName)!=null) {
            MetadataDisplay metadataViewerPanel = new MetadataDisplay(sectionTp.getStepTpfMap().get(stepName), testSession, testInstance, section);
            stepBody.add(metadataViewerPanel.getLabel());
            stepBody.add(metadataViewerPanel);
        } else {
            final HTML noMetadataLabel = new HTML("No Metadata.");
            stepBody.add(noMetadataLabel);
        }

        StringBuilder buf = new StringBuilder();
        buf.append("Goals:<br />");
        if (sectionOverview != null && sectionOverview.getStep(stepName) != null) {
            buf.append(sectionOverview.getStep(stepName).getGoals());
        }
        buf.append("<br /><br />");
//        List<String> goals = sectionOverview.getStep(stepName).getGoals();
//        for (String goal : goals)  buf.append("&nbsp;&nbsp;&nbsp;&nbsp;").append(goal).append("<br />");


        if (step != null) {
            buf.append("Endpoint: " + step.getEndpoint()).append("<br />");
            if (step.isExpectedSuccess())
                buf.append("Expected Status: Success").append("<br />");
            else
                buf.append("Expected Status: Failure").append("<br />");
        }

        for (String fault : step.getSoapFaults()) {
            buf.append("Fault: " + fault).append("<br />");
        }
        stepBody.add(new HTML(buf.toString()));

        // ******************************************************
        // Errors
        // ******************************************************
        formatErrorMessages(stepBody, step.getErrors());

        // ******************************************************
        // Detail
        // ******************************************************
        List<String> dtls = step.getDetails();
        if (dtls != null && !dtls.isEmpty()) {
            FlexTable errTbl = new FlexTable();
            errTbl.setStyleName("with-border");
            errTbl.setCellPadding(3);
            String lastLink = null;
            for (int row = 0; row < dtls.size(); row++) {
                String content = dtls.get(row);
                lastLink = formatDetailRow(content, lastLink, errTbl, row);
            }

            HTML title = new HTML("Detail:");
//            title.addStyleName("detail-section-header");
            stepBody.add(title);
//            stepBody.add(errTbl);

            stepBody.add(new DetailDisplay(errTbl));
        }

        // ******************************************************
        // IDs
        // ******************************************************
        boolean hasContent = false;

        Map<String, String> assignedIds = step.getAssignedIds();
        Map<String, String> assignedUids = step.getAssignedUids();
        Set<String> idNames = new HashSet<String>();
        idNames.addAll(assignedIds.keySet());
        idNames.addAll(assignedUids.keySet());
        int row = 0;
        FlexTable idTable = new FlexTable();
        idTable.setCellPadding(3);
        idTable.setStyleName("with-border");
        idTable.setTitle("Assigned IDs");
        HTML objectHeader = new HTML("Object");
        objectHeader.addStyleName("detail-table-header");
        HTML idHeader = new HTML("ID");
        idHeader.addStyleName("detail-table-header");
        HTML uidHeader = new HTML("UID");
        uidHeader.addStyleName("detail-table-header");
        idTable.setWidget(row, 0, objectHeader);
        idTable.setWidget(row, 1, idHeader);
        idTable.setWidget(row, 2, uidHeader);
        row++;

        for (String idName : idNames) {
            idTable.setWidget(row, 0, new HTML(idName));
            if (assignedIds.containsKey(idName)) {
                idTable.setWidget(row, 1, new HTML(assignedIds.get(idName)));
                hasContent = true;
            }
            if (assignedUids.containsKey(idName)) {
                idTable.setWidget(row, 2, new HTML(assignedUids.get(idName)));
                hasContent = true;
            }
            row++;
        }
        HTML header;
        if (hasContent) {
            header = new HTML("IDs");
            header.addStyleName("detail-section-header");
            stepBody.add(header);
            stepBody.add(idTable);
        }

        // ******************************************************
        // UseReports
        // ******************************************************
        hasContent = false;
        FlexTable useTable = new FlexTable();
        useTable.setStyleName("with-border");
        useTable.setCellPadding(3);
        row = 0;
        useTable.setWidget(row, 0, new HTML("Name"));
        useTable.setWidget(row, 1, new HTML("UseAs"));
        useTable.setWidget(row, 2, new HTML("Value"));
        useTable.setWidget(row, 3, new HTML("Test"));
        useTable.setWidget(row, 4, new HTML("Section"));
        useTable.setWidget(row, 5, new HTML("Step"));
        for (int col=0; col<useTable.getCellCount(row); col++) {
            useTable.getWidget(row, col).addStyleName("detail-table-header");
        }
        row++;
        List<UseReportDTO> useReports = step.getUseReports();
        for (UseReportDTO useReport : useReports) {
            useTable.setWidget(row, 0, new HTML(useReport.getName()));
            useTable.setWidget(row, 1, new HTML(useReport.getUseAs()));
            useTable.setWidget(row, 2, new HTML(useReport.getValue()));
            useTable.setWidget(row, 3, new HTML(useReport.getTest()));
            useTable.setWidget(row, 4, new HTML(useReport.getSection()));
            useTable.setWidget(row, 5, new HTML(useReport.getStep()));
            row++;
            hasContent = true;
        }
        if (hasContent) {
            header = new HTML("Use Reports");
            header.addStyleName("detail-section-header");
            stepBody.add(header);
            stepBody.add(useTable);
        }

        // ******************************************************
        // Reports
        // ******************************************************
        hasContent = false;
        FlexTable reportsTable = new FlexTable();
        reportsTable.setStyleName("with-border");
        reportsTable.setCellPadding(3);
        List<ReportDTO> reports = step.getReportDTOs();
        row = 0;
        reportsTable.setWidget(row, 0, new HTML("Name"));
        reportsTable.setWidget(row, 1, new HTML("Value"));
        for (int col=0; col<reportsTable.getCellCount(row); col++) {
            reportsTable.getWidget(row, col).addStyleName("detail-table-header");
        }
        row++;
        for (ReportDTO report : reports) {
            reportsTable.setWidget(row, 0, new HTML(report.getName()));
            reportsTable.setWidget(row, 1, new HTML(report.getValue()));
            row++;
            String theValue  = report.getValue();
            if (theValue != null && !theValue.equals(""))
                hasContent = true;
        }
        if (hasContent) {
            header = new HTML("Reports");
            header.addStyleName("detail-section-header");
            stepBody.add(header);
            stepBody.add(reportsTable);
        }

    }

    @Override
    public Widget asWidget() {
        return stepPanel;
    }

    private String substringBeforeLast(String str, String sep) {
        int i = str.lastIndexOf(sep);
        if (i < 0) return str;
        return str.substring(0, i);
    }
    private String substringAfterLast(String str, String sep) {
        int i = str.lastIndexOf(sep);
        if (i < 0) return str;
        i += sep.length();
        return str.substring(i);
    }
}
