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

public class GetFolderAndContentsTab extends GenericQueryTab {
	
	static List<TransactionType> transactionTypes = new ArrayList<TransactionType>();
	static {
		transactionTypes.add(TransactionType.STORED_QUERY);
		transactionTypes.add(TransactionType.IG_QUERY);
		transactionTypes.add(TransactionType.XC_QUERY);
	}
	
	static CoupledTransactions couplings = new CoupledTransactions();
	static {
		// If an Initiating Gateway is selected (IG_QUERY) then 
		// a Responding Gateway (XC_QUERY) must also be selected
		// to determine the homeCommunityId to put in the 
		// query request to be sent to the Initiating Gateway
		couplings.add(TransactionType.IG_QUERY, TransactionType.XC_QUERY);
	}

	TextBox ta;
	GetFolderAndContentsTab tab;
	String help ="Retrieve full metadata for list of Folder UUIDs. " +
	"UUIDs can be separated by any of [,;() \\t\\n\\r']";
	
	public GetFolderAndContentsTab() {
		super(new GetDocumentsSiteActorManager());
	}
	

	public void onTabLoad(TabContainer container, boolean select, String eventName) {
		tab = this;
		myContainer = container;
		topPanel = new VerticalPanel();
		container.addTab(topPanel, "GetFolderAndContents", select);
		addCloseButton(container,topPanel, help);

		HTML title = new HTML();
		title.setHTML("<h2>Get FolderAndContents</h2>");
		topPanel.add(title);

		mainGrid = new FlexTable();
		int row = 0;
		
		topPanel.add(mainGrid);
		

		HTML pidLabel = new HTML();
		pidLabel.setText("Folder UUID or UID");
		mainGrid.setWidget(row,0, pidLabel);

		ta = new TextBox();
	    ta.setWidth("400px");
		mainGrid.setWidget(row, 1, ta);
		row++;

		queryBoilerplate = addQueryBoilerplate(new Runner(), transactionTypes, couplings);
	}
	
	class Runner implements ClickHandler {

		public void onClick(ClickEvent event) {
			resultPanel.clear();

			SiteSpec siteSpec = queryBoilerplate.getSiteSelection();
			if (siteSpec == null) {
				new PopupMessage("You must select a site first");
				return;
			}
			
			List<String> values = formatIds(ta.getValue());
			
			if (!verifyUuids(values)) {
				new PopupMessage("All values must be a UUID (have urn:uuid: prefix) or be UIDs (not have urn:uuid: prefix)");
				return;
			}
			
			addStatusBox();
			getGoButton().setEnabled(false);
			getInspectButton().setEnabled(false);

			toolkitService.getFolderAndContents(siteSpec, getAnyIds(values), queryCallback);
		}
		
	}

	public String getWindowShortName() {
		return "getfolderandcontents";
	}


}
