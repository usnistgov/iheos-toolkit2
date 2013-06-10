package gov.nist.toolkit.repository.simple.index.db;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.commons.beanutils.PropertyUtils;


import gov.nist.toolkit.repository.api.Asset;
import gov.nist.toolkit.repository.api.AssetIterator;
import gov.nist.toolkit.repository.api.Repository;
import gov.nist.toolkit.repository.api.RepositoryException;
import gov.nist.toolkit.repository.api.Type;
import gov.nist.toolkit.repository.api.TypeIterator;
import gov.nist.toolkit.repository.simple.SimpleAssetIterator;
import gov.nist.toolkit.repository.simple.SimpleRepositoryIterator;
import gov.nist.toolkit.repository.simple.SimpleType;
import gov.nist.toolkit.repository.simple.SimpleTypeIterator;
import gov.nist.toolkit.repository.simple.index.Index;
import gov.nist.toolkit.repository.simple.index.IndexContainer;
import gov.nist.toolkit.repository.simple.index.db.DbConnection;
import gov.nist.toolkit.repository.simple.search.SearchTerm;

/**
 * 
 * @author Sunil.Bhaskarla
 *
 */
public class DbIndexContainer implements IndexContainer, Index {

	/* Use an upgrade script to update existing tables in case a newer version of TTT (new RI API) runs against an older copy of the repositoryIndex table in the database */
	private static final String repContainerDefinition = 
	"(id integer not null  generated always as identity," 	/* This is the internal Id (not used by the application) */
	+"repId varchar(64) not null,"							/* This is the repository Id as it appears on the filesystem */
	+"assetId varchar(64) not null,"						/* This is the asset Id of the asset under the repository folder */
	+"parentAssetId varchar(64),"							/* The parent asset Id. A null-value indicates top-level asset and no children */ 
	+"assetType varchar(32) not null)";						/* The type of asset */
	
	private static final String containerLabel = "repositoryIndex";
	
	@Override
	public String getIndexContainerDefinition() {
		String repContainerHead = 
		"create table " + containerLabel;				/* This is the master container for all indexable asset properties */
	
		System.out.println("using label " + containerLabel);
		
		return repContainerHead + repContainerDefinition;
		
	}
	
	public int getIndexCount() throws RepositoryException {
		
		try {
			return getQuickCount("select count(*)ct from "+containerLabel);
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
		int records = 0;
		try {
			DbContext dbc = new DbContext();
			dbc.setConnection(DbConnection.getInstance().getConnection());
			ResultSet rs = dbc.executeQuery(sqlStr);
			while (rs.next()) {
		          records = rs.getInt("ct");
		          System.out.println("records: " + records);

			}
			dbc.close(rs);

		} catch (SQLException e) {
			throw new RepositoryException("table error, sqlstate:" + e.getSQLState() , e);
		}
		
		return records;
		
	}

	@Override
	public boolean doesIndexContainerExist() throws RepositoryException {

		try {
			if (getIndexCount()>=0)
				return true;
		} catch (Exception e) {
			return false;
		}
		
		return true;
	}
	


	@Override
	public void createIndexContainer() throws RepositoryException {
			try {
				DbContext dbc = new DbContext();
				dbc.setConnection(DbConnection.getInstance().getConnection());
				dbc.executeCmd(getIndexContainerDefinition());
				
				try {
					
					String index = "create unique index \"repAssetUniqueIdx" + repContainerDefinition.hashCode() + "\" on " + containerLabel + " (id,repId,assetId)";
					dbc.executeCmd(index);					
					index = "create index \"repAssetIdx" + repContainerDefinition.hashCode() + "\" on " + containerLabel + " (repId,assetType)";
					dbc.executeCmd(index);
					
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
			dbc.executeCmd("drop table "+containerLabel);
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
			String sqlStr = "insert into "+ containerLabel + "(repId,assetId,assetType,"+assetType+"_"+property+") values('" + repositoryId + "','"+ assetId + "','" + assetType + "','"+value + "')";
			dbc.executeCmd(sqlStr);
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
	public void updateIndex(String repositoryId, String assetId, String assetType, String property, String value)
			throws RepositoryException {
		try {
			String sqlStr = "select count(*)ct from "+ containerLabel + " where assetId='" + assetId +"'";
			if (this.getQuickCount(sqlStr)==0) {
				addIndex(repositoryId,assetId,assetType,property,value);
			} else { /* Update */
				DbContext dbc = new DbContext();
				dbc.setConnection(DbConnection.getInstance().getConnection());
				String propCol = assetType+"_"+property;
				sqlStr = "update "+ containerLabel + " set "+propCol+"='"+value + "' where assetId='"+ assetId +"' and repId='"+repositoryId 
						+"' and assetType='"+assetType+"' and ("+propCol+" is null or "+ propCol +" !='" + value +"')";
				System.out.println("rows affected: " + dbc.executeUpdate(sqlStr));
				dbc.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RepositoryException(RepositoryException.INDEX_ERROR, e);
		}
		
	}

	
	@Override
	public void removeIndex(String assetId) throws RepositoryException {
		// TODO Auto-generated method stub
		
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
			String sqlStr = "delete from "+ containerLabel;
			dbc.executeCmd(sqlStr);
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
	public void expandContainer(String[] column, String assetType) throws RepositoryException {
		try {
			DbContext dbc = new DbContext();
			dbc.setConnection(DbConnection.getInstance().getConnection());
			String sqlStr = "";
			String index = "";
			
			if (column!=null) {
				int cx=0; 
				for (String c : column) {
					String dbCol = getDbColumn(assetType, c);
					
					if (!isIndexed(assetType, dbCol)) {				
						if (cx++>0) index+=",";
						index += dbCol;
						
						sqlStr = "alter table "+ containerLabel + " add column " + dbCol + " varchar(64)";					
						dbc.executeCmd(sqlStr);
					} else {
						System.out.println("Column "+ c +" already exists " + ((assetType!=null)?"for assetType: "+assetType:""));
					}
					
				}
				
			}
			try {
				if (sqlStr!="") {
					index = "create index \"repAssetIdxp" + sqlStr.hashCode() + "\" on " + containerLabel + " ("+ index +")";
					dbc.executeCmd(index);					
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
	 * 
	 * @param property (in the form of assetType_property)
	 * @return
	 * @throws RepositoryException
	 */
	public boolean isIndexed(String property) throws RepositoryException {
		return this.isIndexed(null, property);
	}
	
	/**
	 * Returns whether a given property for the assetType has a container column associated in the index database.
	 * @param assetType
	 * @param property
	 * @return
	 * @throws RepositoryException
	 */
	public boolean isIndexed(String assetType, String property) throws RepositoryException {
		int records=0;
		try {
			String dbCol = getDbColumn(assetType, property);
				
			
			DbContext dbc = new DbContext();
			dbc.setConnection(DbConnection.getInstance().getConnection());
			String sqlStr = "select count(c.columnname)ct from sys.syscolumns c, sys.systables t "
							+"where c.referenceid=t.tableid and t.tabletype='T' and lower(t.tablename)=lower('" + containerLabel 
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

	/**
	 * @param assetType
	 * @param property
	 * @return
	 * @throws RepositoryException
	 */
	private String getDbColumn(String assetType, String property)
			throws RepositoryException {
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

	/**
	 * 
	 * @param propertyName
	 * @return
	 */
	public String getDbColumnSuffix(String columnName) {
		if (columnName!=null && columnName.indexOf('_')>-1) {
			String[] assetProp = columnName.split("_");
			if (assetProp.length>1) {
				return assetProp[1];
			}
		} 
		return null;
	}
	
	/**
	 * 
	 * @return
	 */
	static public ArrayList<String> getIndexableAssets() {
		return getIndexableProperties(false);
	}
	
	/**
	 * 
	 * @return
	 */
	static public ArrayList<String> getIndexableProperties() {
		return getIndexableProperties(true);
		
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
	static private ArrayList<String> getIndexableProperties(boolean combinedForm) {
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
							
							if (combinedForm) {
								iap = t.getKeyword() + "_";
							
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
						
			ArrayList<String> iapAssets = getIndexableAssets();
			ArrayList<String> iap = getIndexableProperties();
		
			for (String aType : iapAssets) {
				System.out.println("found indexable asset type: " + aType);
				SimpleRepositoryIterator it = new SimpleRepositoryIterator();

				while (it.hasNextRepository()) {
					Repository repos = it.nextRepository();					
					
					SimpleAssetIterator sit = new SimpleAssetIterator(repos.getId(), new SimpleType(aType)); 
					
					while (sit.hasNextAsset()) {				
						Asset a = sit.nextAsset();
						//System.out.println("found indexable asset property: " + a.getDescription());
						

						
						for (String s : iap) {
							
							try {
								String propertyName = getDbColumnSuffix(s);								
								String propertyValue = PropertyUtils.getProperty(a, propertyName).toString();
								System.out.println(propertyName);
								updateIndex(repos.getId().getIdString(), a.getId().getIdString(), a.getAssetType().getKeyword(), propertyName, propertyValue);
								totalAssetsIndexed++;
							} catch (Exception e)  {
								; //Ignore NoSuchMethodException if such property doesn't exist
							}

						
					}	
				}
				totalRepositoriesInvolved++;
			  }				
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new Exception(e);
		}
		
		System.out.println("Full Index Summary\n"
						+  "==================\n"
						+  "Total Assets Indexed: " + totalAssetsIndexed + "\nRepositories: " + totalRepositoriesInvolved + "\nIndexes up to date.");
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
			
			String dbCol  = getDbColumn(assetType, property);
			
			ResultSet rs = dbc.executeQuery("select "+ dbCol +" from "+containerLabel + " where assetId='"+assetId+"'");
			String out = "";
			
			while (rs.next()) {
				out = rs.getString(dbCol);

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

	/*
	public AssetIterator getAssetsByIndexedProperty(Repository[] repositories, Properties searchCriteria) {
	*/
	/**
	 * Null repositories could mean to search across all repositories? 
	 * @param repositories
	 * @param searchCriteria
	 * @return
	 */
	public AssetIterator getAssetsByIndexedProperty(Repository[] repositories, SearchTerm[] searchCriteria) {	
	try {
			
			// String whereClause = getWhereClause(searchCriteria);
			
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
						propertyClause = getDbColumn(st.getAssetType(), st.getPropName()) + " = ";
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