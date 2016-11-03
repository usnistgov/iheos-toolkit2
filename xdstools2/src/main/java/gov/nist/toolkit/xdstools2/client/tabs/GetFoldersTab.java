package gov.nist.toolkit.xdstools2.client.tabs;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.CoupledTransactions;
import gov.nist.toolkit.xdstools2.client.command.command.GetFoldersCommand;
import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.GetDocumentsSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;
import gov.nist.toolkit.xdstools2.shared.command.request.GetFoldersRequest;

import java.util.ArrayList;
import java.util.List;

public class GetFoldersTab extends GenericQueryTab {

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

	TextArea ta;
	GetFoldersTab tab;
	String help ="Retrieve full metadata for list of Folder UUIDs. " +
			"UUIDs can be separated by any of [,;() \\t\\n\\r']";

	public GetFoldersTab() {
		super(new GetDocumentsSiteActorManager());
	}

	@Override
	protected Widget buildUI() {
		tab = this;

		FlowPanel flowPanel=new FlowPanel();
		HTML title = new HTML();
		title.setHTML("<h2>Get Folders</h2>");
		flowPanel.add(title);

		mainGrid = new FlexTable();
		int row = 0;



		HTML pidLabel = new HTML();
		pidLabel.setText("Folder UUIDs or UIDs");
		mainGrid.setWidget(row,0, pidLabel);

		ta = new TextArea();
		ta.setCharacterWidth(40);
		ta.setVisibleLines(10);
		mainGrid.setWidget(row, 1, ta);
		row++;

		flowPanel.add(mainGrid);
		return flowPanel;
	}

	@Override
	protected void bindUI() {
	}

	@Override
	protected void configureTabView() {
		queryBoilerplate = addQueryBoilerplate(new Runner(), transactionTypes, couplings, false);
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

			new GetFoldersCommand(){
				@Override
				public void onComplete(List<Result> result) {
					queryCallback.onSuccess(result);
				}
			}.run(new GetFoldersRequest(getCommandContext(),siteSpec,getAnyIds(values)));
		}

	}

	public String getWindowShortName() {
		return "getfolders";
	}


}
