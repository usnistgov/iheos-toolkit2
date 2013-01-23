package gov.nist.toolkit.osid.repository.file;

import gov.nist.toolkit.osid.repository.IRepository;
import gov.nist.toolkit.osid.repository.IType;
import gov.nist.toolkit.osid.shared.Id;
import gov.nist.toolkit.osid.shared.LongValueIterator;
import gov.nist.toolkit.osid.shared.NotImplemented;
import gov.nist.toolkit.osid.shared.Properties;
import gov.nist.toolkit.osid.shared.RepositoryException;

import java.io.File;
import java.io.Serializable;
import java.util.Calendar;

public class Repository implements IRepository {
	File home;
	Id id;
	
	public Repository(Id id, File home) {
		this.id = id;
		this.home = home;
	}
	
	/* (non-Javadoc)
	 * @see gov.nist.toolkit.osid.repository.file.IRepository#copyAsset(gov.nist.toolkit.osid.repository.file.Asset)
	 */
	@Override
	public Id copyAsset(Asset asset) throws RepositoryException {
		throw new NotImplemented();
	}

	/* (non-Javadoc)
	 * @see gov.nist.toolkit.osid.repository.file.IRepository#createAsset(java.lang.String, java.lang.String, gov.nist.toolkit.osid.repository.Type)
	 */
	@Override 
	public Asset createAsset(String displayName, String description,
			IType assetType) throws RepositoryException {
		throw new NotImplemented();
	}

	/* (non-Javadoc)
	 * @see gov.nist.toolkit.osid.repository.file.IRepository#deleteAsset(gov.nist.toolkit.osid.shared.Id)
	 */
	@Override
	public void deleteAsset(Id assetId) throws RepositoryException {
		throw new NotImplemented();
	}

	/* (non-Javadoc)
	 * @see gov.nist.toolkit.osid.repository.file.IRepository#getAsset(gov.nist.toolkit.osid.shared.Id)
	 */
	@Override
	public Asset getAsset(Id assetId) throws RepositoryException {
		throw new NotImplemented();
	}

	/* (non-Javadoc)
	 * @see gov.nist.toolkit.osid.repository.file.IRepository#getAssetByDate(gov.nist.toolkit.osid.shared.Id, java.util.Calendar)
	 */
	@Override
	public Asset getAssetByDate(Id assetId, Calendar date)
			throws RepositoryException {
		throw new NotImplemented();
	}

	/* (non-Javadoc)
	 * @see gov.nist.toolkit.osid.repository.file.IRepository#getAssetDates(gov.nist.toolkit.osid.shared.Id)
	 */
	@Override
	public LongValueIterator getAssetDates(Id asset) throws RepositoryException {
		throw new NotImplemented();
	}

	/* (non-Javadoc)
	 * @see gov.nist.toolkit.osid.repository.file.IRepository#getAssets()
	 */
	@Override
	public AssetIterator getAssets() throws RepositoryException {
		throw new NotImplemented();
	}

	/* (non-Javadoc)
	 * @see gov.nist.toolkit.osid.repository.file.IRepository#getAssetsBySearch(java.io.Serializable, gov.nist.toolkit.osid.repository.Type, gov.nist.toolkit.osid.shared.Properties)
	 */
	@Override
	public AssetIterator getAssetsBySearch(Serializable searchCriteria,
			IType searchType, Properties searchProperties)
			throws RepositoryException {
		throw new NotImplemented();
	}

	/* (non-Javadoc)
	 * @see gov.nist.toolkit.osid.repository.file.IRepository#getAssetTypes()
	 */
	@Override
	public TypeIterator getAssetTypes() throws RepositoryException {
		throw new NotImplemented();
	}

	/* (non-Javadoc)
	 * @see gov.nist.toolkit.osid.repository.file.IRepository#getDescription()
	 */
	@Override
	public String getDescription() throws RepositoryException {
		throw new NotImplemented();
	}

	/* (non-Javadoc)
	 * @see gov.nist.toolkit.osid.repository.file.IRepository#getDisplayName()
	 */
	@Override
	public String getDisplayName() throws RepositoryException {
		throw new NotImplemented();
	}

	/* (non-Javadoc)
	 * @see gov.nist.toolkit.osid.repository.file.IRepository#getId()
	 */
	@Override
	public Id getId() throws RepositoryException {
		throw new NotImplemented();
	}

	/* (non-Javadoc)
	 * @see gov.nist.toolkit.osid.repository.file.IRepository#getProperties()
	 */
	@Override
	public PropertiesIterator getProperties() throws RepositoryException {
		throw new NotImplemented();
	}

	/* (non-Javadoc)
	 * @see gov.nist.toolkit.osid.repository.file.IRepository#getPropertiesByType(gov.nist.toolkit.osid.repository.Type)
	 */
	@Override
	public Properties getPropertiesByType(IType propertiesType)
			throws RepositoryException {
		throw new NotImplemented();
	}

	/* (non-Javadoc)
	 * @see gov.nist.toolkit.osid.repository.file.IRepository#getPropertyTypes()
	 */
	@Override
	public TypeIterator getPropertyTypes() throws RepositoryException {
		throw new NotImplemented();
	}

	/* (non-Javadoc)
	 * @see gov.nist.toolkit.osid.repository.file.IRepository#getSearchTypes()
	 */
	@Override
	public TypeIterator getSearchTypes() throws RepositoryException {
		throw new NotImplemented();
	}

	/* (non-Javadoc)
	 * @see gov.nist.toolkit.osid.repository.file.IRepository#getStatus(gov.nist.toolkit.osid.shared.Id)
	 */
	@Override
	public Type getStatus(Id assetId) throws RepositoryException {
		throw new NotImplemented();
	}

	/* (non-Javadoc)
	 * @see gov.nist.toolkit.osid.repository.file.IRepository#getStatusTypes()
	 */
	@Override
	public TypeIterator getStatusTypes() throws RepositoryException {
		throw new NotImplemented();
	}

	/* (non-Javadoc)
	 * @see gov.nist.toolkit.osid.repository.file.IRepository#getType()
	 */
	@Override
	public Type getType() throws RepositoryException {
		throw new NotImplemented();
	}

	/* (non-Javadoc)
	 * @see gov.nist.toolkit.osid.repository.file.IRepository#invalidateAsset(gov.nist.toolkit.osid.shared.Id)
	 */
	@Override
	public void invalidateAsset(Id assetId) throws RepositoryException {
		throw new NotImplemented();
	}

	/* (non-Javadoc)
	 * @see gov.nist.toolkit.osid.repository.file.IRepository#validateAsset(gov.nist.toolkit.osid.shared.Id)
	 */
	@Override
	public boolean validateAsset(Id assetId) throws RepositoryException {
		throw new NotImplemented();
	}

	@Override
	public AssetIterator getAssetsByType(IType searchType)
			throws RepositoryException {
		throw new NotImplemented();
	}

}
