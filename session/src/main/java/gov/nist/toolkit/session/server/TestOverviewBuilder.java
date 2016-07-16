package gov.nist.toolkit.session.server;

import gov.nist.toolkit.session.client.Htmlize;
import gov.nist.toolkit.session.client.SectionOverviewDTO;
import gov.nist.toolkit.session.client.StepOverviewDTO;
import gov.nist.toolkit.session.client.TestOverviewDTO;
import gov.nist.toolkit.session.server.serviceManager.XdsTestServiceManager;
import gov.nist.toolkit.testenginelogging.LogFileContent;
import gov.nist.toolkit.testenginelogging.TestLogDetails;
import gov.nist.toolkit.testenginelogging.TestStepLogContent;
import gov.nist.toolkit.testkitutilities.ReadMe;
import gov.nist.toolkit.testkitutilities.TestDefinition;
import gov.nist.toolkit.testkitutilities.TestkitBuilder;

import java.io.File;
import java.util.List;

/**
 *
 */
public class TestOverviewBuilder {
    TestLogDetails testLogDetails;
    String testId;
    File testDir;
    TestOverviewDTO testOverview = new TestOverviewDTO();
    XdsTestServiceManager tsm;

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
            return;  // no sections
        }
        if (sectionNames == null) return;
        for (String sectionName : sectionNames) {
            LogFileContent logFileContent = testLogDetails.sectionLogMap.get(sectionName);
            SectionOverviewDTO sectionOverview = addSection(sectionName, logFileContent);
            if (!sectionOverview.isPass())
                testOverview.setPass(false);
        }
    }

    private SectionOverviewDTO addSection(String sectionName, LogFileContent logFileContent) {
        SectionOverviewDTO sectionOverview = new SectionOverviewDTO();

        sectionOverview.setName(sectionName);
        sectionOverview.setPass(logFileContent.isSuccess());
        for (String stepName : logFileContent.getStepMap().keySet()) {
            TestStepLogContent stepContent = logFileContent.getStepLog(stepName);
            addStep(stepName, stepContent, sectionOverview);
        }

        testOverview.addSection(sectionOverview);
        return sectionOverview;
    }

    private void addStep(String stepName, TestStepLogContent stepContent, SectionOverviewDTO sectionOverview) {
        StepOverviewDTO stepOverview = new StepOverviewDTO();

        stepOverview.setName(stepContent.getName());
        stepOverview.setPass(stepContent.isSuccess());
        stepOverview.setDetails(stepContent.getDetails());
        stepOverview.setErrors(stepContent.getErrors());

        sectionOverview.addStep(stepName, stepOverview);
    }
}
