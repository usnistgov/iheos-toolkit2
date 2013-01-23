package gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab;

import gov.nist.toolkit.actortransaction.client.ATFactory;
import gov.nist.toolkit.actortransaction.client.ATFactory.ActorType;
import gov.nist.toolkit.actortransaction.client.ATFactory.TransactionType;
import gov.nist.toolkit.results.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.CoupledTransactions;

import java.util.List;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;

public class QueryBoilerplate {
	/**
	 * 
	 */
	public final GenericQueryTab genericQueryTab;
	Anchor reload;
	
	public void enableRun(boolean enable) {
		genericQueryTab.runEnabled = enable;
	}

	QueryBoilerplate(GenericQueryTab genericQueryTab, ClickHandler runner, List<TransactionType> transactionTypes, CoupledTransactions couplings, ActorType selectByActor) {
		this.genericQueryTab = genericQueryTab;
		genericQueryTab.selectByActor = selectByActor;
		genericQueryTab.row_initial = genericQueryTab.mainGrid.getRowCount();
		genericQueryTab.runner = runner;
		genericQueryTab.transactionTypes = transactionTypes;
		genericQueryTab.couplings = couplings;

		genericQueryTab.resultPanel = new VerticalPanel();
		genericQueryTab.topPanel.add(genericQueryTab.resultPanel);


		genericQueryTab.addActorReloader();

		if (GenericQueryTab.transactionOfferings == null) {
			genericQueryTab.reloadTransactionOfferings();
		} else {
			genericQueryTab.redisplay();
		}
	}


	QueryBoilerplate(GenericQueryTab genericQueryTab2, ClickHandler runner, List<TransactionType> transactionTypes, CoupledTransactions couplings) {
		this(genericQueryTab2, runner, transactionTypes, couplings, null);

	}

	public void enableTls(boolean enable) {
		genericQueryTab.tlsEnabled = enable;
	}

	public void enableSaml(boolean enable) {
		genericQueryTab.samlEnabled = enable;
	}
	
	public void enableInspectResults(boolean enable) {
		genericQueryTab.enableInspectResults = enable;
	}

	void remove() {
		if (genericQueryTab == null)
			return;
		if (genericQueryTab.resultPanel != null)
			genericQueryTab.topPanel.remove(genericQueryTab.resultPanel);
		if (reload != null)
			genericQueryTab.menuPanel.remove(reload);
		genericQueryTab.initMainGrid();
	}

	boolean isDisplayGW() {
		for (TransactionType tt : genericQueryTab.transactionTypes) {
			if (ATFactory.isGatewayTransaction(tt))
				return true;
		}
		return false;
	}

	public SiteSpec getSiteSelection() {
		if (genericQueryTab.selectByActor != null) {    // Used in Mesa test tab
			for (RadioButton b : genericQueryTab.byActorButtons) {
				if (b.getValue()) {
					genericQueryTab.setCommonSiteSpec(new SiteSpec(b.getText(), genericQueryTab.selectByActor, genericQueryTab.getCommonSiteSpec()));
					return genericQueryTab.getCommonSiteSpec();
				}
			}
		} else {   // Select by transaction (used in GetDocuments tab)
			SiteSpec siteSpec = genericQueryTab.transactionSelectionManager.generateSiteSpec();
			genericQueryTab.setCommonSiteSpec(siteSpec);
			return siteSpec;
			//				for (TransactionType tt : genericQueryTab.perTransTypeRadioButtons.keySet()) {
			//					for (RadioButton rb : genericQueryTab.perTransTypeRadioButtons.get(tt)) {
			//						if (rb.getValue()) {
			//							genericQueryTab.getCommonSiteSpec().setName(rb.getText());
			//							genericQueryTab.getCommonSiteSpec().setActorType(ActorType.getActorType(tt));
			//							return genericQueryTab.getCommonSiteSpec();
			//						}
			//					}
			//				}
		}

		return null;
	}

	public String getPatientId() { return genericQueryTab.getCommonPatientId(); }


}