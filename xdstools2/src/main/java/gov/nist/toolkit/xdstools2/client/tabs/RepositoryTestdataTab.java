package gov.nist.toolkit.xdstools2.client.tabs;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.xdstools2.client.CoupledTransactions;
import gov.nist.toolkit.xdstools2.client.command.command.GetTestdataSetListingCommand;
import gov.nist.toolkit.xdstools2.client.command.command.SubmitRepositoryTestdataCommand;
import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.GetDocumentsSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;
import gov.nist.toolkit.xdstools2.shared.command.request.GetTestdataSetListingRequest;
import gov.nist.toolkit.xdstools2.shared.command.request.SubmitTestdataRequest;

import java.util.ArrayList;
import java.util.List;

public class RepositoryTestdataTab  extends GenericQueryTab {
	
	static List<TransactionType> transactionTypes = new ArrayList<TransactionType>();
	static {
		transactionTypes.add(TransactionType.PROVIDE_AND_REGISTER);
	}
	
	// Coupled transaction semantics not relevant to this tool. To see how it is used
	// look in FindDocuments tab.
	static CoupledTransactions couplings = new CoupledTransactions();


	//TextBox pid;
	ListBox testlistBox;
	
	String help = "Submit selected test data set to the selected Repository " +
	"in a Provide and Register transaction"; 
	
	public RepositoryTestdataTab() {
		super(new GetDocumentsSiteActorManager());
	}

	@Override
	protected Widget buildUI() {
		FlowPanel container=new FlowPanel();
		// Build UI content of tab
		container.add(new HTML("<h2>Send XDS Provide & Register transaction</h2>"));

		mainGrid = new FlexTable();
		int row = 0;

		container.add(mainGrid);

		mainGrid.setWidget(row,0, new HTML("Select Test Data Set"));

		testlistBox = new ListBox();
		mainGrid.setWidget(row, 1, testlistBox);
		row++;

		// build drop down box for selecting data set to send.
		testlistBox.setVisibleItemCount(1);
		return container;
	}

	@Override
	protected void bindUI() {
		new GetTestdataSetListingCommand(){
			@Override
			public void onComplete(List<String> result) {
				testlistBox.addItem("");
				for (String testName : result) {
					testlistBox.addItem(testName);
				}
			}
		}.run(new GetTestdataSetListingRequest(getCommandContext(),"testdata-repository"));
	}

    @Override
    protected void configureTabView() {
		queryBoilerplate = addQueryBoilerplate(new Runner(), transactionTypes, couplings, true);
    }

	// Run button triggers the onClick method of this class
	class Runner implements ClickHandler {

		public void onClick(ClickEvent event) {
			resultPanel.clear();

			if (!verifySiteProvided()) return;
			if (!verifyPidProvided()) return;

			// getRetrievedDocumentsModel selected data set
			int selected = testlistBox.getSelectedIndex();
			if (selected < 1 || selected >= testlistBox.getItemCount()) {
				new PopupMessage("You must select Test Data Set first");
				return;
			}
			
			String testdataSetName = testlistBox.getItemText(selected);	
			

			// Initiate the transaction
			// queryCallback comes out of GenericQueryTab, the super class of the main class of this tab.
			rigForRunning();
			new SubmitRepositoryTestdataCommand(){
				@Override
				public void onComplete(List<Result> result) {
					queryCallback.onSuccess(result);
				}
			}.run(new SubmitTestdataRequest(getCommandContext(),getSiteSelection(),testdataSetName,pidTextBox.getValue().trim()));
		}
		
	}



	public String getWindowShortName() {
		return "reptestdata";
	}

}
