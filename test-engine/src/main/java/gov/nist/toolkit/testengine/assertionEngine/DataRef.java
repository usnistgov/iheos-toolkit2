package gov.nist.toolkit.testengine.assertionEngine;

/**
 *
 */
class DataRef {
    String file;
	// special files
	//  MGMT - testConfig.testmgmt_dir
	//  THIS - output of current step
    String as;

	// name to use in data store
    DataRef(String file, String as) {
		this.file = file;
		this.as = as;
	}

	public String toString() {
		return String.format("DataRef: file:%s as:%s", file, as);
	}
}
