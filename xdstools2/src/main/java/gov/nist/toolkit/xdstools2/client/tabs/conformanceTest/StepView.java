package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.user.client.ui.*;

import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.session.client.logtypes.SectionOverviewDTO;
import gov.nist.toolkit.session.client.logtypes.TestPartFileDTO;
import gov.nist.toolkit.testenginelogging.client.ReportDTO;
import gov.nist.toolkit.testenginelogging.client.TestStepLogContentDTO;
import gov.nist.toolkit.testenginelogging.client.UseReportDTO;
import gov.nist.toolkit.xdstools2.client.widgets.HorizontalFlowPanel;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        List<String> goals = sectionOverview.getStep(stepName).getGoals();
        for (String goal : goals)  buf.append("&nbsp;&nbsp;&nbsp;&nbsp;").append(goal).append("<br />");


        buf.append("Endpoint: " + step.getEndpoint()).append("<br />");
        if (step.isExpectedSuccess())
            buf.append("Expected Status: Success").append("<br />");
        else
            buf.append("Expected Status: Failure").append("<br />");

        for (String fault : step.getSoapFaults()) {
            buf.append("Fault: " + fault).append("<br />");
        }
        stepBody.add(new HTML(buf.toString()));
        
        List<String> dtls = step.getDetails();
        if (dtls != null && dtls.isEmpty() == false) {
           FlexTable errTbl = new FlexTable();
           errTbl.setStyleName("with-border");
           for (int row = 0; row < dtls.size(); row++) {
              errTbl.setWidget(row, 0, new HTML(dtls.get(row)));
           }
           stepBody.add(new HTML("Detail:"));
           stepBody.add(errTbl);
        }
        
        List<String> errors = step.getErrors();
        if (errors != null && errors.isEmpty() == false) {
           FlexTable errTbl = new FlexTable();
           errTbl.setStyleName("with-border");
           int row = 0;
           errTbl.setWidget(row, 0, new HTML("Message"));
           errTbl.setWidget(row, 1, new HTML("Step"));
           row++;
           String last = "";
           for (String error : errors) {
              String msg = substringBeforeLast(error, "(stepId=");
              String stp = substringAfterLast(error, "(stepId=");
              stp = substringBeforeLast(stp, ")");
              if (stp.equals(last)) stp = "";
              else last = stp;
              errTbl.setWidget(row, 0, new HTML(msg));
              errTbl.setWidget(row, 1, new HTML(stp));
              row++;
           }
           stepBody.add(new HTML("Error:"));
           stepBody.add(errTbl);
        }
//        for (String error : step.getErrors()) {
//            buf.append("Error: " + error).append("<br />");
//        }

        // ******************************************************
        // IDs
        // ******************************************************
        Map<String, String> assignedIds = step.getAssignedIds();
        Map<String, String> assignedUids = step.getAssignedUids();
        Set<String> idNames = new HashSet<String>();
        idNames.addAll(assignedIds.keySet());
        idNames.addAll(assignedUids.keySet());
        FlexTable idTable = new FlexTable();
        idTable.setCellPadding(3);
        idTable.setStyleName("with-border");
        int row = 0;
        idTable.setTitle("Assigned IDs");
        idTable.setWidget(row, 0, new HTML("Object"));
        idTable.setWidget(row, 1, new HTML("ID"));
        idTable.setWidget(row, 2, new HTML("UID"));
        row++;

        for (String idName : idNames) {
            idTable.setWidget(row, 0, new HTML(idName));
            if (assignedIds.containsKey(idName))
                idTable.setWidget(row, 1, new HTML(assignedIds.get(idName)));
            if (assignedUids.containsKey(idName))
                idTable.setWidget(row, 2, new HTML(assignedUids.get(idName)));
            row++;
        }
        stepBody.add(new HTML("IDs"));
        stepBody.add(idTable);

        // ******************************************************
        // UseReports
        // ******************************************************
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
        }
        stepBody.add(new HTML("Use Reports"));
        stepBody.add(useTable);

        // ******************************************************
        // Reports
        // ******************************************************
        FlexTable reportsTable = new FlexTable();
        reportsTable.setStyleName("with-border");
        reportsTable.setCellPadding(3);
        List<ReportDTO> reports = step.getReportDTOs();
        row = 0;
        reportsTable.setWidget(row, 0, new HTML("Name"));
        reportsTable.setWidget(row, 1, new HTML("Value"));
        row++;
        for (ReportDTO report : reports) {
            reportsTable.setWidget(row, 0, new HTML(report.getName()));
            reportsTable.setWidget(row, 1, new HTML(report.getValue()));
            row++;
        }
        stepBody.add(new HTML("Reports"));
        stepBody.add(reportsTable);

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
