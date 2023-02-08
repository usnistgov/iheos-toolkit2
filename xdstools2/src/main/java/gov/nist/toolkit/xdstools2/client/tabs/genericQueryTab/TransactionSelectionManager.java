package gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.RadioButton;
import gov.nist.toolkit.actortransaction.shared.ActorType;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.client.TkActorNotFoundException;
import gov.nist.toolkit.xdstools2.client.CoupledTransactions;
import gov.nist.toolkit.xdstools2.client.ObjectSort;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionSelectionManager {
	CoupledTransactions couplings;
//	boolean tls;
	GenericQueryTab genericQueryTab;
//	Map<TransactionType, List<RadioButton>> perTransTypeRadioButtons;
//	static ArrayList<String>
	final int idHashCode = System.identityHashCode(this);

	class ActorTran {
		ActorType at;
		TransactionType tt; // Unique (fits into perTransRB Map below

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			ActorTran actorTran = (ActorTran) o;

			if (!at.equals(actorTran.at)) return false;
			return (tt.equals(actorTran.tt));
		}

		@Override
		public int hashCode() {
			int result = at != null ? at.hashCode() : 0;
			result = 31 * result + (tt != null ? tt.hashCode() : 0);
			return result;
		}

		public ActorTran(ActorType at, TransactionType tt) {
			this.at = at;
			this.tt = tt;

		}
	}
	
	class RbSite {
	    ActorTran actorTran;
		Site site; // Many sites per TransactionType.
		RadioButton rb;

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			RbSite rbSite = (RbSite) o;

			if (actorTran != null ? !actorTran.equals(rbSite.actorTran) : rbSite.actorTran != null) return false;
			return  (site != null ? site.equals(rbSite.site) : false);
		}

		@Override
		public int hashCode() {
			int result = actorTran != null ? actorTran.hashCode() : 0;
			result = 31 * result + (site != null ? site.hashCode() : 0);
			result = 31 * result + (rb != null ? rb.hashCode() : 0);
			return result;
		}
	}
	Map<ActorTran, List<RbSite>> perTransRB = new HashMap<ActorTran, List<RbSite>>();
		
	public TransactionSelectionManager(CoupledTransactions couplings, GenericQueryTab genericQueryTab) {
		this.couplings = couplings;
		this.genericQueryTab = genericQueryTab;
	}

	/*
	public void selectSite(SiteSpec siteSpec) {
		for (List<RbSite> rbSites : perTransRB.values()) {
			for (RbSite rbSite : rbSites) {
				if (rbSite.site.getName().equals(siteSpec.getName())) {
					rbSite.rb.setValue(true);
					return;
				}
			}
		}
	}
	*/
	
	public void addTransactionType(ActorType at, TransactionType tt, List<Site> sites) {
		ClickHandler ch= new ActorClickHandlerByTransaction(this, tt);

		ActorTran actorTran = new ActorTran(at,tt);
		if (perTransRB.get(actorTran)!=null) {
			perTransRB.get(actorTran).clear();
		}

		for (Site site : sites) {
			String siteName = site.getName();
			String radioGroupName = "rbGroup_" + idHashCode + "_" + genericQueryTab.getWindowShortName() + "_" + tt.getName() ;
			radioGroupName = radioGroupName.replaceAll(" ","");
			RadioButton rb = new RadioButton(radioGroupName, siteName);
			rb.addClickHandler(ch);

			RbSite rbs = new RbSite();

			rbs.site = site;
			rbs.rb = rb;
			actorTran = new ActorTran(at,tt);
            rbs.actorTran = actorTran;
			if (!perTransRB.containsKey(actorTran)) {
			   perTransRB.put(actorTran, new ArrayList<RbSite>());
            }
			List<RbSite> rbSites1 = perTransRB.get(actorTran);
			rbSites1.add(rbs);
		}
	}
	
	public List<RadioButton> getRadioButtons(ActorType at, TransactionType tt) {
		ArrayList<RadioButton> buttons = new ArrayList<RadioButton>();
		List<RbSite> rbSites = perTransRB.get(new ActorTran(at, tt));
		new ObjectSort().sort(rbSites, new Comparator<RbSite>() {
			public int compare(RbSite ra, RbSite rb) {
				return ra.site.getName().compareTo(rb.site.getName());
			}
		});
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
		if (!couplings.contains(selectedTransactionType)) {
			turnOffButtonsNotIn(selectedTransactionType);
		}
		if (selectedTransactionType == couplings.from()) {
			turnOffButtonsNotIn(selectedTransactionType);
		}
		if (selectedTransactionType == couplings.to()) {
			turnOffButtonsNotIn(selectedTransactionType, couplings.from());
		}
	}

	public void adjustInstructionMessage(TransactionType selectedTransactionType) {
		if (!couplings.contains(selectedTransactionType)) {
			if (couplings.getCoupling().getBeginSelectionInstructions()!=null)
				couplings.getCoupling().getBeginSelectionInstructions().setVisible(false);
				couplings.getCoupling().getEndSelectionInstruction().setVisible(false);
		}
		if (selectedTransactionType == couplings.from()) {
			if (couplings.getCoupling().getBeginSelectionInstructions()!=null) {
				couplings.getCoupling().getEndSelectionInstruction().setVisible(false);
				couplings.getCoupling().getBeginSelectionInstructions().setVisible(true);
			}

		}
		if (selectedTransactionType == couplings.to()) {
			if (couplings.getCoupling().getPrimaryId()!=null && couplings.getCoupling().getSecondaryId()!=null) {
				couplings.getCoupling().getBeginSelectionInstructions().setVisible(false);
				String text = couplings.getCoupling().getEndSelectionFormattedString();
				text = text.replaceFirst("%s", couplings.getCoupling().getPrimaryId());
				text = text.replaceFirst("%s", couplings.getCoupling().getSecondaryId());
				couplings.getCoupling().getEndSelectionInstruction().setHTML(text);
				couplings.getCoupling().getEndSelectionInstruction().setVisible(true);

			}

		}
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
//			for (RadioButton rb : perTransTypeRadioButtons.getRetrievedDocumentsModel(t)) {
//				if (rb.getValue()) {
//					selections.addTest(t);
//					break;
//				}
//			}
//		}
//		return selections;
//	}
	
	public SiteSpec generateSiteSpec() {
		SiteSpec ss = new SiteSpec(ClientUtils.INSTANCE.getCurrentTestSession());

		if (genericQueryTab.samlEnabled)
			ss.setStsAssertion(genericQueryTab.samlAssertion);

		List<RbSite> selections = selections2();
		
		if (selections.size() == 1) {
			RbSite r = selections.get(0);
			ss.actorType = ActorType.getActorType(r.actorTran.tt);
			try {
				ss.actorType = r.site.determineActorTypeByTransactionsInSite(r.actorTran.tt);
			} catch (TkActorNotFoundException tke) {
				GWT.log("TransactionSelectionManager error: " + tke.toString());
			}
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
			ss.actorType = ActorType.getActorType(r.actorTran.tt);
			try {
				ss.actorType = r.site.determineActorTypeByTransactionsInSite(r.actorTran.tt);
			} catch (TkActorNotFoundException tke) {
				GWT.log("TransactionSelectionManager error: " + tke.toString());
			}
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
		for (ActorTran at : perTransRB.keySet()) {
			for (RbSite rbs : perTransRB.get(at)) {
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
			tts.add(r.actorTran.tt);
		return tts;
	}
	
	void turnOffButtonsNotIn(TransactionType tt) {
		turnOffButtonsNotIn(tt, null);
	}
	
	void turnOffButtonsNotIn(TransactionType tt1, TransactionType tt2) {  // Not in tt1 or tt2
		for (ActorTran at : perTransRB.keySet()) {
			if (at.tt == tt1)
				continue;
			if (at.tt == tt2)
				continue;
			for (RbSite rbs : perTransRB.get(at)) {
				rbs.rb.setValue(false);
			}
		}
	}

	public List<RbSite> getPerTransRB(ActorType at, TransactionType tt) {
		return perTransRB.get(new ActorTran(at,tt));
	}
}
