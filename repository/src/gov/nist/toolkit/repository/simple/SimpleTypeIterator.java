package gov.nist.toolkit.repository.simple;

import gov.nist.toolkit.repository.api.RepositoryException;
import gov.nist.toolkit.repository.api.Type;
import gov.nist.toolkit.repository.api.TypeIterator;

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Properties;

public class SimpleTypeIterator implements TypeIterator, FilenameFilter {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5027811189603265527L;
	File typesDir;
	String[] typesFileNames;
	int typesFileNamesIndex;

	public SimpleTypeIterator() throws RepositoryException {
		typesDir = Configuration.getRepositoryTypesDir();
		typesFileNames = typesDir.list(this);
		typesFileNamesIndex = 0;
	}

	@Override
	public boolean hasNextType() throws RepositoryException {
		return typesFileNamesIndex < typesFileNames.length;
	}

	@Override
	public Type nextType() throws RepositoryException {
		if (!hasNextType())
			throw new RepositoryException(RepositoryException.NO_MORE_ITERATOR_ELEMENTS);
		Properties typeProps = loadProperties(new File(typesDir + File.separator + typesFileNames[typesFileNamesIndex++]));
		String keyword = typeProps.getProperty("keyword");
		String description = typeProps.getProperty("description");
		return new SimpleType(keyword, description);
	}
	
	// For use with File.list(filter) above. This is the filter
	@Override
	public boolean accept(File file, String arg1) {
		boolean val = arg1.endsWith(Configuration.PROPERTIES_FILE_EXT);
		return val;
	}

	/**
	 * Load the individual type definitions so they can be iterated through.
	 * @param propFile
	 * @return Properties object
	 * @throws RepositoryException if properties cannot be loaded
	 */
	Properties loadProperties(File propFile) throws RepositoryException {
		Properties properties = new Properties();
		try {
			properties.load(new FileReader(propFile));
		} catch (IOException e) {
			throw new RepositoryException(RepositoryException.IO_ERROR + " : " +
					"Cannot load repository type description from [" + propFile + "]");
		}
		return properties;

	}

}
