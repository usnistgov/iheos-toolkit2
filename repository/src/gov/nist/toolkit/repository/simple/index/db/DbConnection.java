package gov.nist.toolkit.repository.simple.index.db;

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

	public boolean executeCmd(String sqlStr) throws SQLException {
		if (debugMode)
			System.out.println("IndexContainer SQL: " +sqlStr);
		if (connection!=null) {
			Statement statement = connection.createStatement();			
			if (statement.execute(sqlStr)) { 
				System.out.println("success!");
			}
				
		}  else {
			throw new SQLException("No connection is associated with this context.");
		}
		return true;
	}
	
	public int executeUpdate(String sqlStr) throws SQLException {
		int rowsAffected = 0;
		if (debugMode)
			System.out.println("IndexContainer SQL: " +sqlStr);
		if (connection!=null) {
			Statement statement = connection.createStatement();
			
			rowsAffected=statement.executeUpdate(sqlStr);			
				
		}  else {
			System.out.println("No connection is associated with this context.");
			//  It is possible that another Db client is already running outside this scope.
		}
		return rowsAffected;
	}
	
	public ResultSet executeQuery(String sqlStr) throws SQLException {
		if (debugMode)
			System.out.println("IndexContainer SQL: "+sqlStr);
		
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
}
