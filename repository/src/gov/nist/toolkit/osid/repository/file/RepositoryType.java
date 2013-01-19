package gov.nist.toolkit.osid.repository.file;

import gov.nist.toolkit.osid.shared.Id;

public class RepositoryType implements IRepositoryType {
	Id type;
	
	/* (non-Javadoc)
	 * @see gov.nist.toolkit.osid.repository.file.IRepositoryType#getId()
	 */
	@Override
	public Id getId() {
		return type;
	}
	
	public void setType(Id type) {
		this.type = type;
	}

}
