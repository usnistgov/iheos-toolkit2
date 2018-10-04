package gov.nist.toolkit.session.server.testlog;

import gov.nist.toolkit.commondatatypes.MetadataSupport;
import gov.nist.toolkit.installation.shared.TestSession;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.session.client.logtypes.SectionOverviewDTO;
import gov.nist.toolkit.session.client.logtypes.TestOverviewDTO;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.session.server.markdown.Markdown;
import gov.nist.toolkit.session.server.serviceManager.XdsTestServiceManager;
import gov.nist.toolkit.testenginelogging.client.QuickScanLogAttribute;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import gov.nist.toolkit.testkitutilities.ReadMe;
import gov.nist.toolkit.testkitutilities.TestDefinition;
import gov.nist.toolkit.testkitutilities.client.ConfTestPropertyName;
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

    public List<TestOverviewDTO> quickScanLogs(TestSession testSession, List<TestInstance> testInstances, QuickScanLogAttribute[] quickScanAttributes) throws Exception {
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
     * Quickly scan a Test log (a log essentially comprises every section log under the test folder) and return test status/other top-level Test log attributes
     * @param testSession
     * @param testInstance
     * @return
     * @throws Exception
     */
    public TestOverviewDTO quickScanLog(TestSession testSession, TestInstance testInstance, QuickScanLogAttribute[] quickScanAttributes) throws Exception {
        TestDefinition testDefinition = session.getTestkitSearchPath().getTestDefinition(testInstance.getId());
        XdsTestServiceManager xdsTestServiceManager = new XdsTestServiceManager(session);
        testInstance.setTestSession(testSession);
        File testDir = XdsTestServiceManager.getTestLogCache().getTestDir(testSession, testInstance);
        List<String> testPlanSections = xdsTestServiceManager.getTestSections(testInstance.getId());


        TestOverviewDTO testOverviewDTO = new TestOverviewDTO(testInstance);

        for (QuickScanLogAttribute qsa : quickScanAttributes) {
           if (qsa.equals(QuickScanLogAttribute.CONFTEST_PROPERTIES)) {
                testOverviewDTO.setConfTestPropertyMap(testDefinition.getConfTestProperties());
               break;
           }
        }

        setDTOAttributes(testOverviewDTO, testDefinition);
        testOverviewDTO.setDependencies(new HashSet<String>());
        File logFile;

        if (testPlanSections == null || testPlanSections.isEmpty()) {
            // No Test Sections
            // See XdsTestServiceManager Line 743 if (testDir == null) dto.setRun(false)
            // See TestOverviewBuilder Line 48  testOverview.setPass(true);   will be updated by addSections()
            testOverviewDTO.setPass(true);
            if (testDir == null) {
                testOverviewDTO.setRun(false);
                testOverviewDTO.setPass(false);
            }

            logFile = new File(testDir, "log.xml");
            if (logFile.exists()) {
                /*
                 * This is used for non-sectional test plan. Ie., Test plan at the root level of the test folder.
                 */
                // TODO: test this path.
                setTestOverviewDTOStatus(testOverviewDTO, logFile);
            }
            return testOverviewDTO;
        } else {
            // has Test Sections
            boolean scanTestDependencies = Arrays.asList(quickScanAttributes).contains(QuickScanLogAttribute.TEST_DEPENDENCIES);
            boolean atLeastOneFailed = false;
            boolean atLeastOneNotRun = false;
            int passCt = 0;
            for (String sectionName : testPlanSections) {
                SectionOverviewDTO sectionOverviewDTO = new SectionOverviewDTO();
                try {
                    if (scanTestDependencies) {
                        // Section details
                        sectionOverviewDTO.setRun(false); // Need to set False because default is True!
                        sectionOverviewDTO.setName(sectionName);
                        SectionDefinitionDAO sectionDef = testDefinition.getSection(sectionName);
                        // Skip loading Section Level ConTest.properties here. (It is not used by ConfTest lazy loading feature.)
                        sectionOverviewDTO.setSutInitiated(sectionDef.isSutInitiated());
                        testOverviewDTO.getDependencies().addAll(sectionDef.getSectionDependencies());
                        testOverviewDTO.addSection(sectionOverviewDTO);
                    }
                    logFile = new File(new File(testDir, sectionName), "log.xml");
                    // Log details
                    SectionOverviewDTO resultDTO = quickScanSectionLog(logFile,quickScanAttributes);
                    copyAttributes(quickScanAttributes, resultDTO, sectionOverviewDTO);

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
            /* See TestDisplay#display for color coding logic */
            if (atLeastOneFailed) {
                testOverviewDTO.setRun(true);
                testOverviewDTO.setPass(false);
            } else if (atLeastOneNotRun) {
                if (passCt == 0) {
                    // No section logs at all
                    testOverviewDTO.setRun(false);
                } else {
                    // Incomplete status (some run, some not, none failed.) Mark as failed since this is how it was represented before and we don't really have an Incomplete status. In the test overview only three Status exists: not run, pass, fail.
                    testOverviewDTO.setRun(true);
                    testOverviewDTO.setPass(false);
                }
            } else if (passCt == testPlanSections.size()) {
                testOverviewDTO.setRun(true);
                testOverviewDTO.setPass(true);
            }
            return testOverviewDTO;
        }
    }

    private void setDTOAttributes(TestOverviewDTO testOverview, TestDefinition testDefinition){
        String testId = testOverview.getTestInstance().getId();
        testOverview.setTestKitSource(testDefinition.detectSource().toString());
        testOverview.setTestKitSection(testDefinition.getTestKitSection());
        // Do not call setName, it is not need if the constructor with testInstance is already called. Calling setName overwrites testInstance!
        //  testOverview.setName(testId);
        ReadMe readme = testDefinition.getTestReadme();
        if (readme != null) {
            testOverview.setTitle(stripHeaderMarkup(readme.line1));
            testOverview.setDescription(Markdown.toHtml(readme.rest));
        }
    }

    private void copyAttributes(QuickScanLogAttribute[] attributes, SectionOverviewDTO src, SectionOverviewDTO dst) {
        for (QuickScanLogAttribute qsa : attributes) {
            switch (qsa) {
                case SITE:
                    dst.setSite(src.getSite());
                    break;
                case IS_TLS:
                    dst.setTls(src.isTls());
                    break;
                case HL7TIME:
                    dst.setHl7Time(src.getHl7Time());
                    break;
                case IS_RUN:
                    dst.setRun(src.isRun());
                    break;
                case IS_PASS:
                    dst.setPass(src.isPass());
                    break;
            }
        }
    }

    /**
     * Not tested.
     * This is used for non-sectional test plan. Ie., Test plan at the root level of the test folder.
     * @param testOverviewDTO
     * @param logFile
     * @throws Exception
     */
    private void setTestOverviewDTOStatus(TestOverviewDTO testOverviewDTO, File logFile) throws Exception {
        try {
            SectionOverviewDTO temp = quickScanSectionLog(logFile, new QuickScanLogAttribute[]{QuickScanLogAttribute.IS_RUN,QuickScanLogAttribute.IS_PASS});
            testOverviewDTO.setRun(temp.isRun());
            testOverviewDTO.setPass(temp.isPass());
        } catch (TkNotFoundException tknfe) {
            testOverviewDTO.setRun(false);
        }
    }

    private SectionOverviewDTO quickScanSectionLog(File logFile, QuickScanLogAttribute[] attributes) throws Exception {
        SectionOverviewDTO sectionOverviewDTO = new SectionOverviewDTO();
        if (!logFile.exists()) {
            throw new TkNotFoundException("Requested log does not exist.","quickScanSectionLog");
        } else {
            FileInputStream fis = new FileInputStream(logFile);
            try {
                if (fis != null) {
                    OMElement logEl = Util.parse_xml(fis);
                    for (QuickScanLogAttribute qsa : attributes) {
                        switch (qsa) {
                            case HL7TIME:
                                // Set Time
                                String hl7time = hl7Time(logEl);
                                sectionOverviewDTO.setHl7Time(hl7time);
                                break;
                            case IS_TLS:
                                // Set TLS true if any one step uses https endpoint
                                boolean isTls = isTls(logEl);
                                sectionOverviewDTO.setTls(isTls);
                                break;
                            case SITE:
                                // Set Site
                                String site = site(logEl);
                                sectionOverviewDTO.setSite(site);
                                break;
                            case IS_PASS:
                            case IS_RUN:
                                // TestResults is the root node in log.xml
                                boolean status = logStatus(logEl);
                                sectionOverviewDTO.setRun(true);
                                sectionOverviewDTO.setPass(status);
                                break;
                        }
                    }

                }
            } finally {
                fis.close();
            }
            return sectionOverviewDTO;
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
                if (endpontEl != null && endpontEl.getText() != null) {
                    atLeastOneHttpsEndpoint = endpontEl.getText().startsWith("https");
                    if (atLeastOneHttpsEndpoint) {
                        break;
                    }
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
