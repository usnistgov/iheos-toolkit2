package gov.nist.toolkit.testengine.assertionEngine;

import gov.nist.toolkit.testengine.engine.TestConfig;
import org.apache.commons.lang.StringUtils;

/**
 * struct to hold parsed {@code <Assert>} element
 */
public class Assertion {
	/**
	 * unique (inside {@code <Assertions>} element) id.
	 */
	public String id;
	/**
	 * process if present, indicates non-xpath evaluation process name
	 */
	public String process;
	/**
	 * String assertion xpath expression
	 */
	public String xpath;

	Assertion(String id, String xpath, TestConfig testConfig) {
		this.id = id;
		this.xpath = xpath.replaceAll("SITE", testConfig.siteXPath);
	}

	Assertion(String id, String xpath, TestConfig testConfig, String process) {
		this(id, xpath, testConfig);
		this.process = process;
	}

	@Override
	public String toString() {
		String prs = "";
		if (StringUtils.isNotBlank(process)) prs = " prs=" + process;
		return "[Assertion: id=" + id + prs + " xpath=" + xpath + "]";
	}

}
