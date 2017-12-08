package gov.nist.toolkit.xdstools2.client.tabs;


import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.registrymetadata.client.ObjectRef;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.xdstools2.client.CoupledTransactions;
import gov.nist.toolkit.xdstools2.client.command.command.GetRelatedCommand;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.GetDocumentsSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;
import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;
import gov.nist.toolkit.xdstools2.shared.command.request.GetRelatedRequest;

import java.util.ArrayList;
import java.util.List;

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

		couplings.add(TransactionType.IG_QUERY, TransactionType.XC_QUERY, new HTML("Choose a Responding Gateway also, so that its homeCommunityId can be included in the request."),  "This request will be sent to %s and will include the homeCommunityId from %s.");
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

	@Override
	protected Widget buildUI() {
		FlowPanel flowPanel=new FlowPanel();
		HTML title = new HTML();
		title.setHTML("<h2>Get Related Documents</h2>");
		flowPanel.add(title);

		mainGrid = new FlexTable();
		int row = 0;

		flowPanel.add(mainGrid);


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

			if (!verifySiteProvided()) return;

			if (uuid.getValue() == null || uuid.getValue().equals("")) {
				new PopupMessage("You must enter a UUID first");
				return;
			}

			List<String> assocs = new ArrayList<String>();
			for (CheckBox cb : assocCheckBoxes) {
				if (cb.getValue())
					assocs.add(cb.getText());
			}
			if (assocs.size() == 0) {
				new PopupMessage("You must choose at least one Association type");
				return;
			}

			rigForRunning();
			new GetRelatedCommand(){
				@Override
				public void onComplete(List<Result> result) {
					queryCallback.onSuccess(result);
				}
			}.run(new GetRelatedRequest(getCommandContext(),getSiteSelection(),new ObjectRef(uuid.getValue().trim()),assocs));
		}

	}


	public String getWindowShortName() {
		return "getrelated";
	}


}
