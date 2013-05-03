package gov.nist.toolkit.repository.simple;

import gov.nist.toolkit.repository.api.Asset;
import gov.nist.toolkit.repository.api.AssetIterator;
import gov.nist.toolkit.repository.api.Id;
import gov.nist.toolkit.repository.api.Repository;
import gov.nist.toolkit.repository.api.RepositoryException;
import gov.nist.toolkit.repository.api.RepositoryFactory;
import gov.nist.toolkit.repository.api.Type;
import gov.nist.toolkit.utilities.io.Io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

public class SimpleAsset implements Asset, Flushable {	
	String classNameForSerializable;
	Properties properties = new Properties();
	byte[] content = null;
	boolean autoFlush = true;

	public void setRepository(Id repositoryId) throws RepositoryException {
		properties.setProperty("repository", repositoryId.getIdString());
		if (autoFlush) flush();
	}

	public void setType(Type type) throws RepositoryException {
		properties.setProperty("type", type.getKeyword());
		if (autoFlush) flush();
	}

	public void setId(Id id) throws RepositoryException {
		properties.setProperty("id", id.getIdString());
		if (autoFlush) flush();
	}
	
	public void setProperty(String key, String value) throws RepositoryException {
		properties.setProperty(key, value);
		if (autoFlush) flush();
	}
	
	public String getProperty(String key) {
		return properties.getProperty(key);
	}

	public SimpleAsset load(Id assetId, File assetBaseFile, Id repositoryId) throws RepositoryException {
		File assetPropFile = new File(assetBaseFile.toString() + "." + Configuration.PROPERTIES_FILE_EXT);
		File assetContentFile = new File(assetBaseFile.toString() + "." + Configuration.CONTENT_FILE_EXT);
		properties = new Properties();
		try {
			properties.load(new FileReader(assetPropFile));
		} catch (Exception e) {
			throw new RepositoryException(RepositoryException.UNKNOWN_ID + " : " + 
					"properties cannot be loaded for " +
					"asset [" + assetId.getIdString() + "] in repository [" +
					repositoryId.getIdString() + "]", e);
		}
		try {
			content = Io.bytesFromFile(assetContentFile);
		} catch (Exception e) {
			// content may not exist
		}
		return this;
	}

	@Override
	public void updateDisplayName(String displayName)
			throws RepositoryException {
		properties.setProperty("displayName", displayName);
		if (autoFlush) flush();
	}

	@Override
	public void updateEffectiveDate(long effectiveDate)
			throws RepositoryException {
		properties.setProperty("effectiveDate", String.valueOf(effectiveDate));
		if (autoFlush) flush();
	}

	@Override
	public void updateExpirationDate(long expirationDate)
			throws RepositoryException {
		properties.setProperty("expirationDate", String.valueOf(expirationDate));
		if (autoFlush) flush();
	}

	@Override
	public String getDisplayName() throws RepositoryException {
		return properties.getProperty("displayName");
	}

	@Override
	public String getDescription() throws RepositoryException {
		return properties.getProperty("description");
	}

	@Override
	public Id getId() throws RepositoryException {
		return new SimpleId(properties.getProperty("id"));
	}

	@Override
	public Type getAssetType() throws RepositoryException {
		return new SimpleType(properties.getProperty("type"));
	}

	@Override
	public long getEffectiveDate() throws RepositoryException {
		return new Integer(properties.getProperty("effectiveDate")).longValue();
	}

	@Override
	public long getExpirationDate() throws RepositoryException {
		return new Integer(properties.getProperty("expirationDate")).longValue();
	}

	@Override
	public void updateDescription(String description)
			throws RepositoryException {
		properties.setProperty("description", description);
		if (autoFlush) flush();
	}

	@Override
	public Id getRepository() throws RepositoryException {
		return new SimpleId(properties.getProperty("repository"));
	}

	@Override
	public byte[] getContent() throws RepositoryException {
		return content;
	}

	@Override
	public void updateContent(byte[] content) throws RepositoryException {
		this.content = content;
		if (autoFlush) flush();
	}

	@Override
	public void addAsset(Id assetId) throws RepositoryException {
		Id repositoryId = getRepository();   // this does not support cross-repository linking
		Repository repository = new RepositoryFactory().getRepository(repositoryId);
		SimpleAsset asset = (SimpleAsset) repository.getAsset(assetId);
		if (assetId.isEqual(getId())) 
			throw new RepositoryException(RepositoryException.CIRCULAR_OPERATION + " : " +
		    "trying to create parent relationship between asset [" + assetId.getIdString() + 
		    "] and itself (repository [" + repositoryId.getIdString() + "]");
		asset.setProperty("parent", getId().getIdString());
		if (autoFlush) flush();
	}

	@Override
	public void removeAsset(Id assetId, boolean includeChildren)
			throws RepositoryException {
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
	public boolean isAutoFlush() {
		return autoFlush;
	}

	@Override
	public void setAutoFlush(boolean autoFlush) {
		this.autoFlush = autoFlush;
	}

	File getPropFile() throws RepositoryException {
		File repositoryFile = Configuration.getRepositoryLocation(getRepository());
		if (!repositoryFile.exists())
			throw new RepositoryException(RepositoryException.CONFIGURATION_ERROR + " : " +
					"repository directory [" + repositoryFile.toString() + "] does not exist");
		Id assetId = getId();
		return new File(repositoryFile.toString() + File.separator + 
				assetId.getIdString() + "." + Configuration.PROPERTIES_FILE_EXT);
	}

	File getContentFile() throws RepositoryException {
		File repositoryFile = Configuration.getRepositoryLocation(getRepository());
		if (!repositoryFile.exists())
			throw new RepositoryException(RepositoryException.CONFIGURATION_ERROR + " : " +
					"repository directory [" + repositoryFile.toString() + "] does not exist");
		Id assetId = getId();
		return new File(repositoryFile.toString() + File.separator + 
				assetId.getIdString() + "." + Configuration.CONTENT_FILE_EXT);
	}

	@Override
	public void flush() throws RepositoryException {
		autoFlush = true;
		try {
			FileWriter writer = new FileWriter(getPropFile());
			properties.store(writer, "");
			writer.close();
			if (content != null) {
				OutputStream os = new FileOutputStream(getContentFile());
				os.write(content);
				os.close();
			}
		} catch (IOException e) {
			throw new RepositoryException(RepositoryException.IO_ERROR, e);
		}
	}

}
