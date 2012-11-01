package gov.nist.toolkit.registrysupport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;

public class MetadataSupport {
	static public OMFactory om_factory = OMAbstractFactory.getOMFactory();

	static public String ebRSns2_uri = "urn:oasis:names:tc:ebxml-regrep:registry:xsd:2.1";
	static public OMNamespace ebRSns2 =  om_factory.createOMNamespace(ebRSns2_uri, "rs");

	static public String ebRIMns2_uri = "urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.1";
	static public OMNamespace ebRIMns2 = om_factory.createOMNamespace(ebRIMns2_uri, "rim");

	static public String ebQns2_uri = "urn:oasis:names:tc:ebxml-regrep:query:xsd:2.1";
	static public OMNamespace ebQns2 =   om_factory.createOMNamespace(ebQns2_uri, "query");

	static public boolean isV2Namespace(String ns) { return ns != null && (ns.equals(ebRSns2_uri) || ns.equals(ebRIMns2_uri) || ns.equals(ebQns2_uri)); }
	static public boolean isV2Namespace(OMNamespace ns) { return isV2Namespace(ns.getNamespaceURI()); }

	static public String ebRSns3_uri = "urn:oasis:names:tc:ebxml-regrep:xsd:rs:3.0";
	static public OMNamespace ebRSns3 =  om_factory.createOMNamespace(ebRSns3_uri, "rs");

	static public String ebRIMns3_uri = "urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0";
	static public OMNamespace ebRIMns3 = om_factory.createOMNamespace(ebRIMns3_uri, "rim");

	static public String ebQns3_uri = "urn:oasis:names:tc:ebxml-regrep:xsd:query:3.0";
	static public OMNamespace ebQns3 =   om_factory.createOMNamespace(ebQns3_uri, "query");

	static public String ebLcm3_uri = "urn:oasis:names:tc:ebxml-regrep:xsd:lcm:3.0";
	static public OMNamespace ebLcm3 =   om_factory.createOMNamespace(ebLcm3_uri, "lcm");

	static public String xdsB_uri = "urn:ihe:iti:xds-b:2007";
	static public OMNamespace xdsB =   om_factory.createOMNamespace(xdsB_uri, "xdsb");
	
	static public OMNamespace xop_include = om_factory.createOMNamespace("http://www.w3.org/2004/08/xop/include", "xopinclude");

	static public String xdsB_eb_assoc_namespace_uri = "urn:oasis:names:tc:ebxml-regrep:AssociationType";
	static public String xdsB_ihe_assoc_namespace_uri = "urn:ihe:iti:2007:AssociationType";
	
	static public String ws_addressing_namespace_uri = "http://www.w3.org/2005/08/addressing";
	static public OMNamespace ws_addressing_namespace = om_factory.createOMNamespace(ws_addressing_namespace_uri, "wsa");
	static public String oasis_wsnb2_namespace_uri = "http://docs.oasis-open.org/wsn/b-2";
	static public OMNamespace oasis_wsnb2_namespace = om_factory.createOMNamespace(oasis_wsnb2_namespace_uri, "wsnt");

	static public QName slot_qnamens = new QName(ebRIMns3_uri, "Slot", "rim");
	static public QName valuelist_qnamens = new QName(ebRIMns3_uri, "ValueList", "rim");
	static public QName value_qnamens = new QName(ebRIMns3_uri, "Value", "rim");
	
	static public QName externalidentifier_qnamens = new QName(ebRIMns3_uri, "ExternalIdentifier", "rim");
	static public QName name_qnamens = new QName(ebRIMns3_uri, "Name", "rim");
	static public QName description_qnamens = new QName(ebRIMns3_uri, "Description", "rim");
	static public QName localizedstring_qnamens = new QName(ebRIMns3_uri, "LocalizedString", "rim");
	static public QName classification_qnamens = new QName(ebRIMns3_uri, "Classification", "rim");
	static public QName extrinsicobject_qnamens = new QName(ebRIMns3_uri, "ExtrinsicObject", "rim");
	static public QName registrypackage_qnamens = new QName(ebRIMns3_uri, "RegistryPackage", "rim");
	static public QName association_qnamens = new QName(ebRIMns3_uri, "Association", "rim");

	static public QName versioninfo_qnamens = new QName(ebRIMns3_uri, "VersionInfo", "rim");
	static public QName versionname_qname = new QName("versionName");

	static public String soap_env_uri = "http://www.w3.org/2003/05/soap-envelope";
	static public OMNamespace soap_env_namespace = om_factory.createOMNamespace(soap_env_uri, "s");
	static public String fault_pre = "fault";
	static public QName fault_qnamens = new QName(soap_env_uri, "Fault", fault_pre);
	static public QName fault_code_qnamens = new QName(soap_env_uri, "Code", fault_pre);
	static public QName fault_value_qnamens = new QName(soap_env_uri, "Value", fault_pre);
	static public QName fault_reason_qnamens = new QName(soap_env_uri, "Reason", fault_pre);
	static public QName fault_detail_qnamens = new QName(soap_env_uri, "Detail", fault_pre);
	static public QName fault_text_qnamens = new QName(soap_env_uri, "Text", fault_pre);
	
	static public QName soap_env_qnamens = new QName(soap_env_uri, "Envelope", "S");
	static public QName soap_hdr_qnamens = new QName(soap_env_uri, "Header", "S");
	static public QName soap_body_qnamens = new QName(soap_env_uri, "Body", "S");
	
	static public boolean isV3Namespace(String ns) { return ns != null && (ns.equals(ebRSns3_uri) || ns.equals(ebRIMns3_uri) || ns.equals(ebQns3_uri) || ns.equals(ebLcm3_uri) || ns.equals(xdsB_uri)); }
	static public boolean isV3Namespace(OMNamespace ns) { return isV3Namespace(ns.getNamespaceURI()); }

	static public OMNamespace xml_namespace =   om_factory.createOMNamespace("http://www.w3.org/XML/1998/namespace", "xml");

	static public String status_type_namespace ="urn:oasis:names:tc:ebxml-regrep:StatusType:";
	static public String response_status_type_namespace = "urn:oasis:names:tc:ebxml-regrep:ResponseStatusType:";
	static public String ihe_response_status_type_namespace = "urn:ihe:iti:2007:ResponseStatusType:";
	static public String error_severity_type_namespace = "urn:oasis:names:tc:ebxml-regrep:ErrorSeverityType:";
	static public String error_severity = error_severity_type_namespace + "Error";
	static public String warning_severity = error_severity_type_namespace + "Warning";
	static public String association_type_namespace = "urn:oasis:names:tc:ebxml-regrep:AssociationType:";

	static public String document_entry_object_type = "urn:uuid:7edca82f-054d-47f2-a032-9b2a5b5186c1";
	
	static public QName href_qname = new QName("href");
	
	public static QName name_qname = new QName("Name");
	static public QName value_qname = new QName("value");

	public static QName object_type_qname = new QName("objectType");

	public static QName return_type_qname = new QName("returnType");

	public static QName slot_name_qname = new QName("name");

	public static QName identificationscheme_qname = new QName("identificationScheme");

	static public QName value_att_qname = new QName("value");

	public static QName classificationscheme_qname   = new QName("classificationScheme");

	public static QName noderepresentation_qname = new QName("nodeRepresentation");

	public static QName classificationnode_qname = new QName("classificationNode");

	public static QName id_qname = new QName("id");

	public static QName lid_qname = new QName("lid");

	public static QName registry_object_qname = new QName("registryObject");

	public static QName association_type_qname = new QName("associationType");

	public static QName source_object_qname = new QName("sourceObject");

	public static QName target_object_qname = new QName("targetObject");

	public static QName classified_object_qname = new QName("classifiedObject");

	public static QName mime_type_qname = new QName("mimeType");

	public static QName home_qname = new QName("home");

	public static QName code_qname = new QName("code");

	public static QName codingscheme_qname = new QName("codingScheme");

	public static QName classscheme_qname = new QName("classScheme");

	public static QName status_qname = new QName("status");

	public static QName severity_qname = new QName("severity");

	public static QName home_community_id_qname = new QName(xdsB.getNamespaceURI(), "HomeCommunityId", "xdsb");
	
	public static QName code_context_qname = new QName("codeContext");
	
	public static QName error_code_qname = new QName("errorCode");
	
	public static QName location_qname = new QName("location");
	
	public static QName registry_errorlist_qname = new QName(ebRSns3_uri, "RegistryErrorList");
	
	public static QName expected_error_message_qname = new QName("ExpectedErrorMessage");
	
	public static QName document_response_qnamens = new QName(xdsB.getNamespaceURI(), "DocumentResponse", "xdsb");
	public static QName retrieve_document_set_response_qnamens = new QName(xdsB.getNamespaceURI(), "RetrieveDocumentSetResponse", "xdsb");
	public static QName repository_unique_id_qnamens = new QName(xdsB.getNamespaceURI(), "RepositoryUniqueId", "xdsb");
	public static QName document_unique_id_qnamens = new QName(xdsB.getNamespaceURI(), "DocumentUniqueId", "xdsb");
	public static QName mimetype_qnamens = new QName(xdsB.getNamespaceURI(), "mimeType", "xdsb");
	public static QName document_qnamens = new QName(xdsB.getNamespaceURI(), "Document", "xdsb");
	
	public static QName xop_include_qname = new QName(xop_include.getNamespaceURI(), "Include", "xop");

	public static OMNamespace soapns = om_factory.createOMNamespace("http://www.w3.org/2003/05/soap-envelope", "soapenv");
	public static OMNamespace wsans = om_factory.createOMNamespace("http://www.w3.org/2005/08/addressing", "wsa");
	public static QName must_understand_qname = new QName("http://www.w3.org/2003/05/soap-envelope","mustUnderstand");

	public static QName v2ObjectRefQName = new QName(MetadataSupport.ebRIMns2_uri, "ObjectRef");
	public static QName v3ObjectRefQName = new QName(MetadataSupport.ebRIMns3_uri, "ObjectRef");
	
	public static String epsos_ns_uri = "urn:ihe:iti:xds-ebrim:extensions:2010";
	public static OMNamespace epsos_rimext_ns = om_factory.createOMNamespace(epsos_ns_uri, "rimext");
	public static QName epsos_document_qnamens = new QName(epsos_ns_uri, "Document");
	public static QName epsos_extrinsicobject_qnamens = new QName(epsos_ns_uri, "ExtrinsicObject");

	public static QName xop_include_qnamens = new QName("http://www.w3.org/2004/08/xop/include", "Include", "xop");

	public static QName xop_content_type_qnamens = new QName("http://www.w3.org/2004/11/xmlmime","contentType");
	
	// Status values
	
	public static String status_success = "urn:oasis:names:tc:ebxml-regrep:ResponseStatusType:Success";
	public static String status_partial_success = "urn:ihe:iti:2007:ResponseStatusType:PartialSuccess";
	public static String status_failure = "urn:oasis:names:tc:ebxml-regrep:ResponseStatusType:Failure";
	
	// Association types
	
	public static String assoctype_has_member = "urn:oasis:names:tc:ebxml-regrep:AssociationType:HasMember";
	public static String assoctype_rplc = "urn:ihe:iti:2007:AssociationType:RPLC";
	public static String assoctype_xfrm = "urn:ihe:iti:2007:AssociationType:XFRM";
	public static String assoctype_apnd = "urn:ihe:iti:2007:AssociationType:APND";
	public static String assoctype_xfrm_rplc = "urn:ihe:iti:2007:AssociationType:XFRM_RPLC";
	public static String assoctype_signs = "urn:ihe:iti:2007:AssociationType:signs";
	
	// Additional MU & on-demand Association types
	
	public static String assoctype_update_availabilityStatus = "urn:ihe:iti:2010:AssociationType:UpdateAvailabilityStatus";
	public static String assoctype_submitAssociation = "urn:ihe:iti:2010:AssociationType:SubmitAssociation";
	
	public static String assoctype_isSnapshotOf = "urn:ihe:iti:2010:AssociationType:IsSnapshotOf";
	
	public static List<String> relationship_associations = 
		Arrays.asList(
				assoctype_rplc,
				assoctype_xfrm,
				assoctype_apnd,
				assoctype_xfrm_rplc
				);

	// Association slots
	
	public static String assoc_slot_submission_set_status = "SubmissionSetStatus";
	
	// Stored Query query ids

	public static String SQ_FindDocuments = "urn:uuid:14d4debf-8f97-4251-9a74-a90016b0af0d";
	public static String SQ_FindSubmissionSets = "urn:uuid:f26abbcb-ac74-4422-8a30-edb644bbc1a9";
	public static String SQ_FindFolders = "urn:uuid:958f3006-baad-4929-a4de-ff1114824431";
	public static String SQ_GetAll = "urn:uuid:10b545ea-725c-446d-9b95-8aeb444eddf3";
	public static String SQ_GetDocuments = "urn:uuid:5c4f972b-d56b-40ac-a5fc-c8ca9b40b9d4";
	public static String SQ_GetFolders = "urn:uuid:5737b14c-8a1a-4539-b659-e03a34a5e1e4";
	public static String SQ_GetAssociations = "urn:uuid:a7ae438b-4bc2-4642-93e9-be891f7bb155";
	public static String SQ_GetDocumentsAndAssociations = "urn:uuid:bab9529a-4a10-40b3-a01f-f68a615d247a";
	public static String SQ_GetSubmissionSets = "urn:uuid:51224314-5390-4169-9b91-b1980040715a";
	public static String SQ_GetSubmissionSetAndContents = "urn:uuid:e8e3cb2c-e39c-46b9-99e4-c12f57260b83";
	public static String SQ_GetFolderAndContents = "urn:uuid:b909a503-523d-4517-8acf-8e5834dfc4c7";
	public static String SQ_GetFoldersForDocument = "urn:uuid:10cae35a-c7f9-4cf5-b61e-fc3278ffb578";
	public static String SQ_GetRelatedDocuments = "urn:uuid:d90e5407-b356-4d91-a89f-873917b4b0e6";
	
	public static boolean isSQId(String id) {
		if (id == null) return false;
		if (id.equals(SQ_FindDocuments)) return true;
		if (id.equals(SQ_FindSubmissionSets)) return true;
		if (id.equals(SQ_FindFolders)) return true;
		if (id.equals(SQ_GetAll)) return true;
		if (id.equals(SQ_GetDocuments)) return true;
		if (id.equals(SQ_GetFolders)) return true;
		if (id.equals(SQ_GetAssociations)) return true;
		if (id.equals(SQ_GetDocumentsAndAssociations)) return true;
		if (id.equals(SQ_GetSubmissionSets)) return true;
		if (id.equals(SQ_GetSubmissionSetAndContents)) return true;
		if (id.equals(SQ_GetFolderAndContents)) return true;
		if (id.equals(SQ_GetFoldersForDocument)) return true;
		if (id.equals(SQ_GetRelatedDocuments)) return true;
		return false;
	}
	
	public static String getSQName(String id) {
		if (id == null) return "";
		if (id.equals(SQ_FindDocuments)) return "FindDocuments";
		if (id.equals(SQ_FindSubmissionSets)) return "FindSubmissionSets";
		if (id.equals(SQ_FindFolders)) return "FindFolders";
		if (id.equals(SQ_GetAll)) return "GetAll";
		if (id.equals(SQ_GetDocuments)) return "GetDocuments";
		if (id.equals(SQ_GetFolders)) return "GetFolders";
		if (id.equals(SQ_GetAssociations)) return "GetAssociations";
		if (id.equals(SQ_GetDocumentsAndAssociations)) return "GetDocumentsAndAssociations";
		if (id.equals(SQ_GetSubmissionSets)) return "GetSubmissionSets";
		if (id.equals(SQ_GetSubmissionSetAndContents)) return "GetSubmissionSetAndContents";
		if (id.equals(SQ_GetFolderAndContents)) return "GetFolderAndContents";
		if (id.equals(SQ_GetFoldersForDocument)) return "GetFoldersForDocument";
		if (id.equals(SQ_GetRelatedDocuments)) return "GetRelatedDocuments";
		return "";
	}
	
	public static String SQ_action = "urn:ihe:iti:2007:RegistryStoredQuery";
	public static String MPQ_action = "urn:ihe:iti:2009:MultiPatientStoredQuery";
	
	// multi-patient stored query ids
	public static String SQ_FindDocumentsForMultiplePatients = "urn:uuid:3d1bdb10-39a2-11de-89c2-2f44d94eaa9f";
	public static String SQ_FindFoldersForMultiplePatients = "urn:uuid:50d3f5ac-39a2-11de-a1ca-b366239e58df";

	public static boolean isMPQId(String id) {
		if (id == null) return false;
		if (id.equals(SQ_FindDocumentsForMultiplePatients)) return true;
		if (id.equals(SQ_FindFoldersForMultiplePatients)) return true;
		return false;
	}
	

	
	// uuids defined 

	public static String XDSSubmissionSet_patientid_uuid = "urn:uuid:6b5aea1a-874d-4603-a4bc-96a0a7b38446";
	public static String XDSDocumentEntry_patientid_uuid = "urn:uuid:58a6f841-87b3-4a3e-92fd-a8ffeff98427";
	public static String XDSFolder_patientid_uuid = "urn:uuid:f64ffdf0-4b97-4e06-b79f-a52b38ec2f8a";

	public static String XDSSubmissionSet_uniqueid_uuid = "urn:uuid:96fdda7c-d067-4183-912e-bf5ee74998a8";
	public static String XDSDocumentEntry_uniqueid_uuid = "urn:uuid:2e82c1f6-a085-4c72-9da3-8640a32e42ab";
	public static String XDSFolder_uniqueid_uuid = "urn:uuid:75df8f67-9973-4fbe-a900-df66cefecc5a";

	public static String XDSSubmissionSet_author_uuid = "urn:uuid:a7058bb9-b4e4-4307-ba5b-e3f0ab85e12d";
	public static String XDSDocumentEntry_author_uuid = "urn:uuid:93606bcf-9494-43ec-9b4e-a7748d1a838d";

	public static String XDSSubmissionSet_sourceid_uuid="urn:uuid:554ac39e-e3fe-47fe-b233-965d2a147832";

	public static String XDSDocumentEntry_objectType_uuid="urn:uuid:7edca82f-054d-47f2-a032-9b2a5b5186c1";

	public static String XDSSubmissionSet_classification_uuid = "urn:uuid:a54d6aa5-d40d-43f9-88c5-b4633d873bdd";
	public static String XDSFolder_classification_uuid = "urn:uuid:d9d542f3-6cc4-48b6-8870-ea235fbc94c2";

	public static String XDSDocumentEntry_formatCode_uuid="urn:uuid:a09d5840-386c-46f2-b5ad-9c3699a4309d";
	public static String XDSDocumentEntry_classCode_uuid="urn:uuid:41a5887f-8865-4c09-adf7-e362475b143a";
	public static String XDSDocumentEntry_psCode_uuid="urn:uuid:cccf5598-8b07-4b77-a05e-ae952c785ead";
	public static String XDSDocumentEntry_hcftCode_uuid="urn:uuid:f33fb8ac-18af-42cc-ae0e-ed0b0bdb91e1";
	public static String XDSDocumentEntry_eventCode_uuid="urn:uuid:2c6b8cb7-8b2a-4051-b291-b1ae6a575ef4";
	public static String XDSDocumentEntry_confCode_uuid="urn:uuid:f4f85eac-e6cb-4883-b524-f2705394840f";
	public static String XDSDocumentEntry_typeCode_uuid="urn:uuid:f0306f51-975f-434e-a61c-c59651d33983";
	
	public static String XDSSubmissionSet_contentTypeCode_uuid = "urn:uuid:aa543740-bdda-424e-8c96-df4873be8500";
	
	public static String XDSFolder_codeList_uuid="urn:uuid:1ba97051-7806-41a8-a48b-8fce7af683c5";
	
	public static String XDSAssociationDocumentation_uuid = "urn:uuid:abd807a3-4432-4053-87b4-fd82c643d1f3";
	
	public static String XDSSubmissionSet_limitedMetadata_uuid = "urn:uuid:5003a9db-8d8d-49e6-bf0c-990e34ac7707";
	public static String XDSDocumentEntry_limitedMetadata_uuid = "urn:uuid:ab9b591b-83ab-4d03-8f5d-f93b1fb92e85";
	public static String XDSFolder_limitedMetadata_uuid = "urn:uuid:2c144a76-29a9-4b7c-af54-b25409fe7d03";

	// XDS error codes	
	public static String XDSMissingDocument = "XDSMissingDocument";
	public static String XDSMissingDocumentMetadata = "XDSMissingDocumentMetadata";
	public static String XDSRegistryNotAvailable = "XDSRegistryNotAvailable";
	public static String XDSRegistryError = "XDSRegistryError";
	public static String XDSRepositoryError = "XDSRepositoryError";
	public static String XDSRepositoryWrongRepositoryUniqueId = "XDSRepositoryWrongRepositoryUniqueId";
	public static String XDSRegistryDuplicateUniqueIdInMessage = "XDSRegistryDuplicateUniqueIdInMessage";
	public static String XDSRepositoryDuplicateUniqueIdInMessage = "XDSRepositoryDuplicateUniqueIdInMessage";
	public static String XDSDuplicateUniqueIdInRegistry = "XDSDuplicateUniqueIdInRegistry";
	public static String XDSNonIdenticalHash = "XDSNonIdenticalHash";
	public static String XDSRegistryBusy = "XDSRegistryBusy";
	public static String XDSRepositoryBusy  = "XDSRepositoryBusy";
	public static String XDSRegistryOutOfResources = "XDSRegistryOutOfResources";
	public static String XDSRepositoryOutOfResources = "XDSRepositoryOutOfResources";
	public static String XDSRegistryMetadataError = "XDSRegistryMetadataError";
	public static String XDSRepositoryMetadataError = "XDSRepositoryMetadataError";
	public static String XDSTooManyResults = "XDSTooManyResults";
	public static String XDSExtraMetadataNotSaved = "XDSExtraMetadataNotSaved";
	public static String XDSUnknownPatientId = "XDSUnknownPatientId";
	public static String XDSPatientIdDoesNotMatch = "XDSPatientIdDoesNotMatch";
	public static String XDSUnknownStoredQuery = "XDSUnknownStoredQuery";
	public static String XDSStoredQueryMissingParam = "XDSStoredQueryMissingParam";
	public static String XDSStoredQueryParamNumber = "XDSStoredQueryParamNumber";
	public static String XDSSqlError = "XDSSqlError";

	public static String XDSUnknownRepositoryId = "XDSUnknownRepositoryId";

	public static String XDSMissingHomeCommunityId = "XDSMissingHomeCommunityId";
	public static String XDSUnknownCommunity = "XDSUnknownCommunity";
	public static String XDSUnavailableCommunity = "XDSUnavailableCommunity";


	public static QName response_option_qname = new QName("ResponseOption");
	public static QName adhoc_query_qname = new QName("AdhocQuery");
	
	// Update stuff
	
	public static String documentAvailability_online = "urn:ihe:iti:2010:DocumentAvailability:Online";
	public static String documentAvailability_offline = "urn:ihe:iti:2010:DocumentAvailability:Offline";
	
	// DSUB stuff
	
	public static String wsnt_ns_uri = "http://docs.oasis-open.org/wsn/b-2";
	public static OMNamespace wsnt_ns =  om_factory.createOMNamespace(wsnt_ns_uri, "wsnt");
	
	public static String dsub_ns_uri = "urn:ihe:iti:dsub:2009";
	public static OMNamespace dsub_ns =  om_factory.createOMNamespace(dsub_ns_uri, "dsub");
	public static String dsub_subscribe_action = "http://docs.oasis-open.org/wsn/bw-2/NotificationProducer/SubscribeRequest";
	public static String dsub_subscribe_response_action = "http://docs.oasis-open.org/wsn/bw-2/NotificationProducer/SubscribeResponse";

	public static OMElement firstChildWithLocalName(OMElement ele, String localName) {
		for (Iterator it=ele.getChildElements(); it.hasNext(); ) {
			OMElement child = (OMElement) it.next();
			if (child.getLocalName().equals(localName))
				return child;
		}
		return null;
	}

	public static OMElement firstChildWithLocalNameEndingWith(OMElement ele, String localNameSuffix) {
		for (Iterator it=ele.getChildElements(); it.hasNext(); ) {
			OMElement child = (OMElement) it.next();
			if (child.getLocalName().endsWith(localNameSuffix))
				return child;
		}
		return null;
	}

	public static List<OMElement> childrenWithLocalName(OMElement ele, String localName) {
		List<OMElement> al = new ArrayList<OMElement>();
		for (Iterator it=ele.getChildElements(); it.hasNext(); ) {
			OMElement child = (OMElement) it.next();
			if (child.getLocalName().equals(localName))
				al.add(child);
		}
		return al;
	}

	public static List<String> childrenLocalNames(OMElement ele) {
		List<String> al = new ArrayList<String>();
		for (Iterator it=ele.getChildElements(); it.hasNext(); ) {
			OMElement child = (OMElement) it.next();
				al.add(child.getLocalName());
		}
		return al;
	}

	/**
	 * Get child of ele with matching name and id attribute.
	 * @param ele
	 * @param localName
	 * @param id
	 * @return
	 */
	public static OMElement getChild(OMElement ele, String localName, String id) {
		for (Iterator it=ele.getChildElements(); it.hasNext(); ) {
			OMElement child = (OMElement) it.next();
			if (child.getLocalName().equals(localName)) {
				String idAttVal = child.getAttributeValue(MetadataSupport.id_qname);
				if (idAttVal != null && idAttVal.equals(id))
					return child;
			}
		}
		return null;
	}

	public static OMElement firstDecendentWithLocalName(OMElement ele, String localName) {
		List<OMElement> decendents = decendentsWithLocalName(ele, localName);
		if (decendents.size() == 0) return null;
		return decendents.get(0);
	}
	
	public static List<OMElement> decendentsWithLocalName(OMElement ele, String localName) {
		return decendentsWithLocalName(ele, localName, -1);
	}

		public static List<OMElement> decendentsWithLocalName(OMElement ele, String localName, int depth) {
		List<OMElement> al = new ArrayList<OMElement>();
		if (ele == null || localName == null)
			return al;
		decendentsWithLocalName1(al, ele, localName, depth);
		return al;
	}

	private static void decendentsWithLocalName1(List<OMElement> decendents, OMElement ele, String localName, int depth) {
		if (depth == 0)
			return;
		for (Iterator it=ele.getChildElements(); it.hasNext(); ) {
			OMElement child = (OMElement) it.next();
			if (child.getLocalName().equals(localName))
				decendents.add(child);
			decendentsWithLocalName1(decendents, child, localName, depth - 1);
		}
	}
	
	public static OMElement createElement(String localName, OMNamespace ns) {
		return om_factory.createOMElement(localName, ns);
	}

	public static OMElement addChild(String localName, OMNamespace ns, OMElement parent) {
		return om_factory.createOMElement(localName, ns, parent);
	}
	
	public static String associationTypeWithNamespace(String type) {
		if (type.startsWith("urn"))
			return type;
		if (type.equals("HasMember"))
			return "urn:oasis:names:tc:ebxml-regrep:AssociationType:" + type;
		return "urn:ihe:iti:2007:AssociationType:" + type;
	}
	
	public static String associationTypeWithoutNamespace(String type) {
		if ( ! type.startsWith("urn"))
			return type;
		String[] parts = type.split(":");
		if (parts.length == 0)
			return type;
		return parts[parts.length - 1];
	}

}