package gov.nist.toolkit.fhir.simulators.sim.reg.store;

import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.xdsexception.client.XdsException;

public interface ProcessMetadataInterface {

	// MU will change
	void checkUidUniqueness(Metadata m);

	// set logicalId to id
	void setLidToId(Metadata m);

	// install version attribute in SubmissionSet, DocumentEntry and Folder objects
	// install default version in Association, Classification, ExternalIdentifier
	void setInitialVersion(Metadata m);

	// set folder lastUpdateTime on folders in the submission
	// must be done after metadata index built
	void setNewFolderTimes(Metadata m);

	// set folder lastUpdateTime on folders already in the registry
	// that this submission adds documents to
	// must be done after metadata index built
	void updateExistingFolderTimes(Metadata m) throws XdsException;

	// verify that no associations are being added that:
	//     reference a non-existant model in submission or registry
	//     reference a Deprecated model in registry
	void verifyAssocReferences(Metadata m);

	// check for RPLC and RPLC_XFRM and do the deprecation
	void doRPLCDeprecations(Metadata m);

	// if a replaced doc is in a Folder, then new doc is placed in folder
	// and folder lastUpateTime is updated
	void updateExistingFoldersWithReplacedDocs(Metadata m);

	// verify model/patient id linking rules are observed
	void associationPatientIdRules();

	// when a Folder is updated, all its contained DocEntries must be linked to new version
	void addDocsToUpdatedFolders(Metadata m);
}
