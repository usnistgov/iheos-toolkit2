package gov.nist.toolkit.xdstools2.client.tabs;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.registrymetadata.client.Uid;
import gov.nist.toolkit.registrymetadata.client.Uids;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.actortransaction.shared.SiteSpec;
import gov.nist.toolkit.xdstools2.client.CoupledTransactions;
import gov.nist.toolkit.xdstools2.client.command.command.RetrieveImagingDocSetCommand;
import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.RetrieveSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;
import gov.nist.toolkit.xdstools2.shared.command.request.RetrieveImagingDocSetRequest;

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


	@Override
	protected Widget buildUI() {
		FlowPanel flowPanel=new FlowPanel();
		HTML title = new HTML();
		title.setHTML("<h2>Retrieve Imaging Document Set</h2>");
		flowPanel.add(title);

		mainGrid = new FlexTable();
		int row = 0;

		tabTopPanel.add(mainGrid);

		HTML studyRequestLabel = new HTML();
		studyRequestLabel.setText("Study Request");
		mainGrid.setWidget(row,0, studyRequestLabel);

		studyRequestArea = new TextArea();
		studyRequestArea.setCharacterWidth(120);
		studyRequestArea.setVisibleLines(20);

		mainGrid.setWidget(row, 1, studyRequestArea);
		row++;

		return flowPanel;
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

			new RetrieveImagingDocSetCommand(){
				@Override
				public void onComplete(List<Result> result) {
					queryCallback.onSuccess(result);
				}
			}.run(new RetrieveImagingDocSetRequest(getCommandContext(),siteSpec,uids,fullRequest,""));
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
//				rb.setBooleanValue(false);
//			}
//			for (RadioButton rb : repositoryButtons) {
//				rb.setBooleanValue(false);
//			}
//			for (RadioButton rb : igButtons) {
//				rb.setBooleanValue(false);
//			}
//		}
//
//	}
//
//	class RepSelect implements ClickHandler {
//
//		public void onClick(ClickEvent event) {
//			for (RadioButton rb : registryButtons) {
//				rb.setBooleanValue(false);
//			}
//			for (RadioButton rb : rgButtons) {
//				rb.setBooleanValue(false);
//			}
//			for (RadioButton rb : igButtons) {
//				rb.setBooleanValue(false);
//			}
//		}
//
//	}
//
//	class RGSelect implements ClickHandler {
//
//		public void onClick(ClickEvent event) {
//			for (RadioButton rb : registryButtons) {
//				rb.setBooleanValue(false);
//			}
//			for (RadioButton rb : repositoryButtons) {
//				rb.setBooleanValue(false);
//			}
//		}
//
//	}
//
//	class IGSelect implements ClickHandler {
//
//		public void onClick(ClickEvent event) {
//			for (RadioButton rb : registryButtons) {
//				rb.setBooleanValue(false);
//			}
//			for (RadioButton rb : repositoryButtons) {
//				rb.setBooleanValue(false);
//			}
//		}
//
//	}

	public String getWindowShortName() {
		return "imagingdocsetretrieve";
	}



}
