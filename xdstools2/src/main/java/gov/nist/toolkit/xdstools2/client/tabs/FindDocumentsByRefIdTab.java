package gov.nist.toolkit.xdstools2.client.tabs;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.CoupledTransactions;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.FindDocumentsSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;

import java.util.ArrayList;
import java.util.List;

public class FindDocumentsByRefIdTab extends GenericQueryTab {

	static List<TransactionType> transactionTypes = new ArrayList<TransactionType>();
	static {
		transactionTypes.add(TransactionType.STORED_QUERY);
		transactionTypes.add(TransactionType.IG_QUERY);
		transactionTypes.add(TransactionType.XC_QUERY);
	}

	static CoupledTransactions couplings = new CoupledTransactions();

//	CheckBox selectOnDemand;
	TextBox refIdTextBox1 = new TextBox();
	TextBox refIdTextBox2 = new TextBox();
	TextBox refIdTextBox3 = new TextBox();
	

	public FindDocumentsByRefIdTab() {
		super(new FindDocumentsSiteActorManager(),"");
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
		registerTab(select, "FindDocumentsByRefId");

		HTML title = new HTML();
		title.setHTML("<h2>Find Documents by Reference ID Stored Query</h2>");
		tabTopPanel.add(title);

		mainGrid = new FlexTable();
		int row = 0;

//		selectOnDemand = new CheckBox();
//		selectOnDemand.setText("Include On-Demand DocumentEntries");
//		mainGrid.setWidget(row, 0, selectOnDemand);
//		row++;
		
		mainGrid.setWidget(row, 0, new HTML("Reference IDs"));
		
		HorizontalPanel horizPanel = new HorizontalPanel();
		refIdTextBox1.setWidth("25em");
		refIdTextBox2.setWidth("25em");
		refIdTextBox3.setWidth("25em");
		horizPanel.add(refIdTextBox1);
		horizPanel.add(refIdTextBox2);
		horizPanel.add(refIdTextBox3);
		mainGrid.setWidget(row, 1, horizPanel);
		row++;

		tabTopPanel.add(mainGrid);

		addQueryBoilerplate(new Runner(), transactionTypes, couplings, true);
	}

	class Runner implements ClickHandler {

		public void onClick(ClickEvent event) {
			resultPanel.clear();

			SiteSpec siteSpec = queryBoilerplate.getSiteSelection();
			if (siteSpec == null) {
				new PopupMessage("You must select a site first");
				return;
			}
			
			String refId1 = refIdTextBox1.getText().trim();
			String refId2 = refIdTextBox2.getText().trim();
			String refId3 = refIdTextBox3.getText().trim();
			
			List<String> refIds = new ArrayList<String>();
			if (!refId1.equals("")) refIds.add(refId1);
			if (!refId2.equals("")) refIds.add(refId2);
			if (!refId3.equals("")) refIds.add(refId3);
			
			if (refIds.size() == 0) {
				new PopupMessage("You must enter at least one Reference ID");
				return;
			}
			
			if (pidTextBox.getValue() == null || pidTextBox.getValue().equals("")) {
				new PopupMessage("You must enter a Patient ID first");
				return;
			}
			addStatusBox();
			getGoButton().setEnabled(false);
			getInspectButton().setEnabled(false);

			toolkitService.findDocumentsByRefId(siteSpec, pidTextBox.getValue().trim(), refIds, queryCallback);
		}

	}

	public String getWindowShortName() {
		return "finddocuments";
	}


}
