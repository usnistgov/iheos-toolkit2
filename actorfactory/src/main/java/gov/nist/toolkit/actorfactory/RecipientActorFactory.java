package gov.nist.toolkit.actorfactory;

import gov.nist.toolkit.actorfactory.client.Simulator;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.actortransaction.client.ATFactory.ActorType;
import gov.nist.toolkit.actortransaction.client.ATFactory.ParamType;
import gov.nist.toolkit.actortransaction.client.ATFactory.TransactionType;
import gov.nist.toolkit.envSetting.EnvSetting;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.sitemanagement.client.TransactionBean;
import gov.nist.toolkit.sitemanagement.client.TransactionBean.RepositoryType;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.xdsexception.EnvironmentNotSelectedException;
import gov.nist.toolkit.xdsexception.NoSessionException;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class RecipientActorFactory  extends ActorFactory {

	static final List<TransactionType> incomingTransactions = 
		Arrays.asList(TransactionType.PROVIDE_AND_REGISTER);


	protected Simulator buildNew(SimManager simm, boolean configureBase) throws EnvironmentNotSelectedException, NoSessionException {
		ActorType actorType = ActorType.DOCUMENT_RECIPIENT;
		SimulatorConfig sc;
		if (configureBase)
			sc = configureBaseElements(actorType);
		else 
			sc = new SimulatorConfig();
		if (sc.getValidationContext() == null)
			sc.setValidationContext(new ValidationContext());
		

		File codesFile = EnvSetting.getEnvSetting(simm.sessionId).getCodesFile();

		addEditableConfig(sc, codesEnvironment, ParamType.SELECTION, codesFile.toString());
		
		addEditableEndpoint(sc, pnrEndpoint, actorType, TransactionType.XDR_PROVIDE_AND_REGISTER, false);
		addEditableEndpoint(sc, pnrTlsEndpoint, actorType, TransactionType.XDR_PROVIDE_AND_REGISTER, true);

		return new Simulator(sc);
	}

	protected void verifyActorConfigurationOptions(SimulatorConfig sc) {

	}

	public Site getActorSite(SimulatorConfig sc, Site site) {
		String siteName = sc.getDefaultName();

		if (site == null)
			site = new Site(siteName);

		boolean isAsync = false;

		site.addTransaction(new TransactionBean(
				TransactionType.XDR_PROVIDE_AND_REGISTER.getCode(),
				RepositoryType.NONE,
				sc.get(pnrEndpoint).asString(), 
				false, 
				isAsync));
		site.addTransaction(new TransactionBean(
				TransactionType.XDR_PROVIDE_AND_REGISTER.getCode(),
				RepositoryType.NONE,
				sc.get(pnrTlsEndpoint).asString(), 
				true, 
				isAsync));

		return site;
	}

	public List<TransactionType> getIncomingTransactions() {
		return incomingTransactions;
	}


}
