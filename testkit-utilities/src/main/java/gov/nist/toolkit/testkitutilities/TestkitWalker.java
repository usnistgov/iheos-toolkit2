package gov.nist.toolkit.testkitutilities;

import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.utilities.xml.Util;

import java.io.File;
import java.util.List;

import javax.xml.parsers.FactoryConfigurationError;

import org.apache.axiom.om.OMElement;

abstract public class TestkitWalker {
	protected   String testkitPathName;
	int testkitPathNameSize;
	protected   int testkitPathElementsToIgnore;
	static boolean debug = false;
	protected static int errors = 0;
	protected String area;
	String[] areas = { "examples", "testdata", "tests", "server" };


	abstract public void startSection(File section) throws Exception;
	abstract public void endSection(File section) throws Exception;
	abstract public void startTest(File test) throws Exception;
	abstract public void endTest(File test) throws Exception;

	abstract public void startPart(File part) throws Exception;
	abstract public void endPart(File part) throws Exception;
	// test plan may be inside test or part but not both
	abstract public void startTestPlan(File testplan) throws Exception;
	abstract public void endTestPlan(File testplan) throws Exception;
	
	abstract public void startServer(File test) throws Exception;
	abstract public void endServer(File test) throws Exception;

	protected int testPlanCount = 0;

	abstract public void doStep(String step) throws Exception;

	abstract public void begin() throws Exception;
	abstract public void end() throws Exception;

	protected File testkit;

	public File getTestkit() {
		return testkit;
	}
	public void setTestkit(File testkit) {
		this.testkit = testkit;
	}
	
	protected String getCurrentArea() {
		return area;
	}
	
	protected void setAreas(String[] areas) {
		this.areas = areas;
	}
	
	public void walkTree(File testkit) throws FactoryConfigurationError, Exception {
		testkitPathName = testkit.toString();
		testkitPathNameSize = testkitPathName.split("\\/").length;
		testkitPathElementsToIgnore = testkitPathNameSize + 1;


		System.out.println("Scanning testkit at " + testkit);
 
		setTestkit(testkit);

		begin();
		
		if ( !new File(testkit + File.separator + "tests").exists())
			throw new Exception("Testkit " + testkit + " is not really the testkit");

		for (String area : areas) {
			this.area = area;
			System.out.println("Scanning " + area);
						
			File areaDir = new File(testkit.toString() + File.separator + area);
			if (debug)
				System.out.println("Area: " + areaDir);

			startSection(areaDir);

			for (File test : areaDir.listFiles()) {
				if (test.getName().equals(".svn"))
					continue;

				if (debug)
					System.out.println("Test: " + test);

				if (!test.isDirectory())
					continue;

				if ("server".equals(area)) {
					startServer(test);
					endServer(test);
				}
				
				startTest(test);

				for (File part : test.listFiles()) {
					if (part.getName().equals(".svn"))
						continue;

					if (debug)
						System.out.println("Part: " + part);

					if (part.isFile()) {
						if (part.getName().equals("testplan.xml")) {
							startTestPlan(part);
							walkTestPlan(part);
							endTestPlan(part);
						}
					}

					if (part.isDirectory()) {
						for (File subelement : part.listFiles()) {
							if (subelement.getName().equals(".svn"))
								continue;

							if (debug)
								System.out.println("Subelement: " + subelement);

							startPart(part);

							if (subelement.isFile()) {
								if (subelement.getName().equals("testplan.xml")) {
									startTestPlan(subelement);
									walkTestPlan(subelement);
									endTestPlan(subelement);
								}
							}

							endPart(part);
						}
					}
				}

				endTest(test);
			}

			endSection(areaDir);
		}

		end();
	}

	void walkTestPlan(File testPlanFile) throws FactoryConfigurationError, Exception {
		OMElement testplanEle = Util.parse_xml(testPlanFile);

		List<OMElement> steps = MetadataSupport.childrenWithLocalName(testplanEle, "TestStep");

		for(int i=0; i<steps.size(); i++) {
			OMElement stepEle = steps.get(i);
			doStep(stepEle.getLocalName());
		}

	}
	protected String join(String[] parts, int first, int last, String separator) {
		StringBuffer buf = new StringBuffer();

		for (int i=first; i<= last; i++) {
			if (i != first)
				buf.append(separator);
			buf.append(parts[i]);
		}

		return buf.toString();
	}


}
