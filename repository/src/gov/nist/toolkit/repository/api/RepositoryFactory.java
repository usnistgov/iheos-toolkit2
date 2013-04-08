package gov.nist.toolkit.repository.api;

import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.repository.simple.SimpleRepository;

import java.io.File;

public class RepositoryFactory {

	enum RepositoryType { SITE };
	File ec;
	
	public RepositoryFactory() {
		ec = Installation.installation().externalCache();
	}
	
	/**
	 * Get a handle for the repository of specified type.
	 * @param type - the repository type
	 * @return repository handle
	 * @throws RepositoryException 
	 */
	public Repository getRepositoryHandle(RepositoryType type) throws RepositoryException {
		return new SimpleRepository(getRoot(type));
	}

	File getRoot(RepositoryType type) throws RepositoryException {
		switch (type) {
		case SITE:
			return new File(ec + File.separator + "sites");
		default:
			throw new RepositoryException(RepositoryException.CONFIGURATION_ERROR);
		}
	}
	
}
