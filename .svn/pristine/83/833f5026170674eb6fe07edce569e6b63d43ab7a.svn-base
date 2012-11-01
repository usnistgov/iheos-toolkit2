package gov.nist.toolkit.common.adt;

import java.sql.SQLException;

public class Verify {
	AdtJdbcConnection connection;

	public Verify() {
		this.connection = null;
	}

	public boolean isValid(String patientId) throws SQLException, ClassNotFoundException {
		AdtJdbcConnection conn = getConnection();
		boolean isv = conn.doesIdExist(patientId);
		conn.close();
		return isv;
	}

	AdtJdbcConnection getConnection() throws java.sql.SQLException, ClassNotFoundException {
		if (connection == null)
			connection = new AdtJdbcConnection("localhost" + ":" + "5432");
		return connection;
	}


}
