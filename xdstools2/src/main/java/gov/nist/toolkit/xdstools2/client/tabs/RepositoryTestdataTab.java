package gov.nist.toolkit.xdstools2.client.tabs;

import gov.nist.toolkit.actortransaction.client.TransactionType;
import gov.nist.toolkit.results.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.CoupledTransactions;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.TabContainer;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.GetDocumentsSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class RepositoryTestdataTab  extends GenericQueryTab {
	
	static List<TransactionType> transactionTypes = new ArrayList<TransactionType>();
	static {
		transactionTypes.add(TransactionType.PROVIDE_AND_REGISTER);
	}
	
	// Coupled transaction semantics not relevant to this tool. To see how it is used
	// look in FindDocuments tab.
	static CoupledTransactions couplings = new CoupledTransactions();


	TextBox pid;
	ListBox testlistBox;
	
	String help = "Submit selected test data set to the selected Repository " +
	"in a Provide and Register transaction"; 
	
	public RepositoryTestdataTab() {
		super(new GetDocumentsSiteActorManager());
	}
	
	public void onTabLoad(TabContainer container, boolean select, String eventName) {
		
		// Create top level VerticalPanel that defines this tab and link it into the
		// tab system.  Also add Close button.
		myContainer = container;
		topPanel = new VerticalPanel();
		
		
		container.addTab(topPanel, "RepositoryTestdata", select);
		addCloseButton(container,topPanel, help);

		
		// Build UI content of tab
		HTML title = new HTML();
		title.setHTML("<h2>Submit Repository Testdata</h2>");
		topPanel.add(title);

		mainGrid = new FlexTable();
		int row = 0;
		
		topPanel.add(mainGrid);

		HTML pidLabel = new HTML();
		pidLabel.setText("Patient ID");
		mainGrid.setWidget(row,0, pidLabel);

		pid = new TextBox();
		pid.setWidth("400px");
		mainGrid.setWidget(row, 1, pid);
		row++;

		HTML dataLabel = new HTML();
		dataLabel.setText("Select Test Data Set");
		mainGrid.setWidget(row,0, dataLabel);

		testlistBox = new ListBox();
		mainGrid.setWidget(row, 1, testlistBox);
		row++;

		// build drop down box for selecting data set to send. Initiate call to 
		// back end to load this list.
		testlistBox.setVisibleItemCount(1); 
		toolkitService.getTestdataSetListing("testdata-repository", loadRepositoryTestListCallback);

		queryBoilerplate = addQueryBoilerplate(new Runner(), transactionTypes, couplings);
	}
	
	// Callback for data set listing.  Add it to the screen.
	protected AsyncCallback<List<String>> loadRepositoryTestListCallback = new AsyncCallback<List<String>>() {

		public void onFailure(Throwable caught) {
			showMessage(caught);
		}

		public void onSuccess(List<String> result) {
			testlistBox.addItem("");
			for (String testName : result) {
				testlistBox.addItem(testName);
			}
		}

	};


	// Run button triggers the onClick method of this class
	class Runner implements ClickHandler {

		public void onClick(ClickEvent event) {
			resultPanel.clear();

			// get selected site
			SiteSpec siteSpec = queryBoilerplate.getSiteSelection();
			if (siteSpec == null)
				return;

			// get entered Patient ID
			if (pid.getValue() == null || pid.getValue().equals("")) {
				new PopupMessage("You must enter a Patient ID first");
				return;
			}
			
			
			// get selected data set
			int selected = testlistBox.getSelectedIndex();
			if (selected < 1 || selected >= testlistBox.getItemCount()) {
				new PopupMessage("You must select Test Data Set first");
				return;
				
			}
			
			String testdataSetName = testlistBox.getItemText(selected);	
			
			// disable the Run and Inspect buttons until the transaction completes
			addStatusBox();
			getGoButton().setEnabled(false);
			getInspectButton().setEnabled(false);

//			siteSpec.isTls = doTLS;
//			siteSpec.isSaml = doSAML;
//			siteSpec.isAsync = doASYNC;
			
			// Initiate the transaction
			// queryCallback comes out of GenericQueryTab, the super class of the main class of this tab.
			toolkitService.submitRepositoryTestdata(siteSpec, testdataSetName, pid.getValue().trim(), queryCallback);
		}
		
	}



	public String getWindowShortName() {
		return "reptestdata";
	}

}
