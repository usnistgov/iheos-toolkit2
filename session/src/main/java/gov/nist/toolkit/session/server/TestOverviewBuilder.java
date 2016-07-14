package gov.nist.toolkit.session.server;

import gov.nist.toolkit.session.client.SectionOverviewDTO;
import gov.nist.toolkit.session.client.StepOverviewDTO;
import gov.nist.toolkit.session.client.TestOverviewDTO;
import gov.nist.toolkit.session.server.serviceManager.XdsTestServiceManager;
import gov.nist.toolkit.testenginelogging.LogFileContent;
import gov.nist.toolkit.testenginelogging.TestDetails;
import gov.nist.toolkit.testenginelogging.TestStepLogContent;

/**
 *
 */
public class TestOverviewBuilder {
    TestDetails testDetails;
    String testId;
    TestOverviewDTO testOverview = new TestOverviewDTO();
    XdsTestServiceManager tsm;

    public TestOverviewBuilder(Session session, TestDetails testDetails) {
        this.testDetails = testDetails;
        this.testId = testDetails.getTestInstance().getId();
        tsm = new XdsTestServiceManager(session);
    }

    public TestOverviewDTO build() throws Exception {
        testOverview.setPass(true);  // will be updated by addSections()
        testOverview.setName(testId);
        XdsTestServiceManager.ReadMe readme = tsm.getReadme(testId);
        testOverview.setTitle(readme.line1);
        testOverview.setDescription(readme.rest);
        addSections();
        return testOverview;
    }

    private void addSections() throws Exception {
        for (String sectionName : tsm.getTestIndex(testId)) {
            LogFileContent logFileContent = testDetails.sectionLogMap.get(sectionName);
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
