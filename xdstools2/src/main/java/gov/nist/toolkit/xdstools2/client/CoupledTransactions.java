package gov.nist.toolkit.xdstools2.client;

import com.google.gwt.user.client.ui.HTML;
import gov.nist.toolkit.configDatatypes.client.TransactionType;

import java.util.ArrayList;
import java.util.List;

/**
 * CoupledTransactions are TransactionTypes that are linked for 
 * a particular query type. Linked (coupled) transactions require that if
 * a site is selected for one of the linked transactions then a 
 * site must be selected for the other transaction as well. The typical
 * use is a secondary query (has no patient id parameter) targeting an Initiating Gateway.
 * For this to be runable a Responding Gateway must also be chosen.  This 
 * is handled operationally by primary queries returning homeCommunityIds.
 * This GUI structure simulates this pre-selection allowing to happen with 
 * GUI selections.
 * @author bill
 *
 */
public class CoupledTransactions {
	public class And {
		String primaryId;
		String secondaryId;
		HTML beginSelectionInstructions;
		String beginSelectionFormattedString;
		HTML endSelectionInstruction;
		String endSelectionFormattedString;
		List<TransactionType> trans = new ArrayList<TransactionType>();
		
		void add(TransactionType tt) {
			trans.add(tt);
		}

		public HTML getBeginSelectionInstructions() {
			return beginSelectionInstructions;
		}

		public void setBeginSelectionInstructions(HTML beginSelectionInstructions) {
			this.beginSelectionInstructions = beginSelectionInstructions;
		}

		public HTML getEndSelectionInstruction() {
			return endSelectionInstruction;
		}

		public void setEndSelectionInstruction(HTML endSelectionInstruction) {
			this.endSelectionInstruction = endSelectionInstruction;
		}

		public String getPrimaryId() {
			return primaryId;
		}

		public void setPrimaryId(String primaryId) {
			this.primaryId = primaryId;
		}

		public String getSecondaryId() {
			return secondaryId;
		}

		public void setSecondaryId(String secondaryId) {
			this.secondaryId = secondaryId;
		}

		public String getEndSelectionFormattedString() {
			return endSelectionFormattedString;
		}

		public void setEndSelectionFormattedString(String endSelectionFormattedString) {
			this.endSelectionFormattedString = endSelectionFormattedString;
		}
	}
	
	// This is overkill, only one coupling
	// can be added
	List<And> couplings = new ArrayList<And>(); 
	
	public boolean hasCouplings() {
		return couplings.size() > 0;
	}
	
	public And add(TransactionType t1, TransactionType t2) {
		And a = new And();
		a.add(t1);
		a.add(t2);
		couplings.add(a);
		return a;
	}

	public And add(TransactionType t1, TransactionType t2, HTML beginSelectionInstruction) {
		And a = add(t1, t2);
		a.setBeginSelectionInstructions(beginSelectionInstruction);
		return a;
	}
	public And add(TransactionType t1, TransactionType t2, HTML beginSelectionInstruction, String formattedString) {
	    And a = add(t1, t2, beginSelectionInstruction);
	    a.setEndSelectionInstruction(new HTML(""));
		a.setEndSelectionFormattedString(formattedString);
		return a;
	}
	
	public boolean isCoupled(TransactionType tt1, TransactionType tt2) {
		for (And a : couplings) {
			if (a.trans.contains(tt1) && a.trans.contains(tt2))
				return true;
		}
		return false;
	}
	
	public TransactionType from() {
		try {
			return couplings.get(0).trans.get(0);
		} catch (Exception e) {
			return null;
		}
	}
	
	public TransactionType to() {
		try {
			return couplings.get(0).trans.get(1);
		} catch (Exception e) {
			return null;
		}
	}
	
	public boolean contains(TransactionType tt) {
		if (from() == tt || to() == tt)
			return true;
		return false;
	}

	public And getCoupling() {
	    if (hasCouplings())
			return couplings.get(0);
	    return null;
	}

}
