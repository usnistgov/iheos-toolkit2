package gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.VerticalPanel;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.actortransaction.client.TransactionType;
import gov.nist.toolkit.results.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.CoupledTransactions;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.TabContainer;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.BaseSiteActorManager;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.FindDocumentsSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used for editing an On-Demand Document Source simulator configuration.
 */
public class OddsEditTab extends GenericQueryTab {
    SimulatorConfig config = null;
    SimulatorControlTab simulatorControlTab = null;
    GenericQueryTab genericQueryTab = null;

    static List<TransactionType> transactionTypes = new ArrayList<TransactionType>();
    static {
        transactionTypes.add(TransactionType.PROVIDE_AND_REGISTER);
    }
    static CoupledTransactions couplings = new CoupledTransactions();

    public OddsEditTab(BaseSiteActorManager siteActorManager) {
        super(siteActorManager);
    }

    public OddsEditTab(SimulatorControlTab simulatorControlTab, SimulatorConfig config) {
        super(new FindDocumentsSiteActorManager());
        this.config = config;
        this.simulatorControlTab = simulatorControlTab;
    }

    public void onTabLoad(TabContainer container, boolean select, String eventName) {
        genericQueryTab = this;
        myContainer = container;
        topPanel = new VerticalPanel();

        container.addTab(topPanel, "ODDS Sim Edit", select);
        addCloseButton(container, topPanel, null);


//        tlsOptionEnabled = false; // Does this apply here?

        // begin test context
        genericQueryTab.reloadTransactionOfferings();

        // customization of GenericQueryTab
        autoAddRunnerButtons = false;  // want them in a different place
        /*
        genericQueryTitle = "Select System Under Test";
        genericQueryInstructions = new HTML(
                "<p>When the test is run a Stored Query or Retrieve transaction will be sent to the " +
                        "Initiating Gateway " +
                        "selected below. This will start the test. Before running a test, make sure your " +
                        "Initiating Gateway is configured to send to the Responding Gateways above.  This " +
                        "test only uses non-TLS endpoints (for now). TLS selection is disabled.</p>"
        );
        */
//        addResultsPanel = false;  // manually done below




        addRunnerButtons(topPanel);

        topPanel.add(resultPanel);


        // end

        mainGrid = new FlexTable();
        int row = 0;


        topPanel.add(mainGrid);


        SimConfigMgr simConfigMgr = new SimConfigMgr(simulatorControlTab, topPanel, config, getCurrentTestSession());
        simConfigMgr.displayInPanel();

        addQueryBoilerplate(new Runner(), transactionTypes, couplings, true);



    }

    class Runner implements ClickHandler {

        public void onClick(ClickEvent event) {
            resultPanel.clear();

            SiteSpec siteSpec = queryBoilerplate.getSiteSelection();
            if (siteSpec == null) {
                new PopupMessage("You must select a site first");
                return;
            }

            if (pidTextBox.getValue() == null || pidTextBox.getValue().equals("")) {
                new PopupMessage("You must enter a Patient ID first");
                return;
            }
            addStatusBox();
            getGoButton().setEnabled(false);
            getInspectButton().setEnabled(false);

            // Some of this needs to go to Save
//            toolkitService.findDocuments(siteSpec, pidTextBox.getValue().trim(), selectOnDemand.getValue(), queryCallback);
        }

    }



    public String getWindowShortName() {
        return "oddssimedit";
    }

}
