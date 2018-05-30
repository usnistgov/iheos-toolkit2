package gov.nist.toolkit.xdstools2.client.tabs.GatewayTestsTabs;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.RadioButton;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.xdstools2.client.CoupledTransactions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class TransactionSelectionManager {
	private CoupledTransactions couplings;

	class RbSite {
		TransactionType tt; // Unique (fits into perTransRB Map below
		Site site; // Many sites per TransactionType.
		RadioButton rb;
	}
	private Map<TransactionType, List<RbSite>> perTransRB = new HashMap<TransactionType, List<RbSite>>();
		
	TransactionSelectionManager(CoupledTransactions couplings, TransactionOptions options) {
		this.couplings = couplings;
	}
	
	void addTransactionType(TransactionType tt, List<Site> sites) {
		ClickHandler ch= new ActorClickHandlerByTransaction(this, tt);

		List<RbSite> rbSites = new ArrayList<RbSite>();
		for (Site site : sites) {
			String siteName = site.getName();
			RadioButton rb = new RadioButton(tt.getName(), siteName);
			rb.addClickHandler(ch);
			RbSite rbs = new RbSite();
			rbs.tt = tt;
			rbs.site = site;
			rbs.rb = rb;
			rbSites.add(rbs);
		}
		perTransRB.put(tt, rbSites);
	}
	
	List<RadioButton> getRadioButtons(TransactionType tt) {
		ArrayList<RadioButton> buttons = new ArrayList<RadioButton>();
		List<RbSite> rbSites = perTransRB.get(tt);
		if (rbSites == null)
			return buttons;
		for (RbSite rbs : rbSites) {
			buttons.add(rbs.rb);
		}
		return buttons;
	}

	void adjustForCurrentSelection(TransactionType selectedTransactionType) {
		if (!couplings.contains(selectedTransactionType)) 
			turnOffButtonsNotIn(selectedTransactionType);
		if (selectedTransactionType == couplings.from())
			turnOffButtonsNotIn(selectedTransactionType);
		if (selectedTransactionType == couplings.to())
			turnOffButtonsNotIn(selectedTransactionType, couplings.from());
	}

	void turnOffButtonsNotIn(TransactionType tt) {
		turnOffButtonsNotIn(tt, null);
	}
	
	void turnOffButtonsNotIn(TransactionType tt1, TransactionType tt2) {  // Not in tt1 or tt2
		for (TransactionType t : perTransRB.keySet()) {
			if (t == tt1)
				continue;
			if (t == tt2)
				continue;
			for (RbSite rbs : perTransRB.get(t)) {
				rbs.rb.setValue(false);
			}
		}
	}
}
