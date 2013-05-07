package gov.nist.toolkit.repository.simple;

import gov.nist.toolkit.repository.api.Id;
import gov.nist.toolkit.repository.api.RepositoryException;
import gov.nist.toolkit.repository.api.Type;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Configuration {
	public static final String PROPERTIES_FILE_EXT = "props.txt";
	public static final String CONTENT_FILE_EXT = "bytes";
	public static final String REPOSITORY_TYPES_DIR = "types";
	public static final String REPOSITORY_DATA_DIR = "data";
	public static final String REPOSITORY_PROP_FILE_BASENAME = "repository";
	public static final String REPOSITORY_PROP_FILE = REPOSITORY_PROP_FILE_BASENAME + PROPERTIES_FILE_EXT;
	// Do not reference this static variable directly. Use
	// private accessor getRootOfAllRepositories
	// which verifies it is initialized.
	static File RootOfAllRepositories = null;
	static Configuration configuration = null;

	public static Configuration getConfiguration() {
		return configuration;
	}

	public static Configuration initialize(File rootOfAllRepositories) throws RepositoryException {
		
		// Root cannot be changed during operation
		if (RootOfAllRepositories != null && !RootOfAllRepositories.equals(rootOfAllRepositories))
			throw new RepositoryException(RepositoryException.MANAGER_INSTANTIATION_ERROR + 
					" : " +
					"configuration already created for [" + getRootOfAllRepositories().getAbsolutePath() +
					"] and now it is being re-created for [" + rootOfAllRepositories.getAbsolutePath() + 
					"] - a single configuration is required!");
		if (!exists())
			throw new RepositoryException(RepositoryException.CONFIGURATION_ERROR + 
					" : Repository system does not exist - repository root directory ["  +
					rootOfAllRepositories.toString() + "] does not exist.");
		if (!isConfigured())
			throw new RepositoryException(RepositoryException.CONFIGURATION_ERROR + 
					" : Repository system not configured - repository root directory ["  +
					rootOfAllRepositories.toString() + "] exists but is not configured.");
		return new Configuration(rootOfAllRepositories);
	}

	public Configuration(File rootOfAllRepositories) throws RepositoryException {
		RootOfAllRepositories = rootOfAllRepositories;
	}

	public boolean isRepositorySystemInitialized() throws RepositoryException  {
		return exists() && isConfigured();
	}

	static public boolean exists() throws RepositoryException {
		return getRootOfAllRepositories().exists() && RootOfAllRepositories.isDirectory();
	}

	static public File getRootOfAllRepositories() throws RepositoryException {
		if (RootOfAllRepositories == null)
			throw new RepositoryException(RepositoryException.MANAGER_NOT_FOUND + " : " +
					"Repository Configuration object not initialized");
		return RootOfAllRepositories;
	}
	
	/**
	 * Get directory holding the contents of the repository
	 * @param id
	 * @return
	 * @throws RepositoryException
	 */
	static public File getRepositoryLocation(Id id) throws RepositoryException {
		assert(id != null);
		assert(RootOfAllRepositories != null);
		return new File(getRepositoryDataDir().toString() + File.separator + id.getIdString());
	}

	static public boolean isConfigured() throws RepositoryException {
		if (RootOfAllRepositories == null) return false;
		File typesDir = getRepositoryTypesDir();
		if (!(typesDir.exists() && typesDir.isDirectory())) return false;
		File dataDir = getRepositoryDataDir();
		if (!(dataDir.exists() && dataDir.isDirectory())) return false;
		return true;
	}

	static public boolean repositoryExists(Id id) throws RepositoryException {
		File repositoryRoot = getRepositoryLocation(id);
		return repositoryRoot.exists() && repositoryRoot.isDirectory();
	}

	public Properties getRepositoryTypeProperties(Type type) throws RepositoryException {
		File propFile = getRepositoryTypeFile(type);
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(propFile));
		} catch (IOException e) {
			throw new RepositoryException(
					RepositoryException.UNKNOWN_TYPE + " : " +
							"Repository type [" + type.getDomain() + "] is unknown");
		}
		return props;
	}
	
	File getRepositoryTypeFile(Type type) throws RepositoryException {
		return new File(
				Configuration.getRootOfAllRepositories() + File.separator + 
				Configuration.REPOSITORY_TYPES_DIR + File.separator + 
				type.getDomain() + "." + Configuration.PROPERTIES_FILE_EXT);
	}
	


	public static File getRepositoryTypesDir() throws RepositoryException {
		return new File(
				getRootOfAllRepositories() + File.separator + 
				Configuration.REPOSITORY_TYPES_DIR);
	}

	public static File getRepositoryDataDir() throws RepositoryException {
		return new File(
				getRootOfAllRepositories() + File.separator + 
				Configuration.REPOSITORY_DATA_DIR);
	}

	public static Id getAssetIdFromFilename(String filename) {
		File fn = new File(filename);
		String fullName = fn.getName();
		// extract id from filename
		String[] parts = fullName.split("\\.");
		if (parts != null && parts.length > 0)
			return new SimpleId(parts[0]);
		else
			return new SimpleId(fullName);
	}
}
