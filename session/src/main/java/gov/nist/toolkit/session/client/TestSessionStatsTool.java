package gov.nist.toolkit.session.client;

import com.google.gwt.user.client.ui.*;

import java.util.List;

public class TestSessionStatsTool implements IsWidget {
    FlowPanel panel = new FlowPanel();

    public TestSessionStatsTool(List<TestSessionStats> stats) {
        panel.add(new HTML("<h2>Test Session Statistics</h2>"));
        FlexTable table = new FlexTable();

        table.setHTML(0, 0, "Test Session");
        table.setHTML(0, 1, "Last Updated");
        table.setHTML(0, 2, "Expires");
        table.setHTML(0, 3, "is expired");
        table.setHTML(0, 4, "Expiration Policy");

        int row = 1;
        for (TestSessionStats stat : stats) {
            table.setHTML(row, 0, stat.getTestSession().getValue());
            table.setHTML(row, 1, stat.getLastUpdated());
            table.setHTML(row, 2, (stat.isExpired()) ? "" : stat.getExpires());
            table.setHTML(row, 3, (stat.isExpired()) ? "True" : "False");
            table.setHTML(row, 4, stat.getExpirationPolicy().toString());
            row++;
        }
        panel.add(table);
    }


    @Override
    public Widget asWidget() {
        return panel;
    }
}
