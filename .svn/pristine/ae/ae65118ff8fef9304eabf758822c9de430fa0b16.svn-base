package gov.nist.toolkit.sitemanagement.client;

import gov.nist.toolkit.actortransaction.client.ATFactory;
import gov.nist.toolkit.actortransaction.client.ATFactory.ActorType;
import gov.nist.toolkit.actortransaction.client.ATFactory.TransactionType;
import gov.nist.toolkit.sitemanagement.client.TransactionBean.RepositoryType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.rpc.IsSerializable;

// Transaction names are listed in TransactionCollection.java


public class Site  implements IsSerializable, Serializable {
	private static final long serialVersionUID = 1L;
	private String name = null;
	TransactionCollection transactions = new TransactionCollection(false);
	// There can be only one ODDS repository
	// and one XDS.b repository in a site.
	// An XDS.b Repository and a ODDS Repository
	// can have the same repositoryUniqueId and endpoint. But
	// they require two entries to identify them.
	TransactionCollection repositories = new TransactionCollection(true);
	public String home = null;
	public String pifHost = null;
	public String pifPort = null;

	public String pidAllocateURI = null;
	transient public boolean changed = false;
	
	public boolean equals(Site s) {
		return
				((name == null) ? s.name == null : name.equals(s.name)) &&
				((home == null) ? s.home == null : home.equals(s.home)) &&
				((pifHost == null) ? s.pifHost == null : pifHost.equals(s.pifHost)) &&
				((pifPort == null) ? s.pifPort == null : pifPort.equals(s.pifPort)) &&
				((pidAllocateURI == null) ? s.pidAllocateURI == null : pidAllocateURI.equals(s.pidAllocateURI)) &&
				transactions.equals(s.transactions) &&
				repositories.equals(s.repositories);
				
	}
	
	public TransactionCollection transactions() {
		return transactions;
	}
	
	public TransactionCollection repositories() {
		return repositories;
	}
	
	public boolean validate() {
		StringBuffer buf = new StringBuffer();
		validate(buf);
		return buf.length() == 0;
	}
	
	public void validate(StringBuffer buf) {
		
		for (TransactionBean b : transactions.transactions) {
			for (TransactionBean c : transactions.transactions) {
				if (b == c)
					continue;
				if (b.hasSameIndex(c) && !b.equals(c)) {
					buf.append("Site ").append(name).append(" has a conflict:\n");
					buf.append("\tThese entries conflict\n");
					buf.append("\t\t").append(b).append("\n");
					buf.append("\t\t").append(c).append("\n");
				}
			}
		}
		
		for (TransactionBean b : repositories.transactions) {
			if (ActorType.REPOSITORY.equals(b.actorType))
			for (TransactionBean c : repositories.transactions) {
				if (b == c)
					continue;
				if (b.hasSameIndex(c) && !b.equals(c)) {
					buf.append("Site ").append(name).append(" has a conflict:\n");
					buf.append("\tThese entries conflict\n");
					buf.append("\t\t").append(b).append("\n");
					buf.append("\t\t").append(c).append("\n");
				}
			}
		}
		
		// All Repository transactions must be for the same repositoryUniqueId
		Set<String> repUids = repositoryUniqueIds();
		if (repUids.size() > 1) {
			buf.append("Site ").append(name).append(" contains more than one repositoryUniqueId. ")
			.append("A site can define a single Document Repository.");
		}		
	}
	
	

	public void cleanup() {
//		transactions.removeEmptyEndpoints();
//		repositories.removeEmptyEndpoints();
		repositories.removeEmptyNames();
		transactions.fixTlsEndpoints();
		repositories.fixTlsEndpoints();
	}
	
	public int size() {
		return transactions.size() + repositories.size();
	}
	
	public void addTransaction(String transactionName, String endpoint, boolean isSecure, boolean isAsync) {
		addTransaction(new TransactionBean(transactionName, RepositoryType.NONE, endpoint, isSecure, isAsync));
	}

	public void addTransaction(TransactionBean transbean) {
		transactions.addTransaction(transbean);
	}
	
	public String getPidAllocateURI() {
		return pidAllocateURI;
	}
	
	public boolean hasActor(ActorType actorType) {
		return transactions.hasActor(actorType);
	}
	
	public boolean hasTransaction(TransactionType tt) {
		return transactions.hasTransaction(tt);
	}

	/**
	 * Get Repository Bean
	 * @param isSecure
	 * @return TransactionBean
	 */
	public TransactionBean getRepositoryBean(boolean isSecure) {
		for (TransactionBean b : repositories.transactions) {
			if (b.repositoryType == RepositoryType.REPOSITORY && b.isSecure == isSecure)
				return b;
		}
		return null;
	}

	public void addRepository(TransactionBean transbean) {
		repositories.addTransaction(transbean);
	}
	
	public void addRepository(String repositoryUniqueId, RepositoryType repositoryType, String endpoint, boolean isSecure, boolean isAsync) {
		TransactionBean bean = new TransactionBean(repositoryUniqueId, repositoryType, endpoint, isSecure, isAsync);
		addRepository(bean);
	}

	public String getRepositoryUniqueId() throws Exception {
		if (!hasRepositoryB())
			throw new Exception("Site " + name + " does not define an XDS.b Repository");
		TransactionBean transbean;
		transbean = getRepositoryBean(false);  // try non-secure first
		if (transbean != null) {
			String repUid =  transbean.name;
			if (repUid != null && !repUid.equals(""))
				return repUid;
		}
		transbean = getRepositoryBean(true);  // secure next
		if (transbean != null) {
			String repUid =  transbean.name;
			if (repUid != null && !repUid.equals(""))
				return repUid;
		}
		return null;
	}
	
	public boolean isAllRepositories() {
		return (name != null && name.equals("allRepositories"));
	}

	/**
	 * This counts endpoints.  A repository can have endpoints
	 * for all combinations of secure, async, and type.  There are
	 * two basically different repository types, Document
	 * Repository and On-Demand Document Source.
	 * @return
	 */
	public int repositoryBCount() {
		int cnt = 0;
		if (name != null && name.equals("allRepositories")) return 0;
		for (TransactionBean b : repositories.transactions) {
			if (b.repositoryType == RepositoryType.REPOSITORY)
				cnt++;
		}
		return cnt;
	}
	
	public Set<String> repositoryUniqueIds() {
		Set<String> ids = new HashSet<String>();
		for (TransactionBean b : repositories.transactions) {
			if (b.repositoryType == RepositoryType.REPOSITORY) {
				ids.add(b.name); // repositoryUniqueId since this is a retrieve
			}
		}		
		return ids;
	}
	
	List<TransactionBean> transactionBeansForRepositoryUniqueId(String repuid) {
		List<TransactionBean> tbs = new ArrayList<TransactionBean>();
		if (repuid == null || repuid.equals(""))
				return tbs;
		for (TransactionBean b : repositories.transactions) {
			if (repuid.equals(b.name))
				tbs.add(b);
		}		
		return tbs;
	}
		
	public boolean hasRepositoryB() {
		return repositoryBCount() > 0;
	}

	public int getRepositoryCount() {
		return repositories.size();
	}

	public String getHome() {
		return home;
	}
	
	public void setHome(String home) {
		this.home = home;
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append(name);
		return buf.toString();
	}

	public Site() {}

	public Site(String name) {
		setName(name);
	}

	public void setName(String name) {
		this.name = name;
		transactions.setName(name); 
		repositories.setName(name);
	}

	public String getName() {
		return name;
	}

	public String getEndpoint(ATFactory.TransactionType transaction, boolean isSecure, boolean isAsync) throws Exception {
		String endpoint = getRawEndpoint(transaction, isSecure, isAsync);
		if (endpoint == null) 
			throw new Exception("Site#getEndpoint: no endpoint defined for site=" + name + " transaction=" + transaction + " secure=" + isSecure + " async=" + isAsync);
		return endpoint;
	}

	public String getRawEndpoint(ATFactory.TransactionType transaction, boolean isSecure,
			boolean isAsync) {
		return transactions.get(transaction, isSecure, isAsync);
	}

	public String getRetrieveEndpoint(String reposUid, boolean isSecure, boolean isAsync) throws Exception {
		if (reposUid == null || reposUid.equals(""))
			throw new Exception("Site#getRetrieveEndpoint: no repository uid specified");
		String endpoint = null;
		endpoint = repositories.get(reposUid, isSecure, isAsync);
		if (endpoint == null) 
			throw new Exception("Site#getRetrieveEndpoint: no endpoint defined for repository uid " + reposUid + " and secure=" + isSecure + " and async=" + isAsync);
		return endpoint;
	}
	
	public String getSiteName() {
		return name;
	}

}
