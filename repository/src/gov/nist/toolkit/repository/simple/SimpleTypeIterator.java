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
	private String domainType = null;
	private String typeFilter="";

	public SimpleTypeIterator() throws RepositoryException {
		typesDir = Configuration.getRepositoryTypesDir();
		typesFileNames = typesDir.list(this);
		typesFileNamesIndex = 0;
	}
	
	public SimpleTypeIterator(Type t) throws RepositoryException {
		typesDir = Configuration.getRepositoryTypesDir();
		setTypeFilter(t.getKeyword());
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
		String domain = typeProps.getProperty("domain");		

		SimpleType st = null;
		if (domain!=null) {
			String indexes = typeProps.getProperty("indexes");
			return new SimpleType(domain, keyword, description, indexes);
		} else		
			return new SimpleType(domain, keyword, description);

	}
	
	// For use with File.list(filter) above. This is the filter
	@Override
	public boolean accept(File file, String arg1) {
		String filter = Configuration.PROPERTIES_FILE_EXT; // base filter
		
		if (!"".equals(getTypeFilter())) { // Apply type specific filter here
			filter = getTypeFilter() + "." + filter;
		}
		
		boolean val = arg1.endsWith(filter);
				
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

	/**
	 * @return the typeFilter
	 */
	public String getTypeFilter() {
		return typeFilter;
	}

	/**
	 * @param typeFilter the typeFilter to set
	 */
	public void setTypeFilter(String typeFilter) {
		this.typeFilter = typeFilter;
	}

}
