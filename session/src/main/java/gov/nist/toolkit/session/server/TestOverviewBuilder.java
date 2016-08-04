package gov.nist.toolkit.session.server;

import gov.nist.toolkit.session.client.Htmlize;
import gov.nist.toolkit.session.client.SectionOverviewDTO;
import gov.nist.toolkit.session.client.StepOverviewDTO;
import gov.nist.toolkit.session.client.TestOverviewDTO;
import gov.nist.toolkit.session.server.serviceManager.XdsTestServiceManager;
import gov.nist.toolkit.testenginelogging.client.LogFileContentDTO;
import gov.nist.toolkit.testenginelogging.TestLogDetails;
import gov.nist.toolkit.testenginelogging.client.TestStepLogContentDTO;
import gov.nist.toolkit.testkitutilities.ReadMe;
import gov.nist.toolkit.testkitutilities.TestDefinition;
import gov.nist.toolkit.testkitutilities.TestkitBuilder;

import java.io.File;
import java.util.List;

/**
 *
 */
public class TestOverviewBuilder {
    private TestLogDetails testLogDetails;
    private String testId;
    private File testDir;
    private TestOverviewDTO testOverview = new TestOverviewDTO();
    private XdsTestServiceManager tsm;

    public TestOverviewBuilder(Session session, TestLogDetails testLogDetails) throws Exception {
        this.testLogDetails = testLogDetails;
        this.testId = testLogDetails.getTestInstance().getId();
        this.testDir = TestkitBuilder.getTestDir(testId);
        tsm = new XdsTestServiceManager(session);
    }

    public TestOverviewDTO build() throws Exception {
        testOverview.setPass(true);  // will be updated by addSections()
        testOverview.setName(testId);
        ReadMe readme = new TestDefinition(testDir).getTestReadme();
        testOverview.setTitle(readme.line1);
        testOverview.setDescription(Htmlize.asString(readme.rest));
        addSections();
        return testOverview;
    }

    private void addSections() throws Exception {
        List<String> sectionNames = null;
        try {
            sectionNames = tsm.getTestIndex(testId);
        } catch (Exception e) {
            return;
        }
        if (sectionNames == null) {
            // No test results available - scan
            // test definition to get section names
            TestDefinition testDefinition = new TestDefinition(testDir);
            try {
                List<String> sections = testDefinition.getSectionIndex();
                for (String section : sections) {
                    SectionOverviewDTO sectionOverview = addSection(section, null);
                    sectionOverview.setRun(false);
                    testOverview.addSection(sectionOverview);
                }
                return;
            } catch (Exception e) {
                // no sections defined - maybe single un-named section
                if (testLogDetails.getTestPlanLogs().get("log.xml") != null) {
                    LogFileContentDTO logFileContentDTO = testLogDetails.getTestPlanLogs().get("log.xml");
                    SectionOverviewDTO sectionOverview = addSection("", logFileContentDTO);
                    if (!sectionOverview.isPass())
                        testOverview.setPass(false);
                }
                return;
            }
        }
        for (String sectionName : sectionNames) {
            LogFileContentDTO logFileContentDTO = testLogDetails.sectionLogMapDTO.get(sectionName);
            if (testId.equals("12346"))
                testOverview.setPass(false);
            SectionOverviewDTO sectionOverview = addSection(sectionName, logFileContentDTO);
            if (!sectionOverview.isPass()) {
                testOverview.setPass(false);
            }
        }
    }

    private SectionOverviewDTO addSection(String sectionName, LogFileContentDTO logFileContentDTO) {
        SectionOverviewDTO sectionOverview = new SectionOverviewDTO();

        sectionOverview.setName(sectionName);
        if (logFileContentDTO == null) {
            sectionOverview.setRun(false);
            testOverview.addSection(sectionOverview);
            return sectionOverview;
        }
        sectionOverview.setPass(logFileContentDTO.isSuccess());
        for (String stepName : logFileContentDTO.getStepMap().keySet()) {
            TestStepLogContentDTO stepContent = logFileContentDTO.getStepLog(stepName);
            addStep(stepName, stepContent, sectionOverview);
        }

        testOverview.addSection(sectionOverview);
        return sectionOverview;
    }

    private void addStep(String stepName, TestStepLogContentDTO stepContent, SectionOverviewDTO sectionOverview) {
        StepOverviewDTO stepOverview = new StepOverviewDTO();

        stepOverview.setName(stepContent.getId());
        stepOverview.setPass(stepContent.isSuccess());
        stepOverview.setDetails(stepContent.getDetails());
        stepOverview.addErrors(stepContent.getSoapFaults());
        stepOverview.addErrors(stepContent.getErrors());
        stepOverview.addErrors(stepContent.getAssertionErrors());
        sectionOverview.addStep(stepName, stepOverview);
    }
}
