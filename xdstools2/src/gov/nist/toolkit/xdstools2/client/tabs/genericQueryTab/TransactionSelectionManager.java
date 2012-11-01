package gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab;

import gov.nist.toolkit.actortransaction.client.ATFactory.ActorType;
import gov.nist.toolkit.actortransaction.client.ATFactory.TransactionType;
import gov.nist.toolkit.results.client.SiteSpec;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.xdstools2.client.CoupledTransactions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.RadioButton;

public class TransactionSelectionManager {
	CoupledTransactions couplings;
//	boolean tls;
	GenericQueryTab genericQueryTab;
//	Map<TransactionType, List<RadioButton>> perTransTypeRadioButtons; 
	
	class RbSite {
		TransactionType tt; // Unique (fits into perTransRB Map below
		Site site; // Many sites per TransactionType.
		RadioButton rb;
	}
	Map<TransactionType, List<RbSite>> perTransRB = new HashMap<TransactionType, List<RbSite>>();
		
	public TransactionSelectionManager(CoupledTransactions couplings, GenericQueryTab genericQueryTab) {
		this.couplings = couplings;
		this.genericQueryTab = genericQueryTab;
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

//	public TransactionSelectionManager(CoupledTransactions couplings,
//			Map<TransactionType, List<RadioButton>> perTransTypeRadioButtons) {
//		this.couplings = couplings;
//		this.perTransTypeRadioButtons = perTransTypeRadioButtons;
//	}
	
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
	
//	List<TransactionType> selections() {
//		List<TransactionType> selections = new ArrayList<TransactionType>();
//		for (TransactionType t : perTransTypeRadioButtons.keySet()) {
//			for (RadioButton rb : perTransTypeRadioButtons.get(t)) {
//				if (rb.getValue()) {
//					selections.add(t);
//					break;
//				}
//			}
//		}
//		return selections;
//	}
	
	public SiteSpec generateSiteSpec() {
		SiteSpec ss = new SiteSpec();
		List<RbSite> selections = selections2();
		
		if (selections.size() == 1) {
			RbSite r = selections.get(0);
			ss.actorType = ActorType.getActorType(r.tt);
			ss.name = r.site.getName();
			ss.homeId = r.site.home;
			ss.isSaml = genericQueryTab.isSaml();
			if (ss.homeId != null)
				ss.homeName = r.site.getName();
			ss.isTls = genericQueryTab.isTLS();
			return ss;		
		}
		if (selections.size() == 2) { // First is IG second is RG
			// Return site info from IG with home of RG
			RbSite r = selections.get(0);
			ss.actorType = ActorType.getActorType(r.tt);
			ss.name = r.site.getName();
			RbSite r2 = selections.get(1);
			ss.isSaml = genericQueryTab.isSaml();
			ss.homeId = r2.site.home;
			if (ss.homeId != null)
				ss.homeName = r2.site.getName();
			ss.isTls = genericQueryTab.isTLS();
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
