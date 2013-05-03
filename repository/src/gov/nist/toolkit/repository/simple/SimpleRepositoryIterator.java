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
	
	public SimpleRepositoryIterator() throws RepositoryException {
		reposDir = Configuration.RootOfAllRepositories;
		reposDirNames = reposDir.list(this);
		reposDirsIndex = 0;
	}

	public SimpleRepositoryIterator(Type type) throws RepositoryException {
		reposDir = Configuration.RootOfAllRepositories;
		reposDirNames = reposDir.list(this);
		reposDirsIndex = 0;
		this.type = type;
	}

	@Override
	public boolean hasNextRepository() throws RepositoryException {
		if (type == null)
			return reposDirsIndex < reposDirNames.length;
		else {
			if (reposDirsIndex < reposDirNames.length) {
				Repository r = peekNextRepository();
				if (type.isEqual(r.getType()))
					return true;
			} 
		}
		return false;
	}

	@Override
	public Repository nextRepository() throws RepositoryException {
		Repository r = peekNextRepository();
		reposDirsIndex++;
		return r;
	}

	public Repository peekNextRepository() throws RepositoryException {
		if (!(reposDirsIndex < reposDirNames.length))
			throw new RepositoryException(RepositoryException.NO_MORE_ITERATOR_ELEMENTS);
		SimpleId id = new SimpleId(reposDirNames[reposDirsIndex]);
		return new SimpleRepository(id).load();
	}

	@Override
	public boolean accept(File dir, String name) {
		return !(name.equals(Configuration.REPOSITORY_TYPES_DIR) 
				|| name.startsWith("."));
	}

}
