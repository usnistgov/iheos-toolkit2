package gov.nist.toolkit.repository.simple.index;

import gov.nist.toolkit.repository.api.Asset;
import gov.nist.toolkit.repository.api.Id;
import gov.nist.toolkit.repository.api.RepositoryException;
import gov.nist.toolkit.repository.api.Type;
import gov.nist.toolkit.repository.simple.Configuration;
import gov.nist.toolkit.repository.simple.IdFactory;
import gov.nist.toolkit.repository.simple.SimpleId;
import gov.nist.toolkit.repository.simple.SimpleRepository;
import gov.nist.toolkit.repository.simple.index.db.DbIndexContainer;

/**
 * 
 * @author Sunil.Bhaskarla
 *
 */
public class IndexableRepository extends SimpleRepository {
	private static final long serialVersionUID = 7941866244710221445L;


	/**
	 * Open an existing repository.
	 * @param root - filesystem directory thaw holds the repository contents
	 * @return 
	 * @throws RepositoryException
	 */
	public IndexableRepository(Id id) throws RepositoryException {
		super(id);
	}

	/**
	 * Create new Repository.
	 * @throws RepositoryException
	 */
	public IndexableRepository() throws RepositoryException {
		super();
	}
	
	/**
	 * Create new named Repository.
	 * @throws RepositoryException
	 */
	public IndexableRepository(String name) throws RepositoryException {
		super(name);
	}


	@Override
	public void setDisplayName(String displayName)
			throws RepositoryException {
		super.setDisplayName(displayName);
	}

	@Override
	public void setDescription(String description)
			throws RepositoryException {
		super.setDescription(description);
	}
	
	@Override
	public Asset createAsset(String displayName, String description,
			Type assetType) throws RepositoryException {

		IndexableAsset a = new IndexableAsset();
		a.setAutoFlush(false);
		a.setRepository(getId());
		a.setType(assetType);
		a.setId(new IdFactory().getNewId());
		a.updateDisplayName(displayName);
		a.updateDescription(description);
		a.setIndexable(DbIndexContainer.isRepositoryIndexable(this.getType()));	
		a.flush();
		return a;
	}

	@Override
	public Asset createNamedAsset(String displayName, String description,
			Type assetType, String name) throws RepositoryException {
		if (name == null)
			throw new RepositoryException("null is not a name for an Asset");
		if (name.equals(Configuration.REPOSITORY_PROP_FILE_BASENAME))
			throw new RepositoryException(Configuration.REPOSITORY_PROP_FILE_BASENAME + " is an illegal Asset name");

		IndexableAsset a = new IndexableAsset();
		a.setAutoFlush(false);
		a.setRepository(getId());
		a.setType(assetType);
		a.setId(new SimpleId(name));
		a.updateDisplayName(displayName);
		a.updateDescription(description);
		a.setIndexable(DbIndexContainer.isRepositoryIndexable(this.getType()));
		a.flush();
		return a;

	}

	public void flush() throws RepositoryException {
		super.flush();
		// add the rep level index here if needed, most indexing will take place at the Asset property flush level		
	}

	/* (non-Javadoc)
	 * @see gov.nist.toolkit.repository.simple.RepositoryImpl#getAsset(gov.nist.toolkit.repository.api.Id)
	 */
	@Override
	public Asset getAsset(Id assetId) throws RepositoryException {
		// TODO Auto-generated method stub
		IndexableAsset a = (IndexableAsset)super.getAsset(assetId);
		a.setIndexable(DbIndexContainer.isRepositoryIndexable(this.getType()));
		return a;
	}
	

}
