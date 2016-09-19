package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static gov.nist.toolkit.xdstools2.client.ToolWindow.toolkitService;

/**
 *
 */
public class TestSectionComponent implements IsWidget {
    private final HorizontalFlowPanel header = new HorizontalFlowPanel();
    private final DisclosurePanel panel = new DisclosurePanel(header);
    private final String sessionName;
    private final TestInstance testInstance;
    private final FlowPanel body = new FlowPanel();
    private final FlowPanel sectionDescription = new FlowPanel();
    private final FlowPanel sectionResults = new FlowPanel();
    private TestInstance fullTestInstance;
    private SectionOverviewDTO sectionOverview;
    TestRunner testRunner;
    TestSectionComponent me;


    public TestSectionComponent(String sessionName, TestInstance testInstance, SectionOverviewDTO sectionOverview, TestRunner testRunner) {
        me = this;
//        this.toolkitService = toolkitService;
        this.sessionName = sessionName;
        this.testInstance = testInstance;
        this.testRunner = testRunner;
        this.sectionOverview = sectionOverview;
        fullTestInstance = new TestInstance(testInstance.getId(), sectionOverview.getName());

        HTML sectionLabel = new HTML("Section: " + sectionOverview.getName());
        sectionLabel.addStyleName("section-title");
        sectionLabel.setTitle("A section can be run independently although frequently a test section depends on the output of a previous section in the test.");
        if (sectionOverview.isRun()) {
            if (sectionOverview.isPass())
                header.addStyleName("testOverviewHeaderSuccess");
            else
                header.addStyleName("testOverviewHeaderFail");
        } else
            header.addStyleName("testOverviewHeaderNotRun");
        header.add(sectionLabel);
        header.add(new HTML(sectionOverview.getDisplayableTime()));
        if (sectionOverview.isRun()) {
            Image status = (sectionOverview.isPass()) ?
                    new Image("icons2/correct-16.png")
                    :
                    new Image("icons/ic_warning_black_24dp_1x.png");
            status.addStyleName("right");
            header.add(status);

            panel.addOpenHandler(new SectionOpenHandler(new TestInstance(testInstance.getId(), sectionOverview.getName())));
        }
        panel.add(body);
        Image play = new Image("icons2/play-16.png");
        play.addClickHandler(new RunSection(fullTestInstance));
        play.setTitle("Run");
        header.add(play);
        body.add(sectionDescription);
        sectionDescription.add(new HTML(sectionOverview.getDescription()));
        body.add(sectionResults);
    }

    private class RunSection implements ClickHandler {
        TestInstance testInstance;

        RunSection(TestInstance testInstance) {
            this.testInstance = testInstance;
        }

        @Override
        public void onClick(ClickEvent clickEvent) {
            clickEvent.preventDefault();
            clickEvent.stopPropagation();

            me.testRunner.runTest(testInstance, null);
        }
    }

    private class SectionOpenHandler implements OpenHandler<DisclosurePanel> {
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
                    sectionResults.clear();
                    int row;
                    if (log.hasFatalError()) body.add(new HTML("Fatal Error: " + log.getFatalError() + "<br />"));
                    boolean singleStep = log.getSteps().size() == 1;
                    for (TestStepLogContentDTO step : log.getSteps()) {
                        String stepName = step.getId();
                        HorizontalFlowPanel stepHeader = new HorizontalFlowPanel();
                        FlowPanel stepResults = new FlowPanel();

                        HTML stepHeaderTitle = new HTML("Step: " + step.getId());
                        if (step.isSuccess()) stepHeaderTitle.addStyleName("testOverviewHeaderSuccess");
                        else stepHeaderTitle.addStyleName("testOverviewHeaderFail");
                        stepHeader.add(stepHeaderTitle);

                        DisclosurePanel stepPanel = new DisclosurePanel(stepHeader);
                        stepPanel.setOpen(singleStep);

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
                        for (String error : step.getErrors()) {
                            buf.append("Error: " + error).append("<br />");
                        }
                        for (String assertion : step.getAssertionErrors()) {
                            buf.append("Error: " + assertion).append("<br />");
                        }
                        stepResults.add(new HTML(buf.toString()));

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
                        stepResults.add(new HTML("IDs"));
                        stepResults.add(idTable);

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
                        stepResults.add(new HTML("Use Reports"));
                        stepResults.add(useTable);

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
                        stepResults.add(new HTML("Reports"));
                        stepResults.add(reportsTable);
                        stepPanel.add(stepResults);
                        sectionResults.add(stepPanel);
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
