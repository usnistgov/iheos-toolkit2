package gov.nist.toolkit.testengine.engine;

import gov.nist.toolkit.testengine.transactions.BasicTransaction;
import gov.nist.toolkit.testkitutilities.TestkitWalker;
import gov.nist.toolkit.utilities.xml.Util;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.xpath.AXIOMXPath;
import org.apache.log4j.Logger;
import org.jaxen.JaxenException;

import javax.xml.parsers.FactoryConfigurationError;
import java.io.File;

/**
 * Scan testkit for problems.
 * @author bill
 *
 */
public class ErrorScan extends TestkitWalker {
	static boolean debug = false;
	private final static Logger logger = Logger.getLogger(ErrorScan.class);

	void evalTestPlan(File testplan, String expectedTestLabel) throws XdsInternalException, FactoryConfigurationError, JaxenException {
		testPlanCount++;
		OMElement tplan = Util.parse_xml(testplan);
		AXIOMXPath xpathExpression = new AXIOMXPath ("//TestPlan/Test");
		String test = xpathExpression.stringValueOf(tplan);
		if (test == null || test.equals("")) {
			logger.info(testplan.toString() + ": No Test element");
			errors++;
		}
		else if (!test.equals(expectedTestLabel)) {
			logger.info(testplan.toString() + ": expected " + expectedTestLabel + " found " + test);
			errors++;
		}
	}

	@Override
	public void doStep(String step) {

	}

	@Override
	public void endPart(File part) {

	}

	@Override
	public void endSection(File section) {

	}

	@Override
	public void endTest(File test) {

	}

	@Override
	public void endTestPlan(File testplan) {

	}

	@Override
	public void startPart(File part) {

	}

	public void startSection(File section) {
	}

	public void startTest(File test) {
	}

	public void startTestPlan(File testplan) throws XdsInternalException, JaxenException, FactoryConfigurationError {
		evalTestPlan(testplan, nameFilter(testplan));
	}
	
	// given the testplan filename and the testkit base name, calculate the
	// string that should be in the <Test/> element in the testplan file
	String nameFilter(File testplan) {
		String[] filenameElements = testplan.toString().split("\\/");
		
		return join(filenameElements, 
				testkitPathElementsToIgnore, 
				filenameElements.length-2,  // -2 so testplan.xml is removed 
				"/");
	}

	@Override
	public void begin() throws Exception {

	}

	@Override
	public void end() throws Exception {

	}

	public static void main(String[] args) {
		ErrorScan tst = null;
		if (args.length != 1) {
			System.err.println("Single arg required, testkit location");
			System.exit(-1);
		}
		String testkit = args[0];
		try {
			tst = new ErrorScan();
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

	@Override
	public void endServer(File test) throws Exception {

	}

	@Override
	public void startServer(File test) throws Exception {

	}

}
