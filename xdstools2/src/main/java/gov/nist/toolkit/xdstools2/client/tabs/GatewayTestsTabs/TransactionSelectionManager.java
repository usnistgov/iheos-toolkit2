package gov.nist.toolkit.xdstools2.client.tabs.GatewayTestsTabs;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.RadioButton;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.CoupledTransactions;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionSelectionManager {
	CoupledTransactions couplings;
    TransactionOptions options;

	class RbSite {
		TransactionType tt; // Unique (fits into perTransRB Map below
		Site site; // Many sites per TransactionType.
		RadioButton rb;
	}
	Map<TransactionType, List<RbSite>> perTransRB = new HashMap<TransactionType, List<RbSite>>();
		
	public TransactionSelectionManager(CoupledTransactions couplings, TransactionOptions options) {
		this.couplings = couplings;
        this.options = options;
	}
	
	public void addTransactionType(TransactionType tt, List<Site> sites) {
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
	
	public List<RadioButton> getRadioButtons(TransactionType tt) {
		ArrayList<RadioButton> buttons = new ArrayList<RadioButton>();
		List<RbSite> rbSites = perTransRB.get(tt);
		if (rbSites == null)
			return buttons;
		for (RbSite rbs : rbSites) {
			buttons.add(rbs.rb);
		}
		return buttons;
	}

	public void adjustForCurrentSelection(TransactionType selectedTransactionType) {
		if (!couplings.contains(selectedTransactionType)) 
			turnOffButtonsNotIn(selectedTransactionType);
		if (selectedTransactionType == couplings.from())
			turnOffButtonsNotIn(selectedTransactionType);
		if (selectedTransactionType == couplings.to())
			turnOffButtonsNotIn(selectedTransactionType, couplings.from());
	}
	
	public String verifySelection() {  // ActorClickHandlerByTransaction handles most of this ...
		List<TransactionType> selections = transactionTypes(selections2());
		if (selections.isEmpty())
			return "Site must be selected";
		if (!couplings.hasCouplings())
			return null;
		if (selections.size() == 1)
			if (couplings.from() == selections.get(0))
				return ActorType.getActorType(couplings.from()).getName() + " is selected so " + ActorType.getActorType(couplings.to()).getName() + " must be selected";
		return null;
	}

	public SiteSpec generateSiteSpec() {
		SiteSpec ss = new SiteSpec(ClientUtils.INSTANCE.getCurrentTestSession());
		List<RbSite> selections = selections2();
		
		if (selections.size() == 1) {
			RbSite r = selections.get(0);
			ss.actorType = ActorType.getActorType(r.tt);
			ss.name = r.site.getName();
			ss.homeId = r.site.home;
			ss.isSaml = options.isSaml();
			if (ss.homeId != null)
				ss.homeName = r.site.getName();
			ss.isTls = options.isTls();
			return ss;		
		}
		if (selections.size() == 2) { // First is IG second is RG
			// Return site info from IG with home of RG
			RbSite r = selections.get(0);
			ss.actorType = ActorType.getActorType(r.tt);
			ss.name = r.site.getName();
			RbSite r2 = selections.get(1);
			ss.isSaml = options.isSaml();
			ss.homeId = r2.site.home;
			if (ss.homeId != null)
				ss.homeName = r2.site.getName();
			ss.isTls = options.isTls();
			return ss;		
		}
		return null;  // Should never happen given the checking that comes earlier.
	}
	
	List<RbSite> selections2() {
		List<RbSite> selections = new ArrayList<RbSite>();
		for (TransactionType t : perTransRB.keySet()) {
			for (RbSite rbs : perTransRB.get(t)) {
				RadioButton rb = rbs.rb;
				if (rb.getValue()) {
					selections.add(rbs);
					break;
				}
			}
		}
		return selections;
	}
	
	List<TransactionType> transactionTypes(List<RbSite> rbs) {
		List<TransactionType> tts = new ArrayList<TransactionType>();
		for (RbSite r : rbs)
			tts.add(r.tt);
		return tts;
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
