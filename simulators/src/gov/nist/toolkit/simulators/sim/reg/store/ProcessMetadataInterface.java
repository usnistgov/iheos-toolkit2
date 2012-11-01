package gov.nist.toolkit.simulators.sim.reg.store;

import gov.nist.toolkit.registrymetadata.Metadata;

public interface ProcessMetadataInterface {

	public void checkUidUniqueness(Metadata m);
	public void setLidToId(Metadata m);
	public void setInitialVersion(Metadata m);
	public void setNewFolderTimes(Metadata m);
	public void updateExistingFolderTimes(Metadata m);
	public void verifyAssocReferences(Metadata m);
	public void doRPLCDeprecations(Metadata m);
	public void updateExistingFoldersWithReplacedDocs(Metadata m);
}
