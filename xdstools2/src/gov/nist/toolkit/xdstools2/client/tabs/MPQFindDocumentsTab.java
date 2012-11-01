package gov.nist.toolkit.xdstools2.client.tabs;

import gov.nist.toolkit.actortransaction.client.ATFactory.TransactionType;
import gov.nist.toolkit.results.client.AssertionResult;
import gov.nist.toolkit.results.client.CodesConfiguration;
import gov.nist.toolkit.results.client.CodesResult;
import gov.nist.toolkit.results.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.CoupledTransactions;
import gov.nist.toolkit.xdstools2.client.TabContainer;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.GetDocumentsSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.CodeEditButtonSelector;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.QueryBoilerplate;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MPQFindDocumentsTab extends GenericQueryTab {
	
	static List<TransactionType> transactionTypes = new ArrayList<TransactionType>();
	static {
		transactionTypes.add(TransactionType.MPQ);
	}
	
	static CoupledTransactions couplings = new CoupledTransactions();


//	TextBox pid;
	Button classCodeEditButton;
	Button eventCodeEditButton;
	Button hcftCodeEditButton;
	
	ListBox classCode;
	ListBox eventCode;
	ListBox hcftCode;
	
	CodesConfiguration codesConfiguration;
	int codeBoxSize = 1;
	
	GenericQueryTab genericQueryTab;

	public MPQFindDocumentsTab() {
		super(new GetDocumentsSiteActorManager());
	}
	
	QueryBoilerplate queryBoilerplate = null;

	public void onTabLoad(TabContainer container, boolean select, String eventName) {
		myContainer = container;
		topPanel = new VerticalPanel();
		genericQueryTab = this;


		container.addTab(topPanel, "MPQFindDocuments", select);
		addCloseButton(container,topPanel, null);
		
		HTML title = new HTML();
		title.setHTML("<h2>Multi-Patient Find Documents</h2>");
		topPanel.add(title);

		if (codesConfiguration == null)
			toolkitService.getCodesConfiguration(loadCodeConfigCallback);

		mainGrid = new FlexTable();
		int row = 0;

		topPanel.add(mainGrid);

		HTML classCodeLabel = new HTML();
		classCodeLabel.setText("Class Code");
		mainGrid.setWidget(row, 0, classCodeLabel);

		classCode = new ListBox();
		classCode.setVisibleItemCount(codeBoxSize);
		mainGrid.setWidget(row, 1, classCode);

		classCodeEditButton = new Button("edit");
		mainGrid.setWidget(row, 2, classCodeEditButton);
		classCodeEditButton.setEnabled(false);

		HTML codeRequiredLabel = new HTML();
		codeRequiredLabel.setText("One or more codes must have values selected");
		mainGrid.setWidget(row, 3, codeRequiredLabel);
		row++;

		HTML eventCodeLabel = new HTML();
		eventCodeLabel.setText("Event Code");
		mainGrid.setWidget(row, 0, eventCodeLabel);

		eventCode = new ListBox();
		eventCode.setVisibleItemCount(codeBoxSize);
		mainGrid.setWidget(row, 1, eventCode);

		eventCodeEditButton = new Button("edit");
		mainGrid.setWidget(row, 2, eventCodeEditButton);
		eventCodeEditButton.setEnabled(false);
		row++;

		HTML hcftCodeLabel = new HTML();
		hcftCodeLabel.setText("Healthcare Facility Type Code");
		mainGrid.setWidget(row, 0, hcftCodeLabel);

		hcftCode = new ListBox();
		hcftCode.setVisibleItemCount(codeBoxSize);
		mainGrid.setWidget(row, 1, hcftCode);

		hcftCodeEditButton = new Button("edit");
		mainGrid.setWidget(row, 2, hcftCodeEditButton);
		hcftCodeEditButton.setEnabled(false);
		row++;
		
		queryBoilerplate = addQueryBoilerplate(new Runner(), transactionTypes, couplings, true);
		
	}

	class Runner implements ClickHandler {

		public void onClick(ClickEvent event) {
			String pid = queryBoilerplate.getPatientId();

			resultPanel.clear();

			SiteSpec siteSpec = queryBoilerplate.getSiteSelection();
			if (siteSpec == null)
				return;

			addStatusBox();
			getGoButton().setEnabled(false);
			getInspectButton().setEnabled(false);

//			siteSpec.isTls = doTLS;
//			siteSpec.isSaml = doSAML;
//			siteSpec.isAsync = doASYNC;
			toolkitService.mpqFindDocuments(siteSpec, 
					pid.trim(), 
					getValuesFromListBox(classCode),
					getValuesFromListBox(hcftCode),
					getValuesFromListBox(eventCode),
					queryCallback);
		}

	}
	
	List<String> getValuesFromListBox(ListBox lb) {
		List<String> values = new ArrayList<String>();
		
		for (int i=0; i<lb.getItemCount(); i++) {
			values.add(lb.getValue(i));
		}
		
		return values;
	}

	void enableCodeEditButtons() {
		classCodeEditButton.setEnabled(true);
		eventCodeEditButton.setEnabled(true);
		hcftCodeEditButton.setEnabled(true);

	}

	protected AsyncCallback<CodesResult> loadCodeConfigCallback = new AsyncCallback<CodesResult>() {

		public void onFailure(Throwable caught) {
			resultPanel.clear();
			resultPanel.add(addHTML("<font color=\"#FF0000\">" + "Error running validation: " + caught.getMessage() + "</font>"));
		}

		public void onSuccess(CodesResult result) {
			for (AssertionResult a : result.result.assertions.assertions) {
				if (!a.status) {
					resultPanel.add(addHTML("<font color=\"#FF0000\">" + a.assertion + "</font>"));
				}
			}
			codesConfiguration = result.codesConfiguration;
			classCodeEditButton.addClickHandler(new CodeEditButtonSelector(
					genericQueryTab, 
					codesConfiguration.getCodeConfiguration(CodesConfiguration.ClassCode),  
					classCode));
			eventCodeEditButton.addClickHandler(new CodeEditButtonSelector(
					genericQueryTab, 
					codesConfiguration.getCodeConfiguration(CodesConfiguration.EventCodeList), 
					eventCode));
			hcftCodeEditButton.addClickHandler(new CodeEditButtonSelector(
					genericQueryTab, 
					codesConfiguration.getCodeConfiguration(CodesConfiguration.HealthcareFacilityTypeCode), 
					hcftCode));
			enableCodeEditButtons();
		}

	};

	public String getWindowShortName() {
		return "mpqfinddocuments";
	}

}
