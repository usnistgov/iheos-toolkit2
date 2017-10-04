package gov.nist.toolkit.xdstools2.client.inspector;


import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.xdstools2.client.abstracts.AbstractView;

public class InspectorView extends AbstractView<InspectorPresenter> {
    /*
       * Main panels below tab title display.
       * History/Contents/Difference control panel
       * Detail display relative to control panel
       */
    VerticalPanel historyPanel;
    VerticalPanel detailPanel;
    VerticalPanel structPanel;

    /*
     * Radio Buttons in history panel to select display.
     */
    RadioButton selectHistory = null;
    RadioButton selectContents = null;
    RadioButton selectDiff = null;
    ListBox groupByListBox;
    HorizontalPanel groupByPanel = new HorizontalPanel();

    HorizontalPanel hpanel;

    static HTML historyCaption = HyperlinkFactory.addHTML("<h3>History</h3>");
    static HTML contentsCaption = HyperlinkFactory.addHTML("<h3>Contents</h3>");

    @Override
    protected void bindUI() {

        groupByListBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent changeEvent) {
                getPresenter().doShowHistoryOrContents();
            }
        });


    }
    @Override
    protected Widget buildUI() {

        hpanel = new HorizontalPanel();
        hpanel.setBorderWidth(1);

        historyPanel = new VerticalPanel();
        hpanel.add(historyPanel);
        hpanel.setCellWidth(historyPanel, "30%");

        detailPanel = new VerticalPanel();
        detailPanel.add(HyperlinkFactory.addHTML("<h3>Detail</h3>"));
        hpanel.add(detailPanel);

        structPanel = new VerticalPanel();
        structPanel.add(HyperlinkFactory.addHTML("<h3>Structure</h3>"));
        hpanel.add(structPanel);

        groupByListBox = new ListBox();
        groupByListBox.addItem("(none)"); // No group by
        groupByListBox.addItem("homeCommunityId"); // hcId groups object types
        groupByListBox.addItem("repositoryId");		// Object type groups repositoryId

        addToHistory(results);
    }



}


