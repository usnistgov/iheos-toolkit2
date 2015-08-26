package gov.nist.toolkit.results.client;


import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.HashMap;
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

	static public Map<String, String> titles = new HashMap<>();

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
	}

	Map<String, CodeConfiguration> codes;

	public void setCodes(Map<String, CodeConfiguration> codes) {
		this.codes = codes;
	}
	
	public CodeConfiguration getCodeConfiguration(String codeType) {
		return codes.get(codeType);
	}
		
}
