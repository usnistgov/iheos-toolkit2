package gov.nist.toolkit.xdstools2.client;

import gov.nist.toolkit.actortransaction.client.ATFactory.TransactionType;

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
		List<TransactionType> trans = new ArrayList<TransactionType>();
		
		void add(TransactionType tt) {
			trans.add(tt);
		}
	}
	
	// This is overkill, only one coupling
	// can be added
	List<And> couplings = new ArrayList<And>(); 
	
	public boolean hasCouplings() {
		return couplings.size() > 0;
	}
	
	public void add(TransactionType t1, TransactionType t2) {
		And a = new And();
		a.add(t1);
		a.add(t2);
		couplings.add(a);
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
	
}
