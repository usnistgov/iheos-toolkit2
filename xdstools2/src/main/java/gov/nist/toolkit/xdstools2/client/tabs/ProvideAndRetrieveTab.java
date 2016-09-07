package gov.nist.toolkit.xdstools2.client.tabs;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.CoupledTransactions;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.GetDocumentsSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;

import java.util.ArrayList;
import java.util.List;

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
		super(new GetDocumentsSiteActorManager(),"");
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

	@Override
	public void onTabLoad(boolean select, String eventName) {
		registerTab(select, "SubmitAndRetrieve");

		HTML title = new HTML();
		title.setHTML("<h2>Submit / Retrieve</h2>");
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

			toolkitService.provideAndRetrieve(siteSpec, pidTextBox.getValue().trim(), queryCallback);
		}
		
	}

	public String getWindowShortName() {
		return "provandret";
	}


}
