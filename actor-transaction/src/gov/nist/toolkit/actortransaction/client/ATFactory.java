package gov.nist.toolkit.actortransaction.client;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * This factory defines the available Actors and Transactions and the relationships between Actors and Transactions.
 * @author bill
 *
 */
public class ATFactory implements IsSerializable, Serializable {

	private static final long serialVersionUID = 1L;
		
	public enum TransactionType  implements IsSerializable, Serializable {
		PROVIDE_AND_REGISTER      ("ITI-41", "Provide and Register",              "prb",    "pr.b",  "pr.as",    false),
		XDR_PROVIDE_AND_REGISTER  ("ITI-41", "XDR Provide and Register",          "xdrpr",  "xdrpr", "xdrpr.as", false),
		REGISTER                  ("ITI-42", "Register",                          "rb",     "r.b",   "r.as",     false),
		RETRIEVE                  ("ITI-43", "Retrieve",                          "ret",    "ret.b", "ret.as",   true),
		IG_RETRIEVE               ("ITI-43", "Initiating Gateway Retrieve",       "igr",    "igr",   "igr.as",   false),
		ODDS_RETRIEVE             ("ITI-43", "On-Demand Document Source Retrieve","odds",   "odds",  "odds.as",  false),
		ISR_RETRIEVE              ("ITI-43", "Integrated Source/Repository Retrieve","isr",   "isr",  "isr.as",  false),
		STORED_QUERY              ("ITI-18", "Stored Query",                      "sq",     "sq.b",  "sq.as",    false),
		IG_QUERY                  ("ITI-18", "Initiating Gateway Query",          "igq",    "igq",   "igq.as",   false),
		UPDATE                    ("ITI-57", "Update",                            "update", "update.b", "update.b.as", false),
		XC_QUERY                  ("ITI-38", "Cross-Community Query",             "xcq",    "xcq",   "xcq.as",   false),
		XC_RETRIEVE               ("ITI-39", "Cross-Community Retrieve",          "xcr",    "xcr",   "xcr.as",   false),
		MPQ                       ("ITI-51", "Multi-Patient Query",               "mpq",    "mpq",   "mpq.as",   false),
		XC_PATIENT_DISCOVERY      ("ITI-55", "Cross Community Patient Discovery", "xcpd",   "xcpd",  "xcpd.as",  false),
		DIRECT                    ("ONC-DIRECT", "ONC-DIRECT", "direct",   "direct",  "direct.as",  false);
		
		private static final long serialVersionUID = 1L;
		String id = "";
		String name = ""; 
		String shortName = "";
		String code = "";   // like pr.b - used in actors table
		String asyncCode = "";
		boolean needsRepUid = false;
		
		TransactionType() {}  // For GWT

		TransactionType(String id, String name, String shortName, String code, String asyncCode, boolean needsRepUid) {
			this.id = id;
			this.name = name;
			this.shortName = shortName;
			this.code = code;
			this.asyncCode = asyncCode;
			this.needsRepUid = needsRepUid;
		}
		
		public String getId() {
			return id;
		}

		public String getName() {
			return name;
		}
		
		public String getShortName() {
			return shortName;
		}
		
		public String getCode() {
			return code;
		}
		
		public String getAsyncCode() {
			return asyncCode;
		}
		
		// if lookup by id is needed, must also select off of receiving actor
		static public TransactionType find(String s) {
			if (s == null) return null;
			for (TransactionType t : values()) {
				if (s.equals(t.name)) return t;
				if (s.equals(t.shortName)) return t;
				if (s.equals(t.code)) return t;
				if (s.equals(t.asyncCode)) return t;
			}
			return null;
		}
		
		public boolean isIdentifiedBy(String s) {
			if (s == null) return false;
			return 
					s.equals(id) ||
					s.equals(name) ||
					s.equals(shortName) ||
					s.equals(code) ||
					s.equals(asyncCode);
		}
		
		static public TransactionType find(ActorType a, String transString) {
			if (a == null) return null;
			
			for (TransactionType t : a.getTransactions()) {
				if (t.isIdentifiedBy(transString)) 
					return t;
			}
				
			return null;
		}
		
		static public TransactionType find(String receivingActorStr, String transString) {
			if (receivingActorStr == null || transString == null) return null;
			
			ActorType a = ActorType.findActor(receivingActorStr);
			return find(a, transString);
		}
		
		static public List<TransactionType> asList() {
			List<TransactionType> l = new ArrayList<TransactionType>();
			for (TransactionType t : values())
				l.add(t);
			return l;
		}
		

	}
	
	/**
	 * Map from transaction code (used in actors file) and transaction definition
	 */
	static Map<String, TransactionType> transactionMapByCode = new HashMap<String, TransactionType>();
	
	
	// Create definitions of transactions
	static {
		
		for (TransactionType tr : TransactionType.values()) {
			transactionMapByCode.put(tr.code, tr);
		}
	}
	
	
	public enum ActorType implements IsSerializable, Serializable {
		REGISTRY (
				"Document Registry", 
				Arrays.asList("DOC_REGISTRY", "Initialize_for_Stored_Query"),
				"reg", 
				Arrays.asList(TransactionType.REGISTER, TransactionType.STORED_QUERY, TransactionType.UPDATE, TransactionType.MPQ),
				true,
				null
				),
				// Update option on Document Registry
				// this should be removed once implications are re-discovered
//		UPDATE (
//				"Update Option",       
//				new ArrayList<String>(),
//				"update", 
//				new ArrayList<TransactionType>(),
//				false,
//				null
//				),
		REPOSITORY (
				"Document Repository", 
				Arrays.asList("DOC_REPOSITORY"),
				"rep", 
				Arrays.asList(TransactionType.PROVIDE_AND_REGISTER, TransactionType.RETRIEVE), 
				true,
				"repository"
				),
		ONDEMAND_DOCUMENT_SOURCE (
				"On-Demand Document Source", 
				Arrays.asList("ODDS", "ON_DEMAND_DOC_SOURCE"),
				"odds", 
				Arrays.asList(TransactionType.ODDS_RETRIEVE), 
				true,
				"odds"
				),
		ISR (
				"Integrated Source/Repository", 
				Arrays.asList("EMBED_REPOS"),
				"isr", 
				Arrays.asList(TransactionType.ISR_RETRIEVE), 
				true,
				"isr"
				),
		REPOSITORY_REGISTRY (
				"Document Repository/Registry",   
				new ArrayList<String>(),
				"rr", 
				Arrays.asList(TransactionType.REGISTER, TransactionType.STORED_QUERY, TransactionType.UPDATE, TransactionType.MPQ, TransactionType.PROVIDE_AND_REGISTER, TransactionType.RETRIEVE),
				false,
				null
				),
		DOCUMENT_RECIPIENT (
				"Document Recipient",  
				Arrays.asList("DOC_RECIPIENT"),
				"rec", 
				Arrays.asList(TransactionType.XDR_PROVIDE_AND_REGISTER),
				true,
				null
				),
		RESPONDING_GATEWAY (
				"Responding Gateway",  
				Arrays.asList("RESP_GATEWAY"),
				"rg", 
				Arrays.asList(TransactionType.XC_QUERY, TransactionType.XC_RETRIEVE, TransactionType.XC_PATIENT_DISCOVERY),
				true,
				null
				),
		INITIATING_GATEWAY (
				"Initiating Gateway",  
				Arrays.asList("INIT_GATEWAY"),
				"ig", 
				Arrays.asList(TransactionType.IG_QUERY, TransactionType.IG_RETRIEVE),
				true,
				null
				),
		DIRECT_SERVER (
				"Direct Server",  
				Arrays.asList("DIRECT_SERVER"),
				"direct", 
				Arrays.asList(TransactionType.DIRECT),
				true,
				null
				); 
		
		private static final long serialVersionUID = 1L;
		String name;   
		List<String> altNames;
		String shortName;
		List<TransactionType> transactionTypes; // TransactionTypes this actor can receive
		boolean showInConfig;
		String actorsFileLabel;
		
		ActorType() {} // for GWT

		ActorType(String name, List<String> altNames, String shortName, List<TransactionType> tt, boolean showInConfig, String actorsFileLabel) {
			this.name = name;
			this.altNames = altNames;
			this.shortName = shortName;
			this.transactionTypes = tt;   // This actor receives
			this.showInConfig = showInConfig;
			this.actorsFileLabel = actorsFileLabel;
		}
		
		public boolean showInConfig() {
			return showInConfig;
		}
						
		public boolean isRepositoryActor() {
			return this.equals(REPOSITORY); 
		}
		
		public boolean isRGActor() {
			return this.equals(RESPONDING_GATEWAY);
		}
		
		public boolean isIGActor() {
			return this.equals(INITIATING_GATEWAY);
		}
		
		public boolean isGW() {
			return isRGActor() || isIGActor();
		}
		
		public String getActorsFileLabel() {
			return actorsFileLabel;
		}

		static public List<String> getActorNames() {
			List<String> names = new ArrayList<String>();
			
			for (ActorType a : values())
				names.add(a.name);
			
			return names;
		}
		
		/**
		 * Within toolkit, each TransactionType maps to a unique ActorType
		 * (as receiver of the transaction). To make this work, transaction
		 * names are customized to make this mapping unique.  This goes 
		 * beyond the definition in the TF.
		 * @param tt
		 * @return
		 */
		static public ActorType getActorType(TransactionType tt) {
			if (tt == null)
				return null;
			for (ActorType at : values()) {
				if (at.hasTransaction(tt))
					return at;
			}
			return null;
		}
		
		static public ActorType findActor(String name) {
			if (name == null)
				return null;
			
			for (ActorType actor : values()) {
				if (actor.name.equals(name)) return actor;
				if (actor.shortName.equals(name)) return actor;
				if (actor.altNames.contains(name)) return actor;
			}
			return null;
		}
	
		
		public String toString() {
			StringBuffer buf = new StringBuffer();
			
			buf.append(name).append(" [");
			for (ATFactory.TransactionType tt : transactionTypes)
				buf.append(tt).append(",");
			buf.append("]");
			
			return buf.toString();
		}
		
		public String getName() {
			return name;
		}
		
		public String getShortName() {
			return shortName;
		}
		
		public List<ATFactory.TransactionType> getTransactions() {
			return transactionTypes;
		}
		
		public boolean hasTransaction(ATFactory.TransactionType transType) {
			for (ATFactory.TransactionType transType2 : transactionTypes) {
				if (transType2.equals(transType))
					return true;
			}
			return false;
		}
		
		
		public boolean equals(ActorType at) {
			try {
				return name.equals(at.name);
			} catch (Exception e) {}
			return false;
		}
	}	
	
	static public List<ActorType> RetrieveActorTypes = Arrays.asList(
			ActorType.REPOSITORY,
			ActorType.ONDEMAND_DOCUMENT_SOURCE,
			ActorType.ISR);
	
	public enum ParamType implements IsSerializable, Serializable {
		OID,
		ENDPOINT,
		TEXT,
		BOOLEAN,
		TIME,
		SELECTION;
		
		ParamType() {} // for GWT
	}
		
	
	static List<TransactionType> gatewayTransactions = new ArrayList<TransactionType>();
	
	static {
		gatewayTransactions.add(TransactionType.XC_QUERY);
		gatewayTransactions.add(TransactionType.XC_RETRIEVE);
		gatewayTransactions.add(TransactionType.XC_PATIENT_DISCOVERY);
		gatewayTransactions.add(TransactionType.IG_QUERY);
		gatewayTransactions.add(TransactionType.IG_RETRIEVE);
	}
	
	
	///////////////////////////////////////////////////////////////////////
	// END OF CONFIGURATIONS
	///////////////////////////////////////////////////////////////////////
	
	
	static public boolean isGatewayTransaction(TransactionType tt) {
		return gatewayTransactions.contains(tt);
	}
	
	static public TransactionType getTransactionFromCode(String code) {
		return transactionMapByCode.get(code);
	}	
	
	static public TransactionType findTransactionByShortName(String name) {
		if (name == null)
			return null;
		
		for (TransactionType trans : TransactionType.values()) {
			if (trans.shortName.equals(name))
				return trans;
		}
		return null;
	}
	
	
	public ATFactory() {
		
	}
	
	static public List<TransactionType> getAllTransactionTypes() {
		return TransactionType.asList();
	}
	
}
