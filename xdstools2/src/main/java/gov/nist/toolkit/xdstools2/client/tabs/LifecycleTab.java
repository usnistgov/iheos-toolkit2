package gov.nist.toolkit.xdstools2.client.tabs;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.CoupledTransactions;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.TabContainer;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.GetDocumentsSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;

import java.util.ArrayList;
import java.util.List;

public class LifecycleTab extends GenericQueryTab {
	
	static List<TransactionType> transactionTypes = new ArrayList<TransactionType>();
	static {
		transactionTypes.add(TransactionType.STORED_QUERY);
		transactionTypes.add(TransactionType.REGISTER);
	}
	
	static CoupledTransactions couplings = new CoupledTransactions();

	String help = "The first validation (testkit test 11992) has 3 steps: submit a single DocumentEntry to the Registry; " +
	"submit a replacement DocumentEntry to the Registry; use GetSubmissionSetAndContents " +
	"stored query to verify the Registry contents."; 

	public LifecycleTab() {
		super(new GetDocumentsSiteActorManager());
	}
	

	public void onTabLoad(TabContainer container, boolean select, String eventName) {
		myContainer = container;
		container.addTab(tabTopPanel, "Lifecycle", select);
		addToolHeader(container, tabTopPanel, help);

		HTML title = new HTML();
		title.setHTML("<h2>Lifecycle validation</h2>");
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


            toolkitService.lifecycleValidation(siteSpec, pidTextBox.getValue().trim(), queryCallback);
		}
		
	}

	public String getWindowShortName() {
		return "lifecycle";
	}



}
