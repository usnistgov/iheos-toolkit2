package gov.nist.toolkit.osid.repository.file;

import gov.nist.toolkit.osid.repository.IType;
import gov.nist.toolkit.osid.shared.Id;

public class Type implements IType {
	Id id;
	
	public Type() {}
	public Type(Id typeId) { id = typeId; }
	
	/* (non-Javadoc)
	 * @see gov.nist.toolkit.osid.repository.file.IType#getId()
	 */
	@Override
	public Id getId() {
		return id;
	}

	public String toString() { return "Type:" + id.getId(); }
}
