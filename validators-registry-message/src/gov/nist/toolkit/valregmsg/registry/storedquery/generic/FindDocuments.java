package gov.nist.toolkit.valregmsg.registry.storedquery.generic;

import gov.nist.toolkit.docref.EbRim;
import gov.nist.toolkit.docref.SqDocRef;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.registrysupport.logging.LoggerException;
import gov.nist.toolkit.valregmsg.registry.SQCodedTerm;
import gov.nist.toolkit.valregmsg.registry.storedquery.generic.StoredQueryFactory.QueryReturnType;
import gov.nist.toolkit.valregmsg.registry.storedquery.support.StoredQuerySupport;
import gov.nist.toolkit.xdsexception.MetadataException;
import gov.nist.toolkit.xdsexception.MetadataValidationException;
import gov.nist.toolkit.xdsexception.XDSRegistryOutOfResourcesException;
import gov.nist.toolkit.xdsexception.XdsException;
import gov.nist.toolkit.xdsexception.XdsInternalException;

import java.util.ArrayList;
import java.util.List;

/**
Generic implementation of FindDocuments Stored Query. This class knows how to parse a 
 * FindDocuments Stored Query request producing a collection of instance variables describing
 * the request.  A sub-class must provide the runImplementation() method that uses the pre-parsed
 * information about the stored query and queries a metadata database.
 * @author bill
 *
 */
abstract public class FindDocuments extends StoredQuery {

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
	 * @throws MetadataValidationException
	 */
	public FindDocuments(StoredQuerySupport sqs) throws MetadataValidationException {
		super(sqs);
	}

	/**
	 * Implementation of Stored Query specific logic including parsing and validating parameters.
	 * @throws XdsInternalException
	 * @throws XdsException
	 * @throws LoggerException
	 * @throws XDSRegistryOutOfResourcesException
	 */
	public Metadata runSpecific() throws XdsInternalException, XdsException, LoggerException, XDSRegistryOutOfResourcesException {

		validateParameters();

		parseParameters();

		if (sqs.returnType == QueryReturnType.LEAFCLASS || sqs.returnType == QueryReturnType.LEAFCLASSWITHDOCUMENT) {
			QueryReturnType save = sqs.returnType;

			// since the Public Registry gets some crazy requests, first do an ObjectRefs query to see how many 
			// results are planned.  If not out of order then do the real query for LeafClass

			sqs.returnType = QueryReturnType.OBJECTREF;

			Metadata m = runImplementation();
			if (m.getObjectRefs().size() > 25) 
				throw new XDSRegistryOutOfResourcesException("GetDocuments Stored Query for LeafClass is limited to 25 documents on this Registry. Your query targeted " + m.getObjectRefs().size() + " documents");


			sqs.returnType = save;;
		}


		Metadata m = runImplementation();

		if (sqs.log_message != null)
			sqs.log_message.addOtherParam("Results structure", m.structure());

		return m;
	}


	public void validateParameters() throws MetadataValidationException {
		//                         param name,                                 required?, multiple?, is string?,   is code?,      support AND/OR                          alternative
		sqs.validate_parm("$XDSDocumentEntryPatientId",                         true,      false,     true,         false,           false,                             (String[])null												);
		sqs.validate_parm("$XDSDocumentEntryClassCode",                         false,     true,      true,         true,            false,                              (String[])null												);
		sqs.validate_parm("$XDSDocumentEntryTypeCode",                         false,     true,      true,         true,             false,                             (String[])null												);
		sqs.validate_parm("$XDSDocumentEntryPracticeSettingCode",               false,     true,      true,         true,            false,                              (String[])null												);
		sqs.validate_parm("$XDSDocumentEntryCreationTimeFrom",                  false,     false,     true,         false,           false,                               (String[])null												);
		sqs.validate_parm("$XDSDocumentEntryCreationTimeTo",                    false,     false,     true,         false,           false,                               (String[])null												);
		sqs.validate_parm("$XDSDocumentEntryServiceStartTimeFrom",              false,     false,     true,         false,           false,                               (String[])null												);
		sqs.validate_parm("$XDSDocumentEntryServiceStartTimeTo",                false,     false,     true,         false,           false,                               (String[])null												);
		sqs.validate_parm("$XDSDocumentEntryServiceStopTimeFrom",               false,     false,     true,         false,           false,                               (String[])null												);
		sqs.validate_parm("$XDSDocumentEntryServiceStopTimeTo",                 false,     false,     true,         false,           false,                               (String[])null												);
		sqs.validate_parm("$XDSDocumentEntryHealthcareFacilityTypeCode",        false,     true,      true,         true,            false,                              (String[])null												);
		sqs.validate_parm("$XDSDocumentEntryEventCodeList",                     false,     true,      true,         true,            true,                                    (String[])null												);
		sqs.validate_parm("$XDSDocumentEntryConfidentialityCode",               false,     true,      true,         true,            true,                              (String[])null												);
		sqs.validate_parm("$XDSDocumentEntryFormatCode",                        false,     true,      true,         true,            false,                              (String[])null												);
		sqs.validate_parm("$XDSDocumentEntryStatus",                            true,      true,      true,         false,           false,                               (String[])null												);
		sqs.validate_parm("$XDSDocumentEntryAuthorPerson",                      false,     true,     true,          false,           false,                               (String[])null												);

		if (sqs.has_validation_errors) 
			throw new MetadataValidationException(QueryParmsErrorPresentErrMsg, SqDocRef.Individual_query_parms);

	}



	protected String    patient_id;
	protected String    creation_time_from;
	protected String    creation_time_to;
	protected String    service_start_time_from;
	protected String    service_start_time_to;
	protected String    service_stop_time_from;
	protected String    service_stop_time_to;
	protected SQCodedTerm format_codes;
	protected List<String> status;
	protected List<String> author_person;
	
	protected SQCodedTerm class_codes;
	protected SQCodedTerm type_codes;
	protected SQCodedTerm practice_setting_codes;
	protected SQCodedTerm hcft_codes;
	protected SQCodedTerm event_codes;
	protected SQCodedTerm conf_codes;
		
	void toBuffer(StringBuffer buf, String name, String arg) {
		if (arg != null && !arg.equals(""))
			buf.append(name).append("=").append(arg).append("\n");
	}
	
	void toBuffer(StringBuffer buf, String name, SQCodedTerm arg) {
		if (arg != null && !arg.equals(""))
			buf.append(name).append("=").append(arg).append("\n");
	}
		
	public String toString() {
		StringBuffer buf = new StringBuffer();
		
		buf.append("FindDocuments: [\n");

		toBuffer(buf, "patient_id", patient_id);
		toBuffer(buf, "class_codes", class_codes);
		toBuffer(buf, "practice_setting_codes", practice_setting_codes);
		toBuffer(buf, "hcft_codes", hcft_codes);
		toBuffer(buf, "event_codes", event_codes);
		toBuffer(buf, "conf_codes", conf_codes);
		
		buf.append("]\n");
		
		return buf.toString();
	}

	void parseParameters() throws XdsInternalException, XdsException, LoggerException {

		patient_id                        = sqs.params.getStringParm   ("$XDSDocumentEntryPatientId");
		class_codes                       = sqs.params.getCodedParm("$XDSDocumentEntryClassCode");
		type_codes                       = sqs.params.getCodedParm("$XDSDocumentEntryTypeCode");
		practice_setting_codes            = sqs.params.getCodedParm("$XDSDocumentEntryPracticeSettingCode");
		creation_time_from                = sqs.params.getIntParm      ("$XDSDocumentEntryCreationTimeFrom");
		creation_time_to                  = sqs.params.getIntParm      ("$XDSDocumentEntryCreationTimeTo");
		service_start_time_from           = sqs.params.getIntParm      ("$XDSDocumentEntryServiceStartTimeFrom");
		service_start_time_to             = sqs.params.getIntParm      ("$XDSDocumentEntryServiceStartTimeTo");
		service_stop_time_from            = sqs.params.getIntParm      ("$XDSDocumentEntryServiceStopTimeFrom");
		service_stop_time_to              = sqs.params.getIntParm      ("$XDSDocumentEntryServiceStopTimeTo");
		hcft_codes                        = sqs.params.getCodedParm("$XDSDocumentEntryHealthcareFacilityTypeCode");
		event_codes                       = sqs.params.getCodedParm("$XDSDocumentEntryEventCodeList");
		conf_codes                        = sqs.params.getCodedParm("$XDSDocumentEntryConfidentialityCode");
		format_codes                      = sqs.params.getCodedParm("$XDSDocumentEntryFormatCode");
		status                            = sqs.params.getListParm("$XDSDocumentEntryStatus");
		author_person                     = sqs.params.getListParm("$XDSDocumentEntryAuthorPerson");


		String status_ns_prefix = MetadataSupport.status_type_namespace;

		ArrayList<String> new_status = new ArrayList<String>();
		for (int i=0; i<status.size(); i++) {
			String stat = (String) status.get(i);

			if ( ! stat.startsWith(status_ns_prefix)) 
				throw new MetadataValidationException("Status parameter must have namespace prefix " + status_ns_prefix + " found " + stat, EbRim.RegistryObject_attributes);
			new_status.add(stat.replaceFirst(status_ns_prefix, ""));
		}
		status = new_status;

		if (sqs.log_message != null)
			sqs.log_message.addOtherParam("Some Parameters", toString());

		
	}



}
