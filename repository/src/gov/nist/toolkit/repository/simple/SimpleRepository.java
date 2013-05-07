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

public class SimpleRepository implements Repository, Flushable {
	private static final long serialVersionUID = 7941866267155906518L;
	static final String REPOSITORY_PROPERTY_FILE = "repository.props.txt";

	File root = null;  // directory holding this repository
	boolean initialized = false;
	boolean loaded = false;
	Properties properties = new Properties();
	boolean autoFlush = true;
	boolean isNew;

	boolean isLoaded() {
		return loaded;
	}

	public boolean isAutoFlush() {
		return autoFlush;
	}

	/**
	 * Control whether repository objects are flushed to disk after every set method.
	 * AutoFlush == false requires that eventually the flush() method be called to force
	 * the data to disk.
	 * AutoFlush is reset to true at next flush.
	 * @param autoFlush
	 */
	public void setAutoFlush(boolean autoFlush) {
		this.autoFlush = autoFlush;
	}

	/**
	 * Open an existing repository.
	 * @param root - filesystem directory thaw holds the repository contents
	 * @throws RepositoryException
	 */
	public SimpleRepository(Id id) throws RepositoryException {
		root = Configuration.getRepositoryLocation(id);
		isNew = false;
	}

	/**
	 * Create new Repository.
	 * @throws RepositoryException
	 */
	public SimpleRepository() throws RepositoryException {
		isNew = true;
		Id id = new IdFactory().getNewId();
		properties.setProperty("id", id.getIdString());
		root = Configuration.getRepositoryLocation(id);
	}
	
	/**
	 * Create new named Repository.
	 * @throws RepositoryException
	 */
	public SimpleRepository(String name) throws RepositoryException {
		isNew = true;
		Id id = new SimpleId(name);
		properties.setProperty("id", id.getIdString());
		root = Configuration.getRepositoryLocation(id);
	}
	

	public void setType(Type type) throws RepositoryException {
		load();
		properties.setProperty("repositoryType", type.toString());
	}

	@Override
	public Type getType() throws RepositoryException {
		load();
		return new SimpleType(properties.getProperty("repositoryType"), "");
	}

	public SimpleRepository load() throws RepositoryException {
		if (isNew)
			return this;
		if (isLoaded())
			return this;
		loaded = true;
		File typeDescriptorFile = new File(
				root + File.separator + 
				REPOSITORY_PROPERTY_FILE);
		properties = new Properties();
		try {
			properties.load(new FileReader(typeDescriptorFile));
		} catch (Exception e) {
			throw new RepositoryException(RepositoryException.UNKNOWN_REPOSITORY + " : " + root, e);
		}
		return this;
	}

	@Override
	public void setDisplayName(String displayName)
			throws RepositoryException {
		load();
		properties.setProperty("DisplayName", displayName);
		if (autoFlush)
			flush();
	}

	@Override
	public String getDisplayName() throws RepositoryException {
		load();
		return properties.getProperty("DisplayName");
	}

	@Override
	public Id getId() throws RepositoryException {
		load();
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
		load();
		return properties.getProperty("description");
	}

	@Override
	public void setDescription(String description)
			throws RepositoryException {
		load();
		properties.setProperty("description", description);
		if (autoFlush)
			flush();
	}

	@Override
	public SimpleAsset createAsset(String displayName, String description,
			Type assetType) throws RepositoryException {
		load();
		SimpleAsset a = new SimpleAsset();
		a.setAutoFlush(false);
		a.setRepository(getId());
		a.setType(assetType);
		a.setId(new IdFactory().getNewId());
		a.updateDisplayName(displayName);
		a.updateDescription(description);
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
		load();
		SimpleAsset a = new SimpleAsset();
		a.setAutoFlush(false);
		a.setRepository(getId());
		a.setType(assetType);
		a.setId(new SimpleId(name));
		a.updateDisplayName(displayName);
		a.updateDescription(description);
		a.flush();
		return a;
	}

	@Override
	public SimpleAsset getAsset(Id assetId) throws RepositoryException {
		load();
		File reposDir = Configuration.getRepositoryLocation(getId());
		if (!reposDir.exists() || !reposDir.isDirectory())
			throw new RepositoryException(RepositoryException.UNKNOWN_REPOSITORY + " : " +
					"directory for repositoryId [" + getId() + "] does not exist");
		File assetBaseFile = new File(reposDir.toString() + File.separator + assetId.getIdString());
		SimpleAsset a = new SimpleAsset().load(assetId, assetBaseFile, getId());
		return a;
	}

	@Override
	public void deleteAsset(Id assetId) throws RepositoryException {
		load();
//		try {
			SimpleAsset a = getAsset(assetId);
			a.deleteAsset();
//		} catch (Exception e) {
//
//		}
	}

	@Override
	public AssetIterator getAssets() throws RepositoryException {
		load();
		return new SimpleAssetIterator(getId());
	}

	@Override
	public AssetIterator getAssetsByType(Type assetType)
			throws RepositoryException {
		load();
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	@Override
	public TypeIterator getAssetTypes() throws RepositoryException {
		load();
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	@Override
	public Properties getPropertiesByType(Type propertiesType)
			throws RepositoryException {
		load();
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	@Override
	public TypeIterator getPropertyTypes() throws RepositoryException {
		load();
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	@Override
	public PropertiesIterator getProperties() throws RepositoryException {
		load();
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	@Override
	public TypeIterator getSearchTypes() throws RepositoryException {
		load();
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	@Override
	public TypeIterator getStatusTypes() throws RepositoryException {
		load();
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	@Override
	public Type getStatus(Id assetId) throws RepositoryException {
		load();
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	@Override
	public boolean validateAsset(Id assetId) throws RepositoryException {
		load();
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	@Override
	public void invalidateAsset(Id assetId) throws RepositoryException {
		load();
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	@Override
	public Asset getAssetByDate(Id assetId, long date)
			throws RepositoryException {
		load();
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	@Override
	public LongValueIterator getAssetDates(Id assetId)
			throws RepositoryException {
		load();
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	@Override
	public AssetIterator getAssetsBySearch(Serializable searchCriteria,
			Type searchType, Properties searchProperties)
					throws RepositoryException {
		load();
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	@Override
	public Id copyAsset(Asset asset) throws RepositoryException {
		load();
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	@Override
	public boolean supportsVersioning() throws RepositoryException {
		load();
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	@Override
	public boolean supportsUpdate() throws RepositoryException {
		load();
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	File getRepositoryPropFile() throws RepositoryException {
		load();
		return new File(root.toString() + File.separator + REPOSITORY_PROPERTY_FILE);
	}

	public void flush() throws RepositoryException {
		assert(root != null);
		autoFlush = true;
		try {
			File propFile = getRepositoryPropFile();
			root.mkdirs();
			FileWriter writer = new FileWriter(propFile);
			properties.store(writer, "");
		} catch (IOException e) {
			throw new RepositoryException(RepositoryException.IO_ERROR, e);
		}
		isNew = false;
	}

	public boolean isConfigured() throws RepositoryException {
		load();
		File propFile = getRepositoryPropFile();
		return propFile.exists();
	}

	public void delete() throws RepositoryException {
		load();
		if (!isConfigured()) 
			return;
		delete(root);
	}

	void delete(File fileToDelete) throws RepositoryException {
		load();
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
		load();
		return properties.getProperty(name);
	}

}
