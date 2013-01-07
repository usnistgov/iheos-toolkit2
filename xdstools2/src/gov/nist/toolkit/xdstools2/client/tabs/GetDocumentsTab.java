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
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

public class GetDocumentsTab  extends GenericQueryTab {

	// The TransactionTypes to list as actor categories
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

	TextArea textArea;
	GetDocumentsTab tab;
	String help ="Retrieve full metadata for list of DocumentEntry UUIDs. " +
	"UUIDs can be separated by any of [,;() \\t\\n\\r']";
	
	public GetDocumentsTab() {
		super(new GetDocumentsSiteActorManager());
	}
	

	public void onTabLoad(TabContainer container, boolean select, String eventName) {
		tab = this;
		myContainer = container;
		topPanel = new VerticalPanel();
		container.addTab(topPanel, "GetDocuments", select);
		addCloseButton(container,topPanel, help);

		HTML title = new HTML();
		title.setHTML("<h2>Get Documents</h2>");
		topPanel.add(title);

		mainGrid = new FlexTable();
		int row = 0;
		
		topPanel.add(mainGrid);
		

		HTML pidLabel = new HTML();
		pidLabel.setText("Document Entry UUIDs or UIDs");
		mainGrid.setWidget(row,0, pidLabel);

		textArea = new TextArea();
	    textArea.setCharacterWidth(40);
	    textArea.setVisibleLines(10);
		mainGrid.setWidget(row, 1, textArea);
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
			
			List<String> values = formatIds(textArea.getValue());
			
			if (!verifyUuids(values)) {
				new PopupMessage("All values must be a UUID (have urn:uuid: prefix) or be UIDs (not have urn:uuid: prefix)");
				return;
			}
			
			String errMsg = transactionSelectionManager.verifySelection();
			if (errMsg != null) {
				new PopupMessage(errMsg);
				return;
			}
			
			addStatusBox();
			getGoButton().setEnabled(false);
			getInspectButton().setEnabled(false);

			toolkitService.getDocuments(siteSpec, getAnyIds(values), queryCallback);
		}
		
	}

	public String getWindowShortName() {
		return "getdocuments";
	}



}
