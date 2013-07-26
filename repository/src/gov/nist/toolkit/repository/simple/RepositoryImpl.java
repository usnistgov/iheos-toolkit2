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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;

public abstract class RepositoryImpl implements Repository {
	private static final long serialVersionUID = 7947876287785415118L;
	static final String REPOSITORY_PROPERTY_FILE = "repository.props.txt";

	File root = null;  // directory holding this repository
	boolean initialized = false;
	boolean loaded = false;
	Properties properties = new Properties();
	boolean isNew;

	public File getRoot() {
		return root;
	}

	public void setRoot(File root) {
		this.root = root;
	}	
	public boolean isLoaded() {
		return loaded;
	}
	
	public boolean isInitialized() {
		return initialized;
	}

	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	
	/**
	 * Open an existing repository.
	 * @param root - filesystem directory that holds the repository contents
	 * @throws RepositoryException
	 */
	public RepositoryImpl(Id id) throws RepositoryException {
		root = Configuration.getRepositoryLocation(id);
		isNew = false;
		load();
	}

	/**
	 * Create new Repository.
	 * @throws RepositoryException
	 */
	public RepositoryImpl() throws RepositoryException {
		isNew = true;
		Id id = new IdFactory().getNewId();
		properties.setProperty("id", id.getIdString());
		root = Configuration.getRepositoryLocation(id);
	}
	
	/**
	 * Create new named Repository.
	 * @throws RepositoryException
	 */
	public RepositoryImpl(String name) throws RepositoryException {
		isNew = true;
		Id id = new SimpleId(name);
		properties.setProperty("id", id.getIdString());
		root = Configuration.getRepositoryLocation(id);
	}

	public void load() throws RepositoryException {
		
		if (!isNew && !isLoaded()) {
			File typeDescriptorFile = new File(
					root + File.separator + 
					REPOSITORY_PROPERTY_FILE);
			properties = new Properties();
			try {
				FileReader fr = new FileReader(typeDescriptorFile);
				properties.load(fr);
				fr.close(); // This will release the file lock
			} catch (Exception e) {
				throw new RepositoryException(RepositoryException.UNKNOWN_REPOSITORY + " : " + root, e);
			} finally {
				loaded = true;
			}
		}
	}
	
	
	public void setType(Type type) throws RepositoryException {
//		load();
		properties.setProperty("repositoryType", type.toString());
	}

	@Override
	public Type getType() throws RepositoryException {
//		load();
		return new SimpleType(properties.getProperty("repositoryType"), "");
	}


	@Override
	public String getDisplayName() throws RepositoryException {
//		load();
		return properties.getProperty("DisplayName");
	}

	@Override
	public Id getId() throws RepositoryException {
//		load();
		String idString = properties.getProperty("id", "");
		if (idString.equals("")) {
			throw new RepositoryException(RepositoryException.UNKNOWN_ID + 
					" - loading repository but Repository.id is empty");
		}
		Id id = new SimpleId(idString);
		return id;
	}

	@Override
	public String getDescription() throws RepositoryException {
//		load();
		return properties.getProperty("description");
	}


	@Override
	public Asset getAsset(Id assetId) throws RepositoryException {
//		load();
		File reposDir = Configuration.getRepositoryLocation(getId());
		if (!reposDir.exists() || !reposDir.isDirectory())
			throw new RepositoryException(RepositoryException.UNKNOWN_REPOSITORY + " : " +
					"directory for repositoryId [" + getId() + "] does not exist");
		File assetBaseFile = new File(reposDir.toString() + File.separator + assetId.getIdString());
		SimpleAsset a = new SimpleAsset().load(assetId, assetBaseFile, getId());		
		return a;
	}

	@Override
	public AssetIterator getAssets() throws RepositoryException {
//		load();
		return new SimpleAssetIterator(getId());
	}

	@Override
	public AssetIterator getAssetsByType(Type assetType)
			throws RepositoryException {
//		load();
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	@Override
	public TypeIterator getAssetTypes() throws RepositoryException {
//		load();
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	public String getPropertyValueByKey(String key) {
		return properties.getProperty(key);
	}
	
	public void setProperty(String key, String value) {
		properties.setProperty(key, value);
	}
	
	@Override
	public Properties getPropertiesByType(Type propertiesType)
			throws RepositoryException {
//		load();
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	@Override
	public TypeIterator getPropertyTypes() throws RepositoryException {
//		load();
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	public Properties getRepositoryProperties() {
		return properties;
	}

	@Override
	public PropertiesIterator getProperties() throws RepositoryException {
//		load();
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	@Override
	public TypeIterator getSearchTypes() throws RepositoryException {
//		load();
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	@Override
	public TypeIterator getStatusTypes() throws RepositoryException {
//		load();
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	@Override
	public Type getStatus(Id assetId) throws RepositoryException {
//		load();
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	@Override
	public boolean validateAsset(Id assetId) throws RepositoryException {
//		load();
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	@Override
	public void invalidateAsset(Id assetId) throws RepositoryException {
//		load();
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	@Override
	public Asset getAssetByDate(Id assetId, long date)
			throws RepositoryException {
//		load();
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	@Override
	public LongValueIterator getAssetDates(Id assetId)
			throws RepositoryException {
//		load();
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	@Override
	public AssetIterator getAssetsBySearch(Serializable searchCriteria,
			Type searchType, Properties searchProperties)
					throws RepositoryException {
//		load();
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	@Override
	public Id copyAsset(Asset asset) throws RepositoryException {
//		load();
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	@Override
	public boolean supportsVersioning() throws RepositoryException {
//		load();
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	@Override
	public boolean supportsUpdate() throws RepositoryException {
//		load();
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	protected File getRepositoryPropFile() throws RepositoryException {
//		load();
		return new File(root.toString() + File.separator + REPOSITORY_PROPERTY_FILE);
	}


	public boolean isConfigured() throws RepositoryException {
//		load();
		File propFile = getRepositoryPropFile();
		return propFile.exists();
	}

	public void delete() throws RepositoryException {
//		load();
		if (!isConfigured()) 
			return;
		delete(root);
	}

	void delete(File fileToDelete) throws RepositoryException {
//		load();
		File[] files = fileToDelete.listFiles();
		if (files != null) {
			for (int i=0; i<files.length; i++) {
				File file = files[i];
				delete(file);
			}
		}
		fileToDelete.delete();
	}

	public String getProperty(String name) throws RepositoryException {
//		load();
		return properties.getProperty(name);
	}

}
