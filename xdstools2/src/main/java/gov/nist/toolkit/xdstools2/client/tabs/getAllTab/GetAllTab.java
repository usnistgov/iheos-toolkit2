package gov.nist.toolkit.xdstools2.client.tabs.getAllTab;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.xdstools2.client.CoupledTransactions;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.FindDocumentsSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The inheritance tree is:
 *
 * ToolWindow - operation of the tab and interaction with the tab container
 * GenericQueryTab - organization and display of the sites, patientID, run button, inspect results button
 *      basically everything below the horizontal line in the display of the tab
 * GetAllTab - specifics for this query tab
 *
 * A tab implementation has two main sections:
 *    OnTabLoad method that builds the tab contents
 *    Inner Class called Runner that has a onClick handler that responds to the Run button.
 * All other methods are in a supporting role to these two.
 */


public class GetAllTab extends GenericQueryTab {

	// A query tab represents one of several actors.  It is transaction based and adapts
	// to the actor that generates the transaction.  As seen below, this tool can generate
	// the Stored Query, Initiating Gateway Query and Cross Community Query transactions. In
	// each case it takes on the role of a different actor
	static List<TransactionType> transactionTypes = new ArrayList<TransactionType>();
	static {
		transactionTypes.add(TransactionType.STORED_QUERY);
		transactionTypes.add(TransactionType.IG_QUERY);
		transactionTypes.add(TransactionType.XC_QUERY);
	}

	// How are the site selections coupled. Not implemented well yet in this tool.  It is
	// implemented well in FindDocumentsTab and that configuration needs to be moved here.
	// in general, if you focus on a GET* style transaction (no Patient ID parameter)
	// an example of couplings is that if you choose an Intitiating Gateway then you must choose
	// the Responding Gateway that it routes to.
	//  - add proper transaction couplings
	static CoupledTransactions couplings = new CoupledTransactions();

	GenericQueryTab genericQueryTab;
	GetAllParams sqParams;

	public GetAllTab() {
		// this super is kinda useless now - was a good idea for documentation at one time
		super(new FindDocumentsSiteActorManager());
	}

	@Override
	protected Widget buildUI() {
		return null;
	}

	@Override
	protected void bindUI() {

	}

	@Override
	protected void configureTabView() {

	}

	// Tab initialization
	@Override
	public void onTabLoad(boolean select, String eventName) {
		// Panel1 to build inside of

		genericQueryTab = this;   // share with other methods
		registerTab(select, "GetAll");  // link into container/tab management

		// Tab contents starts here
		HTML title = new HTML();
		title.setHTML("<h2>GetAll Stored Query</h2>");
		tabTopPanel.add(title);

		// Generate the composite widget that allows selection of all the GetAll query parameters. Below is the call
		// sqParams.asWidget() which gets the actual Widget.
		sqParams = new GetAllParams(/*toolkitService, */genericQueryTab);

		mainGrid = new FlexTable();  // this is important in some tabs, not this one.  This init should be moved to definition
		tabTopPanel.add(sqParams.asWidget());

		tabTopPanel.add(mainGrid);

		// add below-the-line-stuff (PatientId, site selection etc.)
		// Also link in the Runner class (shown below) which is called when the user clicks on the Run button.
		// Since this call organizes the site selection grid, it needs the transactionTypes and couplings config
		addQueryBoilerplate(new Runner(), transactionTypes, couplings, true);
	}

	class Runner implements ClickHandler {

		// Process the run button click
		public void onClick(ClickEvent event) {
			resultPanel.clear();

			if (!verifySiteProvided()) return;
			if (!verifyPidProvided()) return;

			// Capture the query-specific parameter details.  They have been generated in
			// sqParams and here they are formatted in the codeSpec layout which the server requires
			Map<String, List<String>> codeSpec = new HashMap<>();
			sqParams.addToCodeSpec(codeSpec);

			// tell the server to run the query. The display is handled by GenericQueryTab which
			// is linked in via the queryCallback parameter
			rigForRunning();
			getToolkitServices().getAll(getSiteSelection(), pidTextBox.getValue().trim(), codeSpec, queryCallback);
		}
	}

	public String getWindowShortName() {
		return "getall";
	}


}
