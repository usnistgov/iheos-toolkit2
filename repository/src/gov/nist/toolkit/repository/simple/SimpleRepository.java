package gov.nist.toolkit.repository.simple;

import gov.nist.toolkit.repository.api.Asset;
import gov.nist.toolkit.repository.api.AssetIterator;
import gov.nist.toolkit.repository.api.Id;
import gov.nist.toolkit.repository.api.LongValueIterator;
import gov.nist.toolkit.repository.api.PropertiesIterator;
import gov.nist.toolkit.repository.api.Repository;
import gov.nist.toolkit.repository.api.RepositoryException;
import gov.nist.toolkit.repository.api.Type;
import gov.nist.toolkit.repository.api.TypeIterator;

import java.io.File;
import java.io.Serializable;
import java.util.Properties;

public class SimpleRepository implements Repository {
	private static final long serialVersionUID = 7941866267155906518L;
	public enum CreateType { CREATE, OPEN };
	static final String REPOSITORY_PROPERTY_FILE = "repository.props";

	File root;
	CreateType createType;
	boolean updated;
	boolean initialized = false;
	boolean loaded = false;
	Properties properties = new Properties();

//  Delegated to properties file	
//	String displayName;
//	String description;
//	Id id;
	
//	Type type;  // current unused
	boolean postponeFlush;

	public boolean isPostponeFlush() {
		return postponeFlush;
	}

	public void setPostponeFlush(boolean postponeFlush) {
		this.postponeFlush = postponeFlush;
	}

	public SimpleRepository(File root, CreateType createType) throws RepositoryException {
		this.root = root;
		this.createType = createType;
		initializeRepository();
	}
	
	void initializeRepository() throws RepositoryException {
		if (initialized)
			return;
		initialized = true;
		updated = false;
		if (createType == CreateType.CREATE) {
			if (root.exists()) {
				if (root.isDirectory()) {
					if (isRepository()) {
						loadRepository();
					} else {
						throw new RepositoryException(RepositoryException.CONFIGURATION_ERROR + 
								" - name exists, it is a directory, but not configured as a Repository");
					}
				} else {
					throw new RepositoryException(RepositoryException.CONFIGURATION_ERROR + 
							" - name exists but is not a directory");
				}
			} else {
				root.mkdirs();
				if (!root.exists() || !root.isDirectory()) 
					throw new RepositoryException(RepositoryException.CONFIGURATION_ERROR + 
							" - cannot create directory " + root.toString());
				properties.setProperty("id", new IdFactory().getNewId().getIdString());
				updated = true;
			}

		} else {
			if (!root.exists() || !root.isDirectory()) 
				throw new RepositoryException(RepositoryException.UNKNOWN_REPOSITORY);
		}		
	}
	
	void loadRepository() {
		if (loaded)
			return;
		loaded = true;
	}
	
	boolean isRepository() throws RepositoryException {
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	@Override
	public void updateDisplayName(String displayName)
			throws RepositoryException {
		properties.setProperty("DisplayName", displayName);
		if (!postponeFlush)
			flush();
	}

	@Override
	public String getDisplayName() throws RepositoryException {
		return properties.getProperty("DisplayName");
	}

	@Override
	public Id getId() throws RepositoryException {
		String idString = properties.getProperty("id", "");
		if (idString.equals("")) {
				throw new RepositoryException(RepositoryException.UNKNOWN_ID + 
						" - loading repository but Repository.id is empty");
		}
		Id id = new SimpleId(idString);
		return id;
	}
	
	@Override
	public Type getType() throws RepositoryException {
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	@Override
	public String getDescription() throws RepositoryException {
		return properties.getProperty("description");
	}

	@Override
	public void updateDescription(String description)
			throws RepositoryException {
		properties.setProperty("description", description);
		if (!postponeFlush)
			flush();
	}

	@Override
	public Asset createAsset(String displayName, String description,
			Type assetType) throws RepositoryException {
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	@Override
	public void deleteAsset(Id assetId) throws RepositoryException {
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	@Override
	public AssetIterator getAssets() throws RepositoryException {
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	@Override
	public AssetIterator getAssetsByType(Type assetType)
			throws RepositoryException {
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	@Override
	public TypeIterator getAssetTypes() throws RepositoryException {
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	@Override
	public Properties getPropertiesByType(Type propertiesType)
			throws RepositoryException {
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	@Override
	public TypeIterator getPropertyTypes() throws RepositoryException {
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	@Override
	public PropertiesIterator getProperties() throws RepositoryException {
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	@Override
	public TypeIterator getSearchTypes() throws RepositoryException {
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	@Override
	public TypeIterator getStatusTypes() throws RepositoryException {
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	@Override
	public Type getStatus(Id assetId) throws RepositoryException {
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	@Override
	public boolean validateAsset(Id assetId) throws RepositoryException {
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	@Override
	public void invalidateAsset(Id assetId) throws RepositoryException {
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	@Override
	public Asset getAsset(Id assetId) throws RepositoryException {
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	@Override
	public Asset getAssetByDate(Id assetId, long date)
			throws RepositoryException {
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	@Override
	public LongValueIterator getAssetDates(Id assetId)
			throws RepositoryException {
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	@Override
	public AssetIterator getAssetsBySearch(Serializable searchCriteria,
			Type searchType, Properties searchProperties)
					throws RepositoryException {
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	@Override
	public Id copyAsset(Asset asset) throws RepositoryException {
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	@Override
	public boolean supportsVersioning() throws RepositoryException {
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	@Override
	public boolean supportsUpdate() throws RepositoryException {
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	public void flush() throws RepositoryException {
		postponeFlush = false;
		if (updated) {
			
		}
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}
}
