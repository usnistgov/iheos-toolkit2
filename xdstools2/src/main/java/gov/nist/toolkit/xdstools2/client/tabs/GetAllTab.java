package gov.nist.toolkit.xdstools2.client.tabs;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.actortransaction.client.ATFactory.TransactionType;
import gov.nist.toolkit.results.client.CodesConfiguration;
import gov.nist.toolkit.results.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.*;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.FindDocumentsSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;

import java.util.ArrayList;
import java.util.List;

public class GetAllTab extends GenericQueryTab {

	static List<TransactionType> transactionTypes = new ArrayList<TransactionType>();
	static {
		transactionTypes.add(TransactionType.STORED_QUERY);
		transactionTypes.add(TransactionType.IG_QUERY);
		transactionTypes.add(TransactionType.XC_QUERY);
	}

	static CoupledTransactions couplings = new CoupledTransactions();

	CheckBox selectOnDemand;
	HorizontalPanel dePanel;
	HorizontalPanel ssPanel;
	HorizontalPanel folPanel;

	CodeFilterBank codeFilterBank;
	GenericQueryTab genericQueryTab;

	public GetAllTab() {
		super(new FindDocumentsSiteActorManager());
	}


	public void onTabLoad(TabContainer container, boolean select, String eventName) {
		myContainer = container;
		topPanel = new VerticalPanel();
		genericQueryTab = this;


		container.addTab(topPanel, "GetAll", select);
		addCloseButton(container,topPanel, null);

		codeFilterBank = new CodeFilterBank(toolkitService, genericQueryTab);

		FlexTable paramGrid = new FlexTable();

		HTML title = new HTML();
		title.setHTML("<h2>GetAll Stored Query</h2>");
		topPanel.add(title);

		mainGrid = new FlexTable();
		int prow = 0;

		paramGrid.setText(prow, 0, "Include:");
		prow++;

		paramGrid.setText(prow, 1, "DocumentEntries");
		paramGrid.setWidget(prow, 2, dePanel = buildSelection("DocumentEntries"));
		prow++;

		selectOnDemand = new CheckBox();
		selectOnDemand.setText("On-Demand DocumentEntries");
		paramGrid.setWidget(prow, 2, selectOnDemand);
		prow++;

		paramGrid.setText(prow, 1, "Folders");
		paramGrid.setWidget(prow, 2, folPanel = buildSelection("Folders"));
		prow++;

		paramGrid.setText(prow, 1, "SubmissionSets");
		paramGrid.setWidget(prow, 2, ssPanel = buildSelection("SubmissionSets"));
		prow++;

		paramGrid.setText(prow, 0, "Filter by:");
		prow++;

		codeFilterBank.addCodeFilter(new CodeFilter(paramGrid, prow, 1, "Format Code", CodesConfiguration.FormatCode, codeFilterBank.codeBoxSize));
		prow++;

		codeFilterBank.addCodeFilter(new CodeFilter(paramGrid, prow, 1, "Confidentiality Code", CodesConfiguration.ConfidentialityCode, codeFilterBank.codeBoxSize));
		prow++;

		topPanel.add(paramGrid);
		topPanel.add(new HTML("<hr/>"));
		topPanel.add(mainGrid);


		addQueryBoilerplate(new Runner(), transactionTypes, couplings, true);
	}

	HorizontalPanel buildSelection(String label) {
		HorizontalPanel hp = new HorizontalPanel();
		hp.add(new RadioButton(label, "None"));
		hp.add(new RadioButton(label, "Approved"));
		hp.add(new RadioButton(label, "Deprecated"));
		RadioButton all = new RadioButton(label, "All");
		all.setValue(true);
		hp.add(all);
		return hp;
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

			for (int i=0; i<dePanel.getWidgetCount(); i++) {
				RadioButton rb = (RadioButton) dePanel.getWidget(i);
				if (rb.getValue()) new PopupMessage(rb.getText() + " selected");
			}


			addStatusBox();
			getGoButton().setEnabled(false);
			getInspectButton().setEnabled(false);

			toolkitService.findDocuments(siteSpec, pidTextBox.getValue().trim(), selectOnDemand.getValue(), queryCallback);
		}

	}

	public String getWindowShortName() {
		return "getall";
	}


}
