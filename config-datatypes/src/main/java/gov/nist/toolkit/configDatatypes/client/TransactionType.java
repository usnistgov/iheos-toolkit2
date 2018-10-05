package gov.nist.toolkit.configDatatypes.client;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.nist.toolkit.configDatatypes.server.SimulatorProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public enum TransactionType implements Serializable, IsSerializable {
    PROVIDE_AND_REGISTER("ITI-41", "Provide and Register", "prb", "pr.b", "pr.as", false, "urn:ihe:iti:2007:ProvideAndRegisterDocumentSet-b", "urn:ihe:iti:2007:ProvideAndRegisterDocumentSet-bResponse", true, SimulatorProperties.pnrEndpoint, SimulatorProperties.pnrTlsEndpoint),
    XDR_PROVIDE_AND_REGISTER("ITI-41", "XDR Provide and Register", "xdrpr", "xdrpr", "xdrpr.as", false, "urn:ihe:iti:2007:ProvideAndRegisterDocumentSet-b", "urn:ihe:iti:2007:ProvideAndRegisterDocumentSet-bResponse", true, null, null),
    REGISTER("ITI-42", "Register", "rb", "r.b", "r.as", false, "urn:ihe:iti:2007:RegisterDocumentSet-b", "urn:ihe:iti:2007:RegisterDocumentSet-bResponse", false, SimulatorProperties.registerEndpoint, SimulatorProperties.registerTlsEndpoint),
    REGISTER_ODDE("ITI-61","Register On-Demand Document Entry", "rodde", "rodde", "rodde.as", false, "urn:ihe:iti:2010:RegisterOnDemandDocumentEntry", "urn:ihe:iti:2010:RegisterOnDemandDocumentResponse", false, SimulatorProperties.registerOddeEndpoint, SimulatorProperties.registerOddeTlsEndpoint),
    RETRIEVE("ITI-43", "Retrieve", "ret", "ret.b", "ret.as", true, "urn:ihe:iti:2007:RetrieveDocumentSet", "urn:ihe:iti:2007:RetrieveDocumentSetResponse", true, SimulatorProperties.retrieveEndpoint, SimulatorProperties.retrieveTlsEndpoint),
    IG_RETRIEVE("ITI-43", "Initiating Gateway Retrieve", "igr", "igr", "igr.as", false, "urn:ihe:iti:2007:RetrieveDocumentSet", "urn:ihe:iti:2007:RetrieveDocumentSetResponse", true, SimulatorProperties.igrEndpoint, SimulatorProperties.igrTlsEndpoint),
    ODDS_RETRIEVE("ITI-43", "On-Demand Document Source Retrieve", "ret", "ret.b", "ret.as", true, "urn:ihe:iti:2007:RetrieveDocumentSet", "urn:ihe:iti:2007:RetrieveDocumentSetResponse", true, null, null),
    ISR_RETRIEVE("ITI-43", "Integrated Source/Repository Retrieve", "isr", "isr", "isr.as", false, "urn:ihe:iti:2007:RetrieveDocumentSet", "urn:ihe:iti:2007:RetrieveDocumentSetResponse", true, null, null),
    STORED_QUERY("ITI-18", "Stored Query", "sq", "sq.b", "sq.as", false, "urn:ihe:iti:2007:RegistryStoredQuery", "urn:ihe:iti:2007:RegistryStoredQueryResponse", false, SimulatorProperties.storedQueryEndpoint, SimulatorProperties.storedQueryTlsEndpoint),
    XCPD("ITI-55", "XCPD Query", "xcpd", "xcpd", "xcpd.as", false, "urn:hl7-org:v3:PRPA_IN201305UV02:CrossGatewayPatientDiscovery", "urn:hl7-org:v3:PRPA_IN201305UV02:CrossGatewayPatientDiscoveryResponse", false, null, null),
    IG_QUERY("ITI-18", "Initiating Gateway Query", "igq", "igq", "igq.as", false, "urn:ihe:iti:2007:RegistryStoredQuery", "urn:ihe:iti:2007:RegistryStoredQueryResponse", false, SimulatorProperties.igqEndpoint, SimulatorProperties.igqTlsEndpoint),
    UPDATE("ITI-57", "Update", "update", "update.b", "update.b.as", false, "urn:ihe:iti:2010:UpdateDocumentSet", "urn:ihe:iti:2010:UpdateDocumentSetResponse", false, SimulatorProperties.updateEndpoint, SimulatorProperties.updateTlsEndpoint),
    RMU("ITI-92", "RMU", "rmu", "rmu.b", "rmu.b.as", false, "urn:ihe:iti:2018:RestrictedUpdateDocumentSet", "urn:ihe:iti:2018:RestrictedUpdateDocumentSetResponse", false, SimulatorProperties.rmuEndpoint, SimulatorProperties.rmuTlsEndpoint),
    XC_QUERY("ITI-38", "Cross-Community Query", "xcq", "xcq", "xcq.as", false, "urn:ihe:iti:2007:CrossGatewayQuery", "urn:ihe:iti:2007:CrossGatewayQueryResponse", false, SimulatorProperties.xcqEndpoint, SimulatorProperties.xcqTlsEndpoint),
    XC_RETRIEVE("ITI-39", "Cross-Community Retrieve", "xcr", "xcr", "xcr.as", false, "urn:ihe:iti:2007:CrossGatewayRetrieve", "urn:ihe:iti:2007:CrossGatewayRetrieveResponse", true, SimulatorProperties.xcrEndpoint, SimulatorProperties.xcrTlsEndpoint),
    MPQ("ITI-51", "Multi-Patient Query", "mpq", "mpq", "mpq.as", false, "urn:ihe:iti:2009:MultiPatientStoredQuery", "urn:ihe:iti:2009:MultiPatientStoredQueryResponse", false, null, null),
    XC_PATIENT_DISCOVERY("ITI-55", "Cross Community Patient Discovery", "xcpd", "xcpd", "xcpd.as", false, "urn:hl7-org:v3:PRPA_IN201305UV02:CrossGatewayPatientDiscovery", "urn:hl7-org:v3:PRPA_IN201306UV02:CrossGatewayPatientDiscovery", false, null, null),
    DIRECT("ONC-DIRECT", "ONC-DIRECT", "direct", "direct", "direct.as", false, "", "", false, null, null),
    PIF("PIF", "Patient Identity Feed", "pif", "pif", "pif", false, "", "", false, null, null),
    WADO_RETRIEVE("RAD-55", "WADO Retrieve", "wado.ret.ids", "wado.ret.ids", "wado", false, false, SimulatorProperties.wadoEndpoint, SimulatorProperties.wadoTlsEndpoint, true),
    RET_IMG_DOC_SET("RAD-69", "Retrieve Imaging Document Set", "ret.ids", "ret.ids.b", "ret.ids.as", true, "urn:ihe:rad:2009:RetrieveImagingDocumentSet", "urn:ihe:iti:2007:RetrieveDocumentSetResponse", true, SimulatorProperties.idsrEndpoint, SimulatorProperties.idsrTlsEndpoint),
    RET_IMG_DOC_SET_GW("RAD-69", "Retrieve Img Doc Set Gateway", "ret.iig", "ret.iig.b", "ret.iig.as", true, "urn:ihe:rad:2009:RetrieveImagingDocumentSet", "urn:ihe:iti:2007:RetrieveDocumentSetResponse", true, SimulatorProperties.idsrIigEndpoint, SimulatorProperties.idsrIigTlsEndpoint),
    XC_RET_IMG_DOC_SET("RAD-75", "Cross-Community Ret Img Doc Set", "xcr.ids", "xcr.ids.b", "xcr.ids.as", true, "urn:ihe:rad:2011:CrossGatewayRetrieveImagingDocumentSet", "urn:ihe:rad:2011:CrossGatewayRetrieveImagingDocumentSetResponse", true, SimulatorProperties.xcirEndpoint, SimulatorProperties.xcirTlsEndpoint),
    STS("STS", "Secure Token Service", "sts", "sts", "sts.as", true, "sts", "sts", true, null, null),
//    FHIR("FHIR", "FHIR", "fhir", "fhir", "fhir.as", true, "fhir", "fhir", true, SimulatorProperties.fhirEndpoint, SimulatorProperties.fhirTlsEndpoint, true),
    XAD_PID("ITI-43", "XAD-PID", "xadpid", "xadpid", "xadpid", false, "", "", false, null, null),
    REMOVE_METADATA("ITI-62", "Remove Metadata", "rm", "rm.b", "rm.b.as", false, "urn:ihe:iti:2010:DeleteDocumentSet", "urn:ihe:iti:2010:DeleteDocumentSetResponse", false, SimulatorProperties.removeMetadataEndpoint, SimulatorProperties.removeMetadataTlsEndpoint),
    REMOVE_DOCUMENTS("ITI-86", "Remove Documents", "rd", "rd.b", "rd.b.as", false, "urn:ihe:iti:2017:RemoveDocuments", "urn:ihe:iti:2017:RemoveDocumentsResponse", false, SimulatorProperties.removeDocumentsEndpoint, SimulatorProperties.removeDocumentsTlsEndpoint),
	//    ANY("ANY", "ANY", "any", "any", "any.as", false, "any", "any", false, null, null, false),
    FHIR("FHIR", "FHIR", "fhir", "fhir", "fhir.as", true, "fhir", "fhir", true, SimulatorProperties.fhirEndpoint, SimulatorProperties.fhirTlsEndpoint, true, FhirVerb.NONE),
    PROV_DOC_BUNDLE("ITI-65", "Provide Document Bundle", "pdb", "pdb", "pdb.as", false, "fhir", "fhir", false, SimulatorProperties.pdbEndpoint, SimulatorProperties.pdbTlsEndpoint, true, FhirVerb.TRANSACTION),
    // for these FHIR types, the name field here matches the resource type
    // should only be looked for with the FhirVerb option on find()
    FIND_DOC_REFS("ITI-67", "Find DocumentReference", "fdr", "fdr", "fdr.as", false, "fhir", "fhir", false, SimulatorProperties.fdrEndpoint, SimulatorProperties.fdrTlsEndpoint, true, FhirVerb.QUERY),
    READ_DOC_REF("ITI-67", "Read DocumentReference", "rdr", "rdr", "rdr.as", false, "fhir", "fhir", false, SimulatorProperties.rdrEndpoint, SimulatorProperties.rdrTlsEndpoint, true, FhirVerb.READ),
    READ_BINARY("ITI-68", "Binary", "br", "br", "br.as", false, "fhir", "fhir", false, SimulatorProperties.rdBinaryEndpoint, SimulatorProperties.rdBinaryTlsEndpoint, true, FhirVerb.READ),
    /*
    Reuse ITI-38 and ITI-39 for Sequoia's QD and RD. A new UseSequoiaHeader test plan parameter dynamically adds security related headers.
	QD("QD", "Sequoia Query Document", "qd", "qd", "qd.as", false, "urn:ihe:iti:2007:CrossGatewayQuery", "urn:ihe:iti:2007:CrossGatewayQueryResponse", false, SimulatorProperties.xcqEndpoint, SimulatorProperties.xcqTlsEndpoint),
	RD("RD", "Sequoia Retrieve Document", "rd", "rd", "rd.as", false, "urn:ihe:iti:2007:CrossGatewayRetrieve", "urn:ihe:iti:2007:CrossGatewayRetrieveResponse", true, SimulatorProperties.xcrEndpoint, SimulatorProperties.xcrTlsEndpoint),
	*/
    NONE("NONE", "NONE", "none", "none", "none.as", false, "none", "none", false, null, null, false, FhirVerb.NONE),
    ANY("ANY", "ANY", "any", "any", "any.as", false, "any", "any", false, null, null, false, FhirVerb.NONE);

	private static final long serialVersionUID = 1L;
    String id = "";
    String name = "";
	 String shortName = "";
    String code = "";   // like pr.b - used in actors table
    String asyncCode = "";
    boolean needsRepUid = false;  // I think maybe not used? RM
    String requestAction = "";
    String responseAction = "";
    boolean requiresMtom = false;
    boolean http = false; // Is this Http only (non-SOAP) transaction
//    Map<String, TransactionType> basicTypeMap = new HashMap<>();
    boolean fhir = false;
    String endpointSimPropertyName;  // TODO is this irrelevant?
    String tlsEndpointSimPropertyName;  // TODO is this irrelevant?
    FhirVerb fhirVerb = FhirVerb.NONE;


	TransactionType() {
	}  // For GWT

    TransactionType(String id, String name, String shortName, String code, String asyncCode, boolean needsRepUid, String requestAction, String responseAction, boolean requiresMtom, String endpointSimPropertyName, String tlsEndpointSimPropertyName) {
        this.id = id;
        this.name = name;
        this.shortName = shortName;
        this.code = code;
        this.asyncCode = asyncCode;
        this.needsRepUid = needsRepUid;
        this.requestAction = requestAction;
        this.responseAction = responseAction;
        this.requiresMtom = requiresMtom;
        this.endpointSimPropertyName = endpointSimPropertyName;
        this.tlsEndpointSimPropertyName = tlsEndpointSimPropertyName;
    }

    TransactionType(String id, String name, String shortName, String code, String asyncCode, boolean needsRepUid, String requestAction, String responseAction, boolean requiresMtom, String endpointSimPropertyName, String tlsEndpointSimPropertyName, boolean fhir, FhirVerb fhirVerb) {
        this.id = id;
        this.name = name;
        this.shortName = shortName;
        this.code = code;
        this.asyncCode = asyncCode;
        this.needsRepUid = needsRepUid;
        this.requestAction = requestAction;
        this.responseAction = responseAction;
        this.requiresMtom = requiresMtom;
        this.endpointSimPropertyName = endpointSimPropertyName;
        this.tlsEndpointSimPropertyName = tlsEndpointSimPropertyName;
        this.fhir = fhir;
        this.fhirVerb = fhirVerb;
    }

    TransactionType(String id, String name, String shortName, String code, String asyncCode, boolean needsRepUid, boolean requiresMtom, String endpointSimPropertyName, String tlsEndpointSimPropertyName, boolean httpOnly) {
       this.id = id;
       this.name = name;
       this.shortName = shortName;
       this.code = code;
       this.asyncCode = asyncCode;
       this.needsRepUid = needsRepUid;
       this.requiresMtom = requiresMtom;this.endpointSimPropertyName = endpointSimPropertyName;
        this.endpointSimPropertyName = endpointSimPropertyName;
        this.tlsEndpointSimPropertyName = tlsEndpointSimPropertyName;
       this.http = httpOnly;
   }

   @Override
   public String toString() { return shortName; }

   public boolean isFhir() { return fhir;  }

    public boolean isRequiresMtom() {
        return requiresMtom;
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

    public FhirVerb getFhirVerb() {
        return fhirVerb;
    }

    /**
    * @return the {@link #requestAction} value.
    */
   public String getRequestAction() {
      return requestAction;
   }

   /**
    * @return the {@link #responseAction} value.
    */
   public String getResponseAction() {
      return responseAction;
   }
   
   public boolean isHttpOnly() {
      return http;
   }

   public boolean equals(TransactionType tt) {
        return name.equals(tt.name);
	}

    // if lookup by id is needed, must also select off of receiving actor
    static public TransactionType find(String s) {
        if (s == null) return null;
        for (TransactionType t : values()) {
            if (s.equals(t.name)) return t;
            if (s.equals(t.shortName)) return t;
            if (s.equals(t.code)) return t;
            if (s.equals(t.asyncCode)) return t;
            if (s.equals(t.getId())) return t;
            try {
                if (t == TransactionType.valueOf(s)) return t;
            } catch (IllegalArgumentException e) {
                // continue;
            }
        }
        return null;
    }

    static public TransactionType find(String s, FhirVerb fhirVerb) {
        if (s == null) return null;
        for (TransactionType t : values()) {
            if (s.equals(t.name) && t.fhirVerb == fhirVerb) return t;
            if (s.equals(t.shortName) && t.fhirVerb == fhirVerb) return t;
            if (s.equals(t.code) && t.fhirVerb == fhirVerb) return t;
            if (s.equals(t.asyncCode) && t.fhirVerb == fhirVerb) return t;
            if (s.equals(t.getId()) && t.fhirVerb == fhirVerb) return t;
            try {
                if (t == TransactionType.valueOf(s) && t.fhirVerb == fhirVerb) return t;
            } catch (IllegalArgumentException e) {
                // continue;
            }
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

//	static public TransactionType find(ActorType a, String transString) {
//        if (a == null) return null;
//
//		for (TransactionType t : a.getTransactions()) {
//            if (t.isIdentifiedBy(transString))
//				return t;
//        }
//
//		return null;
//    }
//

	static public List<TransactionType> asList() {
        List<TransactionType> l = new ArrayList<TransactionType>();
        for (TransactionType t : values())
            l.add(t);
        return l;
    }

    public String getEndpointSimPropertyName() {
        return endpointSimPropertyName;
    }

    public String getTlsEndpointSimPropertyName() {
        return tlsEndpointSimPropertyName;
    }
}
