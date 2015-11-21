package gov.nist.toolkit.xdstools2.client.tabs.testsOverviewTab;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import gov.nist.toolkit.actortransaction.client.TransactionType;
import gov.nist.toolkit.xdstools2.client.CoupledTransactions;
import gov.nist.toolkit.xdstools2.client.TabContainer;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.FindDocumentsSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;
import gov.nist.toolkit.xdstools2.client.tabs.testsOverviewTab.commandsWidget.CommandsWidget;
import gov.nist.toolkit.xdstools2.client.widgets.siteSelectionWidget.SiteSelectionWidget;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Diane Azais local on 9/23/2015.
 */
public class TestsOverviewTab extends GenericQueryTab {

    GenericQueryTab genericQueryTab;
    TestsWidgetDataModel dataModel;



    public static List<TransactionType> transactionTypes = new ArrayList<TransactionType>();
    static {
        transactionTypes.add(TransactionType.STORED_QUERY);
        transactionTypes.add(TransactionType.IG_QUERY);
        transactionTypes.add(TransactionType.XC_QUERY);
    }

    // TODO - add proper transaction couplings
    public static CoupledTransactions couplings = new CoupledTransactions();


    // this super is kinda useless now - was a good idea for documentation at one time
    public TestsOverviewTab(){
        super(new FindDocumentsSiteActorManager());
    }


    @Override
    public void onTabLoad(TabContainer container, boolean select, String eventName) {
        myContainer = container;
        genericQueryTab = this;

        // Panel to build inside of
        topPanel = new VerticalPanel();

        container.addTab(topPanel, "Tests Overview", select);  // link into container/tab management
        addCloseButton(container, topPanel, null);   // add the close button

        HTML title = new HTML();
        title.setHTML("<h2>Tests Overview</h2>");
        topPanel.add(title);


        // -------------------------------------------
        // ---------- Site Selection Widget-----------
        // -------------------------------------------
        SiteSelectionWidget siteWidget = new SiteSelectionWidget(this);
        topPanel.add(siteWidget);


        // -------------------------------------------
        // ------------Tests Overview Widget----------
        // -------------------------------------------

        // ----- Create the data model -----
        dataModel = new TestsWidgetDataModel();

        // ----- View Updater ----
        Updater updater = new Updater();

        // ----- Tests View -----
        TestsOverviewWidget testWidget = new TestsOverviewWidget(dataModel, updater);
        updater.setTestsOverviewWidget(testWidget);

        // ----- Upper row of widgets -----
        CommandsWidget commands = new CommandsWidget(updater);

        topPanel.add(commands.asWidget());
        topPanel.add(testWidget.asWidget());

        setDefaults();
    }


    class Runner implements ClickHandler {

        // Process the run button click
        public void onClick(ClickEvent event) {
            resultPanel.clear();
        }
    }

    /**
     * Default display parameters
     * */
    private void setDefaults() {
        topPanel.setSpacing(10);
        topPanel.setWidth("100%");
    }


    @Override
    public String getWindowShortName() {
        return "testsoverview";
    }

}
