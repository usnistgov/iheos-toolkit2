package gov.nist.toolkit.testengine.engine;

import gov.nist.toolkit.commondatatypes.client.MetadataTypes;
import gov.nist.toolkit.testkitutilities.TestkitWalker;
import gov.nist.toolkit.utilities.xml.Util;
import gov.nist.toolkit.utilities.xml.XmlUtil;
import gov.nist.toolkit.valregmsg.message.SchemaValidation;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.xpath.AXIOMXPath;

import java.util.List;
import java.util.logging.Logger;
import org.jaxen.JaxenException;

import javax.xml.parsers.FactoryConfigurationError;
import java.io.File;
import java.util.regex.Matcher;

/**
 * Scan testkit for problems.
 * @author bill
 *
 */
public class ErrorScan extends TestkitWalker {
	static int pnrSchemaPassed;
	static int pnrSchemaFailed;
	static int missingMetadataFileErrors;
	static int metadataParserExceptions;
	static boolean debug = false;
	static boolean doEvalTestPlanEnabled;
	static int skipTestPlanCount;
	static String schemaLocation ="C:\\Users\\skb1\\myprojects\\iheos-toolkit2\\xdstools2\\src\\test\\resources\\war\\toolkitx\\schema";
	private final static Logger logger = Logger.getLogger(ErrorScan.class.getName());


	void evalTestPlan(File testplan, String expectedTestLabel) throws XdsInternalException, FactoryConfigurationError, JaxenException {
		testPlanCount++;
		if (! doEvalTestPlanEnabled) {
			skipTestPlanCount++;
			return;
		}
		OMElement tplan = Util.parse_xml(testplan);
		String reportTestPlan = testplan.toString().replace(testkitPathName, "");
		AXIOMXPath xpathExpression = new AXIOMXPath ("//TestPlan/Test");
		String test = xpathExpression.stringValueOf(tplan);
		if (test == null || test.equals("")) {
			logger.info(reportTestPlan + ": No Test element");
			errors++;
		}
		else if (!test.equals(expectedTestLabel)) {
			logger.info(reportTestPlan + ": expected " + expectedTestLabel + " found " + test);
			errors++;
		}
	}

	@Override
	public void doStep(String step) {

	}

	@Override
	protected void doTransaction(File testPlanFile, OMElement tranElement, String testStepElementName, String stepElementName) throws JaxenException {
		logger.fine("doTransaction " + testStepElementName + "/" + stepElementName);

		String metadataFileElementName = "MetadataFile";
		List<OMElement> steps = XmlUtil.childrenWithLocalName(tranElement, metadataFileElementName);

		if (steps.size()>0) {
			OMElement mEle = steps.get(0); // pick the first one only, should be only one metadatafile element per step

			if (mEle == null) {
				logger.fine(() ->" No MetadataFile element declared.");
			} else {
				String mFileString = mEle.getText();
			    File metadataFile = new File( testPlanFile.getParentFile(), mFileString);
			    if (metadataFile.exists()) {
					logger.finer(" Found MetadataFile element!");
					try {
						OMElement metadataElement = Util.parse_xml(metadataFile);
						switch (stepElementName) {
							case "ProvideAndRegisterTransaction":
								String errors = SchemaValidation.validate(schemaLocation, metadataElement, MetadataTypes.METADATA_TYPE_PR);
								if ("".equals(errors))
									pnrSchemaPassed++;
								else {
									pnrSchemaFailed++;
									System.out.println(
											String.format("Metadatafile %s Failed %s XML Validation result is %s",
													metadataFile,
													"Input",
													 errors));
								}
								break;

						}
					} catch (Exception ex) {
						metadataParserExceptions++;
					}
				} else {
			    	missingMetadataFileErrors++;
				}
			}


		}
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
		String[] filenameElements = testplan.toString().split(Matcher.quoteReplacement(File.separator));
		
		return join(filenameElements, 
				testkitPathElementsToIgnore, 
				filenameElements.length-2,  // -2 so testplan.xml is removed 
				"/" /* always use forward slash inside test plan */);
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
		boolean completed = false;
		try {
			tst = new ErrorScan();
			tst.walkTree(new File(testkit));
			completed = true;
		} catch (XdsInternalException e) {
			e.printStackTrace();
		} catch (JaxenException e) {
			e.printStackTrace();
		} catch (FactoryConfigurationError e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (tst != null) {
			    if (doEvalTestPlanEnabled) {
					System.out.println("Evaluated " + tst.testPlanCount + " testplans" + (!completed ? ", skipping other tests." : "."));
					System.out.println(String.format("Found %d invalid test plan Id errors.", errors));
				} else {
					System.out.println("Testplan evaluation is not enabled.");
					System.out.println("Skipped " + skipTestPlanCount + " testplans.");
				}
			}
			System.out.println(String.format("Found %d missing MetadataFile errors.", missingMetadataFileErrors));
			System.out.println(String.format("Found %d MetadataFile parser exceptions.", metadataParserExceptions));
			System.out.println(String.format("Passed %d MetadataFile PnR transactions.", pnrSchemaPassed));
			System.out.println(String.format("Failed %d MetadataFile PnR transactions.", pnrSchemaFailed));
		}

		int returnCode = (errors > 0 ? 1 : 0);
		System.exit(returnCode);
	}

	@Override
	public void endServer(File test) throws Exception {

	}

	@Override
	public void startServer(File test) throws Exception {

	}

}
