package gov.nist.toolkit.server.server.gazelle.actorConfig;

import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.sitemanagement.client.TransactionBean;
import gov.nist.toolkit.sitemanagement.client.TransactionBean.RepositoryType;

class SysConfig {
	/**
	 * 
	 */
	private final ConfigToXml configToXml;
	String sysName;
	boolean isApproved;
	String url;
	boolean isSecure;
	String actor;
	ActorType actorType;
	TransactionType trans;
	boolean isAsync = false;
	String home;
	String repUid;
	String oddsRepUid;
	String transId;
	
	GazelleEntry entry;

	public TransactionBean getRepositoryBean() {
			return new TransactionBean(
					repUid,
					RepositoryType.REPOSITORY,
					url,
					isSecure,
					isAsync);
	}

	public TransactionBean getTransactionBean() {
		return new TransactionBean(
				trans,
				RepositoryType.NONE,
				actorType,
				url,
				isSecure,
				isAsync);
	}



//	String buildUrl(GazelleEntry entry) {
//		boolean secure = entry.getIsSecure();
//		return 
//				"http" +
//				((secure) ? "s" : "") +
//				"://" +
//				entry.getHost() + 
//				":" + 
//				(secure ? entry.getPortSecured() : entry.getPort()) +
//				"/" + entry.getURL();
//	}

	SysConfig(ConfigToXml configToXml, GazelleEntry entry) {
		this.entry = entry;
		this.configToXml = configToXml;
		sysName = entry.getSystem();
		isApproved = entry.getIsApproved();
		url = entry.getURL();
		isSecure = entry.getIsSecure();
		actor = entry.getActor();
		transId = entry.getTransId();
		isAsync = entry.isAsync();

		System.out.println(entry.line);
	}

	public boolean eval(ConfigToXml configToXml) {
		actorType = ActorType.findActor(actor);
		if (actorType == null) {
			System.out.println("Actor Type [" + actor + "] not understood");
			return false;
		}
		trans = actorType.getTransaction(transId);

		repUid = configToXml.oConfigs.getRepUid(sysName);
		oddsRepUid = configToXml.oConfigs.getODDSRepUid(sysName);
		home = configToXml.oConfigs.getHome(sysName);

		System.out.println(this);
		return true;
	}

	public String toString() {
		return "[SysConfig: sysName=" + sysName + " actor=" + actor + " secure=" + isSecure + 
				" approved=" + isApproved + " " + 
				" repUid=" + repUid +
				" home=" + home + 
				"url=" + entry.getURL() + 
				" transId=" + transId + 
				"]";
	}

	public boolean equals(SysConfig c) {
		return isSecure == c.isSecure &&
				trans == c.trans &&
				isAsync == c.isAsync;
	}

}