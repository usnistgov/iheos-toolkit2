package gov.nist.toolkit.repository.simple;

import gov.nist.toolkit.registrymetadata.UuidAllocator;
import gov.nist.toolkit.repository.api.Id;

public class IdFactory {
	
	public Id getNewId() {
		String id = UuidAllocator.allocate();
		String[] parts = id.split(":");
		id = parts[2];
		//		id = id.replaceAll("-", "_");

		return new SimpleId(id);
	}

	
}
