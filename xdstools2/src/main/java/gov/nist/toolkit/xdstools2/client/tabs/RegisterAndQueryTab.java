package gov.nist.toolkit.xdstools2.client.tabs;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.CoupledTransactions;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.GetDocumentsSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;

import java.util.ArrayList;
import java.util.List;

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

	@Override
	public void onTabLoad(boolean select, String eventName) {
		registerTab(select, "RegisterAndQuery");

		HTML title = new HTML();
		title.setHTML("<h2>Register And Query</h2>");
		tabTopPanel.add(title);

		mainGrid = new FlexTable();
		int row = 0;
		
		tabTopPanel.add(mainGrid);

		queryBoilerplate = addQueryBoilerplate(new Runner(), transactionTypes, couplings, true);

	}

	class Runner implements ClickHandler {

		public void onClick(ClickEvent event) {
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
			addStatusBox();
			getGoButton().setEnabled(false);
			getInspectButton().setEnabled(false);

			toolkitService.registerAndQuery(siteSpec, pidTextBox.getValue().trim(), queryCallback);
		}
		
	}

	public String getWindowShortName() {
		return "regandquery";
	}



}
