package gov.nist.toolkit.xdstools2.client.tabs;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.xdstools2.client.CoupledTransactions;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.AbstractTool;

import java.util.ArrayList;
import java.util.List;

public class FindDocumentsTab extends AbstractTool {

	static List<TransactionType> transactionTypes = new ArrayList<TransactionType>();
	static {
		transactionTypes.add(TransactionType.STORED_QUERY);
		transactionTypes.add(TransactionType.IG_QUERY);
		transactionTypes.add(TransactionType.XC_QUERY);
	}

	static CoupledTransactions couplings = new CoupledTransactions();

	CheckBox selectOnDemand;

	@Override
	public String getTabTitle() { return "FindDocs"; }

	@Override
	public String getToolTitle() { return "Find Documents Stored Query"; }

	@Override
	public void initTool() {
		int row = 0;

		selectOnDemand = new CheckBox();
		selectOnDemand.setText("Include On-Demand DocumentEntries");
		mainGrid.setWidget(row, 0, selectOnDemand);
		row++;

		addQueryBoilerplate(new Runner(), transactionTypes, couplings, true);
	}

	class Runner implements ClickHandler {

		public void onClick(ClickEvent event) {
			resultPanel.clear();

			if (!verifySiteProvided()) return;
			if (!verifyPidProvided()) return;

			prepareToRun();

			toolkitService.findDocuments(queryBoilerplate.getSiteSelection(), pidTextBox.getValue().trim(), selectOnDemand.getValue(), queryCallback);
		}

	}

	public String getWindowShortName() {
		return "finddocuments";
	}


}
