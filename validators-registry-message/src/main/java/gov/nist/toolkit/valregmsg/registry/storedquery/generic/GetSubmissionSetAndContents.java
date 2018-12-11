package gov.nist.toolkit.valregmsg.registry.storedquery.generic;

import gov.nist.toolkit.commondatatypes.MetadataSupport;
import gov.nist.toolkit.docref.SqDocRef;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrysupport.logging.LoggerException;
import gov.nist.toolkit.valregmsg.registry.SQCodedTerm;
import gov.nist.toolkit.valregmsg.registry.storedquery.support.StoredQuerySupport;
import gov.nist.toolkit.xdsexception.client.MetadataException;
import gov.nist.toolkit.xdsexception.client.MetadataValidationException;
import gov.nist.toolkit.xdsexception.client.XdsException;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;

import java.util.ArrayList;
import java.util.List;

/**
Generic implementation of GetAssociations Stored Query. This class knows how to parse a 
 * GetAssociations Stored Query request producing a collection of instance variables describing
 * the request.  A sub-class must provide the runImplementation() method that uses the pre-parsed
 * information about the stored query and queries a metadata database.
 * @author bill
 *
 */
abstract public class GetSubmissionSetAndContents extends StoredQuery {
	
	/**
	 * Method required in subclasses (implementation specific class) to define specific
	 * linkage to local database
	 * @return matching metadata
	 * @throws MetadataException
	 * @throws XdsException
	 * @throws LoggerException
	 */
	abstract protected Metadata runImplementation() throws MetadataException, XdsException, LoggerException;

	/**
	 * Basic constructor
	 * @param sqs
	 */
	public GetSubmissionSetAndContents(StoredQuerySupport sqs) {
		super(sqs);
	}
	
	public void validateParameters() throws MetadataValidationException {


		//                         param name,                        required?, multiple?, is string?,   is code?,  AND/OR ok?,   alternative
		sqs.validate_parm("$XDSSubmissionSetEntryUUID",                true,      false,     true,         false,     false,       "$XDSSubmissionSetUniqueId");
		sqs.validate_parm("$XDSSubmissionSetUniqueId",                 true,      false,     true,         false,     false,       "$XDSSubmissionSetEntryUUID");
		sqs.validate_parm("$XDSDocumentEntryFormatCode",               false,     true,      true,         true,      false,      (String[])null);
		sqs.validate_parm("$XDSDocumentEntryConfidentialityCode",      false,     true,      true,         true,      true,      (String[])null);
		// DocumentEntry level
		sqs.validate_parm("$XDSDocumentEntryType",                         		false,     true,      true,         false,           false,                             (String[])null												);

		if (sqs.has_validation_errors) 
			throw new MetadataValidationException(QueryParmsErrorPresentErrMsg, SqDocRef.Individual_query_parms);
	}
	
	protected String ss_uuid;
	protected String ss_uid;
	protected SQCodedTerm format_code;
	protected SQCodedTerm conf_code;
	protected List<String> entry_type;

	protected void parseParameters() throws XdsInternalException, XdsException, LoggerException {
		super.parseParameters();
		ss_uuid = sqs.params.getStringParm("$XDSSubmissionSetEntryUUID");
		ss_uid = sqs.params.getStringParm("$XDSSubmissionSetUniqueId");
		format_code = sqs.params.getCodedParm("$XDSDocumentEntryFormatCode");
		conf_code = sqs.params.getCodedParm("$XDSDocumentEntryConfidentialityCode");
		entry_type = sqs.params.getListParm("$XDSDocumentEntryType");

		if (entry_type==null) {
			// Rev. 13 Vol 2a 3.18.4.1.2.3.6.2 Valid DocumentEntryType Parameter Values
			entry_type = new ArrayList<>();
			entry_type.add(MetadataSupport.XDSDocumentEntry_objectType_uuid);
		}
	}


	/**
	 * Implementation of Stored Query specific logic including parsing and validating parameters.
	 * @throws XdsException
	 * @throws LoggerException
	 */
	public Metadata runSpecific() throws XdsException, LoggerException {

		validateParameters();
		parseParameters();

		if (ss_uuid == null && ss_uid == null) 
			throw new XdsInternalException("GetSubmissionSetAndContents Stored Query");
		return runImplementation();
	}



}
