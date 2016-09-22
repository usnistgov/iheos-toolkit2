package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;

/**
 *
 */
class TestsHeaderView {
    interface Controller {
        ClickHandler getRunAllClickHandler();
        ClickHandler getDeleteAllClickHandler();
    }
    private Controller controller;
    private FlowPanel testsHeader = new FlowPanel();
    private HTML testsHeaderTitle = new HTML();
    private TestDisplayHeader bar = new TestDisplayHeader();
    private HTML title = new HTML();
    private HTML testCount = new HTML();
    private HTML successes = new HTML();
    private HTML failures = new HTML();
    private HTML notRun = new HTML();
    private FlexTable table = new FlexTable();

    TestsHeaderView(Controller controller) {
        this.controller = controller;
        testsHeader.add(bar);

        bar.add(testsHeaderTitle);

//        testsHeader.add(testsHeaderTitle);

        testCount.setWidth("12em");

        HTML testsLabel = new HTML("Tests:");
        testsLabel.setWidth("12em");
        table.setWidget(0, 0, testsLabel);
        table.setWidget(0, 1, testCount);

        table.setText(1, 0, "Successes:");
        table.setWidget(1, 1, successes);
        successes.setStyleName("testSuccess");

        table.setText(2, 0, "Failures: ");
        table.setWidget(2, 1, failures);
        failures.setStyleName("testFail");

        table.setText(3, 0, "Not Run:");
        table.setWidget(3, 1, notRun);
        notRun.setStyleName("testNotRun");

        table.setBorderWidth(2);
        testsHeader.add(table);

        testsHeader.add(new HTML("<hr />"));


    }

    Widget asWidget() {
        return testsHeader;
    }

    protected void update(TestStatistics testStatistics, String bodyText) {

        testsHeaderTitle.setHTML(bodyText + " Tests");

        bar.clear();
        bar.add(testsHeaderTitle);

        testCount.setHTML(String.valueOf(testStatistics.getTestCount()));

        successes.setHTML(String.valueOf(testStatistics.getSuccesses()));
        failures.setHTML(String.valueOf(testStatistics.getFailures()));
        notRun.setHTML(String.valueOf(testStatistics.getNotRun()));

        // Add controls
        Image play = new Image("icons2/play-24.png");
        play.setTitle("Run");
        play.addClickHandler(controller.getRunAllClickHandler());
        play.addStyleName("iconStyle");
        bar.add(play);

        Image delete = new Image("icons2/garbage-24.png");
        delete.addStyleName("right");
        delete.addClickHandler(controller.getDeleteAllClickHandler());
        delete.setTitle("Delete Log");
        delete.addStyleName("right");
        delete.addStyleName("iconStyle");
        bar.add(delete);

        bar.addStyleName("test-summary");

        if (testStatistics.isAllRun()) {
            if (testStatistics.hasErrors()) {
                bar.setBackgroundColorFailure();
                bar.add(getStatusIcon(false));
            } else {
                bar.setBackgroundColorSuccess();
                bar.add(getStatusIcon(true));
            }
        }
        else if (testStatistics.hasErrors()) {
            bar.setBackgroundColorFailure();
            bar.add(getStatusIcon(false));
        }
        else {
            bar.setBackgroundColorNotRun();
        }

    }

    private Image getStatusIcon(boolean good) {
        Image status;
        if (good) {
            status = new Image("icons2/correct-24.png");
        } else {
            status = new Image("icons/ic_warning_black_24dp_1x.png");
        }
        status.addStyleName("right");
        status.addStyleName("iconStyle");
        return status;
    }

}
