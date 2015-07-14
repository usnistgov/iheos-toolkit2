package gov.nist.toolkit.xdstools2.client;

import java.util.HashMap;
import java.util.Map;
// No longer used
public class TestDocumentation {

	static public final String getDocuments = "GetDocuments";
	static public final String getFolders = "GetFolders";
	static public final String getFoldersForDocument = "GetFoldersForDocument";
	static public final String getFolderAndContents = "GetFolderAndContents";
	static public final String getAssociations = "GetAssociations";
	static public final String getObjects = "GetObjects";
	static public final String getRelated = "GetRelated";
	static public final String findDocuments = "FindDocuments";
	static public final String findPatient = "FindPatient";
	static public final String findFolders = "FindFolders";
	static public final String mpqFindDocuments = "MPQ-FindDocuments";
	static public final String getSubmissionSets = "GetSubmissionSets";
	static public final String getSubmissionSetAndContents = "GetSubmissionSetAndContents";
	
	static public final String retrieveDocumentSet = "RetrieveDocumentSet";

	static public final String registerAndQuery = "RegisterAndQuery";
	static public final String sourceStoresDocumentValidation = "SourceStoresDocumentValidation";

	static public final String submitRegistryTestData = "testdata-registry";
	static public final String provideAndRetrieve = "ProvideAndRetrieve";
	static public final String lifecycleValidation = "tc:lifecycle";
	static public final String folderValidation = "tc:folder";

	static Map<String, String> documentation;
	
	static {
		documentation = new HashMap<String, String>();
	
		documentation.put(getDocuments, "No Documentation Available");
		documentation.put(getFolders, "No Documentation Available");
		documentation.put(getAssociations, "No Documentation Available");
		documentation.put(getObjects, "No Documentation Available");
		documentation.put(getRelated, "No Documentation Available");
		documentation.put(findDocuments, "No Documentation Available");
		documentation.put(findPatient, "No Documentation Available");
		documentation.put(mpqFindDocuments, "No Documentation Available");
		
		documentation.put(getSubmissionSets, 
				"GetSubmissionSets Stored Query: Get the Submission Set and linking HasMember Association " +
				"for a collection of DocumentEntries and Folders");
		
		documentation.put(getSubmissionSetAndContents, "No Documentation Available");
		documentation.put(retrieveDocumentSet, "No Documentation Available");
		documentation.put(registerAndQuery, "No Documentation Available");
		documentation.put(sourceStoresDocumentValidation, "No Documentation Available");
		documentation.put(submitRegistryTestData, "No Documentation Available");
		documentation.put(provideAndRetrieve, "No Documentation Available");
		documentation.put(lifecycleValidation, "No Documentation Available");
		documentation.put(folderValidation, "No Documentation Available");
	}
	
	public static String getDocumentation(String test) {
		return documentation.get(test);
	}

}
