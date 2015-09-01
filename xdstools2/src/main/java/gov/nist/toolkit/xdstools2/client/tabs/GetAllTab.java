package gov.nist.toolkit.xdstools2.client.tabs;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import gov.nist.toolkit.actortransaction.client.ATFactory.TransactionType;
import gov.nist.toolkit.results.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.CoupledTransactions;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.TabContainer;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.FindDocumentsSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;
import gov.nist.toolkit.xdstools2.client.widgets.queryFilter.GetAllParams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetAllTab extends GenericQueryTab {

	static List<TransactionType> transactionTypes = new ArrayList<TransactionType>();
	static {
		transactionTypes.add(TransactionType.STORED_QUERY);
		transactionTypes.add(TransactionType.IG_QUERY);
		transactionTypes.add(TransactionType.XC_QUERY);
	}

	static CoupledTransactions couplings = new CoupledTransactions();

	GenericQueryTab genericQueryTab;
	GetAllParams sqParams;

	public GetAllTab() {
		super(new FindDocumentsSiteActorManager());
	}

	public void onTabLoad(TabContainer container, boolean select, String eventName) {
		myContainer = container;
		topPanel = new VerticalPanel();
		genericQueryTab = this;


		container.addTab(topPanel, "GetAll", select);
		addCloseButton(container,topPanel, null);

		HTML title = new HTML();
		title.setHTML("<h2>GetAll Stored Query</h2>");
		topPanel.add(title);

		sqParams = new GetAllParams(toolkitService, genericQueryTab);

		mainGrid = new FlexTable();
		topPanel.add(sqParams.asWidget());
		topPanel.add(new HTML("<hr/>"));
		topPanel.add(mainGrid);


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

			if (pidTextBox.getValue() == null || pidTextBox.getValue().equals("")) {
				new PopupMessage("You must enter a Patient ID first");
				return;
			}

			addStatusBox();
			getGoButton().setEnabled(false);
			getInspectButton().setEnabled(false);

			Map<String, List<String>> codeSpec = new HashMap<>();

			sqParams.addToCodeSpec(codeSpec);
			toolkitService.getAll(siteSpec, pidTextBox.getValue().trim(), codeSpec, queryCallback);
		}
	}

	public String getWindowShortName() {
		return "getall";
	}


}
