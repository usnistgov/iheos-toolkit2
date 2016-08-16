package gov.nist.toolkit.testengine.assertionEngine;

import gov.nist.toolkit.testengine.engine.TestConfig;

/**
 *
 */
class Assertion {
	String id;
	String xpath;

	Assertion(String id, String xpath, TestConfig testConfig) {
		this.id = id;
		this.xpath = xpath.replaceAll("SITE", testConfig.siteXPath);
	}

	public String toString() {
		return "[Assertion: id=" + id + " xpath=" + xpath + "]";
	}
}
