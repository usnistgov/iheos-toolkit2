package gov.nist.toolkit.testkitutilities;

import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.utilities.xml.Util;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.XdsInternalException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.parsers.FactoryConfigurationError;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.xpath.AXIOMXPath;
import org.jaxen.JaxenException;

/**
 * Build XML file describing structure of testkit.  Used to validate
 * test results.
 * @author bill
 *
 */
public class TestkitStructure extends TestkitWalker {
	OMElement testkitDesc;
	static String outputdir;
	static String toolkit;
	static String testkit;
	String testkitVersion;
	String serverVersion;
	/**
	 * Used when generating testkit structure
	 * @throws IOException 
	 */
	public TestkitStructure() throws IOException {
		testkitDesc = MetadataSupport.om_factory.createOMElement("testkit", null);
		testkitDesc.addAttribute("serverVersion", loadServerVersion(), null);
	}

	/**
	 * Used when reading testkit structure
	 * @param testkitStructFile
	 * @throws XdsInternalException
	 * @throws FactoryConfigurationError
	 * @throws IOException 
	 */
	public TestkitStructure(File testkitStructFile, String testkitVersion) throws XdsInternalException, FactoryConfigurationError, IOException {
		System.out.println("TestkitStructure: loading " + testkitStructFile);
		testkitDesc = Util.parse_xml(testkitStructFile);
		this.testkitVersion = testkitVersion;
		serverVersion = testkitDesc.getAttributeValue(new QName("serverVersion"));
		System.out.println("TestkitStructure: serverVersion = " + serverVersion);
		if (serverVersion == null || serverVersion.equals(""))
			throw new XdsInternalException("Error loading testkit definition - serverVersion not present");
	}

	public OMElement getTest(String testnum) {
		return MetadataSupport.getChild(testkitDesc, "test", testnum);
	}

	public List<String> getSectionNames(String testnum) throws Exception {
		OMElement testEle = getTest(testnum);
		if (testEle == null) 
			throw new Exception("Test " + testnum + " not found in testkit structure database for testkit version " + testkitVersion);
		List<String> names = new ArrayList<String>();
		for (OMElement sectionEle : MetadataSupport.childrenWithLocalName(testEle, "section")) {
			names.add(sectionEle.getAttributeValue(MetadataSupport.id_qname));
		}
		return names;
	}

	public OMElement getSection(String testnum, String sectionId) throws Exception {
		OMElement testEle = getTest(testnum);
		if (testEle == null) 
			throw new Exception("Test " + testnum + " not found in testkit structure database");
		if (sectionId == null)
			return testEle;
		return MetadataSupport.getChild(testEle, "section", sectionId);
	}
	
	public boolean isServerTest(String testnum) throws Exception {
		return "server".equals(getArea(testnum));
	}

	public String getArea(String testnum) throws Exception {
		OMElement testEle = getTest(testnum);
		if (testEle == null) 
			throw new Exception("Test " + testnum + " not found in testkit structure database");
		System.out.println("found test " + testnum);
		String area = testEle.getAttributeValue(new QName("area"));
		if (area == null)
			throw new Exception("Testkit structure error: test " + testnum + " has no area declaration");
		System.out.println("area is " + area);
		return area;
	}

	public String getEndpoint(String testnum) throws Exception {
		OMElement testEle = getTest(testnum);
		if (testEle == null) 
			throw new Exception("Test " + testnum + " not found in testkit structure database");
		String endpoint = testEle.getAttributeValue(new QName("endpoint"));
		if (endpoint == null)
			throw new Exception("Testkit structure error: test " + testnum + " has no endpoint declaration");
		return endpoint;
	}
	
	public List<String> getStepNames(String testnum) throws Exception {
		OMElement testEle = getTest(testnum);
		if (testEle == null) 
			throw new Exception("Test " + testnum + " not found in testkit structure database");
		List<String> names = new ArrayList<String>();
		for (OMElement stepEle : MetadataSupport.childrenWithLocalName(testEle, "step")) {
			names.add(stepEle.getAttributeValue(MetadataSupport.id_qname));
		}
		return names;
	}

	public List<String> getStepNames(String testnum, String section) throws Exception {
		OMElement testEle = getTest(testnum);
		if (testEle == null) 
			throw new Exception("Test " + testnum + " not found in testkit structure database");
		List<String> names = new ArrayList<String>();
		for (OMElement sectionEle : MetadataSupport.childrenWithLocalName(testEle, "section")) {
			String sectionName = sectionEle.getAttributeValue(MetadataSupport.id_qname);
			if (sectionName != null && sectionName.equals(section)) {
				for (OMElement stepEle : MetadataSupport.childrenWithLocalName(sectionEle, "step")) {
					names.add(stepEle.getAttributeValue(MetadataSupport.id_qname));
				}
			}
		}
		System.out.println("steps for test " + testnum + "/" + section + " are " + names);
		return names;
	}

	String getTestId(File testplan) {
		String[] filenameElements = testplan.toString().split("\\/");

		if (testkitPathElementsToIgnore >= filenameElements.length) {
			System.out.println("Cannot parse " + testplan.toString() + 
			" looking for testId");
			System.exit(-1);
		}

		return filenameElements[testkitPathElementsToIgnore];
	}


	// returns null if no section
	String getSectionId(File testplan) {
		String[] filenameElements = testplan.toString().split("\\/");

		int testIndex = testkitPathElementsToIgnore;
		int sectionIndex = testIndex + 1;

		if (sectionIndex >= filenameElements.length)
			return null;

		if (filenameElements[sectionIndex].equals("testplan.xml"))
			return null;
		return filenameElements[sectionIndex];
	}

	List<String> getStepIds(File testplan) {
		ArrayList<String> ids = new ArrayList<String>();
		OMElement tplan;
		try {
			tplan = Util.parse_xml(testplan);
			AXIOMXPath xpathExpression = new AXIOMXPath ("//TestPlan/TestStep");
			List<OMNode> nodes = xpathExpression.selectNodes(tplan);

			for (int i=0; i<nodes.size(); i++) {
				OMElement testStep = (OMElement) nodes.get(i);
				ids.add(testStep.getAttributeValue(MetadataSupport.id_qname));
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(ExceptionUtil.exception_details(e));
			System.exit(-1);
		}

		return ids;
	}

	public void doStep(String step) throws Exception {
	}

	public void endPart(File part) throws Exception {
	}

	public void endSection(File section) throws Exception {
	}

	public void endTest(File test) throws Exception {
	}

	public void endTestPlan(File testplan) throws Exception {
	}

	public void startPart(File part) throws Exception {
	}

	public void startSection(File section) throws Exception {
	}

	public void startTest(File test) throws Exception {
	}

	/**
	 * Testkit scanner.
	 */
	public void startTestPlan(File testplan) throws Exception {
		String testId = getTestId(testplan);
		String sectionId = getSectionId(testplan);
		List<String> stepIds = getStepIds(testplan);

		if (testId == null || testId.equals("")) {
			System.err.println("TestPlan " + testplan.toString() + " cannot find testId");
			System.exit(-1);
		}

		if (stepIds.size() == 0) {
			System.err.println("TestPlan " + testplan.toString() + " has no test steps");
			System.exit(-1);
		}

		OMElement test = MetadataSupport.getChild(testkitDesc, "test", testId);
		if (test == null) {
			test = MetadataSupport.om_factory.createOMElement("test", null);
			test.addAttribute("id", testId, null);
			test.addAttribute("area", getCurrentArea(), null);
			testkitDesc.addChild(test);
		}

		OMElement section = null;
		if (sectionId != null) {
			section = MetadataSupport.om_factory.createOMElement("section", null);
			section.addAttribute("id", sectionId, null);
			test.addChild(section);
		}
		
		if (section != null) {
			// check for endpoint.txt file
			File endpointFile = new File(testplan.getParentFile() + File.separator + "endpoint.txt");
			//System.out.println("endpoint file: " + endpointFile + ( (endpointFile.exists()) ? "      exists" : ""  ));
			if (endpointFile.exists()) {
				String endpointPattern = Io.stringFromFile(endpointFile).trim();
				section.addAttribute("endpoint", endpointPattern, null);
			}
		}

		if (sectionId == null)
			parseValidations(testId, test, testplan);
		else
			parseValidations(testId, section, testplan);

		OMElement stepParent = (section != null) ? section : test;

		for (int i=0; i<stepIds.size(); i++) {
			String stepId = stepIds.get(i);
			OMElement step = MetadataSupport.om_factory.createOMElement("step", null);
			step.addAttribute("id", stepId, null);
			stepParent.addChild(step);
		}

	}

	class Att {
		String name;
		String value;

		void addToElement(OMElement e) {
			e.addAttribute(name, value, null);
		}
	}

	List<Att> parseAtts(String attStr, String testnum) throws Exception {
		List<Att> atts = new ArrayList<Att>();

		String[] atta = attStr.split("\\n");
		if (atta == null)
			throw new Exception("Cannot parse validations.txt from test " + testnum);

		for (int i=0; i<atta.length; i++) {
			String[] name_value = atta[i].split("=");
			if (name_value.length != 2)
				throw new Exception("Cannot parse validations.txt from test " + testnum);
			String name = name_value[0].trim();
			String value = name_value[1].trim();
			if (value.charAt(0) == '"')
				value = value.substring(1);
			if (value.charAt(value.length()-1) == '"')
				value = value.substring(0, value.length()-1);
			Att a = new Att();
			a.name = name;
			a.value = value;
			atts.add(a);
		}

		return atts;
	}

	void parseValidations(String testnum, OMElement test, File testplanFile) throws Exception {
		// look for validations.txt file in same dir as testplanFile
		File valFile = new File(testplanFile.getParent() + File.separator + "validations.txt");
		if ( !valFile.exists())
			return;
		System.out.println("Validations exist for " + testplanFile);
		String attstr = Io.stringFromFile(valFile).trim();
		// atts is in name="value" format, each 'attribute' should be added to test spec
		List<Att> atts = parseAtts(attstr, testnum);
		for (Att a : atts) {
			a.addToElement(test);
		}
	}


	public void begin() throws Exception {
		String[] areas = { "tests", "testdata", "server" };
		setAreas(areas);
	}


	public void end() throws Exception {
		File outfile = new File(outputdir + File.separator + "testkit_structure." + loadTestkitVersion() + ".xml");
		Io.stringToFile(outfile, testkitDesc.toString());
		System.out.println("Wrote " + outfile);
	}

	public static void main(String[] args) {
		if (args.length != 3) {
			System.err.println("Usage: testkitstructure <toolkit_location> <testkit_location> <output_dir>");
			System.exit(-1);
		}
		toolkit = args[0];
		testkit = args[1];
		outputdir = args[2];
		TestkitStructure tst = null;
		try {
			tst = new TestkitStructure();
			System.out.println("starting");
			tst.walkTree(new File(testkit));
		} catch (XdsInternalException e) {
			e.printStackTrace();
		} catch (JaxenException e) {
			e.printStackTrace();
		} catch (FactoryConfigurationError e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (tst != null) {
			System.err.println("Evaluated " + tst.testPlanCount + " testplans");
		} 

		System.exit(errors);
	}

	String loadTestkitVersion() throws IOException {
		File versionFile = new File(toolkit + File.separator + "admin" + File.separator + "version");
		return Io.stringFromFile(versionFile).trim().replaceAll(" ", "_");
	}

	String loadServerVersion() throws IOException {
		File versionFile = new File(testkit + File.separator + "admin" + File.separator + "server_version");
		return Io.stringFromFile(versionFile).trim();
	}
	
	public String getServerVersion() {
		return serverVersion;
	}

	public void endServer(File test) throws Exception {
	}

	public void startServer(File testDir) throws Exception {
		String testnum = testDir.getName();
		File endpointFile = new File(testDir + File.separator + "endpoint.txt");
		if ( !endpointFile.exists()) 
			throw new Exception("No endpoint.txt file found in server test " + testDir);
		String endpoint = Io.stringFromFile(endpointFile).trim();
		if (endpoint == null || endpoint.equals(""))
			throw new Exception("Empty endpoint.txt file found in server test " + testDir);

		OMElement test;
		test = MetadataSupport.om_factory.createOMElement("test", null);
		test.addAttribute("id", testnum, null);
		test.addAttribute("area", getCurrentArea(), null);
		test.addAttribute("endpoint", endpoint, null);
		testkitDesc.addChild(test);

	}


}
