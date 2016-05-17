package gov.nist.toolkit.xdstools2.client.tabs;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.xdstools2.client.CoupledTransactions;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.TabContainer;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.GetDocumentsSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;

import java.util.ArrayList;
import java.util.List;

public class RegistryTestdataTab  extends GenericQueryTab {

	static List<TransactionType> transactionTypes = new ArrayList<TransactionType>();
	static {
		transactionTypes.add(TransactionType.REGISTER);
	}
	
	static CoupledTransactions couplings = new CoupledTransactions();

//	TextBox pid;
	ListBox testlistBox;
	
	String help = "Submit selected test data set to the selected Registry " +
	"in a Register transaction"; 
	
	public RegistryTestdataTab() {
		super(new GetDocumentsSiteActorManager());
	}
	
	public void onTabLoad(TabContainer container, boolean select, String eventName) {
		myContainer = container;
		topPanel = new VerticalPanel();
		
		
		container.addTab(topPanel, "XDS Register", select);
		addCloseButton(container, topPanel, help);

		topPanel.add(new HTML("<h2>Send XDS Register transaction</h2>"));

		mainGrid = new FlexTable();
		int row = 0;
		
		topPanel.add(mainGrid);

//		mainGrid.setWidget(row,0, new HTML("Patient ID"));

		HTML dataLabel = new HTML();
		dataLabel.setText("Select Test Data Set");
		mainGrid.setWidget(row,0, dataLabel);

		testlistBox = new ListBox();
		mainGrid.setWidget(row, 1, testlistBox);
		row++;

		testlistBox.setVisibleItemCount(1);
		toolkitService.getTestdataSetListing(getEnvironmentSelection(),getCurrentTestSession(),"testdata-registry", loadRegistryTestListCallback);

		queryBoilerplate = addQueryBoilerplate(new Runner(), transactionTypes, couplings, true);
	}
	
	protected AsyncCallback<List<String>> loadRegistryTestListCallback = new AsyncCallback<List<String>>() {

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


	
	class Runner implements ClickHandler {

		public void onClick(ClickEvent event) {
			resultPanel.clear();

			if (!verifySiteProvided()) return;
			if (!verifyPidProvided()) return;

			int selected = testlistBox.getSelectedIndex();
			if (selected < 1 || selected >= testlistBox.getItemCount()) {
				new PopupMessage("You must select Test Data Set first");
				return;
			}
			
			String testdataSetName = testlistBox.getItemText(selected);	

			rigForRunning();
			toolkitService.submitRegistryTestdata(getSiteSelection(), testdataSetName, pidTextBox.getValue().trim(), queryCallback);
		}
		
	}



	public String getWindowShortName() {
		return "regtestdata";
	}

}
