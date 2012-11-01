package gov.nist.toolkit.actorfactory;

import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.actortransaction.client.ATFactory.ActorType;
import gov.nist.toolkit.actortransaction.client.ATFactory.ParamType;
import gov.nist.toolkit.actortransaction.client.ATFactory.TransactionType;
import gov.nist.toolkit.sitemanagement.client.Site;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class DirectActorFactory extends ActorFactory {

	static final List<TransactionType> incomingTransactions = 
			Arrays.asList(
					TransactionType.DIRECT
					);
	
	public List<SimulatorConfig> buildNew(SimManager simm, boolean configureBase) {
		ActorType actorType = ActorType.DIRECT_SERVER;
		SimulatorConfig sc;
		if (configureBase)
			sc = configureBaseElements(actorType);
		else
			sc = new SimulatorConfig();
		
		File codesFile = simm.getCodesFile();
		addEditableConfig(sc, codesEnvironment, ParamType.SELECTION, codesFile.toString());


		return asList(sc);
	}
	
	public Site getActorSite(SimulatorConfig asc, Site site) {
		String siteName = asc.getDefaultName();
		
		if (site == null)
			site = new Site(siteName);
		return site;
	}
	 
	protected void verifyActorConfigurationOptions(SimulatorConfig config) {
	}
	
	public List<TransactionType> getIncomingTransactions() {
		return incomingTransactions;
	}


}
