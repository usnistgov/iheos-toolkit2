package gov.nist.toolkit.actorfactory;

import gov.nist.toolkit.actorfactory.client.Simulator;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.actortransaction.client.ATFactory.ActorType;
import gov.nist.toolkit.actortransaction.client.ATFactory.ParamType;
import gov.nist.toolkit.actortransaction.client.ATFactory.TransactionType;
import gov.nist.toolkit.envSetting.EnvSetting;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.xdsexception.EnvironmentNotSelectedException;
import gov.nist.toolkit.xdsexception.NoSessionException;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class DirectActorFactory extends ActorFactory {

	static final List<TransactionType> incomingTransactions = 
			Arrays.asList(
					TransactionType.DIRECT
					);
	
	public Simulator buildNew(SimManager simm, boolean configureBase) throws EnvironmentNotSelectedException, NoSessionException {
		ActorType actorType = ActorType.DIRECT_SERVER;
		SimulatorConfig sc;
		if (configureBase)
			sc = configureBaseElements(actorType);
		else
			sc = new SimulatorConfig();
		
		File codesFile = EnvSetting.getEnvSetting(simm.sessionId).getCodesFile();
		addEditableConfig(sc, codesEnvironment, ParamType.SELECTION, codesFile.toString());


		return new Simulator(sc);
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
