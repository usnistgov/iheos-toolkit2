package gov.nist.toolkit.results.client;


import com.google.gwt.user.client.rpc.IsSerializable;
import gov.nist.toolkit.actorfactory.client.Pid;
import gov.nist.toolkit.actorfactory.client.PidBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CodesConfiguration implements IsSerializable {
	static public String ContentTypeCode = "contentTypeCode";
	static public String ClassCode = "classCode";
	static public String ConfidentialityCode = "confidentialityCode";
	static public String FormatCode = "formatCode";
	static public String HealthcareFacilityTypeCode = "healthcareFacilityTypeCode";
	static public String PracticeSettingCode = "practiceSettingCode";
	static public String EventCodeList = "eventCodeList";
	static public String TypeCode = "typeCode";
	static public String FolderCodeList = "folderCodeList";
	static public String AssociationDocumentation = "associationDocumentation";

	// Technically these are not codes but they are carried around like they are
	// in some secondary uses of this class
	static public String SubmissionSetStatus = "submissionSetStatus";
	static public String DocumentEntryStatus = "documentEntryStatus";
	static public String FolderStatus = "folderStatus";
	static public String DocumentEntryType = "documentEntryType";
	static public String ReturnsType = "returnsType";

	static public Map<String, String> titles = new HashMap<String, String>();

	static {
		titles.put(ContentTypeCode, "Content Type Code");
		titles.put(ClassCode, "Class Code");
		titles.put(ConfidentialityCode, "Confidentiality Code");
		titles.put(FormatCode, "Format Code");
		titles.put(HealthcareFacilityTypeCode, "Healthcare Facility Type Code");
		titles.put(PracticeSettingCode, "Practice Setting Code");
		titles.put(EventCodeList, "Event Code List");
		titles.put(TypeCode, "Type Code");
		titles.put(FolderCodeList, "Folder Code List");
		titles.put(AssociationDocumentation, "Association Documentation");
		titles.put(SubmissionSetStatus, "SubmissionSet Status");
		titles.put(DocumentEntryStatus, "DocumentEntry Status");
		titles.put(FolderStatus, "Folder Status");
		titles.put(DocumentEntryType, "DocumentEntry Type");
		titles.put(ReturnsType, "Returns Type");
	}

	static public String getTitle(String codeName) { return titles.get(codeName);}

	Map<String, CodeConfiguration> codes;

	public void setCodes(Map<String, CodeConfiguration> codes) {
		this.codes = codes;
	}
	
	public CodeConfiguration getCodeConfiguration(String codeType) {
		return codes.get(codeType);
	}

	public List<String> assigningAuthorities = new ArrayList<>();

	// codes configuration may contain multiple, only give access to first (the one we assign from)
	public String getAssigningAuthorityOid() {
		if (assigningAuthorities == null) return null;
		if (assigningAuthorities.size() == 0) return null;
		String aa = assigningAuthorities.get(0); // this has &OID&ISO syntax as configured
		Pid pid = PidBuilder.createPid("111^^^" + aa);  // make complete PID
		return pid.getAd();
	}
		
}
