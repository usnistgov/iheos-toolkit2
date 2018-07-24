package gov.nist.toolkit.session.server.testlog;

import gov.nist.toolkit.commondatatypes.MetadataSupport;
import gov.nist.toolkit.installation.shared.TestSession;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.session.client.logtypes.SectionOverviewDTO;
import gov.nist.toolkit.session.client.logtypes.TestOverviewDTO;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.session.server.markdown.Markdown;
import gov.nist.toolkit.session.server.serviceManager.XdsTestServiceManager;
import gov.nist.toolkit.testenginelogging.client.QuickScanAttribute;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import gov.nist.toolkit.testkitutilities.ReadMe;
import gov.nist.toolkit.testkitutilities.TestDefinition;
import gov.nist.toolkit.testkitutilities.client.SectionDefinitionDAO;
import gov.nist.toolkit.utilities.xml.Util;
import gov.nist.toolkit.utilities.xml.XmlUtil;
import gov.nist.toolkit.xdsexception.client.TkNotFoundException;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.xpath.AXIOMXPath;
import org.apache.log4j.Logger;

import static gov.nist.toolkit.session.server.testlog.TestOverviewBuilder.stripHeaderMarkup;

public class QuickScanLog {

    private Session session;
    private static Logger logger = Logger.getLogger(QuickScanLog.class);

    public QuickScanLog(Session session) {
        this.session = session;
    }

    public List<TestOverviewDTO> quickScanLogs(TestSession testSession, List<TestInstance> testInstances, QuickScanAttribute[] quickScanAttributes) throws Exception {
        List<TestOverviewDTO> results = new ArrayList<>();
        try {
            for (TestInstance testInstance : testInstances) {
                try {
                    results.add(quickScanLog(testSession, testInstance, quickScanAttributes));
                } catch (Exception e) {
                    logger.error("Test " + testInstance + " does not exist");
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return results;
    }

    /**
     * Quickly scan logs and return test status/other top-level test log attributes
     * @param testSession
     * @param testInstance
     * @return
     * @throws Exception
     */
    public TestOverviewDTO quickScanLog(TestSession testSession, TestInstance testInstance, QuickScanAttribute[] quickScanAttributes) throws Exception {
        TestDefinition testDefinition = session.getTestkitSearchPath().getTestDefinition(testInstance.getId());
        XdsTestServiceManager xdsTestServiceManager = new XdsTestServiceManager(session);
        testInstance.setTestSession(testSession);
        File testDir = XdsTestServiceManager.getTestLogCache().getTestDir(testSession, testInstance);
        List<String> testPlanSections = xdsTestServiceManager.getTestSections(testInstance.getId());
        TestOverviewDTO testOverviewDTO = new TestOverviewDTO(testInstance);
        testOverviewDTO.setDependencies(new HashSet<String>());
        File logFile;

        setDTOAttributes(testOverviewDTO, testDefinition);

        if (testPlanSections == null || testPlanSections.isEmpty()) {
            logFile = new File(testDir, "log.xml");
            setDTOStatus(testOverviewDTO, logFile);
        } else {
            boolean atLeastOneFailed = false;
            boolean atLeastOneNotRun = false;
            int passCt = 0;
            for (String sectionName : testPlanSections) {
                SectionOverviewDTO sectionOverviewDTO = new SectionOverviewDTO();
                logFile = new File(new File(testDir, sectionName), "log.xml");
                try {
                    // Section details
                    sectionOverviewDTO.setName(sectionName);
                    SectionDefinitionDAO sectionDef = testDefinition.getSection(sectionName);
                    sectionOverviewDTO.setSutInitiated(sectionDef.isSutInitiated());
                    testOverviewDTO.getDependencies().addAll(sectionDef.getSectionDependencies());
                    testOverviewDTO.addSection(sectionOverviewDTO);

                    // Log details
                    Map<QuickScanAttribute,Object> valueMap = quickScanSectionLog(logFile,quickScanAttributes);
                    copyValues(valueMap, sectionOverviewDTO);

                    // TODO: load steps
                    //        testOverview.setDependencies(null); // TODO: Need this for Part 2

		/* Part 2:
		Create QuickScanLog class
		Make testDefinition member? Not sure why we need it.
		for each section add:
		                        SectionDefinitionDAO sectionDef = testDefinition.getSection(section);
                        sectionOverview.setSutInitiated(sectionDef.isSutInitiated());
                        testDependencies.addAll(sectionDef.getSectionDependencies());

         In Conformance Test Tab:
         	Refresh test display with a call to GetTestOverview with only that TestInstance as the request parameter
         	- 			TestDisplay testDisplay = testDisplayGroup.display(testOverview, null);
						testsPanel.add(testDisplay.asWidget());

						 panel.addOpenHandler
						 TestDisplayView Line 67
		 */
                    boolean passed = sectionOverviewDTO.isPass();
                    if (passed) {
                        passCt++;
                    } else {
                        atLeastOneFailed = true;
                    }
                } catch (TkNotFoundException tknfe) {
                    atLeastOneNotRun = true;
                }
            }
            if (atLeastOneFailed) {
                testOverviewDTO.setRun(true);
                testOverviewDTO.setPass(false);
            } else if (atLeastOneNotRun) {
                testOverviewDTO.setRun(false);
            } else if (passCt == testPlanSections.size()) {
                testOverviewDTO.setRun(true);
                testOverviewDTO.setPass(true);
            }
        }
        return testOverviewDTO;
    }

    private void setDTOAttributes(TestOverviewDTO testOverview, TestDefinition testDefinition){
        String testId = testOverview.getTestInstance().getId();
        testOverview.setTestKitSource(testDefinition.detectSource().toString());
        testOverview.setTestKitSection(testDefinition.getTestKitSection());
        testOverview.setName(testId);
        ReadMe readme = testDefinition.getTestReadme();
        if (readme != null) {
            testOverview.setTitle(stripHeaderMarkup(readme.line1));
            testOverview.setDescription(Markdown.toHtml(readme.rest));
        }


    }

    void copyValues(Map<QuickScanAttribute,Object> valueMap, SectionOverviewDTO sectionOverviewDTO) {
       if (valueMap.get(QuickScanAttribute.IS_RUN)!=null) {
           sectionOverviewDTO.setPass(((Boolean)valueMap.get(QuickScanAttribute.IS_RUN)).booleanValue());
       }
    }

    /**
     * Not tested.
     * This is used for non-sectional test plan. Ie., Test plan at the root level of the test folder.
     * @param testOverviewDTO
     * @param logFile
     * @throws Exception
     */
    private void setDTOStatus(TestOverviewDTO testOverviewDTO, File logFile) throws Exception {
        try {
            SectionOverviewDTO temp = quickScanSectionLog(logFile, new QuickScanAttribute[]{QuickScanAttribute.STATUS});
            testOverviewDTO.setRun(temp.isRun());
            testOverviewDTO.setPass(temp.isPass());
        } catch (TkNotFoundException tknfe) {
            testOverviewDTO.setRun(false);
        }
    }

    private Map<QuickScanAttribute, Object> quickScanSectionLog(File logFile, QuickScanAttribute[] attributes) throws Exception {
        Map<QuickScanAttribute, Object> valueMap = new HashMap<>();
        if (!logFile.exists()) {
            throw new TkNotFoundException("Requested log does not exist.","quickScanSectionLog");
        } else {
            FileInputStream fis = new FileInputStream(logFile);
            try {
                if (fis != null) {
                    OMElement logEl = Util.parse_xml(fis);
                    for (QuickScanAttribute qsa : attributes) {
                        if (QuickScanAttribute.STATUS.equals(qsa)) {
                            // TestResults is the root node in log.xml
                            boolean status = logStatus(logEl);
                            valueMap.put(QuickScanAttribute.IS_RUN, new Boolean(true));
                            valueMap.put(QuickScanAttribute.IS_PASS, new Boolean(status));
                        } else if (QuickScanAttribute.IS_TLS.equals(qsa)) {
                            // Set TLS true if any one step uses https endpoint
                            boolean isTls = isTls(logEl);
                            valueMap.put(QuickScanAttribute.IS_TLS, new Boolean(isTls));
                        } else if (QuickScanAttribute.HL7TIME.equals(qsa)) {
                            // Set Time
                            String hl7time = hl7Time(logEl);
                            valueMap.put(QuickScanAttribute.HL7TIME, hl7time);
                        } else if (QuickScanAttribute.SITE.equals(qsa)) {
                            // Set Site
                            String site = site(logEl);
                            valueMap.put(QuickScanAttribute.SITE, site);
                        }
                    }

                }
            } finally {
                fis.close();
            }
            return valueMap;
        }
    }



    /**
     * Reference: TestStepLogContentDTO#isTls
     * @param rootEL
     * @return
     */
    private boolean isTls(OMElement rootEL) {
        boolean atLeastOneHttpsEndpoint = false;
        List<OMElement> tranEls = new ArrayList<OMElement>();

        List<OMElement> testStepsEle = XmlUtil.childrenWithLocalName(rootEL, "TestStep");
        for (OMElement stepEl : testStepsEle) {
            XmlUtil.descendantsWithLocalNameEndsWith(tranEls, stepEl, "Transaction",1);
            if (!tranEls.isEmpty()) {
                OMElement endpontEl = XmlUtil.firstDecendentWithLocalName(tranEls.get(0), "Endpoint");
                atLeastOneHttpsEndpoint = endpontEl.getText().startsWith("https");
                if (atLeastOneHttpsEndpoint) {
                    break;
                }
            }
        }
        return atLeastOneHttpsEndpoint;
    }

    private boolean logStatus(OMElement rootEl) throws Exception {
        AXIOMXPath xpathEx = new AXIOMXPath("//TestResults");
        OMElement selectEl = (OMElement) xpathEx.selectSingleNode(rootEl);
        String statusValue = selectEl.getAttributeValue(MetadataSupport.status_qname);
        return "Pass".equals(statusValue);
    }

    private String hl7Time(OMElement rootEl) throws Exception {
        AXIOMXPath xpathEx = new AXIOMXPath("//TestResults//Time");
        OMElement selectEl = (OMElement) xpathEx.selectSingleNode(rootEl);
        return selectEl.getText();
    }

    private String site(OMElement rootEl) throws Exception {
        AXIOMXPath xpathEx = new AXIOMXPath("//TestResults//Site");
        OMElement selectEl = (OMElement) xpathEx.selectSingleNode(rootEl);
        return selectEl.getText();
    }


}
