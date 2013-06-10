package gov.nist.toolkit.repository.api;

import gov.nist.toolkit.repository.simple.Configuration;
import gov.nist.toolkit.repository.simple.SimpleRepository;
import gov.nist.toolkit.repository.simple.SimpleRepositoryIterator;
import gov.nist.toolkit.repository.simple.SimpleTypeIterator;
import gov.nist.toolkit.repository.simple.index.IndexableRepository;
import gov.nist.toolkit.repository.simple.index.db.DbIndexContainer;

import java.io.File;
import java.io.Serializable;
import java.util.Properties;

/**
 * RepositoryManager
 * ToDo
 * 	- Create Abstract Repository and refactor simple (in prep for derby based repository)
 *  - Introduce other data types (xml, txt, serialized objects) instead of just byte[]
 *  - Introduce use of type element domain to indicate a type is a repository vs others :
 *      domain=repository
 *  - The DisplayName property should be displayName (repository.props)
 * @author bmajur
 *
 */
public class RepositoryFactory implements RepositoryManager {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4491794003213633389L;

	@Override
	public Repository createRepository(String displayName, String description,
			Type repositoryType) throws RepositoryException {
		
		/*
		 * Determine if this repository type is indexable
		 */
		boolean isIndexable = DbIndexContainer.isRepositoryIndexable(repositoryType);
		
		if (isIndexable) {
			/*
			 * This is the indexable version of the Simple Repository  
			 */						
			IndexableRepository rep = new IndexableRepository();
			rep.setAutoFlush(false);
			rep.setType(repositoryType);
			rep.setDescription(description);
			rep.setDisplayName(displayName);
			rep.flush();
			return rep;
		} else {
			/*
			 * This is the standard Simple Repository (without any indexing features)
			 */			
			SimpleRepository rep = new SimpleRepository();
			rep.setAutoFlush(false);
			rep.setType(repositoryType);
			rep.setDescription(description);
			rep.setDisplayName(displayName);
			rep.flush();
			return rep;
		}
		
	}
	

	@Override
	public Repository createNamedRepository(String displayName,
			String description, Type repositoryType, String repositoryName)
			throws RepositoryException {
		SimpleRepository rep = new SimpleRepository(repositoryName);
		rep.setAutoFlush(false);
		rep.setType(repositoryType);
		rep.setDescription(description);
		rep.setDisplayName(displayName);
		rep.flush();
		return rep;
	}	

	File getRepositoryRoot(Id id) throws RepositoryException {
		return new File(Configuration.getRootOfAllRepositories().toString() + File.separator + id);
	}

	@Override
	public OsidContext getOsidContext() throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void assignOsidContext(OsidContext context) throws RepositoryException {
		// TODO Auto-generated method stub

	}

	@Override
	public void assignConfiguration(Properties configuration)
			throws RepositoryException {
		// TODO Auto-generated method stub

	}

	@Override
	public void osidVersion_2_0() throws RepositoryException {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteRepository(Id repositoryId) throws RepositoryException {
		if (Configuration.repositoryExists(repositoryId))
			return;
		SimpleRepository repos = new SimpleRepository(repositoryId);
		repos.delete();
	}

	@Override
	public RepositoryIterator getRepositories() throws RepositoryException {
		return new SimpleRepositoryIterator();
	}

	@Override
	public RepositoryIterator getRepositoriesByType(Type repositoryType)
			throws RepositoryException {
		return new SimpleRepositoryIterator(repositoryType);
	}

	@Override
	public Repository getRepository(Id id) throws RepositoryException {
		SimpleRepository repos = new SimpleRepository(id);
		repos.load();
		return repos;
	}

	@Override
	public Asset getAsset(Id assetId) throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Asset getAssetByDate(Id assetId, long date)
			throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LongValueIterator getAssetDates(Id assetId)
			throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AssetIterator getAssetsBySearch(Repository[] repositories,
			Serializable searchCriteria, Type searchType,
			gov.nist.toolkit.repository.api.Properties searchProperties)
					throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Id copyAsset(Repository repository, Id assetId)
			throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeIterator getRepositoryTypes() throws RepositoryException {
		return new SimpleTypeIterator();
	}

}
