package gov.nist.toolkit.xdstools2.client.tabs;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import gov.nist.toolkit.actortransaction.client.TransactionType;
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

public class ImagingDocSetRetrieveTab extends GenericQueryTab {

	static List<TransactionType> transactionTypes = new ArrayList<TransactionType>();
	static {
		transactionTypes.add(TransactionType.RET_IMG_DOC_SET);
	}
	
	static CoupledTransactions couplings = new CoupledTransactions();

	TextArea studyRequestArea;
//	TextBox repUidBox;
	
	public ImagingDocSetRetrieveTab() {
		super(new RetrieveSiteActorManager());
	}
	
	public void onTabLoad(TabContainer container, boolean select, String eventName) {
		myContainer = container;
		topPanel = new VerticalPanel();
		container.addTab(topPanel, "RetrieveImagingDocSet", select);
		addCloseButton(container,topPanel, null);

		HTML title = new HTML();
		title.setHTML("<h2>Retrieve Imaging Document Set</h2>");
		topPanel.add(title);

		mainGrid = new FlexTable();
		int row = 0;
		
		topPanel.add(mainGrid);

		HTML studyRequestLabel = new HTML();
		studyRequestLabel.setText("Study Request");
		mainGrid.setWidget(row,0, studyRequestLabel);

		studyRequestArea = new TextArea();
		studyRequestArea.setCharacterWidth(120);
		studyRequestArea.setVisibleLines(20);

		mainGrid.setWidget(row, 1, studyRequestArea);
		row++;

/*
		HTML transferSyntaxLabel = new HTML();
		transferSyntaxLabel.setText("Transfer Syntax");
		mainGrid.setWidget(row,0, transferSyntaxLabel);

		transferSyntaxBox = new TextBox();
		transferSyntaxBox.setWidth("500px");
		mainGrid.setWidget(row, 1, transferSyntaxBox);
		row++;
*/
		
		queryBoilerplate = addQueryBoilerplate(new Runner(), transactionTypes, couplings, false);

	}

	class Runner implements ClickHandler {

		public void onClick(ClickEvent event) {
			System.out.println("ImagingDocSetRetrieveTab:onClick");
			resultPanel.clear();
			SiteSpec siteSpec = queryBoilerplate.getSiteSelection();
			if (siteSpec == null)
				return;

			if (studyRequestArea.getValue() == null || studyRequestArea.getValue().equals("")) {
				new PopupMessage("You must enter a (formatted) Study Request first");
				return;
			}
			String fullRequest = studyRequestArea.getValue().trim();
			Uids uids = this.extractDocumentUids(fullRequest);

			addStatusBox();
			getGoButton().setEnabled(false);
			getInspectButton().setEnabled(false);


			toolkitService.retrieveImagingDocSet(siteSpec, uids, fullRequest, "", queryCallback);

		}
		private Uids extractDocumentUids(String s) {
			Uids uids = new Uids();
			uids.add(new Uid("1.2.3"));
			return uids;
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
		return "imagingdocsetretrieve";
	}



}
