package gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.RadioButton;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.xdstools2.client.CoupledTransactions;

import java.util.List;

class ActorClickHandlerByTransaction implements ClickHandler {
	/**
	 * 
	 */
	TransactionSelectionManager transactionSelectionManager;
	TransactionType selectedTransactionType;

//	public ActorClickHandlerByTransaction(CoupledTransactions couplings, Map<TransactionType, List<RadioButton>> perTransTypeRadioButtons, TransactionType tt) {
//		this.selectedTransactionType = tt;  // A site has been selected for this TransactionType
//		transactionSelectionManager = new TransactionSelectionManager(couplings, perTransTypeRadioButtons);
//	}
	
	public ActorClickHandlerByTransaction(TransactionSelectionManager transactionSelectionManager, TransactionType tt) {
		this.transactionSelectionManager = transactionSelectionManager;
		this.selectedTransactionType = tt;
	}

	// The logic here is controlled by the contents of queyBoilerplate.couplings.
	// Couplings defines a pair of TransactionTypes
	// 1) If the new TransactionType selected is not present in couplings then
	// all other selections are cleared.
	// 2) If the new TransactionType selected matches couplings.from()

	public void onClick(ClickEvent event) {
		// A site has been selected for selectedTransaction. The RadioButton
		// itself will turn off all other buttons for sites for this
		// TransactionType. The logic below determines if another
		// TransactionType can also be selected. If it is selected and is
		// illegal then it is turned off.

		transactionSelectionManager.adjustForCurrentSelection(selectedTransactionType);

		CoupledTransactions couplings = transactionSelectionManager.couplings;
		if (couplings!=null && couplings.hasCouplings()) {
			Object object = event.getSource();
			if (object!=null && object instanceof RadioButton) {
				List<TransactionSelectionManager.RbSite> rbSites = transactionSelectionManager.selections2();
				if (rbSites.size()== 2) {
					 if (couplings.from() == rbSites.get(0).actorTran.tt && couplings.to() == rbSites.get(1).actorTran.tt) {
							 couplings.getCoupling().setPrimaryId(rbSites.get(0).site.getSiteName());
							 couplings.getCoupling().setSecondaryId(rbSites.get(1).site.getSiteName());
					 }
				}
			}

			transactionSelectionManager.adjustInstructionMessage(selectedTransactionType);
		}
	}
	

}