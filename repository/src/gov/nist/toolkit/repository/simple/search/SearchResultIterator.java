package gov.nist.toolkit.repository.simple.search;

import gov.nist.toolkit.repository.api.Asset;
import gov.nist.toolkit.repository.api.AssetIterator;
import gov.nist.toolkit.repository.api.Id;
import gov.nist.toolkit.repository.api.Repository;
import gov.nist.toolkit.repository.api.RepositoryException;
import gov.nist.toolkit.repository.api.RepositoryFactory;
import gov.nist.toolkit.repository.api.Type;
import gov.nist.toolkit.repository.simple.Configuration;
import gov.nist.toolkit.repository.simple.SimpleAssetIterator;
import gov.nist.toolkit.repository.simple.SimpleId;
import gov.nist.toolkit.repository.simple.index.db.DbIndexContainer;
import gov.nist.toolkit.xdsexception.ExceptionUtil;

import java.io.File;
import java.io.FilenameFilter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.rowset.CachedRowSet;

public class SearchResultIterator implements AssetIterator  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -719485351032161998L;
	File reposDir = null;
	String[] assetFileNames;
	int assetFileNamesIndex = 0;
	Id repositoryId = null;
	boolean[] selections = null;
	Type type = null;
	CachedRowSet crs = null;
	int totalRecords = -1;
	int fetchedRecords = 0;
	
	/**
	 * Retrieves a comprehensive search result of all indexed and non-indexed assets. 
	 * Search results are provided in the same order as specified by the repository parameter, grouped by assetType.
	 * Search results are stored in a temporary table called Session.SearchResults, which is automatically cleaned-up when the db session is disconnected.
	 * To avoid page overloading with numerous results, we use a paging method to retrieve a reasonable amount of records each time rather than the entire set.
	 *  
	 * @param repositories
	 * @param searchCriteria
	 * @throws RepositoryException 
	 */
	public SearchResultIterator(Repository[] repositories, SearchCriteria searchCriteria) throws RepositoryException {

		DbIndexContainer dbc = new DbIndexContainer();
		
		crs = dbc.getAssetsBySearch(repositories, searchCriteria);
		totalRecords = crs.size();
		// System.out.println("total records in buffer: " + totalRecords);

	}
	
		
	@Override
	public boolean hasNextAsset() throws RepositoryException {
		if (crs!=null) {
				return fetchedRecords < totalRecords;
			
		}
		return false;
	
	}

	@Override
	public Asset nextAsset() throws RepositoryException {
		if (!hasNextAsset())
			throw new RepositoryException(RepositoryException.NO_MORE_ITERATOR_ELEMENTS);
		SimpleId repId = null;
		SimpleId assetId = null;
		
		try {
			
			if (crs.next()) {
				fetchedRecords++;
				repId = new SimpleId(crs.getString(1));
				assetId = new SimpleId(crs.getString(2));
				
			}			
			// System.out.println(assetId.getIdString());
			
			Repository repos = new RepositoryFactory().getRepository(repId);
			return repos.getAsset(assetId);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	
	}
	

}
