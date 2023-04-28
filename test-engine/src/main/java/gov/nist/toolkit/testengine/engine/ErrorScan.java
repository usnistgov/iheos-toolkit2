package gov.nist.toolkit.testengine.engine;

import gov.nist.toolkit.commondatatypes.MetadataSupport;
import gov.nist.toolkit.commondatatypes.client.MetadataTypes;
import gov.nist.toolkit.testkitutilities.TestkitWalker;
import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.utilities.xml.Util;
import gov.nist.toolkit.utilities.xml.XmlUtil;
import gov.nist.toolkit.valregmsg.message.SchemaValidation;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.xpath.AXIOMXPath;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
	public static final int V2_INDEX = 0;
	public static final int V3_INDEX = 1;
	static int minPass = -1; // -1 is no limit
	static int quitAfterNFailures = -1; // -1 is no limit
    static String transactionCase = "";
    static File bulkParameterOutFile = null;
	static int schemaValidationPassed;
	static int schemaValidationFailed;
	static int missingMetadataFileErrors;
	static int metadataParserExceptions;
	static boolean debug = false;
	static boolean doEvalTestPlanEnabled;
	static int skipTestPlanCount;
	static String schemaLocation ="C:\\Users\\skb1\\myprojects\\iheos-toolkit2\\xdstools2\\src\\test\\resources\\war\\toolkitx\\schema";
	static Set<String> tranList = new HashSet<>();
	static StringBuilder bulkOperationParameters = new StringBuilder();
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
		if (quitAfterNFailures > 0 && schemaValidationFailed + metadataParserExceptions >= quitAfterNFailures && minPass > 0 && minPass <= schemaValidationPassed)
			return;

		tranList.add(stepElementName);
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
//					logger.finer(()->" Found MetadataFile element!");
					try {
						OMElement metadataElement = Util.parse_xml(metadataFile);

						switch (stepElementName) {
	 						case "RegisterTransaction":
								if (! isTransactionCase(stepElementName))
									break;

//							case "ProvideAndRegisterTransaction":
//							    if (! isTransactionCase(stepElementName))
//							    	break;
							    /*
							    Caveat
							    Metadata file content can be wrapped by the ProvideAndRegisterDocumentSetRequest at runtime, otherwise it should be the SubmitObjectsRequest element
							    Make schema validation aware of this caveat
							     */
							    String elementName = metadataElement.getLocalName();
							    String ns = metadataElement.getNamespace().getNamespaceURI();
								logger.fine("Metadata element: " + elementName /* XmlUtil.children(metadataElement).get(0).getLocalName() */ + ", ns: " + ns );
								final int metadataType = findMetadataType(elementName, ns);

								String errors = SchemaValidation.validate(schemaLocation, metadataElement, metadataType);
								if ("".equals(errors)) {
									schemaValidationPassed++;
									logger.fine(String.format("Passed schema validation for Metadatafile %s, metadataType=%s.\r\n", metadataFile, MetadataTypes.getMetadataTypeName(metadataType)));
								} else {
									schemaValidationFailed++;
									logger.info(
											String.format("Failed Metadatafile\r\n%s\r\n%s XML Validation result is %s",
													metadataFile,
													"Input",
													 errors));

									if (errors.contains("Invalid content was found")) {
										// generate a text file with the parameters to use with a call to MetadataCollectionToMetadata
										// The call happens at the end of Summary as a bulk operation
										// or it can be run as a separate process manually that uses the text file
                                        bulkOperationParameters.append(String.format("%s %d\n", metadataFile, metadataType));
									}
								}
								break;

						}
					} catch (Exception ex) {
						logger.severe(String.format("\r\nException Metadatafile\r\n%s", metadataFile));
						ex.printStackTrace();
						metadataParserExceptions++;
					}
				} else {
			    	missingMetadataFileErrors++;
					logger.severe(String.format("\r\nMissing Metadatafile\r\n%s", metadataFile));
				}
			}


		}
	}

	private boolean isTransactionCase(String stepElementName) {
		return null == transactionCase || "".equals(transactionCase) || stepElementName.equals(transactionCase);
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
		if (args.length == 0) {
			logger.severe("Single arg required, testkit location");
			System.exit(-1);
		}
		String testkit = args[0];
		if (args.length >= 2) {
			quitAfterNFailures = Integer.parseInt(args[1]);
			if (args.length >= 3) {
				minPass = Integer.parseInt(args[2]);
				if (args.length >= 4) {
					transactionCase = args[3];
					if (args.length >= 5)
						bulkParameterOutFile = new File(args[4]);
				}
			}
		}
		boolean completed = false;
		try {
			tst = new ErrorScan();
			tst.walkTree(new File(testkit));
			completed = true;

			Io.stringToFile(bulkParameterOutFile, bulkOperationParameters.toString());
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
					logger.info("Evaluated " + tst.testPlanCount + " testplans" + (!completed ? ", skipping other tests." : "."));
					logger.info(String.format("Found %d invalid test plan Id errors.", errors));
				} else {
					logger.info("Testplan evaluation is not enabled.");
					logger.info("Skipped " + skipTestPlanCount + " testplans.");
				}
			}


			StringBuilder sb = new StringBuilder()
			.append(String.format("SUMMARY\r\nFound %d missing metadata file(s).\r\n", missingMetadataFileErrors))
			.append(String.format("Exclusive transaction selective scan is set to '%s'.\r\n", transactionCase))
			.append(String.format("Passed %d MetadataFile schema validations.\r\n", schemaValidationPassed))
			.append(String.format("Failed* %d MetadataFile schema validations.\r\n", schemaValidationFailed))
			.append("*Check if schema errors are expected due to parameter usage.\r\n")
			.append(String.format("Found %d MetadataFile parser exceptions.\r\n", metadataParserExceptions))
			.append(String.format("TranList %s.\r\n", String.join(",", tranList)))
			.append(String.format("Quit after failedN>=%d and passedN=%d.\r\n", quitAfterNFailures, minPass));
			logger.info(sb.toString());
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


	private int findMetadataType(String elementName, String ns) {
		Map<String,List<Integer>> metadataWrapper = new HashMap<>();
		metadataWrapper.put("SubmitObjectsRequest", 				Arrays.asList(MetadataTypes.METADATA_TYPE_R, MetadataTypes.METADATA_TYPE_Rb));
		metadataWrapper.put("ProvideAndRegisterDocumentSetRequest", Arrays.asList(MetadataTypes.METADATA_TYPE_PR,MetadataTypes.METADATA_TYPE_PRb));

		if (! metadataWrapper.containsKey(elementName))
			throw new RuntimeException("Do not understand metadata object type: " + elementName);

		if (MetadataSupport.isV2Namespace(ns)) {
			return metadataWrapper.get(elementName).get(V2_INDEX);
		} else if (MetadataSupport.isV3Namespace(ns)) {
			return metadataWrapper.get(elementName).get(V3_INDEX);
		} else {
			throw new RuntimeException(String.format ("Do not understand %s ns: %s" , elementName, ns));
		}
	}
}
