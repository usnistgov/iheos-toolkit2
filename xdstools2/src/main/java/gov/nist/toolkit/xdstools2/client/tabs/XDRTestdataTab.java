package gov.nist.toolkit.xdstools2.client.tabs;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.xdstools2.client.CoupledTransactions;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.GetDocumentsSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;

import java.util.ArrayList;
import java.util.List;

public class XDRTestdataTab  extends GenericQueryTab {

	static List<TransactionType> transactionTypes = new ArrayList<TransactionType>();
	static {
		transactionTypes.add(TransactionType.XDR_PROVIDE_AND_REGISTER);
	}

	static CoupledTransactions couplings = new CoupledTransactions();


	ListBox testlistBox;

	String help = "Submit selected test data set to the selected Document Recipient " +
			"in a Provide and Register transaction";

	public XDRTestdataTab() {
		super(new GetDocumentsSiteActorManager());
	}

	@Override
	protected Widget buildUI() {
		FlowPanel container = new FlowPanel();
		container.add(new HTML("<h2>Send XDR Provide & Register transaction</h2>"));

		mainGrid = new FlexTable();
		int row = 0;

		container.add(mainGrid);

		HTML dataLabel = new HTML();
		dataLabel.setText("Select Test Data Set");
		mainGrid.setWidget(row,0, dataLabel);

		testlistBox = new ListBox();
		mainGrid.setWidget(row, 1, testlistBox);
		row++;

		testlistBox.setVisibleItemCount(1);
		return container;
	}

	@Override
	protected void bindUI() {
		getToolkitServices().getTestdataSetListing(getEnvironmentSelection(), getCurrentTestSession(), "testdata-xdr", loadRecipientTestListCallback);
		addOnTabSelectionRedisplay();
	}

	@Override
	protected void configureTabView() {
		queryBoilerplate = addQueryBoilerplate(new Runner(), transactionTypes, couplings, true);
	}

	protected AsyncCallback<List<String>> loadRecipientTestListCallback = new AsyncCallback<List<String>>() {

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
			getToolkitServices().submitXDRTestdata(getCurrentTestSession(),getSiteSelection(), testdataSetName, pidTextBox.getValue().trim(), queryCallback);
		}

	}



	public String getWindowShortName() {
		return "rectestdata";
	}

}
