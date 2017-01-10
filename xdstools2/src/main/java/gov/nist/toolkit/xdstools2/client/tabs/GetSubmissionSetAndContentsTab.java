package gov.nist.toolkit.xdstools2.client.tabs;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.results.client.CodesConfiguration;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.CoupledTransactions;
import gov.nist.toolkit.xdstools2.client.command.command.GetSubmissionSetAndContentsCommand;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.GetDocumentsSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;
import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;
import gov.nist.toolkit.xdstools2.client.widgets.queryFilter.OnDemandFilter;
import gov.nist.toolkit.xdstools2.shared.command.request.GetSubmissionSetAndContentsRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetSubmissionSetAndContentsTab extends GenericQueryTab {

	OnDemandFilter onDemandFilter;
	final int idHashCode = System.identityHashCode(this);

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

	TextBox ssid;

	public GetSubmissionSetAndContentsTab() {
		super(new GetDocumentsSiteActorManager());
	}

	@Override
	protected Widget buildUI() {
		FlowPanel flowPanel=new FlowPanel();
		HTML title = new HTML();
		title.setHTML("<h2>Get Submission Set and Contents</h2>");
		flowPanel.add(title);

		mainGrid = new FlexTable();
		int row = 0;

		flowPanel.add(mainGrid);

		// On Demand
		mainGrid.setText(row, 0, "DocumentEntry Type");
		onDemandFilter = new OnDemandFilter("GetSubmissionSetAndContentsTab_"+idHashCode,"Either");
		mainGrid.setWidget(row, 1, onDemandFilter.asWidget());
		row++;

		HTML ssidLabel = new HTML();
		ssidLabel.setText("Submission Set Unique ID or UUID");
		mainGrid.setWidget(row,0, ssidLabel);

		ssid = new TextBox();
		ssid.setWidth("500px");
		mainGrid.setWidget(row, 1, ssid);
		row++;

		return flowPanel;
	}

	@Override
	protected void bindUI() {
	}

	@Override
	protected void configureTabView() {
		queryBoilerplate = addQueryBoilerplate(new GetSSandContentsRunner(), transactionTypes, couplings, false);

	}

	class GetSSandContentsRunner implements ClickHandler {

		public void onClick(ClickEvent event) {
			resultPanel.clear();

			SiteSpec siteSpec = queryBoilerplate.getSiteSelection();
			if (siteSpec == null) {
				new PopupMessage("You must select a site first");
				return;
			}

			if (ssid.getValue() == null || ssid.getValue().equals("")) {
				new PopupMessage("You must enter a Submission Set id first");
				return;
			}

			Map<String, List<String>> codeSpec = new HashMap<String, List<String>>();
			onDemandFilter.addToCodeSpec(codeSpec, CodesConfiguration.DocumentEntryType);


			addStatusBox();
			getGoButton().setEnabled(false);
			getInspectButton().setEnabled(false);

			new GetSubmissionSetAndContentsCommand(){
				@Override
				public void onComplete(List<Result> result) {
					displayResults(result);
				}
			}.run(new GetSubmissionSetAndContentsRequest(getCommandContext(),siteSpec,ssid.getValue().trim(),codeSpec));
		}

	}

	public String getWindowShortName() {
		return "getssandcontents";
	}


}