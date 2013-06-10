package gov.nist.toolkit.repository.simple;

import gov.nist.toolkit.repository.api.Repository;
import gov.nist.toolkit.repository.api.RepositoryException;
import gov.nist.toolkit.repository.api.RepositoryIterator;
import gov.nist.toolkit.repository.api.Type;

import java.io.File;
import java.io.FilenameFilter;

public class SimpleRepositoryIterator implements RepositoryIterator, FilenameFilter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 699742242188652792L;
	File reposDir;
	String[] reposDirNames;
	int reposDirsIndex;
	Type type = null;

	/**
	 * Iterate across all repositories.
	 * @throws RepositoryException
	 */
	public SimpleRepositoryIterator() throws RepositoryException {
		reposDir = Configuration.getRepositoryDataDir();
		reposDirNames = reposDir.list(this);
		reposDirsIndex = 0;
	}

	/**
	 * Iterate across all repositories of specified type.
	 * @param type
	 * @throws RepositoryException
	 */
	public SimpleRepositoryIterator(Type type) throws RepositoryException {
		reposDir = Configuration.getRepositoryDataDir();
		reposDirNames = reposDir.list(this);
		reposDirsIndex = 0;
		this.type = type;
	}
	
	public int size() {
		return reposDirNames.length;
	}
	
	public int remaining() {
		int r = size() - reposDirsIndex;
		return r;
	}

	@Override
	public boolean hasNextRepository() throws RepositoryException {
		return reposDirsIndex < reposDirNames.length;
	}

	@Override
	public Repository nextRepository() throws RepositoryException {
		if (!hasNextRepository())
			throw new RepositoryException(RepositoryException.NO_MORE_ITERATOR_ELEMENTS);
		SimpleId id = new SimpleId(reposDirNames[reposDirsIndex++]);
		// skb
		// return new SimpleRepository(id).load();
		return new SimpleRepository(id);

	}

	@Override
	public boolean accept(File dir, String name) {
		String repId = basename(name);
		try {
			SimpleRepository sRep = new SimpleRepository(new SimpleId(repId));
			String typeStr = sRep.getProperty("repositoryType");
			Type t = new SimpleType(typeStr);
			if (type == null || type.isEqual(t)) 
				return true;
		} catch (RepositoryException e) {
			return false;
		}
		return false;
	}

	String basename(String filename) {
		int i = filename.lastIndexOf(".");
		if (i == -1) return filename;
		return filename.substring(0, i);
	}

}
