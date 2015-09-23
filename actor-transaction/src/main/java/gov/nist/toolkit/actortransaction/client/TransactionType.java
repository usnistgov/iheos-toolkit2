package gov.nist.toolkit.actortransaction.client;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bill on 9/10/15.
 */
public enum TransactionType implements IsSerializable, Serializable {
    PROVIDE_AND_REGISTER("ITI-41", "Provide and Register", "prb", "pr.b", "pr.as", false, "urn:ihe:iti:2007:ProvideAndRegisterDocumentSet-b", "urn:ihe:iti:2007:ProvideAndRegisterDocumentSet-bResponse"),
    XDR_PROVIDE_AND_REGISTER("ITI-41", "XDR Provide and Register", "xdrpr", "xdrpr", "xdrpr.as", false, "urn:ihe:iti:2007:ProvideAndRegisterDocumentSet-b", "urn:ihe:iti:2007:ProvideAndRegisterDocumentSet-bResponse"),
    REGISTER("ITI-42", "Register", "rb", "r.b", "r.as", false, "urn:ihe:iti:2007:RegisterDocumentSet-b", "urn:ihe:iti:2007:RegisterDocumentSet-bResponse"),
    RETRIEVE("ITI-43", "Retrieve", "ret", "ret.b", "ret.as", true, "urn:ihe:iti:2007:RetrieveDocumentSet", "urn:ihe:iti:2007:RetrieveDocumentSetResponse"),
    IG_RETRIEVE("ITI-43", "Initiating Gateway Retrieve", "igr", "igr", "igr.as", false, "urn:ihe:iti:2007:RetrieveDocumentSet", "urn:ihe:iti:2007:RetrieveDocumentSetResponse"),
    ODDS_RETRIEVE("ITI-43", "On-Demand Document Source Retrieve", "odds", "odds", "odds.as", false, "urn:ihe:iti:2007:RetrieveDocumentSet", "urn:ihe:iti:2007:RetrieveDocumentSetResponse"),
    ISR_RETRIEVE("ITI-43", "Integrated Source/Repository Retrieve", "isr", "isr", "isr.as", false, "urn:ihe:iti:2007:RetrieveDocumentSet", "urn:ihe:iti:2007:RetrieveDocumentSetResponse"),
    STORED_QUERY("ITI-18", "Stored Query", "sq", "sq.b", "sq.as", false, "urn:ihe:iti:2007:RegistryStoredQuery", "urn:ihe:iti:2007:RegistryStoredQueryResponse"),
    IG_QUERY("ITI-18", "Initiating Gateway Query", "igq", "igq", "igq.as", false, "urn:ihe:iti:2007:RegistryStoredQuery", "urn:ihe:iti:2007:RegistryStoredQueryResponse"),
    UPDATE("ITI-57", "Update", "update", "update.b", "update.b.as", false, "urn:ihe:iti:2010:UpdateDocumentSet", "urn:ihe:iti:2010:UpdateDocumentSetResponse"),
    XC_QUERY("ITI-38", "Cross-Community Query", "xcq", "xcq", "xcq.as", false, "urn:ihe:iti:2007:CrossGatewayQuery", "urn:ihe:iti:2007:CrossGatewayQueryResponse"),
    XC_RETRIEVE("ITI-39", "Cross-Community Retrieve", "xcr", "xcr", "xcr.as", false, "urn:ihe:iti:2007:CrossGatewayRetrieve", "urn:ihe:iti:2007:CrossGatewayRetrieveResponse"),
    MPQ("ITI-51", "Multi-Patient Query", "mpq", "mpq", "mpq.as", false, "urn:ihe:iti:2009:MultiPatientStoredQuery", "urn:ihe:iti:2009:MultiPatientStoredQueryResponse"),
    XC_PATIENT_DISCOVERY("ITI-55", "Cross Community Patient Discovery", "xcpd", "xcpd", "xcpd.as", false, "urn:hl7-org:v3:PRPA_IN201305UV02:CrossGatewayPatientDiscovery", "urn:hl7-org:v3:PRPA_IN201306UV02:CrossGatewayPatientDiscovery"),
    DIRECT("ONC-DIRECT", "ONC-DIRECT", "direct", "direct", "direct.as", false, "", ""),
    PIF("PIF", "Patient Identity Feed", "pif", "pif", "pif", false, "", "");


	private static final long serialVersionUID = 1L;
    String id = "";
    String name = "";
	String shortName = "";
    String code = "";   // like pr.b - used in actors table
    String asyncCode = "";
    boolean needsRepUid = false;
    String requestAction = "";
    String responseAction = "";

	TransactionType() {
	}  // For GWT

    TransactionType(String id, String name, String shortName, String code, String asyncCode, boolean needsRepUid, String requestAction, String responseAction) {
        this.id = id;
        this.name = name;
        this.shortName = shortName;
        this.code = code;
        this.asyncCode = asyncCode;
        this.needsRepUid = needsRepUid;
        this.requestAction = requestAction;
        this.responseAction = responseAction;
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

    public boolean usesTraditionalTransactions() {
        if (requestAction.equals("")) return false;
        return true;
    }

	public boolean equals(TransactionType tt) {
		return id.equals(tt.id);
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

	static public TransactionType findByRequestAction(String action) {
		if (action == null) return null;
		for (TransactionType t : values()) {
			if (action.equals(t.requestAction)) return t;
		}
		return null;
	}

	static public TransactionType findByResponseAction(String action) {
		if (action == null) return null;
		for (TransactionType t : values()) {
			if (action.equals(t.responseAction)) return t;
		}
		return null;
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
