package gov.nist.toolkit.xdstools2.client.tabs;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.registrymetadata.client.Uid;
import gov.nist.toolkit.registrymetadata.client.Uids;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.CoupledTransactions;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.RetrieveSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;

import java.util.ArrayList;
import java.util.List;

public class DocRetrieveTab extends GenericQueryTab {

	static List<TransactionType> transactionTypes = new ArrayList<TransactionType>();
	static {
		transactionTypes.add(TransactionType.RETRIEVE);
		transactionTypes.add(TransactionType.ODDS_RETRIEVE);
//		transactionTypes.addTest(TransactionType.ISR_RETRIEVE);
	}
	
	static CoupledTransactions couplings = new CoupledTransactions();

	TextBox docUidBox;
	TextArea textArea;

	public DocRetrieveTab() {
		super(new RetrieveSiteActorManager());
	}

	@Override
	protected Widget buildUI() {
		FlowPanel container = new FlowPanel();

		HTML title = new HTML();
		title.setHTML("<h2>Retrieve Documents</h2>");
		tabTopPanel.add(title);

		mainGrid = new FlexTable();
		int row = 0;

		container.add(mainGrid);

		HTML docUidLabel = new HTML();
		docUidLabel.setText("Document UniqueIds");
		mainGrid.setWidget(row,0, docUidLabel);

		textArea = new TextArea();
		textArea.setCharacterWidth(40);
		textArea.setVisibleLines(10);
		mainGrid.setWidget(row, 1, textArea);

		row++;
		return container;
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
			if (siteSpec == null)
				return;

			if (textArea.getValue() == null || textArea.getValue().equals("")) {
				new PopupMessage("You must enter a Document UniqueId first");
				return;
			}


			List<String> values = formatIds(textArea.getValue());

			Uids uids = new Uids();


			for (String value : values) {
				Uid uid = new Uid(value.trim());
				uids.add(uid);
			}


			addStatusBox();
			getGoButton().setEnabled(false);
			getInspectButton().setEnabled(false);

			getToolkitServices().retrieveDocument(siteSpec, uids, queryCallback);
		}
		
	}

	public String getWindowShortName() {
		return "docretrieve";
	}



}
