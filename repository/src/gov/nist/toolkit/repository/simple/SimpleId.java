package gov.nist.toolkit.repository.simple;

import gov.nist.toolkit.repository.api.Id;
import gov.nist.toolkit.repository.api.RepositoryException;

public class SimpleId implements Id {
	String guid;
	/**
	 * 
	 */
	private static final long serialVersionUID = 8299302965195505251L;
	
	protected SimpleId(String id) {
		this.guid = id;
	}

	@Override
	public String getIdString() throws RepositoryException {
		return guid;
	}

	@Override
	public boolean isEqual(Id id) throws RepositoryException {
		return id != null && id.getIdString().equals(guid);
	}

	public String toString() {
		return guid;
	}
	
}
