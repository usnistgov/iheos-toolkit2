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

public class ProvideAndRetrieveTab extends GenericQueryTab {
	
	static List<TransactionType> transactionTypes = new ArrayList<TransactionType>();
	static {
		transactionTypes.add(TransactionType.PROVIDE_AND_REGISTER);
	}
	
	static CoupledTransactions couplings = new CoupledTransactions();


	TextBox pid;
	String help = "Submit a Provide and Register transaction to the selected Repository. " +
	"Then knowing the Document Entry uniqueID and the Repository uniqueId, issue a " +
	"Retrieve Document Set transaction to read back the document contents.  Verify the " +
	"size, hash, and mime type of the document. The Repository must be configured to forward " +
	"metadata to a Registry but this test does not query the Registry so you do not need to " +
	"know the Repository configuration (which Registry it is pointing to) to run this test. " +
	"This test is repeated 3 times with text, xml, and pdf documents.";

	public ProvideAndRetrieveTab() {
		super(new GetDocumentsSiteActorManager());
	}
	

	public void onTabLoad(TabContainer container, boolean select, String eventName) {
		myContainer = container;
		topPanel = new VerticalPanel();
		
		
		container.addTab(topPanel, "SubmitAndRetrieve", select);
		addCloseButton(container,topPanel, help);

		HTML title = new HTML();
		title.setHTML("<h2>Submit / Retrieve</h2>");
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


		queryBoilerplate = addQueryBoilerplate(new Runner(), transactionTypes, couplings);
	}
	
	class Runner implements ClickHandler {

		public void onClick(ClickEvent event) {
			resultPanel.clear();

			SiteSpec siteSpec = queryBoilerplate.getSiteSelection();
			if (siteSpec == null)
				return;

			if (pid.getValue() == null || pid.getValue().equals("")) {
				new PopupMessage("You must enter a Patient ID first");
				return;
			}
			addStatusBox();
			getGoButton().setEnabled(false);
			getInspectButton().setEnabled(false);

			toolkitService.provideAndRetrieve(siteSpec, pid.getValue().trim(), queryCallback);
		}
		
	}

	public String getWindowShortName() {
		return "provandret";
	}


}
