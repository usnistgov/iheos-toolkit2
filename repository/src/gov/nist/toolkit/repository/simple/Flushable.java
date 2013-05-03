package gov.nist.toolkit.repository.simple;

import gov.nist.toolkit.repository.api.RepositoryException;

public interface Flushable {
	boolean isAutoFlush();
	void setAutoFlush(boolean autoFlush);
	void flush() throws RepositoryException;
}
