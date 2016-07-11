package gov.nist.toolkit.testenginelogging;

import gov.nist.toolkit.testenginelogging.client.SectionOverviewDTO;
import gov.nist.toolkit.testenginelogging.client.StepOverviewDTO;
import gov.nist.toolkit.testenginelogging.client.TestOverviewDTO;
import gov.nist.toolkit.xdsexception.XdsInternalException;

/**
 *
 */
public class TestOverviewBuilder {
    TestDetails testDetails;
    TestOverviewDTO testOverview = new TestOverviewDTO();

    public TestOverviewBuilder(TestDetails testDetails) {
        this.testDetails = testDetails;
    }

    public TestOverviewDTO build() throws XdsInternalException {
        testOverview.setPass(true);
        testOverview.setName(testDetails.getTestInstance().getId());
        addSections();
        return testOverview;
    }

    private void addSections() throws XdsInternalException {
        for (String sectionName : testDetails.sectionLogMap.sectionNames) {
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
