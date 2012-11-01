package gov.nist.toolkit.xdstools2.client.tabs;


import gov.nist.toolkit.actortransaction.client.ATFactory.TransactionType;
import gov.nist.toolkit.registrymetadata.client.ObjectRef;
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
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class GetRelatedTab  extends GenericQueryTab {

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

	TextBox uuid;
	static String[] assocTypes =  {
			"RPLC", "APND", "XFRM", "XFRM_RPLC", "signs"
	};

	List<CheckBox> assocCheckBoxes;
	
	public GetRelatedTab() {
		super(new GetDocumentsSiteActorManager());
	}
	

	static public List<String> getAllAssocTypes() {
		List<String> as = new ArrayList<String>();
		
		for (int i=0; i<assocTypes.length; i++) {
			as.add(assocTypes[i]);
		}
		
		return as;
	}

	public void onTabLoad(TabContainer container, boolean select, String eventName) {
		myContainer = container;
		topPanel = new VerticalPanel();
		container.addTab(topPanel, "GetRelated", select);
		addCloseButton(container,topPanel, null);

		HTML title = new HTML();
		title.setHTML("<h2>Get Related Documents</h2>");
		topPanel.add(title);

		mainGrid = new FlexTable();
		int row = 0;

		topPanel.add(mainGrid);


		HTML pidLabel = new HTML();
		pidLabel.setText("Document Entry UUID");
		mainGrid.setWidget(row,0, pidLabel);

		uuid = new TextBox();
		uuid.setWidth("400px");
		mainGrid.setWidget(row, 1, uuid);
		row++;

		HTML assocsLabel = new HTML();
		assocsLabel.setText("Association Types");
		mainGrid.setWidget(row, 0, assocsLabel);
		
		assocCheckBoxes = new ArrayList<CheckBox>();
		for (int i=0; i<assocTypes.length; i++) {
			CheckBox cb = new CheckBox(assocTypes[i]);
			assocCheckBoxes.add(cb);
			mainGrid.setWidget(row, 1, cb);
			row++;
		}
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

			if (uuid.getValue() == null || uuid.getValue().equals("")) {
				new PopupMessage("You must enter a UUID first");
				return;
			}
			
			List<String> assocs = new ArrayList<String>();
			for (CheckBox cb : assocCheckBoxes) {
				if (cb.getValue())
					assocs.add(cb.getText());
			}
			
			addStatusBox();
			getGoButton().setEnabled(false);
			getInspectButton().setEnabled(false);

			toolkitService.getRelated(siteSpec, new ObjectRef(uuid.getValue().trim()), assocs, queryCallback);
		}

	}


	public String getWindowShortName() {
		return "getrelated";
	}


}
