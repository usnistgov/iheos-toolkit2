package gov.nist.toolkit.repository.simple.index;

import gov.nist.toolkit.repository.api.RepositoryException;

import java.sql.Connection;


public interface IndexDataSource {
	void setupDataSource() throws RepositoryException;
	Connection getConnection() throws RepositoryException;
}