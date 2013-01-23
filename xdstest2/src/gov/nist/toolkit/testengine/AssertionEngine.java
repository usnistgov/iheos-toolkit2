package gov.nist.toolkit.testengine;

import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.utilities.xml.Parse;
import gov.nist.toolkit.utilities.xml.Util;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.XMLParserException;
import gov.nist.toolkit.xdsexception.XdsException;
import gov.nist.toolkit.xdsexception.XdsInternalException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Formatter;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.xpath.AXIOMXPath;

public class AssertionEngine {
	ArrayList<OMElement> raw_data_refs = null;
	ArrayList<OMElement> raw_assertions = null;
	Linkage linkage = null;
	OMElement output = null;
	OMElement data = null;
	TestConfig testConfig;

	class DataRef {
		String file;
		String as;
		DataRef(String file, String as) { this.file = file; this.as = as; }
	} 

	ArrayList<DataRef> refs = new ArrayList<DataRef>();

	public void setTestConfig(TestConfig config) {
		testConfig = config;
	}
	
	public void setLinkage(Linkage l) {
		linkage = l;
	}
	
	void parseDataRefs() throws XdsException {
		for (OMElement xref : raw_data_refs) {
			String file = xref.getAttributeValue(new QName("file"));
			String as = xref.getAttributeValue(new QName("as"));
			if (file == null || file.equals(""))
				throw new XdsException("DataRef has missing or empty file attribute", null);
			if (as == null || as.equals(""))
				throw new XdsException("DataRef has missing or empty as attribute", null);

			if (file.contains("MGMT")) {
				file = file.replaceFirst("MGMT", testConfig.testmgmt_dir);
			}

			DataRef dr = new DataRef(file, as);
			refs.add(dr);
		}
	}

	void buildDataModel() throws XMLParserException, XdsInternalException {
		data = MetadataSupport.om_factory.createOMElement(new QName("Data"));
		boolean is_this = false;
		for (DataRef ref : refs) {

			OMElement file_root = null;
			if (ref.file.equals("THIS")) {
				// output holds output of this step.  Here we get the output of
				// the entire test plan (entire contents of log.xml)
				// because some test scripts make reference to previous steps
				file_root = (OMElement)((OMElement) output.getParent()).getParent();
				is_this = true;
			} else {
				try {
					file_root = Parse.parse_xml_file(ref.file);
				} catch (Exception e) {
					// hmmm, input may be in log directory, try that
					file_root = Parse.parse_xml_file(testConfig.logFile.getParent() + "/" + ref.file);
				}
			}
			OMElement wrapper = MetadataSupport.om_factory.createOMElement(new QName(ref.as));
			data.addChild(wrapper);

			if (is_this)
				wrapper.addChild(Util.deep_copy(file_root));
			else
				wrapper.addChild(file_root);
		}
	}

	class Assertion {
		String id;
		String xpath;
		Assertion(String id, String xpath) { 
			this.id = id; 
			this.xpath = xpath.replaceAll("SITE", testConfig.siteXPath);
		}
		
		public String toString() {
			return "[Assertion: id=" +id + " xpath=" + xpath + "]";
		}
	}

	ArrayList<Assertion> assertions = new ArrayList<Assertion>();

	String date() {  // return date in 20081009 format
		StringBuilder sb = new StringBuilder();
		// Send all output to the Appendable object sb
		Formatter formatter = new Formatter(sb, Locale.US);
		Calendar c = new GregorianCalendar();
		formatter.format("%s%02d%02d", c.get(Calendar.YEAR), c.get(Calendar.MONTH)+1, c.get(Calendar.DAY_OF_MONTH));
		return sb.toString();
	}

	void parseAssertions() throws XdsException {
		for (OMElement asser : raw_assertions) {
			if (linkage != null) {
				linkage.apply(asser);
			}
			String id = asser.getAttributeValue(new QName("id"));
			String xpath = asser.getText();
			xpath = xpath.replaceAll("\\$DATE\\$", date());	

			assertions.add(new Assertion(id, xpath));
		}
	}

	public void setDataRefs(ArrayList<OMElement> data_refs) {
		this.raw_data_refs = data_refs;
	}

	public void setAssertions(ArrayList<OMElement> assertions) {
		this.raw_assertions = assertions;
	}

	public void setOutput(OMElement output) {
		this.output = output;
	}

	public void run(ErrorReportingInterface err, OMElement assertion_output) throws XdsInternalException {
		ILogger logger = new TestLogFactory().getLogger();
		try {
			parseDataRefs();
			buildDataModel();
			parseAssertions();
		}
		catch (Exception e) {
			err.fail("AssertionEngine Error: " + e.getMessage());
			return;
		}
		

		logger.add_name_value(assertion_output, "CompiledAssertion", assertions.toString());
		logger.add_name_value(assertion_output,"RawAssertionData", data);
		logger.add_name_value(assertion_output, "AssertionCount", Integer.toString(assertions.size()));
		
		
		try {
			for (Assertion assertion : assertions) {
				AXIOMXPath xpathExpression = new AXIOMXPath (assertion.xpath);
				String result = xpathExpression.stringValueOf(data);
				if (result == null || !result.toLowerCase().equals("true")) {
					StringBuffer errs = new StringBuffer();
					errs.append("AssertionEngine: assertion " + assertion.id + " failed - detailed result is " + result + "\n" +
					      "Assertion is " + assertion.xpath);
					int equals_index = findNotEqualsNotInBrackets(assertion.xpath);
					if (equals_index == -1)
						equals_index = findEqualsNotInBrackets(assertion.xpath);
					if ( equals_index > 0) {
						// offer some more details
						String eqToken = tokenAt(assertion.xpath, equals_index);
						String left_side_xpath = assertion.xpath.substring(0, equals_index).trim();
						String right_side_xpath = assertion.xpath.substring(equals_index + eqToken.length()).trim();
						
						
						String left_side_value;
						if (left_side_xpath.indexOf("//") == -1)
							left_side_value = left_side_xpath;
						else
							left_side_value= (new AXIOMXPath(left_side_xpath)).stringValueOf(data);
						
						String right_side_value;
						if (right_side_xpath.indexOf("//") == -1)
							right_side_value = right_side_xpath;
						else
							right_side_value = (new AXIOMXPath(right_side_xpath)).stringValueOf(data);
						
						errs.append("AssertionEngine: assertion " + assertion.id + " left side value is " + left_side_value + "\n" +
								"AssertionEngine: assertion " + assertion.id + " right side value is " + right_side_value + "\n"+
								"AssertionEngine: operator is " + tokenAt(assertion.xpath, equals_index));
					}
					logger.add_name_value_with_id(assertion_output, "AssertionStatus", assertion.id, "fail");
					err.fail(errs.toString());
				} else {
					logger.add_name_value_with_id(assertion_output, "AssertionStatus", assertion.id, "pass");
					err.setInContext("AssertionEngine: assertion " + assertion.id, "pass");
				}
			}
		} 
		catch (Exception e) {
			err.fail("AssertionEngine: exception " + e.getClass().getName() + ": " + e.getMessage() + '\n' + ExceptionUtil.exception_details(e));
			return ;
		}
	}

	char bracketLeft(String s, int start) {
		for (int i=start; i>0; i--) {
			if (s.charAt(i) == '[') return '[';
			if (s.charAt(i) == ']') return ']';
		}
		return ' ';
	}

	char bracketRight(String s, int start) {
		for (int i=start; i<s.length(); i++) {
			if (s.charAt(i) == '[') return '[';
			if (s.charAt(i) == ']') return ']';
		}
		return ' ';
	}

	boolean closeLeft(String s, int start) {
		char c = bracketLeft(s, start);
		return c == ']' || c == ' ';
	}

	boolean openRight(String s, int start) {
		char c = bracketRight(s, start);
		return c == '[' || c == ' ';
	}

	int findEqualsNotInBrackets(String s) {
		int i = s.indexOf('=');
		while (i != -1) {
			if (closeLeft(s, i) && openRight(s,i))
				return i;
			i = s.indexOf('=', i + 1);
		}
		return -1;
	}

	int findNotEqualsNotInBrackets(String s) {
		int i = s.indexOf("!=");
		while (i != -1) {
			if (closeLeft(s, i) && openRight(s,i))
				return i;
			i = s.indexOf('=', i + 2);
		}
		return -1;
	}

	boolean isWhite(char c) { return c == ' ' || c == '\t' || c == '\n'; }

	String tokenAt(String s, int start) {
		for (int i=start; i<s.length(); i++)
			if (isWhite(s.charAt(i)))
				return s.substring(start, i);
		return "";
	}

}
