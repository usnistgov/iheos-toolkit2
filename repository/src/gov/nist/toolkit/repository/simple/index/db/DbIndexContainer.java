package gov.nist.toolkit.repository.simple.index.db;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

import javax.sql.rowset.CachedRowSet;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.io.FileUtils;

import com.sun.rowset.CachedRowSetImpl;


import gov.nist.toolkit.repository.api.Asset;
import gov.nist.toolkit.repository.api.AssetIterator;
import gov.nist.toolkit.repository.api.Repository;
import gov.nist.toolkit.repository.api.RepositoryException;
import gov.nist.toolkit.repository.api.Type;
import gov.nist.toolkit.repository.api.TypeIterator;
import gov.nist.toolkit.repository.simple.IdFactory;
import gov.nist.toolkit.repository.simple.SimpleAssetIterator;
import gov.nist.toolkit.repository.simple.SimpleRepositoryIterator;
import gov.nist.toolkit.repository.simple.SimpleType;
import gov.nist.toolkit.repository.simple.SimpleTypeIterator;
import gov.nist.toolkit.repository.simple.index.Index;
import gov.nist.toolkit.repository.simple.index.IndexContainer;
import gov.nist.toolkit.repository.simple.index.db.DbConnection;
import gov.nist.toolkit.repository.simple.search.client.PnIdentifier;
import gov.nist.toolkit.repository.simple.search.client.SearchCriteria;
import gov.nist.toolkit.repository.simple.search.client.SearchTerm;
import gov.nist.toolkit.utilities.io.Hash;
import gov.nist.toolkit.xdsexception.XdsInternalException;

/**
 * 
 * @author Sunil.Bhaskarla
 *
 */
public class DbIndexContainer implements IndexContainer, Index {

	
	private static final String repContainerLabel = "repositoryIndex";		
	private static final int syncedStatus = 1001; 
	private static final String assetId = "\"id\"";
	private static final String assetType = "\"type\"";
	private static final String repId = "\"repository\"";

	/* Use an upgrade script to update existing tables in case a newer version of TTT (new ArtRep API) runs against an older copy of the repositoryIndex table in the database */
	private static final String repContainerDefinition = 
	"(repositoryIndexId integer not null  generated always as identity," 	/* (Internal use) This is the primary key */
	+ repId + " varchar(64) not null,"								/* This is the repository Id as it appears on the filesystem */
	+ assetId + " varchar(64) not null,"							/* This is the asset Id of the asset under the repository folder */
	+"parentAssetId varchar(64),"									/* The parent asset Id. A null-value indicates top-level asset and no children */
	+ assetType + " varchar(32) not null," 							/* Asset type - usually same as the keyword property */
	+"hash varchar(40),"											/* (Internal use) The hash of the property file */
	+"repoSession varchar(64))";									/* (Internal use) Stores the indexer repository session id -- later used for removal of stale assets */				
			

	
	@Override
	public String getIndexContainerDefinition() {
		String repContainerHead = 
		"create table " + repContainerLabel;				/* This is the master container for all indexable asset properties */
	
		System.out.println("using label " + repContainerLabel);
		
		return repContainerHead + repContainerDefinition;
		
	}
	
	public int getIndexCount() throws RepositoryException {
		
		try {
			return getQuickCount("select count(*)ct from "+repContainerLabel);
		} catch (RepositoryException e) {
			throw new RepositoryException("count error" , e);
		}
		
	}
	
	/**
	 * Provide a SQL String with ONE count column labeled as "ct"
	 * Returns an integer with the actual count
	 * @param sqlStr
	 * @return
	 * @throws RepositoryException
	 */
	private int getQuickCount(String sqlStr) throws RepositoryException {	
		DbContext dbc = new DbContext();
		dbc.setConnection(DbConnection.getInstance().getConnection());
		
		return dbc.getInt(sqlStr);
		
	}

	@Override
	public boolean doesIndexContainerExist() throws RepositoryException {

		try {
			return (getIndexCount()>=0);
				
		} catch (Exception e) {
			return false;
		}
		
	}
	


	@Override
	public void createIndexContainer() throws RepositoryException {
			try {
				DbContext dbc = new DbContext();
				dbc.setConnection(DbConnection.getInstance().getConnection());
				dbc.internalCmd(getIndexContainerDefinition());
				
				try {
					
					String index = "create unique index \"repAssetUniqueIdx" + repContainerDefinition.hashCode() + "\" on " + repContainerLabel + " (repositoryIndexId,repoSession,"+ repId +"," +  assetId +")";
					dbc.internalCmd(index);					
					index = "create index \"repAssetIdx" + repContainerDefinition.hashCode() + "\" on " + repContainerLabel + " ("+ assetType +",hash)";
					dbc.internalCmd(index);
					
				} catch (SQLException e) {
					System.out.println("index probably exists.");
				}

				dbc.close();				
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RepositoryException(RepositoryException.INDEX_ERROR, e);
			}
		
	}
	
	

	@Override
	public void removeIndexContainer() throws RepositoryException {
		
		try {
			DbContext dbc = new DbContext();
			dbc.setConnection(DbConnection.getInstance().getConnection());
			dbc.internalCmd("drop table "+repContainerLabel);
			dbc.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RepositoryException(RepositoryException.INDEX_ERROR, e);
		}
	}

	@Override
	public void addIndex(String repositoryId) throws RepositoryException {
	
		
	}

	@Override
	public void addIndex(String repositoryId, String assetId, String assetType, String property, String value)
			throws RepositoryException {
		try {
			DbContext dbc = new DbContext();
			dbc.setConnection(DbConnection.getInstance().getConnection());
			String sqlStr = "insert into "+ repContainerLabel + "("+ repId +"," + DbIndexContainer.assetId + ","+ DbIndexContainer.assetType + ", " +  property +" ) values(?,?,?,?)";
			dbc.executePrepared(sqlStr, new String[]{repositoryId,assetId,assetType,value});
			dbc.close();			
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RepositoryException(RepositoryException.INDEX_ERROR, e);
		}
		
	}
	
	
	/**
	 * 
	 * @param repositoryId
	 * @param assetId
	 * @param assetType
	 * @param property
	 * @param value
	 * @throws RepositoryException
	 */
	public void updateIndex(String repositoryId, String assetId, String assetType, String propCol, String value)
			throws RepositoryException {
		try {					
			
			String sqlStr = "select count(*)ct from "+ repContainerLabel + " where "+ DbIndexContainer.assetId +"='" + assetId +"'";
			if (this.getQuickCount(sqlStr)==0) {
				addIndex(repositoryId,assetId,assetType,propCol,value);
			} else { /* Update */
				DbContext dbc = new DbContext();
				dbc.setConnection(DbConnection.getInstance().getConnection());
				sqlStr = "update "+ repContainerLabel + " set "+propCol+"=? where " + DbIndexContainer.assetId + "=? and " + repId   
						+"= ? and " + DbIndexContainer.assetType +  " = ?  and ("+propCol+" is null or "+propCol+" != ?)";
				
				int rowsAffected = dbc.executePrepared(sqlStr, new String[]{value, assetId, repositoryId, assetType, value });				
				System.out.println("rows affected: " + rowsAffected);
				dbc.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RepositoryException(RepositoryException.INDEX_ERROR, e);
		}
		
	}

	
	@Override
	public void removeIndex(String reposId, String sessionId) throws RepositoryException {
		if (reposId!=null && !"".equals(reposId)) {
			
			try {
				DbContext dbc = new DbContext();
				dbc.setConnection(DbConnection.getInstance().getConnection());
	 
				String sqlStr = "delete from "+ repContainerLabel + " where " + repId + " = '" + reposId + "' and repoSession !='" + sessionId + "'";
				dbc.internalCmd(sqlStr);
				dbc.close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RepositoryException(RepositoryException.INDEX_ERROR, e);
			}
		}
		
	}
	
	/**
	 * This method deletes all indexes from the repository index container residing in the database.
	 * The repository on the filesystem is NOT modified.
	 * @throws RepositoryException
	 */
	public void purge() throws RepositoryException {
		try {
			DbContext dbc = new DbContext();
			dbc.setConnection(DbConnection.getInstance().getConnection());
			
			// The TRUNCATE table feature is not implemented Derby 10.4 
			String sqlStr = "delete from "+ repContainerLabel;
			dbc.internalCmd(sqlStr);
			dbc.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RepositoryException(RepositoryException.INDEX_ERROR, e);
		}
		
	}
	
	
	/**
	 * This method will extend the container to allow for new indexable properties.
	 */
	public void expandContainer(String[] column) throws RepositoryException {		 
			expandContainer(column, null);	
	}
	
	/**
	 * This method will extend the container to allow for new indexable properties.
	 */
	public synchronized void expandContainer(String[] column, String assetType) throws RepositoryException {
		try {
			String[] newCol = new String[column.length];
			DbContext dbc = new DbContext();
			dbc.setConnection(DbConnection.getInstance().getConnection());
			String sqlStr = "";
			String index = "";
			
			if (column!=null) {
				int cx=0; 
				for (String c : column) {
					String dbCol = getDbIndexedColumn(assetType, c);
				
					if (!isIndexed(dbCol)) {
						if (cx++>0) index+=",";
						index += dbCol;

						// Can only add one at a time
						sqlStr = "alter table "+ repContainerLabel + " add column " + dbCol + " varchar(64)";					
						dbc.internalCmd(sqlStr);
					} else {
						System.out.println("Column "+ c +" already exists " + ((assetType!=null)?"for assetType: "+assetType:""));
					}
					
				}
				
			}
			try {
				if (sqlStr!="") {
					index = "create index \"repAssetIdxp" + sqlStr.hashCode() + "\" on " + repContainerLabel + " ("+ index +")";
					dbc.internalCmd(index);					
				}
			} catch (SQLException e) {
				System.out.println("Index probably exists.");
			}
			dbc.close();			
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RepositoryException(RepositoryException.INDEX_ERROR, e);
		}		
		
	}
	

	/**
	 * Returns whether a given property for the assetType has a container column associated in the index database.
	 * @param assetType
	 * @param property
	 * @return
	 * @throws RepositoryException
	 */
	public boolean isIndexed(String dbCol) throws RepositoryException {
		int records=0;
		try {
			
			if (!PnIdentifier.uniquePropertyColumn) { // Take care of quoted identifiers
				dbCol = dbCol.replace("\"", "");
			}
			
			DbContext dbc = new DbContext();
			dbc.setConnection(DbConnection.getInstance().getConnection());
			String sqlStr = "select count(c.columnname)ct from sys.syscolumns c, sys.systables t "
							+"where c.referenceid=t.tableid and t.tabletype='T' and lower(t.tablename)=lower('" + repContainerLabel 
							+ "') and lower(c.columnname) = lower('" + dbCol + "')";
			ResultSet rs = dbc.executeQuery(sqlStr);
			while (rs.next()) {
		          records = rs.getInt("ct");
		          System.out.println("records: " + records);

			}
			dbc.close(rs);			

		} catch (Exception e) {
			e.printStackTrace();
			throw new RepositoryException(RepositoryException.INDEX_ERROR, e);			
		}
		if (records==0) 
			return false;
		else
			return true;
	}
	
	public String getColumn(String assetType, String property) throws RepositoryException {
		if (!PnIdentifier.uniquePropertyColumn) {
			return property.replace("\"", "");
		} else 
			return getDbIndexedColumn(assetType, property);
	}

	/**
	 * @param assetType
	 * @param property
	 * @return
	 * @throws RepositoryException
	 */
	
	public static String getDbIndexedColumn(String assetType, String property)
			throws RepositoryException {		
		if (!PnIdentifier.uniquePropertyColumn) {			
			return PnIdentifier.getQuotedIdentifer(property);
		} else {			
			String dbCol = null;		
			
			if (assetType!=null) {
				dbCol = assetType+"_"+property.trim();
			} else if (property.indexOf('_')>-1) {
				dbCol = property.trim();
			} else {
				throw new RepositoryException(RepositoryException.INDEX_ERROR, new Exception("Invalid property or assetType"));
			}
			return dbCol;
		}
	}
	

	/**
	 * 
	 * @param columnName
	 * @return
	 */
	private String getDbColumnSuffix(String columnName) {
		if (!PnIdentifier.uniquePropertyColumn) {
			return columnName; 
		} else {
			if (columnName!=null && columnName.indexOf('_')>-1) {
				String[] assetProp = columnName.split("_");
				if (assetProp.length>1) {
					return assetProp[1];
				}
			} 
			return null;
			
		}
	}
	
	/**
	 * 
	 * @return
	 */
	static public ArrayList<String> getIndexableAssets() {
		return getIndexableProperties(true,false);
	}
	
	/**
	 * Returns full unique column headers including prefix and suffix, ex. "assetTyp_property" 
	 * @return
	 */
	static public ArrayList<String> getIndexableDbProperties() {
		return getIndexableProperties(false,false);		
	}
	
	/**
	 * Returns quoted identifiers if uniquePropertyColumn is not specified, and suffixes only 
	 * @return
	 */
	static public ArrayList<String> getIndexableProperties() {
		return getIndexableProperties(false,true);		
	}
	
	/**
	 * ToDo: this needs to be cached
	 * Returns false to indicate indexing feature is not available
	 */
	static public boolean isRepositoryIndexable(Type rep) {
		
		if (rep==null)
			return false;
		
		TypeIterator it;
		try {
			it = new SimpleTypeIterator(rep);
			while (it.hasNextType()) {				
				Type t = it.nextType();
				
				if (t.getDomain()!=null && t.getDomain().equals(SimpleType.Repository)) {
					if (rep.getKeyword()!=null && rep.getKeyword().equals(t.getKeyword())) {
						return "on".equalsIgnoreCase(t.getIndex());					
					}
				}
			} 
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		System.out.println("returning false");
		return false;
		
	}
	
	/**
	 * ToDo: this needs to be cached  
	 * Note: The repository Configuration must already be setup prior to this call.
	 * Indexable properties should be in the format of: "assetType_property" Example: "siteAsset_patientId"
	 * 
	 * @return
	 */
	static private ArrayList<String> getIndexableProperties(boolean assetOnly, boolean suffixOnly) {
		TypeIterator it;
		ArrayList<String> indexableAssetProperties = null;
		try {
			indexableAssetProperties = new ArrayList<String>();
			
			it = new SimpleTypeIterator();
			while (it.hasNextType()) {
				Type t = it.nextType();
				if (t.getDomain()!=null && t.getDomain().equals(SimpleType.Asset)) {
				 
				/*
				System.out.println ("desc:" + t.getDescription());
				System.out.println ("domain:" + t.getDomain());
				System.out.println ("indexes:" + t.getIndexes());
				*/
				
				if (t.getIndex()!=null) {
					
					String[] indexableProperties = t.getIndex().split(",");
					if (indexableProperties!=null && indexableProperties.length>0) {
						for (String s : indexableProperties) {
							
							String iap = "";
							
							if (!assetOnly) {
								if (!suffixOnly) {
									iap = t.getKeyword() + "_";
								}
								String assetProperty = s.trim();
								int propertyDescriptor = s.indexOf('(');
								if (propertyDescriptor > -1) {
									iap +=assetProperty.substring(0, propertyDescriptor-1);
								} else
									iap += assetProperty.trim();
								
								indexableAssetProperties.add(iap);								
								 
							} else {
								if (!indexableAssetProperties.contains(t.getKeyword())) {
									iap = t.getKeyword();
									indexableAssetProperties.add(iap);
								}
							}


						}
					}
					
				}
				
				}
			}
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return indexableAssetProperties;
	}
	
	/**
	 * This method reindexes all indexable asset properties within all repositories.
	 */
	public void reIndex() throws Exception {
		int totalAssetsIndexed = 0;
		int totalRepositoriesInvolved = 0;
		
		try {
						
			ArrayList<String> assets = getIndexableAssets();
			ArrayList<String> properties = getIndexableProperties(); 
		
			for (String assetType : assets) {
				System.out.println("found indexable asset type: " + assetType);
				SimpleRepositoryIterator it = new SimpleRepositoryIterator();

				while (it.hasNextRepository()) {
					Repository repos = it.nextRepository();					
					
					totalAssetsIndexed += indexRep(repos, properties, assetType );
				totalRepositoriesInvolved++;
			  }				
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		}
		
		System.out.println("Full Index Summary\n"
						+  "==================\n"
						+  "Total Assets Indexed: " + totalAssetsIndexed + "\nRepositories: " + totalRepositoriesInvolved + "\nIndexes up to date.");
	}
	
	private int syncRep(Repository repos, ArrayList<String> properties) throws RepositoryException {

		
		return indexRep(repos,properties,null);
	}

	/**
	 * @param totalAssetsIndexed
	 * @param properties
	 * @param assetType
	 * @param repos
	 * @return
	 * @throws RepositoryException
	 */
	private int indexRep(Repository repos, ArrayList<String> properties, String assetType) throws RepositoryException {
		SimpleAssetIterator iter = null;
		int totalAssetsIndexed = 0;
		String reposId = repos.getId().getIdString();
		
		if (null==assetType) {
			iter = new SimpleAssetIterator(repos.getId());
		} else if (!"".equals(assetType)) {
			iter = new SimpleAssetIterator(repos.getId(), new SimpleType(assetType));	
		}
		 
		String repoSession = new IdFactory().getNewId().getIdString();
		
		while (iter.hasNextAsset()) {				
			Asset a = iter.nextAsset();
			//System.out.println("found indexable asset property: " + a.getDescription());
			
			
			String hash = getHash(a.getPropFile());
			updateIndex(reposId, a.getId().getIdString(), a.getAssetType().getKeyword(), "repoSession", repoSession); 
					
			if (isAssetSynced(a.getId().getIdString(), hash)) {
				Properties assetProps = new Properties();
				try {
					assetProps.load(new FileInputStream(a.getPropFile()));
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				
				expandContainer(assetProps.stringPropertyNames().toArray(new String[assetProps.size()]));
				
				for (String propertyName : assetProps.stringPropertyNames() ) { /* use properties for partial index */ 
					
					try {					
									
						String propertyValue = a.getProperty(propertyName);
						System.out.println("prop-" + propertyName + " -- " + propertyValue);
						if (propertyValue!=null && !"".equals(propertyValue)) {
							updateIndex(reposId, a.getId().getIdString(), a.getAssetType().getKeyword(), getDbIndexedColumn(a.getAssetType().getKeyword(),propertyName), propertyValue);
							totalAssetsIndexed++;
						}
					} catch (Exception e)  {
						; // Ignore if property doesn't exist
					}
				
				}

				updateIndex(reposId, a.getId().getIdString(), a.getAssetType().getKeyword(), "hash", hash); // Note the use of unquoted identifier vs. quoted identifiers for asset property references 								
			}
			
		}
		
		// Take care of stale assets (i.e., assets were indexed at one point but have been removed on filesystem later on)
		// A search would result in ghost assets when stale assets were not removed from the index
		
		removeIndex(reposId,repoSession);
		
		
		
		
		
		
		return totalAssetsIndexed;
	}
	
	private boolean isAssetSynced(String id, String hash) throws RepositoryException {
		
		try {
			DbContext dbc = new DbContext();
			dbc.setConnection(DbConnection.getInstance().getConnection());
			
			int syncStatus = dbc.getInt("select "+ syncedStatus +" from " + repContainerLabel +" where "  + assetId +"='" + id + "' and hash='" + hash + "'");
			return  (syncStatus!=syncedStatus); 
		} catch (Exception e) {
			throw new RepositoryException("Error " + e.toString());
		}
						
	}
		
	private String getHash(File f) {
		try {
			byte[] propContent = FileUtils.readFileToByteArray(f);
			return new Hash().compute_hash(propContent);
		} catch (Exception e) {
			System.out.println("Hash compute error on " + f.toString() + " " + e.toString());
		}
		return "";
	}

	
	/**
	 * 
	 * @param assetId
	 * @param assetType
	 * @param property
	 * @return
	 */
	public String getIndexedPropertyByAssetId(String assetId, String assetType, String  property) {
		try {
			
			DbContext dbc = new DbContext();
			dbc.setConnection(DbConnection.getInstance().getConnection());
			
			String dbCol  = getDbIndexedColumn(assetType, property);
			
			ResultSet rs = dbc.executeQuery("select "+ dbCol +" from "+repContainerLabel + " where " + DbIndexContainer.assetId + "='"+assetId+"'");
			String out = "";
			
			while (rs.next()) {
				out = rs.getString(getColumn(assetType,property));

			}
			dbc.close(rs);			

			return out;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
			
	}
	
	/**
	 * 
	 * @param assetId
	 * @param assetType
	 * @param property
	 * @return
	 */ 

	/**
	 *  
	 * @param repositories
	 * @param searchCriteria
	 * @return
	 */
	public CachedRowSet getAssetsBySearch(Repository[] repositories, SearchCriteria searchCriteria) {
		
		try {
				// Make sure properties exist
			ArrayList<String> searchProperties = searchCriteria.getProperties();
			String searchCriteriaWhere = searchCriteria.toString();
			System.out.println(searchCriteriaWhere);
			
			
			DbContext dbc = new DbContext();
			dbc.setConnection(DbConnection.getInstance().getConnection());
			dbc.internalCmd("drop table session.SearchResults");
			dbc.internalCmd("create table session.SearchResults(repId varchar(64),assetId varchar(64), reposOrder int)");
			
			
			for (Repository rep : repositories) {				
				 syncRep(rep, searchProperties);
			}
			// Search needs to limit search properties to the ones supported by assets belonging to those repositories -
			//  - to avoid searching for out-of-reach properties that do not apply to the repositories in question
			// This needs to be independent from the syncRep call because there columns might not have fully expanded 
			int orderBy=0;
			for (Repository rep : repositories) {
				String sqlString = "insert into session.SearchResults(repId,assetId,reposOrder)"+"select " + DbIndexContainer.repId + ","+ DbIndexContainer.assetId + "," + (orderBy++) +  " from " + repContainerLabel + " where " + repId + " = '"+  rep.getId().getIdString() +"' and( "+ searchCriteriaWhere + ")";
				try {
					dbc.internalCmd(sqlString);
				} catch (SQLException e) {
					System.out.println( "possible non-existent column in where clause? " + e.toString());
				}
			}
					
			ResultSet rs = dbc.executeQuery("select * from session.SearchResults order by reposOrder");
			String out = "";
			
			CachedRowSet crs = new CachedRowSetImpl();
    		crs.populate(rs);
			
			dbc.close(rs);			
			
			return crs;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
			
	}
	
	/**
	 * 
	 * @param searchCriteria
	 * @return
	 
	private String getWhereClause(SearchTerm[] searchCriteria) {
		String whereClause = " where ";
		
		if (searchCriteria!=null) {
			for (SearchTerm st : searchCriteria) {
				if (st.getPropName()!=null && st.getValues()!=null) {
					
					String propertyClause;
					try {
						propertyClause = getDbIndexedColumn(st.getAssetType(), st.getPropName()) + " = ";
					} catch (RepositoryException e) {
						System.out.println("malformed property or asset type");
						e.printStackTrace();
					}
					int propValCt = 1;
					for (String propVal : st.getValues()) {
						// Handle date range here - 
						// see if the property is associated with Timestamp specifier from the Indexable Property List 
						
						propertyClause += "'" + propVal + "'";
						if (propValCt<st.getValues().length) {
							propertyClause += " or ";
						}
						
								
					}
				}
			}
		}
		
		return null;
		
	}
	*/
}