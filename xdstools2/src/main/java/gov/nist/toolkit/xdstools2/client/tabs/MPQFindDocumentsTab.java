package gov.nist.toolkit.xdstools2.client.tabs;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.results.client.CodesConfiguration;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.CoupledTransactions;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.GetDocumentsSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.QueryBoilerplate;
import gov.nist.toolkit.xdstools2.client.widgets.queryFilter.CodeFilterBank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MPQFindDocumentsTab extends GenericQueryTab {
	
	static List<TransactionType> transactionTypes = new ArrayList<TransactionType>();
	static {
		transactionTypes.add(TransactionType.MPQ);
	}
	
	static CoupledTransactions couplings = new CoupledTransactions();

	static List<String> patientIdAlternateFilterNames = new ArrayList<>();
	static {
		patientIdAlternateFilterNames.add(CodesConfiguration.ClassCode);
		patientIdAlternateFilterNames.add(CodesConfiguration.EventCodeList);
		patientIdAlternateFilterNames.add(CodesConfiguration.HealthcareFacilityTypeCode);
	}

	static List<String> otherFilterNames = new ArrayList<>();
	static {
		otherFilterNames.add(CodesConfiguration.TypeCode);
		otherFilterNames.add(CodesConfiguration.PracticeSettingCode);
		otherFilterNames.add(CodesConfiguration.ConfidentialityCode);
		otherFilterNames.add(CodesConfiguration.FormatCode);
	}

	CodeFilterBank codeFilterBank;
	GenericQueryTab genericQueryTab;

	public MPQFindDocumentsTab() {
		super(new GetDocumentsSiteActorManager());
	}
	
	QueryBoilerplate queryBoilerplate = null;

	@Override
	protected Widget buildUI() {
		genericQueryTab=this;

		FlowPanel flowPanel=new FlowPanel();
		codeFilterBank = new CodeFilterBank(/*toolkitService, */genericQueryTab);

		FlexTable paramGrid = new FlexTable();

		HTML title = new HTML();
		title.setHTML("<h2>Multi-Patient Find Documents</h2>");
		flowPanel.add(title);

		mainGrid = new FlexTable();
		int prow = 0;

		FlexTable.FlexCellFormatter cellFormatter = paramGrid.getFlexCellFormatter();
		paramGrid.setText(prow, 0, "Patient ID or at least one of these filters is required");
		cellFormatter.setColSpan(0, 0, 5);
		prow++;

		paramGrid.setText(prow, 0, "Filter by:");
		prow++;


		prow = codeFilterBank.addCodeFiltersByName(patientIdAlternateFilterNames, paramGrid, prow, 1, 2);
		paramGrid.setText(prow, 0, "Optionally also filter by:");
		prow++;
		prow = codeFilterBank.addCodeFiltersByName(otherFilterNames, paramGrid, prow, 1, 2);

		flowPanel.add(paramGrid);
		flowPanel.add(mainGrid);
		return flowPanel;
	}

	@Override
	protected void bindUI() {
		addOnTabSelectionRedisplay();
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
			if (siteSpec == null)
				return;

			addStatusBox();
			getGoButton().setEnabled(false);
			getInspectButton().setEnabled(false);

			getToolkitServices().mpqFindDocuments(siteSpec,
					pid.trim(), 
					codesSpec(),
					queryCallback);
		}

	}

	Map<String, List<String>> codesSpec() {
		Map<String, List<String>> map = new HashMap<>();
		map.put(CodesConfiguration.ClassCode, getValuesFromListBox(codeFilterBank.getCodeFilter(CodesConfiguration.ClassCode).selectedCodes));
		map.put(CodesConfiguration.HealthcareFacilityTypeCode, getValuesFromListBox(codeFilterBank.getCodeFilter(CodesConfiguration.HealthcareFacilityTypeCode).selectedCodes));
		map.put(CodesConfiguration.EventCodeList, getValuesFromListBox(codeFilterBank.getCodeFilter(CodesConfiguration.EventCodeList).selectedCodes));
		return map;
	}
	
	List<String> getValuesFromListBox(ListBox lb) {
		List<String> values = new ArrayList<String>();
		
		for (int i=0; i<lb.getItemCount(); i++) {
			values.add(lb.getValue(i));
		}
		
		return values;
	}



	public String getWindowShortName() {
		return "mpqfinddocuments";
	}

}
