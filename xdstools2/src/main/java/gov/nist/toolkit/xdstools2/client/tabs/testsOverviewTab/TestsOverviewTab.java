package gov.nist.toolkit.xdstools2.client.tabs.testsOverviewTab;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.actortransaction.client.TransactionType;
import gov.nist.toolkit.xdstools2.client.*;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.FindDocumentsSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.QueryBoilerplate;
import gov.nist.toolkit.xdstools2.client.tabs.testsOverviewTab.commandsWidget.CommandsWidget;
import gov.nist.toolkit.xdstools2.client.widgets.siteSelectionWidget.SiteSelectionWidget;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Diane Azais local on 9/23/2015.
 */
public class TestsOverviewTab extends GenericQueryTab {

    GenericQueryTab genericQueryTab;
    TestsWidgetDataModel dataModel;

    // ------ SiteSelectionWidget parameters -------
    final String allSelection = "-- All --";
    final String chooseSelection = "-- Choose --";
    ListBox selectActorList = new ListBox();
    final ToolkitServiceAsync toolkitService = GWT.create(ToolkitService.class);
    Map<String, String> actorCollectionMap;  // name => description
    String selectedActor;
    VerticalPanel resultPanel = new VerticalPanel();


    public static List<TransactionType> transactionTypes = new ArrayList<TransactionType>();
    static {
        transactionTypes.add(TransactionType.STORED_QUERY);
        transactionTypes.add(TransactionType.IG_QUERY);
        transactionTypes.add(TransactionType.XC_QUERY);
    }

    // TODO - add proper transaction couplings
    //public static CoupledTransactions couplings = new CoupledTransactions();


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
        //SiteSelectionWidget siteWidget = new SiteSelectionWidget(this);

        HorizontalPanel siteWidget = new HorizontalPanel();
        siteWidget.add(selectActorList);
        loadActorNames();
        selectActorList.addChangeHandler(new ActorSelectionChangeHandler());
        mainGrid = new FlexTable();
        topPanel.add(mainGrid);


        // -------------------------------------------
        // ------------Tests Overview Widget----------
        // -------------------------------------------

        // ----- Create the data model -----
        dataModel = new TestsWidgetDataModel();

        // ----- View Updater ----
        Updater updater = new Updater(this);

        // ----- Tests View -----
        TestsOverviewWidget testWidget = new TestsOverviewWidget(dataModel, updater);
        updater.setTestsOverviewWidget(testWidget);

        // ----- Upper row of widgets -----
        CommandsWidget commands = new CommandsWidget(updater);

        topPanel.add(commands.asWidget());
        topPanel.add(testWidget.asWidget());

        topPanel.add(siteWidget);

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



    // ------------------------------------------------------
    // ------- Functions for the Site Selection Widget-------
    // ------------------------------------------------------

    /**
     * Loads the list of actor types from the back-end and populates the display on the UI
     */
    private void loadActorNames() {
        toolkitService.getCollectionNames("actorcollections", new AsyncCallback<Map<String, String>>() {

            public void onFailure(Throwable caught) {
                new PopupMessage("getCollectionNames: " + caught.getMessage());
            }

            public void onSuccess(Map<String, String> result) {
                actorCollectionMap = result;
                selectActorList.clear();
                selectActorList.addItem(chooseSelection, "");

                for (String name : actorCollectionMap.keySet()) {
                    String description = actorCollectionMap.get(name);
                    selectActorList.addItem(description, name);
                }
            }
        });
    }

    /**
     * Loads the list of actors for the type of actor selected
     */
    class ActorSelectionChangeHandler implements ChangeHandler {

        public void onChange(ChangeEvent event) {
            // Retrieve the type of actor chosen by the user
            int selectedI = selectActorList.getSelectedIndex();
            selectedActor = selectActorList.getValue(selectedI);

            if (selectedActor == null)
                return;
            if ("".equals(selectedActor))
                return;
            //loadTestsForActor();

            // Find a match in the system for that category of actor
            ActorType act = ActorType.findActor(selectedActor); // should also work with selectedActor
            if (act == null)
                return;

            // Populate the list of transaction types
            List<TransactionType> transactionTypes = act.getTransactions();
            new PopupMessage(transactionTypes.toString());

            queryBoilerplate = addQueryBoilerplate(
                    new Runner(),
                    transactionTypes,
                    new CoupledTransactions(), //TestsOverviewTab.couplings,
                    false); // not using a PID in this tab, should be false
        }
    }

}
