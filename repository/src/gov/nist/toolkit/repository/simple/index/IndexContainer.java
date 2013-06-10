package gov.nist.toolkit.repository.simple.index;

import gov.nist.toolkit.repository.api.RepositoryException;

public interface IndexContainer {
	String getIndexContainerDefinition();
	boolean doesIndexContainerExist() throws RepositoryException;
	void createIndexContainer() throws RepositoryException;
	void removeIndexContainer() throws RepositoryException;
}