package gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.RadioButton;
import gov.nist.toolkit.actortransaction.client.ATFactory;
import gov.nist.toolkit.actortransaction.shared.ActorType;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.CoupledTransactions;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;

import java.util.List;

public class QueryBoilerplate {
	/**
	 * 
	 */

	// This class is really a factory class and should be restructured as such.
	// when this happens a few of the calls below will move to GenericQueryTab
	// Each query type tab
	//    extends GenericQueryTab
	//    contains an instance of QueryBoilerplate
	// this references the tab class (forward and backward pointers)
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

//		genericQueryTab.resultPanel = new VerticalPanel();
//		genericQueryTab.tabTopPanel.add(genericQueryTab.resultPanel);

		genericQueryTab.addActorReloader();

		if (GenericQueryTab.transactionOfferings == null
				|| /* Issue #426 */
				((GenericQueryTab.transactionOfferings!=null && GenericQueryTab.transactionOfferings.map!=null && GenericQueryTab.transactionOfferings.map.isEmpty()) &&
					(GenericQueryTab.transactionOfferings!=null && GenericQueryTab.transactionOfferings.tmap!=null && GenericQueryTab.transactionOfferings.tmap.isEmpty()))) {
			genericQueryTab.reloadTransactionOfferings();
		} else {
            // TODO - this probablt needs to change to true
			genericQueryTab.redisplay(false);
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
			genericQueryTab.tabTopPanel.remove(genericQueryTab.resultPanel);
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
					genericQueryTab.setCommonSiteSpec(new SiteSpec(b.getText(), genericQueryTab.selectByActor, genericQueryTab.getCommonSiteSpec(), ClientUtils.INSTANCE.getCurrentTestSession()));
					return genericQueryTab.getCommonSiteSpec();
				}
			}
		} else {   // Select by transaction (used in GetDocuments tab)
			if (genericQueryTab.transactionSelectionManager != null) {
				SiteSpec siteSpec = genericQueryTab.transactionSelectionManager.generateSiteSpec();
				siteSpec.isTls = this.genericQueryTab.doTls.getValue();
				genericQueryTab.setCommonSiteSpec(siteSpec);
				return siteSpec;
			}
		}

		return null;
	}

	public String getPatientId() { return genericQueryTab.getCommonPatientId(); }


}