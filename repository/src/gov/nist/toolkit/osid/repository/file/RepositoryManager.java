package gov.nist.toolkit.osid.repository.file;

import gov.nist.toolkit.osid.repository.IRepository;
import gov.nist.toolkit.osid.repository.IRepositoryManager;
import gov.nist.toolkit.osid.repository.IType;
import gov.nist.toolkit.osid.repository.RepositoryUtils;
import gov.nist.toolkit.osid.shared.Id;
import gov.nist.toolkit.osid.shared.LongValueIterator;
import gov.nist.toolkit.osid.shared.NotImplemented;
import gov.nist.toolkit.osid.shared.Properties;

import java.io.File;
import java.io.Serializable;

public class RepositoryManager implements IRepositoryManager {
	static Id NOREPID = new Id("None");
	File home;
	
	// only one instance should be created - by RepositoryMapper
	public RepositoryManager(File home) {
		this.home = home;
	}

	/* (non-Javadoc)
	 * @see gov.nist.toolkit.osid.repository.file.IRepositoryManager#copyAsset(gov.nist.toolkit.osid.repository.IRepository, gov.nist.toolkit.osid.shared.Id)
	 */
	@Override
	public Id copyAsset(IRepository repository, Id assetId) {
		throw new NotImplemented();
	}

	/* (non-Javadoc)
	 * @see gov.nist.toolkit.osid.repository.file.IRepositoryManager#createRepository(java.lang.String, java.lang.String, gov.nist.toolkit.osid.repository.Type)
	 */
	@Override
	public Repository createRepository(String displayName, String description, Type repositoryType) {
		String repositoryFileName = RepositoryUtils.AS_FILENAME(repositoryType + "_" + displayName);
		File repositoryHome = new File(home + File.separator + repositoryFileName);
		repositoryHome.mkdirs();
		Repository repository = new Repository(new Id("rep_" + repositoryHome), repositoryHome);
		return repository;
	}

	/* (non-Javadoc)
	 * @see gov.nist.toolkit.osid.repository.file.IRepositoryManager#deleteRepository(gov.nist.toolkit.osid.shared.Id)
	 */
	@Override
	public void deleteRepository(Id repositoryId) {
		throw new NotImplemented();
	}

	/* (non-Javadoc)
	 * @see gov.nist.toolkit.osid.repository.file.IRepositoryManager#getAsset(gov.nist.toolkit.osid.shared.Id)
	 */
	@Override
	public Asset getAsset(Id assetId) {
		throw new NotImplemented();
	}

	/* (non-Javadoc)
	 * @see gov.nist.toolkit.osid.repository.file.IRepositoryManager#getAssetByDate(gov.nist.toolkit.osid.shared.Id, long)
	 */
	@Override
	public Asset getAssetByDate(Id assetId, long date) {
		throw new NotImplemented();
	}

	/* (non-Javadoc)
	 * @see gov.nist.toolkit.osid.repository.file.IRepositoryManager#getAssetDates(gov.nist.toolkit.osid.shared.Id)
	 */
	@Override
	public LongValueIterator getAssetDates(Id assetId) {
		throw new NotImplemented();
	}

	/* (non-Javadoc)
	 * @see gov.nist.toolkit.osid.repository.file.IRepositoryManager#getAssetsBySearch(gov.nist.toolkit.osid.repository.IRepository[], java.io.Serializable, gov.nist.toolkit.osid.repository.Type, gov.nist.toolkit.osid.shared.Properties)
	 */
	@Override
	public AssetIterator getAssetsBySearch(IRepository[] repositories,
			Serializable searchCriteria, IType searchType,
			Properties searchProperties) {
		throw new NotImplemented();
	}

	/* (non-Javadoc)
	 * @see gov.nist.toolkit.osid.repository.file.IRepositoryManager#getRepositories()
	 */
	@Override
	public RepositoryIterator getRepositories() {
		throw new NotImplemented();
	}

	/* (non-Javadoc)
	 * @see gov.nist.toolkit.osid.repository.file.IRepositoryManager#getRepositoresByType(gov.nist.toolkit.osid.repository.Type)
	 */
	@Override
	public RepositoryIterator getRepositoresByType(IType repositoryType) {
		throw new NotImplemented();
	}

	/* (non-Javadoc)
	 * @see gov.nist.toolkit.osid.repository.file.IRepositoryManager#getRepository(gov.nist.toolkit.osid.shared.Id)
	 */
	@Override
	public Repository getRepository(Id repositoryId) {
		throw new NotImplemented();
	}

	/* (non-Javadoc)
	 * @see gov.nist.toolkit.osid.repository.file.IRepositoryManager#getRepositoryTypes()
	 */
	@Override
	public TypeIterator getRepositoryTypes() {
		throw new NotImplemented();
	}
	
	public Id getNoRepository() {
		return NOREPID;
	}

}
