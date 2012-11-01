package gov.nist.toolkit.xdstools2.client.tabs;

import gov.nist.toolkit.actortransaction.client.ATFactory.TransactionType;
import gov.nist.toolkit.results.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.CoupledTransactions;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.TabContainer;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.FindDocumentsSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

public class FindFoldersTab extends GenericQueryTab {

	static List<TransactionType> transactionTypes = new ArrayList<TransactionType>();
	static {
		transactionTypes.add(TransactionType.STORED_QUERY);
		transactionTypes.add(TransactionType.IG_QUERY);
		transactionTypes.add(TransactionType.XC_QUERY);
	}
	
	static CoupledTransactions couplings = new CoupledTransactions();

	public FindFoldersTab() {
		super(new FindDocumentsSiteActorManager());
	}
	

	public void onTabLoad(TabContainer container, boolean select, String eventName) {
		myContainer = container;
		topPanel = new VerticalPanel();
		
		
		container.addTab(topPanel, "FindFolders", select);
		addCloseButton(container,topPanel, null);

		HTML title = new HTML();
		title.setHTML("<h2>Find Folders</h2>");
		topPanel.add(title);

		mainGrid = new FlexTable();
		
		topPanel.add(mainGrid);

		queryBoilerplate = addQueryBoilerplate(new Runner(), transactionTypes, couplings, true);
	}
	
	class Runner implements ClickHandler {

		public void onClick(ClickEvent event) {
			String pid = queryBoilerplate.getPatientId();
			resultPanel.clear();

			SiteSpec siteSpec = queryBoilerplate.getSiteSelection();
			if (siteSpec == null) {
				new PopupMessage("You must select a site first");
				return;
			}

			if (pid == null || pid.equals("")) {
				new PopupMessage("You must enter a Patient ID first");
				return;
			}
			addStatusBox();
			getGoButton().setEnabled(false);
			getInspectButton().setEnabled(false);

			toolkitService.findFolders(siteSpec, pid.trim(), queryCallback);
		}
		
	}

	public String getWindowShortName() {
		return "findfolders";
	}



	
}
