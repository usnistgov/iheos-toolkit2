package gov.nist.toolkit.xdstools2.client.tabs;

import gov.nist.toolkit.actortransaction.client.ATFactory.TransactionType;
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
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SourceStoredDocValTab extends GenericQueryTab {
	
	static List<TransactionType> transactionTypes = new ArrayList<TransactionType>();
	static {
		transactionTypes.add(TransactionType.STORED_QUERY);
	}
	
	static CoupledTransactions couplings = new CoupledTransactions();


	TextBox ssid;
	String help = "For this test a Document Source submits a Provide and " +
	"Register transaction to a Document Repository leading to a Register " +
	"transaction being sent to a Document Registry. The Document Source " +
	"logs in Kudu the unique ID of the Submission Set of the submission. \n\n" +
	"To validate this test, find the Submission Set unique ID in the " +
	"chat window of the test and copy/paste it into the Submission Set " +
	"id box, select the correct Document Registry (as per Kudu), make " +
	"sure TLS? is checked (TLS is required) and press the Run button. " +
	"A GetSubmissionSetAndContents Stored Query will be sent to the " +
	"Registry followed by a Retrieve to the Repository. \n\n" +
	"Several lines of text will display showing completing of the test. " +
	"If some of the text is red then the test failed.  Copy/Paste all the " +
	"text into the chat window in Kudu and mark the test Partially Verified. " +
	"If all the text is black then the monitor must " +
	"check to see that the Repository listed in the test results matches " +
	"the Repository claimed in Kudu so the correct vendors get credit. " +
	"If Repository is wrong then the test should be labeled Failed in Kudu. " +
	"If none of these problems are evident then mark the test Passed in Kudu.\n\n" +
	"The most common error is to have the query complete with complaints of not " +
	"finding SubmissionSet, DocumentEntry, or Association. This probably meant " +
	"that the metadata was forwarded to the wrong Registry (by the Repository). " +
	"This usually requries the Document Source to resubmit so it's best to mark " +
	"the test as Failed and include the message: Metadata not found in Registry " + 
	"required by test.";

	public SourceStoredDocValTab() {
		super(new GetDocumentsSiteActorManager());
	}
	
	public void onTabLoad(TabContainer container, boolean select, String eventName) {
		myContainer = container;
		topPanel = new VerticalPanel();
		container.addTab(topPanel, "SourceStoresDocVal", select);
		addCloseButton(container,topPanel, help);

		HTML title = new HTML();
		title.setHTML("<h2>Source Stores Document Validation</h2>");
		topPanel.add(title);

		mainGrid = new FlexTable();
		int row = 0;
		
		topPanel.add(mainGrid);

		HTML ssidLabel = new HTML();
		ssidLabel.setText("Submission Set Unique ID or UUID");
		mainGrid.setWidget(row,0, ssidLabel);

		ssid = new TextBox();
		ssid.setWidth("500px");
		mainGrid.setWidget(row, 1, ssid);
		row++;


		queryBoilerplate = addQueryBoilerplate(new GetSSandContentsRunner(), transactionTypes, couplings);

	}

	class GetSSandContentsRunner implements ClickHandler {

		public void onClick(ClickEvent event) {
			resultPanel.clear();

			SiteSpec siteSpec = queryBoilerplate.getSiteSelection();
			if (siteSpec == null)
				return;

			if (ssid.getValue() == null || ssid.getValue().equals("")) {
				new PopupMessage("You must enter a Submission Set id first");
				return;
			}
			addStatusBox();
			getGoButton().setEnabled(false);
			getInspectButton().setEnabled(false);

//			siteSpec.isTls = doTLS;
//			siteSpec.isSaml = doSAML;
//			siteSpec.isAsync = doASYNC;
			toolkitService.srcStoresDocVal(siteSpec, ssid.getValue().trim(), queryCallback);
		}
		
	}

	public String getWindowShortName() {
		return "srcstordoc";
	}


}
