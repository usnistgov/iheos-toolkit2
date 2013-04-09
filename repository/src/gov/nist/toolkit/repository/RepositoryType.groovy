package gov.nist.toolkit.repository

abstract class RepositoryType {

	/**
	 * Return Map of contents of req.meta file for this RepositoryType
	 * @return
	 */
	abstract Map getRequiredMetadata() 
	
	abstract String getDefaultLocation() 
}
