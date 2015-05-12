package gov.nist.toolkit.results.client;


import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;

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
	
	Map<String, CodeConfiguration> codes;

	public void setCodes(Map<String, CodeConfiguration> codes) {
		this.codes = codes;
	}
	
	public CodeConfiguration getCodeConfiguration(String codeType) {
		return codes.get(codeType);
	}
		
}
