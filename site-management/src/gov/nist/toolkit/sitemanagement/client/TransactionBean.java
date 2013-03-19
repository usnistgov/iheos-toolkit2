package gov.nist.toolkit.sitemanagement.client;

import gov.nist.toolkit.actortransaction.client.ATFactory;
import gov.nist.toolkit.actortransaction.client.ATFactory.ActorType;
import gov.nist.toolkit.actortransaction.client.ATFactory.TransactionType;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Configuration of a single instance of a transaction. Transactions are
 * split into two major types: Retrieve and All Others. The isRetrieve()
 * determines which kind this is.  In theory a transaction must belong to an
 * Actor definition.  Toolkit is a bit looser and transactions belong
 * to TransactionOfferings which can be thought of as a lightweight
 * definition of an Actor.  
 * @author bill
 *
 */
public class TransactionBean implements IsSerializable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public boolean isSecure = false;
	public boolean isAsync = false;
	public String endpoint = "";   // make private
	
	String name = "";   // can be transaction name or repository uid
						// when a transaction name, it is related to transType
	TransactionType transType = null;
	ActorType actorType = null;
	
	
	// Remove this? Not used for anything real yet.
	public enum RepositoryType  implements IsSerializable, Serializable  { REPOSITORY, ODDS, NONE;
	
		RepositoryType() {}
	};
	
	public RepositoryType repositoryType;
	
	public String getEndpoint() { return endpoint; }
	
	public boolean hasSameIndex(TransactionBean b) {
		return 
				isSecure == b.isSecure &&
				isAsync == b.isAsync &&
				((name == null) ? b.name == null : name.equals(b.name)) && 
				((transType == null) ? b.transType == null : transType == b.transType) &&
				((actorType == null) ? b.actorType == null : actorType == b.actorType) &&
				((repositoryType == null) ? b.repositoryType == null : repositoryType == b.repositoryType);
	}
	
	public boolean equals(TransactionBean b) {
		return hasSameIndex(b) && 
				(
						("".equals(endpoint) && "".equals(b.endpoint)) ||
						endpoint.equals(b.endpoint)
						);
	}
	
	public boolean hasName(String nam) {
		if (name.equals(nam))
			return true;
		if (transType == null)
			return false;
		if (transType.getName().equals(nam))
			return true;
		if (transType.getShortName().equals(nam))
			return true;
		return false;
	}
	
	public String getName() {
		if (transType == null)
			return name;
		return transType.getName();
	}

	public String toString() {
		if (transType != null)
			return "[trans=" + transType + 
//					" RepositoryType=" + repositoryType +
					" ActorType=" + (actorType == null ? "?" : actorType.getName()) + 
					" isSecure=" + isSecure + " isAsync=" + isAsync + "] : " + endpoint; 
		return "[repositoryUniqueId=" + name + " isSecure=" + isSecure + " isAsync=" + isAsync + "] : " + endpoint; 
	}
	
	public boolean isRetrieve() {
		return isNameUid();
	}
	
	public boolean isNameUid() {
		if (name == null || name.equals(""))
			return false;
		return Character.isDigit(name.charAt(0));
	}
	
	public ATFactory.TransactionType getTransactionType() {
		return transType;
	}
	
	public boolean isType(ATFactory.TransactionType transType2) {
		try {
			return transType2.equals(transType);
		} catch (Exception e) {
			return false;
		}
	}
	
	public boolean hasEndpoint() {
		return endpoint != null && !endpoint.equals("");
	}
	
	public TransactionBean() {
		
	}

	// Used by simulator factories, ActorConfigTab and the Gazelle interface
	public TransactionBean(String name, RepositoryType repositoryType, String endpoint, boolean isSecure, boolean isAsync) {
		this.name = name;  // comes from TransactionType.XXX.getCode()
							// param should be TransactionType
							// This constructor should be retired in favor of the next one which depends on TransactionType
		
		// name can be trans name or repository uid
		transType = ATFactory.TransactionType.find(name);
		this.repositoryType = repositoryType;
		this.endpoint = endpoint;
		this.isSecure = isSecure;
		this.isAsync = isAsync;
	}
	
	// Used only by ActorConfigTab
	public TransactionBean(ATFactory.TransactionType transType, RepositoryType repositoryType, String endpoint, boolean isSecure, boolean isAsync) {
		this.transType = transType;
		this.name = transType.getName();
		this.repositoryType = repositoryType;
		this.endpoint = endpoint;
		this.isSecure = isSecure;
		this.isAsync = isAsync;
	}

	// Used only by Gazelle interface
	@Deprecated
	public TransactionBean(ATFactory.TransactionType transType, RepositoryType repositoryType, ActorType actorType, String endpoint, boolean isSecure, boolean isAsync) {
		this.transType = transType;
		this.name = transType.getName();
		this.repositoryType = repositoryType;
		this.actorType = actorType;
		this.endpoint = endpoint;
		this.isSecure = isSecure;
		this.isAsync = isAsync;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
