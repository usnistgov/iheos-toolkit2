package gov.nist.toolkit.repository.simple;

import gov.nist.toolkit.repository.api.Asset;
import gov.nist.toolkit.repository.api.AssetIterator;
import gov.nist.toolkit.repository.api.Id;
import gov.nist.toolkit.repository.api.Repository;
import gov.nist.toolkit.repository.api.RepositoryException;
import gov.nist.toolkit.repository.api.RepositoryFactory;
import gov.nist.toolkit.repository.api.Type;
import gov.nist.toolkit.xdsexception.ExceptionUtil;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class SimpleAssetIterator implements AssetIterator, FilenameFilter {

	/**
	 * 
	 */
	private static final long serialVersionUID = -679485554932669997L;
	File reposDir = null;
	String[] assetFileNames;
	int assetFileNamesIndex = 0;
	Id repositoryId = null;
	boolean[] selections = null;
	Type type = null;
	
	public SimpleAssetIterator(Id repositoryId) throws gov.nist.toolkit.repository.api.RepositoryException {
		this.repositoryId = repositoryId;
		reposDir = Configuration.getRepositoryLocation(repositoryId);
		assetFileNames = reposDir.list(this);
	}
	
	public SimpleAssetIterator(Id repositoryId, Type type) throws RepositoryException {
		this.repositoryId = repositoryId;
		this.type = type;
		reposDir = Configuration.getRepositoryLocation(repositoryId);
		assetFileNames = reposDir.list(this);
	}
	
	@Override
	public boolean hasNextAsset() throws RepositoryException {
		if (repositoryId == null)
			throw new RepositoryException(RepositoryException.UNKNOWN_REPOSITORY + " : " +
		"repository not set via API");
		return assetFileNamesIndex < assetFileNames.length;
	}

	@Override
	public Asset nextAsset() throws RepositoryException {
		if (!hasNextAsset())
			throw new RepositoryException(RepositoryException.NO_MORE_ITERATOR_ELEMENTS);
		String filename = assetFileNames[assetFileNamesIndex++];
		Id assetId = Configuration.getAssetIdFromFilename(filename);
		Repository repos = new RepositoryFactory().getRepository(repositoryId);
		return repos.getAsset(assetId);
	}
	
	/**
	 * Marks the current selection
	 * @throws RepositoryException 
	 */
	public void select() throws RepositoryException {  // marks the current selection
							// -1 offset necessary since index already incremented
		if (selections == null)
			selections = new boolean[assetFileNames.length];
		for (int i=0; i<assetFileNames.length; i++) selections[i] = false;
		int i = assetFileNamesIndex - 1;
		if (i < 0 || i >= assetFileNames.length)
			throw new RepositoryException(RepositoryException.OPERATION_FAILED + " : " + 
		"no current asset selected");
		selections[i] = true;
	}

	public SimpleAssetIterator getAssetIteratorFromSelections() throws RepositoryException {
		SimpleAssetIterator it = new SimpleAssetIterator(repositoryId);
		it.repositoryId = repositoryId;
		it.assetFileNames = (String[]) getSelectedFileNames().toArray();
		return it;
	}
	
	List<String> getSelectedFileNames() {
		List<String> selectedNames = new ArrayList<String>();
		for (int i=0; i<assetFileNames.length; i++) {
			if (selections[i])
				selectedNames.add(assetFileNames[i]);
		}
		return selectedNames;
	}

	@Override
	public boolean accept(File arg0, String arg1) {
		if (arg1.startsWith("repository")) return false;  // not an asset
		if (!arg1.endsWith(Configuration.PROPERTIES_FILE_EXT)) return false;  // not an asset property file
		// parent restriction?
		if (type == null) return true;    // no type restriction
		Id assetId = Configuration.getAssetIdFromFilename(arg1);
		
		try {
			SimpleRepository repos = new SimpleRepository(repositoryId);
			SimpleAsset a =  (SimpleAsset) repos.getAsset(assetId);
			String aTypeStr = a.getProperty("type");
			Type aType = new SimpleType(aTypeStr);
			return type.isEqual(aType);
		} catch (RepositoryException e) {
			System.out.println(ExceptionUtil.exception_details(e));
		}
		return false;
	}

}
