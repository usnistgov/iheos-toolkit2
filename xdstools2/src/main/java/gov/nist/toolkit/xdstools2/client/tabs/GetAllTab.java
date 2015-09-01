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

//	CheckBox selectOnDemand;
	HorizontalPanel dePanel;
	HorizontalPanel ssPanel;
	HorizontalPanel folPanel;
	HorizontalPanel onDemandPanel;
	HorizontalPanel returnsPanel;

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
		paramGrid.setWidget(prow, 2, dePanel = buildIncludeSelection("DocumentEntries"));
		prow++;

		paramGrid.setText(prow, 1, "");
		paramGrid.setWidget(prow, 2, onDemandPanel = buildOnDemandSelection("Type"));
		prow++;

		paramGrid.setText(prow, 1, "Folders");
		paramGrid.setWidget(prow, 2, folPanel = buildIncludeSelection("Folders"));
		prow++;

		paramGrid.setText(prow, 1, "SubmissionSets");
		paramGrid.setWidget(prow, 2, ssPanel = buildIncludeSelection("SubmissionSets"));
		prow++;

		paramGrid.setText(prow, 0, "Filter by:");
		prow++;

		codeFilterBank.addCodeFilter(new CodeFilter(paramGrid, prow, 1, "Format Code", CodesConfiguration.FormatCode, codeFilterBank.codeBoxSize));
		prow++;

		codeFilterBank.addCodeFilter(new CodeFilter(paramGrid, prow, 1, "Confidentiality Code", CodesConfiguration.ConfidentialityCode, codeFilterBank.codeBoxSize));
		prow++;

		paramGrid.setText(prow, 0, "Return");
		paramGrid.setWidget(prow, 2, returnsPanel = buildLeafClassOrObjectRef("Label"));

		topPanel.add(paramGrid);
		topPanel.add(new HTML("<hr/>"));
		topPanel.add(mainGrid);


		addQueryBoilerplate(new Runner(), transactionTypes, couplings, true);
	}

	HorizontalPanel buildIncludeSelection(String label) {
		HorizontalPanel hp = new HorizontalPanel();
//		hp.add(new RadioButton(label, "None"));
		hp.add(new RadioButton(label, "Approved"));
		hp.add(new RadioButton(label, "Deprecated"));
		RadioButton all = new RadioButton(label, "Both");
		all.setValue(true);
		hp.add(all);
		return hp;
	}

	static final String stableString = "Stable";
	static final String onDemandString = "On-Demand";
	static final String bothString = "Both";

	HorizontalPanel buildOnDemandSelection(String label) {
		HorizontalPanel hp = new HorizontalPanel();
		RadioButton _static = new RadioButton(label, stableString);
		hp.add(_static);
		hp.add(new RadioButton(label, onDemandString));
		RadioButton both = new RadioButton(label, bothString);
		both.setValue(true);
		hp.add(both);
		return hp;
	}

	static final String leafClassString = "LeafClass";
	static final String objectRefString = "ObjectRef";

	HorizontalPanel buildLeafClassOrObjectRef(String label) {
		HorizontalPanel hp = new HorizontalPanel();
		RadioButton lc = new RadioButton(label, leafClassString);
		lc.setValue(true);
		hp.add(lc);
		hp.add(new RadioButton(label, objectRefString));
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

			addStatusBox();
			getGoButton().setEnabled(false);
			getInspectButton().setEnabled(false);

			Map<String, List<String>> codeSpec = new HashMap<>();

			// {DocumentEntry, SubmissionSet, Folder}.availabilityStatus
			addToCodeSpec(codeSpec, dePanel, CodesConfiguration.DocumentEntryStatus);
			addToCodeSpec(codeSpec, ssPanel, CodesConfiguration.SubmissionSetStatus);
			addToCodeSpec(codeSpec, folPanel, CodesConfiguration.FolderStatus);

			addOnDemandToCodeSpec(codeSpec, onDemandPanel, CodesConfiguration.DocumentEntryType);
			addReturnsToCodeSpec(codeSpec, returnsPanel, CodesConfiguration.ReturnsType);

			// Codes
			codeFilterBank.addToCodeSpec(codeSpec);

			toolkitService.getAll(siteSpec, pidTextBox.getValue().trim(), codeSpec, queryCallback);
		}

		void addToCodeSpec(Map<String, List<String>> codeSpec, HorizontalPanel panel, String codeType) {
			List<String> status = new ArrayList<>();
			codeSpec.put(codeType, status);
			for (int i=0; i<panel.getWidgetCount(); i++) {
				RadioButton rb = (RadioButton) panel.getWidget(i);
				if (rb.getValue()) {
					if ("Approved".equals(rb.getText())) status.add("urn:oasis:names:tc:ebxml-regrep:StatusType:Approved");
					else if ("Deprecated".equals(rb.getText())) status.add("urn:oasis:names:tc:ebxml-regrep:StatusType:Deprecated");
					else if ("Both".equals(rb.getText())) {
						status.add("urn:oasis:names:tc:ebxml-regrep:StatusType:Approved");
						status.add("urn:oasis:names:tc:ebxml-regrep:StatusType:Deprecated");
					}
				}
			}
		}

		void addOnDemandToCodeSpec(Map<String, List<String>> codeSpec, HorizontalPanel panel, String codeType) {
			List<String> status = new ArrayList<>();
			codeSpec.put(codeType, status);
			for (int i=0; i<panel.getWidgetCount(); i++) {
				RadioButton rb = (RadioButton) panel.getWidget(i);
				if (rb.getValue()) {
					if (stableString.equals(rb.getText())) status.add("urn:uuid:7edca82f-054d-47f2-a032-9b2a5b5186c1");
					if (onDemandString.equals(rb.getText())) status.add("urn:uuid:34268e47-fdf5-41a6-ba33-82133c465248");
					if (bothString.equals(rb.getText())) {
						status.add("urn:uuid:7edca82f-054d-47f2-a032-9b2a5b5186c1");
						status.add("urn:uuid:34268e47-fdf5-41a6-ba33-82133c465248");
					}
				}
			}
		}

		void addReturnsToCodeSpec(Map<String, List<String>> codeSpec, HorizontalPanel panel, String codeType) {
			List<String> status = new ArrayList<>();
			codeSpec.put(codeType, status);
			for (int i=0; i<panel.getWidgetCount(); i++) {
				RadioButton rb = (RadioButton) panel.getWidget(i);
				if (rb.getValue()) {
					if (leafClassString.equals(rb.getText())) status.add("LeafClass");
					if (objectRefString.equals(rb.getText())) status.add("ObjectRef");
				}
			}
		}
	}

	public String getWindowShortName() {
		return "getall";
	}


}
