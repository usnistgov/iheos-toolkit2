package gov.nist.toolkit.testengine.assertionEngine;

import gov.nist.toolkit.testengine.engine.TestConfig;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.commons.lang.StringUtils;

/**
 * struct to hold parsed {@code <Assert>} element
 */
public class Assertion {
   
   /**
    * {@code <Assert>} element from testplan.xml
    */
   public OMElement assertElement;
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
	
	Assertion(OMElement asser, TestConfig testConfig, String date) {
	   assertElement = asser;
	   id = asser.getAttributeValue(new QName("id"));
      process = asser.getAttributeValue(new QName("process"));
      this.xpath = asser.getText()
         .replaceAll("\\$DATE\\$", date)
         .replaceAll("SITE", testConfig.siteXPath);
	}

	@Override
	public String toString() {
		String prs = "";
		if (StringUtils.isNotBlank(process)) prs = " prs=" + process;
		return "[Assertion: id=" + id + prs + " xpath=" + xpath + "]";
	}

}
