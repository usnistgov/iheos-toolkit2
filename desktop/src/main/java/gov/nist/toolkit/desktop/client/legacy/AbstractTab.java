package gov.nist.toolkit.desktop.client.legacy;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 *
 */
public class AbstractTab {
    public FlexTable mainGrid = new FlexTable();
    public VerticalPanel resultPanel = new VerticalPanel();

    public FlexTable getMainGrid() {
        return mainGrid;
    }

    public VerticalPanel getResultPanel() {
        return resultPanel;
    }
}
