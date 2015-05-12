package gov.nist.toolkit.valregmsg.registry.storedquery.generic;

import gov.nist.toolkit.docref.EbRS;
import gov.nist.toolkit.docref.SqDocRef;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrymsg.registry.Response;
import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.registrysupport.logging.LogMessage;
import gov.nist.toolkit.registrysupport.logging.LoggerException;
import gov.nist.toolkit.valregmsg.registry.AdhocQueryResponse;
import gov.nist.toolkit.valregmsg.registry.storedquery.support.ParamParser;
import gov.nist.toolkit.valregmsg.registry.storedquery.support.SqParams;
import gov.nist.toolkit.valregmsg.registry.storedquery.support.StoredQuerySupport;
import gov.nist.toolkit.xdsexception.MetadataException;
import gov.nist.toolkit.xdsexception.MetadataValidationException;
import gov.nist.toolkit.xdsexception.XDSRegistryOutOfResourcesException;
import gov.nist.toolkit.xdsexception.XdsException;
import gov.nist.toolkit.xdsexception.XdsInternalException;

import org.apache.axiom.om.OMElement;

/**
 * Generic Stored Query Factory class that is sub-classed to define a specific stored query implementation.
 * The generic/specific nature relates to the underlying implementation.  The key method, 
 * buildStoredQueryHandler(), which is to be defined in the sub-class, decides which stored queries
 * are implemented and what the implementation classes are. This class provides the generic stored
 * query parsing and support.
 * @author bill
 *
 */
abstract public class StoredQueryFactory {

	/**
	 * Returns an object of generic type StoredQuery which implements a single stored query 
	 * type implemented against a specific registry implementation. The sub-class that implements 
	 * this method is specific to an implementation.
	 * @param sqs
	 * @throws MetadataValidationException
	 * @throws LoggerException 
	 */
	abstract public StoredQueryFactory buildStoredQueryHandler(StoredQuerySupport sqs) throws MetadataValidationException, LoggerException;

	OMElement ahqr;
	
	public enum QueryReturnType  { OBJECTREF, LEAFCLASS, LEAFCLASSWITHDOCUMENT };
	
	
	QueryReturnType returnType = QueryReturnType.OBJECTREF;
	SqParams params;
	protected String query_id;
	protected LogMessage log_message = null;
	protected StoredQuery storedQueryImpl;
	String service_name;
	boolean is_secure = false;
	protected Response response = null;
	protected ErrorRecorder er = null;
	String homeCommunityId = null;

	public void setIsSecure(boolean is) { is_secure = is; }
	public void setServiceName(String serviceName) { serviceName = service_name; }
	public void setQueryId(String qid) { query_id = qid; }
	public String getHome() { return homeCommunityId; }
	public boolean hasHome() { return homeCommunityId != null; }

	public boolean isLeafClassReturnType() {
		OMElement response_option = MetadataSupport.firstChildWithLocalName(ahqr, "ResponseOption");
		if (response_option == null) return true;
		String return_type = response_option.getAttributeValue(MetadataSupport.return_type_qname);
		if (return_type == null || return_type.equals("") || !return_type.equals("LeafClass")) return false;
		return true;
	}

	public StoredQueryFactory(OMElement ahqr) throws XdsException, LoggerException {
		this.ahqr = ahqr;
		this.params = null;
		this.log_message = null;

		build();
	}

	public StoredQueryFactory(OMElement ahqr, ErrorRecorder er) throws XdsException, LoggerException {
		this.ahqr = ahqr;
		this.params = null;
		this.log_message = null;
		this.er = er;

		build();
		
	}

	public StoredQueryFactory(OMElement ahqr, Response response, LogMessage log_message) throws XdsInternalException, MetadataException, XdsException, LoggerException {
		this.ahqr = ahqr;
		this.log_message = log_message;
		this.response = response;
		this.params = null;

		build();
	}

	public StoredQueryFactory(SqParams params, Response response, LogMessage log_message)  throws XdsInternalException, MetadataException, XdsException, LoggerException {
		this.params = params;
		this.response = response;
		ahqr = null;
		this.log_message = log_message;
	}

	public StoredQueryFactory(SqParams params)  throws XdsInternalException, MetadataException, XdsException, LoggerException {
		this.params = params;
		ahqr = null;
		this.log_message = null;
	}


	void build() throws XdsException, LoggerException {
		
		if (response != null && er == null)
			er = response.getErrorRecorder();

		OMElement response_option = MetadataSupport.firstChildWithLocalName(ahqr, "ResponseOption") ;
		if (response_option == null) 
			er.err(XdsErrorCode.Code.XDSRegistryError, "Cannot find /AdhocQueryRequest/ResponseOption element", this, "ebRS 3.0 Section 6.1.1.1");

		String return_type = response_option.getAttributeValue(MetadataSupport.return_type_qname);

		if (return_type == null) throw new XdsException("Attribute returnType not found on query request", SqDocRef.Return_type);
		if (return_type.equals("LeafClass"))
			returnType = QueryReturnType.LEAFCLASS;
		else if (return_type.equals("ObjectRef"))
			returnType = QueryReturnType.OBJECTREF;
		else if (return_type.equals("LeafClassWithRepositoryItem"))
			returnType = QueryReturnType.LEAFCLASSWITHDOCUMENT;
		
		else
			er.err(XdsErrorCode.Code.XDSRegistryError, "/AdhocQueryRequest/ResponseOption/@returnType must be LeafClass or ObjectRef or for some special queries LeafClassWithRepositoryItem. Found value "
					+ return_type, this, EbRS.ReturnTypes);

		OMElement adhoc_query = MetadataSupport.firstChildWithLocalName(ahqr, "AdhocQuery") ;
		if (adhoc_query == null) {
			throw new XdsInternalException("Cannot find /AdhocQueryRequest/AdhocQuery element");
		}
		
		homeCommunityId = adhoc_query.getAttributeValue(MetadataSupport.home_qname);

		ParamParser parser = new ParamParser();
		params = parser.parse(ahqr);

		if (log_message != null)
			log_message.addOtherParam("Parameters", params.toString());

		if (response == null) {
			if (log_message != null)
				log_message.addOtherParam("XXXX Allocating new Response!!!!", "");
			response = new AdhocQueryResponse(Response.version_3);
		}

		query_id = adhoc_query.getAttributeValue(MetadataSupport.id_qname);

		StoredQuerySupport sqs = new StoredQuerySupport(params, returnType, er, log_message, is_secure);

		buildStoredQueryHandler(sqs); // this goes to a sub-class that knows about a specific implementation



	}
	protected void setTestMessage(String sqName) {
		if (log_message == null)
			return;

		if (service_name == null)
			log_message.setTestMessage(sqName);
		else
			log_message.setTestMessage(service_name + " " + sqName);

	}

	public StoredQuery getImpl() {
		return storedQueryImpl;
	}

	public void setLogMessage(LogMessage log_message) { this.log_message = log_message; }

	public Metadata run() throws XDSRegistryOutOfResourcesException, XdsException, LoggerException {
		if (storedQueryImpl == null)
			throw new XdsInternalException("storedQueryImpl is null");
		return storedQueryImpl.run();
	}

	/**
	 * Stored Query API call. Relies on implementation specific sub-class to implement.
	 * @param sqs
	 * @return Metadata object
	 * @throws XdsException
	 * @throws LoggerException
	 * @throws XDSRegistryOutOfResourcesException 
	 */
	abstract public Metadata FindDocuments(StoredQuerySupport sqs) throws XdsException, LoggerException, XDSRegistryOutOfResourcesException;
	/**
	 * Stored Query API call. Relies on implementation specific sub-class to implement.
	 * @param sqs
	 * @return Metadata object
	 * @throws XdsException
	 * @throws LoggerException
	 */
	abstract public Metadata FindFolders(StoredQuerySupport sqs) throws XdsException, LoggerException;
	/**
	 * Stored Query API call. Relies on implementation specific sub-class to implement.
	 * @param sqs
	 * @return Metadata object
	 * @throws XdsException
	 * @throws LoggerException
	 */
	abstract public Metadata FindSubmissionSets(StoredQuerySupport sqs) throws XdsException, LoggerException;
	/**
	 * Stored Query API call. Relies on implementation specific sub-class to implement.
	 * @param sqs
	 * @return Metadata object
	 * @throws XdsException
	 * @throws LoggerException
	 */
	abstract public Metadata GetAssociations(StoredQuerySupport sqs) throws XdsException, LoggerException;
	/**
	 * Stored Query API call. Relies on implementation specific sub-class to implement.
	 * @param sqs
	 * @return Metadata object
	 * @throws XdsException
	 * @throws LoggerException
	 */
	abstract public Metadata GetDocuments(StoredQuerySupport sqs) throws XdsException, LoggerException;
	/**
	 * Stored Query API call. Relies on implementation specific sub-class to implement.
	 * @param sqs
	 * @return Metadata object
	 * @throws XdsException
	 * @throws LoggerException
	 */
	abstract public Metadata GetDocumentsAndAssociations(StoredQuerySupport sqs) throws XdsException, LoggerException;
	/**
	 * Stored Query API call. Relies on implementation specific sub-class to implement.
	 * @param sqs
	 * @return Metadata object
	 * @throws XdsException
	 * @throws LoggerException
	 */
	abstract public Metadata GetFolderAndContents(StoredQuerySupport sqs) throws XdsException, LoggerException;
	/**
	 * Stored Query API call. Relies on implementation specific sub-class to implement.
	 * @param sqs
	 * @return Metadata object
	 * @throws XdsException
	 * @throws LoggerException
	 */
	abstract public Metadata GetFolders(StoredQuerySupport sqs) throws XdsException, LoggerException;
	/**
	 * Stored Query API call. Relies on implementation specific sub-class to implement.
	 * @param sqs
	 * @return Metadata object
	 * @throws XdsException
	 * @throws LoggerException
	 */
	abstract public Metadata GetFoldersForDocument(StoredQuerySupport sqs) throws XdsException, LoggerException;
	/**
	 * Stored Query API call. Relies on implementation specific sub-class to implement.
	 * @param sqs
	 * @return Metadata object
	 * @throws XdsException
	 * @throws LoggerException
	 */
	abstract public Metadata GetRelatedDocuments(StoredQuerySupport sqs) throws XdsException, LoggerException;
	/**
	 * Stored Query API call. Relies on implementation specific sub-class to implement.
	 * @param sqs
	 * @return Metadata object
	 * @throws XdsException
	 * @throws LoggerException
	 */
	abstract public Metadata GetSubmissionSetAndContents(StoredQuerySupport sqs) throws XdsException, LoggerException;
	/**
	 * Stored Query API call. Relies on implementation specific sub-class to implement.
	 * @param sqs
	 * @return Metadata object
	 * @throws XdsException
	 * @throws LoggerException
	 */
	abstract public Metadata GetSubmissionSets(StoredQuerySupport sqs) throws XdsException, LoggerException;
}
