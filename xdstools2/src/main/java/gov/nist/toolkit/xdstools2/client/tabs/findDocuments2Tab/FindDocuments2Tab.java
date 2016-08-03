package gov.nist.toolkit.xdstools2.client.tabs.findDocuments2Tab;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.xdstools2.client.CoupledTransactions;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.FindDocumentsSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Diane Azais local on 9/23/2015.
 * This is the new Find Documents tab which should ultimately replace the old one. This one is programmed to handle all
 * existing parameters of the Find Documents query.
 */
public class FindDocuments2Tab extends GenericQueryTab {


    static List<TransactionType> transactionTypes = new ArrayList<TransactionType>();
    static {
        transactionTypes.add(TransactionType.STORED_QUERY);
        // TODO complete supported transactions
        // the following two types are not supported at the moment. Their testplan section needs to be added
        // under webapp/toolkitx/testkit/utilities/FindDocuments2
        //transactionTypes.add(TransactionType.IG_QUERY);
        //transactionTypes.add(TransactionType.XC_QUERY);
    }
    static CoupledTransactions couplings = new CoupledTransactions();

    GenericQueryTab genericQueryTab;
    FindDocuments2Params sqParams;


    public FindDocuments2Tab() {
        super(new FindDocumentsSiteActorManager());
    }

    // Tab initialization
    @Override
    public void onTabLoad(boolean select, String eventName) {
        // Panel1 to build inside of

        genericQueryTab = this;   // share with other methods

        registerTab(select, eventName);  // link into container/tab management

        HTML title = new HTML();
        title.setHTML("<h2>Find Documents (All Parameters)</h2>");
        tabTopPanel.add(title);

        // Generate the composite widget that allows selection of all the GetAll query parameters. Below is the call
        // sqParams.asWidget() which gets the actual Widget.
        sqParams = new FindDocuments2Params(toolkitService, genericQueryTab);

        mainGrid = new FlexTable();  // this is important in some tabs, not this one.  This init should be moved to definition
        tabTopPanel.add(sqParams.asWidget());
        tabTopPanel.add(new HTML("<hr/>"));

        tabTopPanel.add(mainGrid);

        // add below-the-line-stuff (PatientId, site selection etc.)
        // Also link in the Runner class (shown below) which is called when the user clicks on the Run button.
        // Since this call organizes the site selection grid, it needs the transactionTypes and couplings config
        addQueryBoilerplate(new Runner(), transactionTypes, couplings, true);
    }


class Runner implements ClickHandler {

    // Process the run button click
    public void onClick(ClickEvent clickEvent) {
        resultPanel.clear();

        // TODO check error message is sent to user
        if (!verifySiteProvided()) return;
        if (!verifyPidProvided()) return;

        // Capture the query-specific parameter details.  They have been generated in
        // sqParams and here they are formatted in the codeSpec layout which the server requires
        Map<String, List<String>> codeSpec = new HashMap<String, List<String>>();
        sqParams.addToCodeSpec(codeSpec);

        // tell the server to run the query. The display is handled by GenericQueryTab which
        // is linked in via the queryCallback parameter
        rigForRunning();
        toolkitService.findDocuments2(getSiteSelection(), pidTextBox.getValue().trim(), codeSpec, queryCallback);
    }
    }

    @Override
    public String getWindowShortName() {
        return "finddocuments2";
    }
}
