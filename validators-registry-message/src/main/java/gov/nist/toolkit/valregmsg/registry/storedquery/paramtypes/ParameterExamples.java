package gov.nist.toolkit.valregmsg.registry.storedquery.paramtypes;

import java.util.HashMap;
import java.util.Map;

public class ParameterExamples {

	Map<String, String> examples = new HashMap<String, String>();
	Map<String, String> types = new HashMap<String, String>();
	
	
	public String getExample(String attName) {
		String type = types.get(attName);
		if (type == null)
			return "";
		String ex = examples.get(type);
		if (ex != null)
			ex = ex.replaceAll("<", "&lt;");
		return ex;
	}
	
	public ParameterExamples() {
		examples.put("Integer", "<Value>12345</Value>");
		examples.put("Date", "<Value>201005231345</Value>");
		examples.put("Author", "%Smith%");
		examples.put("Home", "urn:oid:1.2.3");
		examples.put("PatientID", "<Value>'c4d77e21d0dd487^^^&amp;1.3.6.1.4.1.21367.2005.3.7&amp;ISO'</Value>");
		examples.put("List", "<Value>('a', 'b', 'c')</Value> or \n"  + 
				"<Value>('a', 'b')</Value>\n<Value>('c')</Value>\n or \n" +
				"<Value>('a')</Value>\n<Value>('b')</Value>\n<Value>('c')</Value>"
				);
		examples.put("Status", "<Value>('urn:oasis:names:tc:ebxml-regrep:ResponseStatusType:Approved')</Value>");
		examples.put("UUIDs", "<Value>('urn:uuid:51224314-5390-4169-9b91-b1980040715a')</Value>\n" +
				"<Value>('urn:uuid:14d4debf-8f97-4251-9a74-a90016b0af0d')</Value>");
		examples.put("AssocTypes", "<Value>('urn:oasis:names:tc:ebxml-regrep:AssociationType:HasMember')</Value>\n" +
		"<Value>('urn:ihe:iti:2007:AssociationType:RPLC')</Value>");
		examples.put("UUID", "<Value>('urn:uuid:51224314-5390-4169-9b91-b1980040715a')</Value>" );
		examples.put("Code","<Value>('value1^^scheme', 'value2^^scheme2', 'value3^^scheme3')</Value> or \n"  + 
				"<Value>('value1^^scheme', 'value2^^scheme2')</Value>\n<Value>('value3^^scheme3')</Value>\n or \n" +
				"<Value>('value1^^scheme')</Value>\n<Value>('value2^^scheme2')</Value>\n<Value>('value3^^scheme3')</Value>");
		
		types.put("$XDSDocumentEntryPatientId", "PatientID");
		types.put("$XDSDocumentEntryClassCode", "Code");
		types.put("$XDSDocumentEntryTypeCode", "Code");
		types.put("$XDSDocumentEntryPracticeSettingCode", "Code");
		types.put("$XDSDocumentEntryCreationTimeFrom", "Date");
		types.put("$XDSDocumentEntryCreationTimeTo", "Date");
		types.put("$XDSDocumentEntryServiceStartTimeFrom", "Date");
		types.put("$XDSDocumentEntryServiceStartTimeTo", "Date");
		types.put("$XDSDocumentEntryServiceStopTimeFrom", "Date");
		types.put("$XDSDocumentEntryServiceStopTimeTo", "Date");
		types.put("$XDSDocumentEntryHealthcareFacilityTypeCode", "Code");
		types.put("$XDSDocumentEntryEventCodeList", "Code");
		types.put("$XDSDocumentEntryConfidentialityCode", "Code");
		types.put("$XDSDocumentEntryAuthorPerson", "Author");
		types.put("$XDSDocumentEntryFormatCode", "Code");
		types.put("$XDSDocumentEntryStatus", "Status");
		types.put("$XDSSubmissionSetPatientId", "PatientID");
		types.put("$XDSSubmissionSetSourceId", "List");
		types.put("$XDSSubmissionSetSubmissionTimeFrom", "Date");
		types.put("$XDSSubmissionSetSubmissionTimeTo", "Date");
		types.put("$XDSSubmissionSetAuthorPerson", "Author");
		types.put("$XDSSubmissionSetContentType", "Code");
		types.put("$XDSSubmissionSetStatus", "Status");
		types.put("$XDSFolderPatientId", "PatientID");
		types.put("$XDSFolderLastUpdateTimeFrom", "Date");
		types.put("$XDSFolderLastUpdateTimeTo", "Date");
		types.put("$XDSFolderCodeList", "Code");
		types.put("$XDSFolderStatus", "Status");
		types.put("$patientId", "PatientID");
		types.put("$XDSDocumentEntryStatus", "Status");
		types.put("$XDSSubmissionSetStatus", "Status");
		types.put("$XDSDocumentEntryEntryUUID", "UUIDs");
		types.put("$XDSDocumentEntryUniqueId", "List");
		types.put("$homeCommunityId", "Home");
		types.put("$XDSFolderEntryUUID", "UUIDs");
		types.put("$XDSFolderUniqueId", "List");
		types.put("$uuid", "UUIDs");
		types.put("$XDSSubmissionSetEntryUUID", "UUID");
		types.put("$XDSSubmissionSetUniqueId", "List");
		types.put("$AssociationTypes", "AssocTypes");
	}
	
}
