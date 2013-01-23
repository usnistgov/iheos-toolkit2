package gov.nist.toolkit.xdstools2.server.gazelle.actorConfig;

import gov.nist.toolkit.actortransaction.client.ATFactory.ActorType;
import gov.nist.toolkit.actortransaction.client.ATFactory.TransactionType;
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

		actorType = ActorType.findActor(actor);
		trans = TransactionType.find(actorType, transId);

		repUid = configToXml.oConfigs.getRepUid(sysName);
		oddsRepUid = configToXml.oConfigs.getODDSRepUid(sysName);
		home = configToXml.oConfigs.getHome(sysName);
		
//		if (sysName.indexOf("SER") != -1) {
//			System.out.println(entry.line);
//			System.out.println(this);
//		}
	}
	
	public String toString() {
		return "[SysConfig: sysName=" + sysName + " actor=" + actor + " secure=" + isSecure + " approved=" + isApproved + " url=" + entry.getURL() + " transId=" + transId + "]";
	}

	public boolean equals(SysConfig c) {
		return isSecure == c.isSecure &&
				trans == c.trans &&
				isAsync == c.isAsync;
	}

}