package gov.nist.toolkit.valregmsg.service;

import gov.nist.toolkit.registrysupport.MetadataSupport;

import java.util.HashMap;
import java.util.Map;

public class SoapActionFactory {
	
	public final static String pnr_b_async_action = "urn:ihe:iti:2007:ProvideAndRegisterDocumentSet-b";
	public final static String pnr_b_action       = "urn:ihe:iti:2007:ProvideAndRegisterDocumentSet-b";
	public final static String r_b_action         = "urn:ihe:iti:2007:RegisterDocumentSet-b";
	public final static String ret_b_action       = "urn:ihe:iti:2007:RetrieveDocumentSet";
	public final static String ret_b_async_action = "urn:ihe:iti:2007:RetrieveDocumentSet";
	public final static String anon_action        = "urn:anonOutInOp";
	
	public final static String epsos_xcqr_action = "urn:epsos:xcqr";
	
	public final static String xcpd = "urn:hl7-org:v3:PRPA_IN201305UV02:CrossGatewayPatientDiscovery";
	
	private static final Map<String, String> actions =
		new HashMap<String, String>()
		{
		
		     /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
				// this is temp value for epsos
				put(epsos_xcqr_action, "urn:epsos:xcqrResponse");
				
		    	 put(pnr_b_action,                                       "urn:ihe:iti:2007:ProvideAndRegisterDocumentSet-bResponse");
		    	 put("urn:ihe:iti:2010:UpdateDocumentSet",               "urn:ihe:iti:2010:UpdateDocumentSetResponse");
		    	 put(r_b_action,                                         "urn:ihe:iti:2007:RegisterDocumentSet-bResponse");
		    	 put(ret_b_action,                                       "urn:ihe:iti:2007:RetrieveDocumentSetResponse");
		    	 put(MetadataSupport.SQ_action,                          "urn:ihe:iti:2007:RegistryStoredQueryResponse");
		    	 put("urn:ihe:iti:2007:CrossGatewayRetrieve",            "urn:ihe:iti:2007:CrossGatewayRetrieveResponse");
		    	 put("urn:ihe:iti:2007:CrossGatewayQuery",               "urn:ihe:iti:2007:CrossGatewayQueryResponse");
		    	 put(MetadataSupport.dsub_subscribe_action,              MetadataSupport.dsub_subscribe_response_action);
		    	 put(MetadataSupport.MPQ_action,                         "urn:ihe:iti:2009:MultiPatientStoredQueryResponse");

			
		    	 put(pnr_b_async_action,                                 "urn:ihe:iti:2007:ProvideAndRegisterDocumentSet-bResponse");
		    	 put(ret_b_async_action,                                 "urn:ihe:iti:2007:RetrieveDocumentSetResponse");
		    	 //put("urn:ihe:iti:2007:RegistryStoredQueryAsync",        "urn:ihe:iti:2007:RegistryStoredQueryResponse");
		    	 //put("urn:ihe:iti:2007:CrossGatewayQueryAsync",          "urn:ihe:iti:2007:CrossGatewayQueryResponse");
		    	 put("urn:hl7-org:v3:PRPA_IN201305UV02:CrossGatewayPatientDiscovery", "urn:hl7-org:v3:PRPA_IN201306UV02:CrossGatewayPatientDiscovery");
			}
		
		};

	static public String getResponseAction(String requestAction) {
		if (requestAction == null) return null;
		return actions.get(requestAction);
	}
}
