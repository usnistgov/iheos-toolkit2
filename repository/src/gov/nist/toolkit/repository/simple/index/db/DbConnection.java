package gov.nist.toolkit.repository.simple.index.db;

import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.repository.api.RepositoryException;
import gov.nist.toolkit.repository.simple.index.IndexDataSource;

// This is for the Apache Commons DBCP
//import org.apache.commons.dbcp.BasicDataSource;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * This class provides a JDBC compatible (Derby) database connection.
 * DbProvider is a singleton class that allows for automatic connection pooling through Tomcat JDBC Pooling.
 * @author Sunil.Bhaskarla
 *
 */
public class DbConnection implements IndexDataSource {


	// See this web page on why Tomcat's own JDBC pooling is preferred to DBCP
	// http://tomcat.apache.org/tomcat-7.0-doc/jdbc-pool.htm
	
	// This is for the DBCP method	
	//private static BasicDataSource bds = null;
	
     private static DataSource bds = null;
     
	static DbConnection self = null;
	
	private DbConnection()  {		
		try {
			setupDataSource();
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static public DbConnection getInstance() {
		if (self==null) {
			synchronized (DbConnection.class) {
				if (self==null) {
					self = new DbConnection();
				}
			}
		}
		return self;
	}

	@Override
	public void setupDataSource() throws RepositoryException {
		if (bds==null) {

			String ecDir = null; 
			try {
				ecDir = Installation.installation().propertyServiceManager().getToolkitProperties().get("External_Cache");
				
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			
			
			if (ecDir==null) {
				System.out.println("E100: Could not get External_Cache from Installation setup. Database could not instantiated.");
				return;
			}
			
			  PoolProperties p = new PoolProperties();
			 
	          p.setUrl("jdbc:derby:"+ ecDir +"/db;create=true");
	          
	          //local "jdbc:derby:c:\\e\\myderby.db;create=true"
	          
	          p.setDriverClassName("org.apache.derby.jdbc.EmbeddedDriver");
//	          p.setUsername("root");
//	          p.setPassword("password");
//	          p.setJmxEnabled(true);
//	          p.setTestWhileIdle(false);
//	          p.setTestOnBorrow(true);
//	          p.setValidationQuery("SELECT 1");
//	          p.setTestOnReturn(false);
//	          p.setValidationInterval(30000);
	          p.setTimeBetweenEvictionRunsMillis(30000);
	          p.setMaxActive(500);
	          p.setInitialSize(10);
	          p.setMaxWait(10000);
	          p.setRemoveAbandonedTimeout(60);
	          p.setMinEvictableIdleTimeMillis(30000);
	          p.setMinIdle(20);
	          p.setLogAbandoned(true);
	          p.setRemoveAbandoned(true);
//	          p.setJdbcInterceptors(
//	            "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"+
//	            "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");

			  bds = new DataSource();
	          bds.setPoolProperties(p);
	          
			// DBCP 
			//bds = new BasicDataSource();	          			
//			bds.setDriverClassName("org.apache.derby.jdbc.ClientDriver");
//			bds.setUrl("jdbc:derby:c:\\e\\myderby.db;create=true");			
		}
		
		
	}
	
	@Override
	public Connection getConnection()  {
		Connection cnx = null;
		try {
			if (bds!=null) {
				cnx = bds.getConnection();
				if (cnx.isClosed())
					System.out.println("Connection failed because it is already closed.");				
			} else {
				return null;
			}
			
		} catch (SQLException e) {
			System.out.println("Connect failed");
			e.printStackTrace();
		}
		return cnx;
	}
	
	/**
	 * 
	 */
	public void printConnectionSummary() {
		System.out.println("Active " + bds.getActive());
		System.out.println("Max active "+ bds.getMaxActive());
		System.out.println("Max idle "+ bds.getMaxIdle());
		System.out.println("Max wait "+ bds.getMaxWait());
		System.out.println("------");
	}
	
}