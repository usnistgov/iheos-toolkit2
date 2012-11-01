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

public class RegisterAndQueryTab extends GenericQueryTab {
	
	static List<TransactionType> transactionTypes = new ArrayList<TransactionType>();
	static {
		transactionTypes.add(TransactionType.REGISTER);
	}
	
	static CoupledTransactions couplings = new CoupledTransactions();

	TextBox pid;
	String help = "Send a Register Document Set transaction consisting of a single Document Entry " + 
	" (with the necessary Submission Set) to a Document Registry. Then send a GetSubmissionSetAndContents " +
	" Stored Query to verify the submission. The Patient ID used must fed to the Registry prior to " +
	" running this test.";

	public RegisterAndQueryTab() {
		super(new GetDocumentsSiteActorManager());
	}
	
	public void onTabLoad(TabContainer container, boolean select, String eventName) {
		myContainer = container;
		topPanel = new VerticalPanel();
		container.addTab(topPanel, "RegisterAndQuery", select);
		addCloseButton(container,topPanel, help);

		HTML title = new HTML();
		title.setHTML("<h2>Register And Query</h2>");
		topPanel.add(title);

		mainGrid = new FlexTable();
		int row = 0;
		
		topPanel.add(mainGrid);

		HTML pidLabel = new HTML();
		pidLabel.setText("Patient ID");
		mainGrid.setWidget(row,0, pidLabel);

		pid = new TextBox();
		pid.setWidth("500px");
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

			toolkitService.registerAndQuery(siteSpec, pid.getValue().trim(), queryCallback);
		}
		
	}

	public String getWindowShortName() {
		return "regandquery";
	}



}
