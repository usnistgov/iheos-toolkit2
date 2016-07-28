package gov.nist.toolkit.xdstools2.client.components;

import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.session.client.SectionOverviewDTO;
import gov.nist.toolkit.testenginelogging.client.LogFileContentDTO;
import gov.nist.toolkit.testenginelogging.client.ReportDTO;
import gov.nist.toolkit.testenginelogging.client.TestStepLogContentDTO;
import gov.nist.toolkit.testenginelogging.client.UseReportDTO;
import gov.nist.toolkit.xdstools2.client.HorizontalFlowPanel;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.ToolkitServiceAsync;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public class TestSectionComponent implements IsWidget {
    HorizontalFlowPanel header = new HorizontalFlowPanel();
    DisclosurePanel panel = new DisclosurePanel(header);
    ToolkitServiceAsync toolkitService;
    String sessionName;
    TestInstance testInstance;
    FlowPanel body = new FlowPanel();

    public TestSectionComponent(ToolkitServiceAsync toolkitService, String sessionName, TestInstance testInstance, SectionOverviewDTO sectionOverview) {
        this.toolkitService = toolkitService;
        this.sessionName = sessionName;
        this.testInstance = testInstance;

        HTML sectionLabel = new HTML("Section: " + sectionOverview.getName());
        if (sectionOverview.isRun()) {
            if (sectionOverview.isPass())
                header.addStyleName("testOverviewHeaderSuccess");
            else
                header.addStyleName("testOverviewHeaderFail");
        } else
            header.addStyleName("testOverviewHeaderNotRun");
        header.add(sectionLabel);
        if (sectionOverview.isRun()) {
            header.add((sectionOverview.isPass()) ?
                    new Image("icons2/correct-32.png")
                    :
                    new Image("icons2/cancel-32.png"));

            panel.add(body);

            panel.addOpenHandler(new SectionOpenHandler(new TestInstance(testInstance.getId(), sectionOverview.getName())));
        }
        Image play = new Image("icons2/play-32.png");
        play.setTitle("Run");
        header.add(play);
        Image delete = new Image("icons2/remove-32.png");
        delete.setTitle("Delete Log");
        header.add(delete);
    }

    class SectionOpenHandler implements OpenHandler<DisclosurePanel> {
        TestInstance testInstance; // must include section name

        SectionOpenHandler(TestInstance testInstance) { this.testInstance = testInstance; }

        @Override
        public void onOpen(OpenEvent<DisclosurePanel> openEvent) {
            toolkitService.getTestLogDetails(sessionName, testInstance, new AsyncCallback<LogFileContentDTO>() {
                @Override
                public void onFailure(Throwable throwable) {
                    new PopupMessage("getTestLogDetails failed " + throwable.getMessage());
                }

                @Override
                public void onSuccess(LogFileContentDTO log) {
                    if (log == null) new PopupMessage("section is " + testInstance.getSection());
                    body.clear();
                    int row;
                    if (log.hasFatalError()) body.add(new HTML("Fatal Error: " + log.getFatalError() + "<br />"));
                    int stepI = 0;
                    for (TestStepLogContentDTO step : log.getSteps()) {
                        StringBuilder buf = new StringBuilder();
                        if (stepI > 0) buf.append("<br />");
                        buf.append("Step: " + step.getId()).append("<br />");
                        buf.append("Goal: " + step.getStepGoalsDTO().getGoals()).append("<br />");
                        buf.append("Endpoint: " + step.getEndpoint()).append("<br />");
                        for (String error : step.getErrors()) {
                            buf.append("Error: " + error).append("<br />");
                        }
                        body.add(new HTML(buf.toString()));

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
                        row = 0;
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
                        body.add(new HTML("IDs"));
                        body.add(idTable);

                        // ******************************************************
                        // UseReports
                        // ******************************************************
                        FlexTable useTable = new FlexTable();
                        useTable.setStyleName("with-border");
                        useTable.setCellPadding(3);
                        row = 0;
                        useTable.setWidget(row, 0, new HTML("Name"));
                        useTable.setWidget(row, 1, new HTML("Value"));
                        useTable.setWidget(row, 2, new HTML("Test"));
                        useTable.setWidget(row, 3, new HTML("Section"));
                        useTable.setWidget(row, 4, new HTML("Step"));
                        row++;
                        List<UseReportDTO> useReports = step.getUseReports();
                        for (UseReportDTO useReport : useReports) {
                            useTable.setWidget(row, 0, new HTML(useReport.getName()));
                            useTable.setWidget(row, 1, new HTML(useReport.getValue()));
                            useTable.setWidget(row, 2, new HTML(useReport.getTest()));
                            useTable.setWidget(row, 3, new HTML(useReport.getSection()));
                            useTable.setWidget(row, 4, new HTML(useReport.getStep()));
                            row++;
                        }
                        body.add(new HTML("Use Reports"));
                        body.add(useTable);

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
                        body.add(new HTML("Reports"));
                        body.add(reportsTable);

                        stepI++;
                    }
                }
            });

        }
    }

    @Override
    public Widget asWidget() {
        return panel;
    }
}
