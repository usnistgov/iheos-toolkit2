package gov.nist.toolkit.metadataModel;

import gov.nist.toolkit.registrymetadata.Metadata;

public interface ProcessMetadataInterface {

	void checkUidUniqueness(Metadata m);

	void setLidToId(Metadata m);

	void setInitialVersion(Metadata m);

	void setNewFolderTimes(Metadata m);

	void updateExistingFolderTimes(Metadata m);

	void verifyAssocReferences(Metadata m);

	void doRPLCDeprecations(Metadata m);

	void updateExistingFoldersWithReplacedDocs(Metadata m);

	void associationPatientIdRules();
}
