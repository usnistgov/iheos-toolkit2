package gov.nist.toolkit.actorfactory;

import gov.nist.toolkit.actorfactory.client.SimId;
import gov.nist.toolkit.actorfactory.client.Simulator;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.actortransaction.client.TransactionType;
import gov.nist.toolkit.sitemanagement.client.Site;

import java.util.List;

/**
 * This class exists only to expose methods in ActorFactory such as
 * the buildNewSimulator method which
 * would otherwise be buried in the abstract class ActorFactory.
 * @author bill
 *
 */
public class GenericSimulatorFactory extends AbstractActorFactory {
	SimId newID = null;
	@SuppressWarnings("unused")
	private GenericSimulatorFactory() {
	}
	
	public GenericSimulatorFactory(SimManager simManager) {
		super(simManager);
	}

	// ActorFactory needs to be directly
	public Simulator buildNewSimulator(SimManager simm, String simtype, SimId simID) throws Exception {
		return buildNewSimulator(simm, simtype, simID, true);
	}

	@Override
	protected Simulator buildNew(SimManager simm, SimId newID, boolean configureBase) throws Exception {
		this.newID = newID;
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected void verifyActorConfigurationOptions(SimulatorConfig config) {
		// TODO Auto-generated method stub

	}

	@Override
	public Site getActorSite(SimulatorConfig asc, Site site) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TransactionType> getIncomingTransactions() {
		// TODO Auto-generated method stub
		return null;
	}
	

}
