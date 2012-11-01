package gov.nist.toolkit.valregmsg.registry;

import java.util.List;

public abstract class SQCodedTerm {

	public abstract boolean isEmpty();
	public abstract boolean isMatch(List<String> coded_values);

	static String[] codeParameters = { 
			"$XDSDocumentEntryClassCode",
			"$XDSDocumentEntryTypeCode"	,
			"$XDSDocumentEntryPracticeSettingCode",
			"$XDSDocumentEntryHealthcareFacilityTypeCode",
			"$XDSDocumentEntryEventCodeList",
			"$XDSDocumentEntryConfidentialityCode",
			"$XDSDocumentEntryFormatCode",
			"$XDSSubmissionSetContentType",
			"$XDSFolderCodeList"
	};

	static String[] codeParmVarName = {
			"classCode",
			"typeCode"	,
			"practiceSettingCode",
			"healthcareFacilityTypeCode",
			"eventCodeList",
			"confidentialityCode",
			"formatCode",
			"contentType",
			"codeList"
	};

	static String[] codeParmUUID = {
			"urn:uuid:41a5887f-8865-4c09-adf7-e362475b143a",
			"urn:uuid:f0306f51-975f-434e-a61c-c59651d33983"	,
			"urn:uuid:cccf5598-8b07-4b77-a05e-ae952c785ead",
			"urn:uuid:f33fb8ac-18af-42cc-ae0e-ed0b0bdb91e1",
			"urn:uuid:2c6b8cb7-8b2a-4051-b291-b1ae6a575ef4",
			"urn:uuid:f4f85eac-e6cb-4883-b524-f2705394840f",
			"urn:uuid:a09d5840-386c-46f2-b5ad-9c3699a4309d",
			"urn:uuid:aa543740-bdda-424e-8c96-df4873be8500",
			"urn:uuid:1ba97051-7806-41a8-a48b-8fce7af683c5"
	};

	public static int codeIndex(String name) {
		for (int i=0; i<codeParameters.length; i++)
			if (codeParameters[i].equals(name))
				return i;
		return -1;
	}

	public static boolean isCodeParameter(String name) {
		return (codeIndex(name) != -1);
	}

	public static String codeVarName(String codeName) {
		return codeParmVarName[codeIndex(codeName)];
	}

	public static String codeUUID(String codeName) {
		return codeParmUUID[codeIndex(codeName)];
	}
	
}
