package gov.nist.toolkit.xdstools2.client.tabs.findDocumentsAllTab;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import gov.nist.toolkit.actortransaction.client.TransactionType;
import gov.nist.toolkit.results.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.CoupledTransactions;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.TabContainer;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.FindDocumentsSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Diane Azais local on 9/23/2015.
 */
public class FindDocumentsAll extends GenericQueryTab {


    static List<TransactionType> transactionTypes = new ArrayList<TransactionType>();
    static {
        transactionTypes.add(TransactionType.STORED_QUERY);
        transactionTypes.add(TransactionType.IG_QUERY);
        transactionTypes.add(TransactionType.XC_QUERY);
    }
    static CoupledTransactions couplings = new CoupledTransactions();

    GenericQueryTab genericQueryTab;
    FindDocumentsAllParams sqParams;

    public FindDocumentsAll() {
        super(new FindDocumentsSiteActorManager());
    }

    // Tab initialization
    @Override
    public void onTabLoad(TabContainer container, boolean select, String eventName) {
        myContainer = container;
        // Panel to build inside of
        topPanel = new VerticalPanel();

        genericQueryTab = this;   // share with other methods


        container.addTab(topPanel, "Find Documents (All Parameters)", select);  // link into container/tab management
        addCloseButton(container, topPanel, null);   // add the close button

        HTML title = new HTML();
        title.setHTML("<h2>Find Documents (All Parameters)</h2>");
        topPanel.add(title);

        // Generate the composite widget that allows selection of all the GetAll query parameters. Below is the call
        // sqParams.asWidget() which gets the actual Widget.
        sqParams = new FindDocumentsAllParams(toolkitService, genericQueryTab);

        mainGrid = new FlexTable();  // this is important in some tabs, not this one.  This init should be moved to definition
        topPanel.add(sqParams.asWidget());
        topPanel.add(new HTML("<hr/>"));

        topPanel.add(mainGrid);

        // add below-the-line-stuff (PatientId, site selection etc.)
        // Also link in the Runner class (shown below) which is called when the user clicks on the Run button.
        // Since this call organizes the site selection grid, it needs the transactionTypes and couplings config
        addQueryBoilerplate(new Runner(), transactionTypes, couplings, true);
    }


class Runner implements ClickHandler {

    // Process the run button click
    public void onClick(ClickEvent clickEvent) {
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

        // Where the bottom-of-screen listing from server goes
        addStatusBox();

        getGoButton().setEnabled(false);
        getInspectButton().setEnabled(false);

        // Capture the query-specific parameter details.  They have been generated in
        // sqParams and here they are formatted in the codeSpec layout which the server requires
        Map<String, List<String>> codeSpec = new HashMap<>();
        sqParams.addToCodeSpec(codeSpec);

        // tell the server to run the query. The display is handled by GenericQueryTab which
        // is linked in via the queryCallback parameter
        //TODO replace with new service function for Find Documents
        toolkitService.getAll(siteSpec, pidTextBox.getValue().trim(), codeSpec, queryCallback);
    }
    }

    @Override
    public String getWindowShortName() {
        return "findalldocuments";
    }
}
