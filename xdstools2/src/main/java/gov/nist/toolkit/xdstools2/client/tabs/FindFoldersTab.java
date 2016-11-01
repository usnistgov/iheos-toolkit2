package gov.nist.toolkit.xdstools2.client.tabs;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.CoupledTransactions;
import gov.nist.toolkit.xdstools2.client.command.command.FindFoldersCommand;
import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.FindDocumentsSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;
import gov.nist.toolkit.xdstools2.shared.command.request.FindFoldersRequest;

import java.util.ArrayList;
import java.util.List;

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

	@Override
	protected Widget buildUI() {
		FlowPanel flowPanel=new FlowPanel();

		HTML title = new HTML();
		title.setHTML("<h2>Find Folders</h2>");
		flowPanel.add(title);

		mainGrid = new FlexTable();

		flowPanel.add(mainGrid);
		return flowPanel;
	}

	@Override
	protected void bindUI() {
	}

	@Override
	protected void configureTabView() {
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

			new FindFoldersCommand(){
				@Override
				public void onComplete(List<Result> result) {
					queryCallback.onSuccess(result);
				}
			}.run(new FindFoldersRequest(getCommandContext(),siteSpec, pid.trim()));
		}
		
	}

	public String getWindowShortName() {
		return "findfolders";
	}



	
}
