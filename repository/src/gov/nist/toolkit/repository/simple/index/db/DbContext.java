package gov.nist.toolkit.repository.simple.index.db;

import gov.nist.toolkit.repository.api.RepositoryException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * 
 * @author Sunil.Bhaskarla
 *
 */
public class DbContext {

	private Connection connection = null;
	private static boolean debugMode = true;
	
	public DbContext(Connection connection) {
		this.connection = connection;
	}
	
	public DbContext() {
		
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public int getInt(String sqlStr) throws RepositoryException {
		int intVal = 0;
		try {
			if (connection!=null) {
				ResultSet rs = executeQuery(sqlStr);
				while (rs.next()) {
			          intVal = rs.getInt(1);
				}
				close(rs);				
			}

		} catch (SQLException e) {
			throw new RepositoryException("Error, Sqlstate:" + e.getSQLState() , e);
		}
		
		System.out.println("value: " + intVal);
		return intVal;
		
	}

	public String getString(String sqlStr) throws RepositoryException {
		String stringVal = "";
		try {
			if (connection!=null) {
				ResultSet rs = executeQuery(sqlStr);
				while (rs.next()) {
			          stringVal = rs.getString(1);
				}
				close(rs);				
			}

		} catch (SQLException e) {
			throw new RepositoryException("Error, Sqlstate:" + e.getSQLState() , e);
		}
		
		System.out.println("value: " + stringVal);
		return stringVal;
		
	}
	
	/**
	 * This method should be used for DDL or internal container manipulations only WITHOUT any user-provided parameters
	 * @param sqlStr
	 * @return
	 * @throws SQLException
	 */
	public void internalCmd(String sqlStr) throws SQLException {
		if (isDebugMode()) {
			System.out.println("IndexContainer SQL: " +sqlStr);
		}
		if (connection!=null) {
			Statement statement = connection.createStatement();
			statement.execute(sqlStr);			
		}  else {
			throw new SQLException("No connection.");
		}

	}
	

	
	/**
	 * This method should be used for all user-provided values
	 * All key/value pairs are specified as strings in Java properties
	 * @param sqlStr
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public int executePrepared(String sqlStr, String[] params) throws SQLException {
		if (isDebugMode())
			System.out.println("IndexContainer SQL: " +sqlStr);
		if (connection!=null) {
			PreparedStatement statement = connection.prepareStatement(sqlStr);
			int parameterIndex=1;
			for (String p : params) {
				if (isDebugMode()) {
					System.out.println("Setting param: "+parameterIndex + " to <" + p + ">");			
				}
				statement.setString(parameterIndex++, p);
			}
			return statement.executeUpdate();
		}
		throw new SQLException("No connection.");
	}
	
	public ResultSet executeQuery(String sqlStr) throws SQLException {
		if (isDebugMode()) {
			System.out.println("IndexContainer SQL: "+sqlStr);
		}
		
		PreparedStatement statement = connection.prepareStatement(sqlStr);
		return  statement.executeQuery();
		
	}	
	
	public void close(ResultSet rs) {		
		try {
			rs.close();
			this.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void close() {
		if (getConnection()!=null)
			try {
				getConnection().close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}

	public static boolean isDebugMode() {
		return debugMode;
	}
}
