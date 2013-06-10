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
	transient boolean indexable = false;

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

	File getAssetBaseFile(Id assetId) throws RepositoryException {
		return new File(Configuration.getRepositoryLocation(getRepository()).toString() + File.separator + assetId.getIdString());
	}

	File getPropertyFile(Id assetId) throws RepositoryException {
		return new File(getAssetBaseFile(assetId).toString() + "." + Configuration.PROPERTIES_FILE_EXT);
	}

	File getContentFile(Id assetId) throws RepositoryException {
		return new File(getAssetBaseFile(assetId).toString() + "." + Configuration.CONTENT_FILE_EXT);
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
	public void updateContent(String content, String mimeType) throws RepositoryException {
		properties.setProperty("mimeType", mimeType);
		this.content = content.getBytes();
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

	/**
	 * Simple delete of this Asset - no recursion. Not part of the API. 
	 * Supports SimpleRepository.deleteAsset(id)
	 * @throws RepositoryException
	 */
	public void deleteAsset() throws RepositoryException {
		File assetPropFile = getPropertyFile(getId());
		File assetContentFile = getContentFile(getId());
		assetPropFile.delete();
		assetContentFile.delete();
	}

	@Override
	public AssetIterator getAssets() throws RepositoryException {
		return new SimpleAssetIterator(getRepository());
	}

	@Override
	public AssetIterator getAssetsByType(Type assetType)
			throws RepositoryException {
		return new SimpleAssetIterator(getRepository(), assetType);
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
		return getContentFile(Configuration.CONTENT_FILE_EXT);
	}

	File getContentFile(String ext) throws RepositoryException {
		File repositoryFile = Configuration.getRepositoryLocation(getRepository());
		if (!repositoryFile.exists())
			throw new RepositoryException(RepositoryException.CONFIGURATION_ERROR + " : " +
					"repository directory [" + repositoryFile.toString() + "] does not exist");
		Id assetId = getId();
		return new File(repositoryFile.toString() + File.separator + 
				assetId.getIdString() + "." + ext);
	}

	String partTwo(String in, String separater) {
		String[] parts = in.split(separater);
		if (parts == null || parts.length < 2) return in;
		return parts[1];
	}

	String partOne(String in, String separater) {
		String[] parts = in.split(separater);
		if (parts == null || parts.length < 2) return in;
		return parts[0];
	}

	/**
	 * Load asset off disk into memory.
	 * @param assetId
	 * @param assetBaseFile - path of asset without file extension
	 * @param repositoryId
	 * @return
	 * @throws RepositoryException
	 */
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
		String mimeType = properties.getProperty("mimeType");
		if (mimeType != null && partOne(mimeType, "\\/").equals("text")) {
			String ext = partTwo(mimeType, "\\/");
			if (ext.equals("plain")) ext = "txt";
			try {
				content = Io.stringFromFile(getContentFile(ext)).getBytes();
			} catch (IOException e) {
				// content may not exist
			}
		} else {
			try {
				content = Io.bytesFromFile(assetContentFile);
			} catch (Exception e) {
				// content may not exist
			}
		}
		return this;
	}

	@Override
	public void flush() throws RepositoryException {
		autoFlush = true;
		try {
			FileWriter writer = new FileWriter(getPropFile());
			properties.store(writer, "");
			writer.close();
			if (content != null) {
				String mimeType = properties.getProperty("mimeType");
				if (mimeType != null && mimeType.startsWith("text/")) {
					String ext = partTwo(mimeType, "\\/");
					if (ext.equals("plain")) ext = "txt";
					Io.stringToFile(getContentFile(ext), new String(content));
				} else {
					OutputStream os = new FileOutputStream(getContentFile());
					os.write(content);
					os.close();
				}
			}
		} catch (IOException e) {
			throw new RepositoryException(RepositoryException.IO_ERROR, e);
		}
	}

	@Override
	public String getMimeType() throws RepositoryException {
		return properties.getProperty("mimeType");
	}

	/**
	 * @return the indexable
	 */
	public boolean isIndexable() {
		return indexable;
	}

	/**
	 * @param indexable the indexable to set
	 */
	public void setIndexable(boolean indexable) {
		this.indexable = indexable;
	}

}
