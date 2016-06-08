package gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import gov.nist.toolkit.xdstools2.client.TabContainer;

/**
 *
 */
public abstract class AbstractTool extends GenericQueryTab {

    /**
     * @return Short name to be displayed in tool tab
     */
    abstract public String getTabTitle();

    /**
     * @return Full title to be displayed at top of tool.
     */
    abstract public String getToolTitle();

    public AbstractTool() {
        super(null);
    }

    abstract public void initTool();

    @Override
    public void onTabLoad(TabContainer container, boolean select, String eventName) {
        myContainer = container;
        topPanel = new VerticalPanel();

        container.addTab(topPanel, getTabTitle(), select);
        addToolHeader(container,topPanel, null);

        HTML title = new HTML();
        title.setHTML("<h2>" + getToolTitle() + "</h2>");
        topPanel.add(title);

        mainGrid = new FlexTable();
        topPanel.add(mainGrid);

        initTool();
    }
}
