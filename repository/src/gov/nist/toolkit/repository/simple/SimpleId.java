package gov.nist.toolkit.repository.simple;

import gov.nist.toolkit.repository.api.Id;
import gov.nist.toolkit.repository.api.SharedException;

public class SimpleId implements Id {
	String guid;
	/**
	 * 
	 */
	private static final long serialVersionUID = 8299302965195505251L;

	@Override
	public String getIdString() throws SharedException {
		return guid;
	}

	@Override
	public boolean isEqual(Id id) throws SharedException {
		return id != null && id.getIdString().equals(guid);
	}

}
