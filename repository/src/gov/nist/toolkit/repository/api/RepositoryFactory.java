package gov.nist.toolkit.repository.api;

import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.repository.simple.SimpleRepository;

import java.io.File;

public class RepositoryFactory {

	enum RepositoryType { SIMPLE, SITE };
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
		return new SimpleRepository(getRoot(type), SimpleRepository.CreateType.OPEN);
	}
	
	public Repository createRepository(RepositoryType repType, String displayName, String description, Type type) 
			throws RepositoryException {
		File root = new File(ec.toString() + File.separator + repType.toString());
		SimpleRepository rep = new SimpleRepository(root, SimpleRepository.CreateType.CREATE);
		rep.setPostponeFlush(true);
		rep.setDescription(description);
		rep.setDisplayName(displayName);
		rep.flush();
		return rep;
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
