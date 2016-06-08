package gov.nist.toolkit.xdstools2.client.tabs;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.registrymetadata.client.Uid;
import gov.nist.toolkit.registrymetadata.client.Uids;
import gov.nist.toolkit.results.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.CoupledTransactions;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.TabContainer;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.RetrieveSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;

import java.util.ArrayList;
import java.util.List;

public class DocRetrieveTab extends GenericQueryTab {

	static List<TransactionType> transactionTypes = new ArrayList<TransactionType>();
	static {
		transactionTypes.add(TransactionType.RETRIEVE);
		transactionTypes.add(TransactionType.ODDS_RETRIEVE);
//		transactionTypes.add(TransactionType.ISR_RETRIEVE);
	}
	
	static CoupledTransactions couplings = new CoupledTransactions();

	TextBox docUidBox;
	TextArea textArea;
//	TextBox repUidBox;
	
	public DocRetrieveTab() {
		super(new RetrieveSiteActorManager());
	}
	
	public void onTabLoad(TabContainer container, boolean select, String eventName) {
		myContainer = container;
		topPanel = new VerticalPanel();
		container.addTab(topPanel, "RetrieveDoc", select);
		addCloseButton(container,topPanel, null);

		HTML title = new HTML();
		title.setHTML("<h2>Retrieve Documents</h2>");
		topPanel.add(title);

		mainGrid = new FlexTable();
		int row = 0;
		
		topPanel.add(mainGrid);

		HTML docUidLabel = new HTML();
		docUidLabel.setText("Document UniqueIds");
		mainGrid.setWidget(row,0, docUidLabel);

//		docUidBox = new TextBox();
//		docUidBox.setWidth("500px");
//		mainGrid.setWidget(row, 1, docUidBox);

		textArea = new TextArea();
		textArea.setCharacterWidth(40);
		textArea.setVisibleLines(10);
		mainGrid.setWidget(row, 1, textArea);

		row++;
		
		queryBoilerplate = addQueryBoilerplate(new Runner(), transactionTypes, couplings, false);

	}

	class Runner implements ClickHandler {

		public void onClick(ClickEvent event) {
			resultPanel.clear();

			SiteSpec siteSpec = queryBoilerplate.getSiteSelection();
			if (siteSpec == null)
				return;

//			if (docUidBox.getValue() == null || docUidBox.getValue().equals("")) {
//				new PopupMessage("You must enter a Document UniqueId first");
//				return;
//			}

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

//			siteSpec.isTls = doTLS;
//			siteSpec.isSaml = doSAML;
//			siteSpec.isAsync = doASYNC;
			toolkitService.retrieveDocument(siteSpec, uids, queryCallback);
		}
		
	}

//	class RegSelect implements ClickHandler {
//
//		public void onClick(ClickEvent event) {
//			for (RadioButton rb : rgButtons) {
//				rb.setValue(false);
//			}
//			for (RadioButton rb : repositoryButtons) {
//				rb.setValue(false);
//			}
//			for (RadioButton rb : igButtons) {
//				rb.setValue(false);
//			}
//		}
//
//	}
//
//	class RepSelect implements ClickHandler {
//
//		public void onClick(ClickEvent event) {
//			for (RadioButton rb : registryButtons) {
//				rb.setValue(false);
//			}
//			for (RadioButton rb : rgButtons) {
//				rb.setValue(false);
//			}
//			for (RadioButton rb : igButtons) {
//				rb.setValue(false);
//			}
//		}
//
//	}
//
//	class RGSelect implements ClickHandler {
//
//		public void onClick(ClickEvent event) {
//			for (RadioButton rb : registryButtons) {
//				rb.setValue(false);
//			}
//			for (RadioButton rb : repositoryButtons) {
//				rb.setValue(false);
//			}
//		}
//
//	}
//
//	class IGSelect implements ClickHandler {
//
//		public void onClick(ClickEvent event) {
//			for (RadioButton rb : registryButtons) {
//				rb.setValue(false);
//			}
//			for (RadioButton rb : repositoryButtons) {
//				rb.setValue(false);
//			}
//		}
//
//	}

	public String getWindowShortName() {
		return "docretrieve";
	}



}
