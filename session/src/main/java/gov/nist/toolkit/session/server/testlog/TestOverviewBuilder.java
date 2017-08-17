package gov.nist.toolkit.session.server.testlog;

import gov.nist.toolkit.session.client.logtypes.SectionOverviewDTO;
import gov.nist.toolkit.session.client.logtypes.StepOverviewDTO;
import gov.nist.toolkit.session.client.logtypes.TestOverviewDTO;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.session.server.markdown.Markdown;
import gov.nist.toolkit.session.server.serviceManager.XdsTestServiceManager;
import gov.nist.toolkit.testenginelogging.TestLogDetails;
import gov.nist.toolkit.testenginelogging.client.LogFileContentDTO;
import gov.nist.toolkit.testenginelogging.client.StepGoalsDTO;
import gov.nist.toolkit.testenginelogging.client.TestStepLogContentDTO;
import gov.nist.toolkit.testkitutilities.ReadMe;
import gov.nist.toolkit.testkitutilities.TestDefinition;
import gov.nist.toolkit.testkitutilities.client.SectionDefinitionDAO;
import gov.nist.toolkit.testkitutilities.client.StepDefinitionDAO;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 */
public class TestOverviewBuilder {
    private TestLogDetails testLogDetails;
    private String testId;
    private Set<String> testDependencies = new HashSet<>();
    private TestDefinition testDefinition;
    private TestOverviewDTO testOverview = new TestOverviewDTO();
    private XdsTestServiceManager testServiceManager;

    public TestOverviewBuilder(Session session, TestLogDetails testLogDetails) throws Exception {
        this.testLogDetails = testLogDetails;
        this.testId = testLogDetails.getTestInstance().getId();
        this.testDefinition = session.getTestkitSearchPath().getTestDefinition(testId);
        testOverview.setTestKitSource(testDefinition.detectSource().toString());
        testOverview.setTestKitSection(testDefinition.getTestKitSection());
        testServiceManager = new XdsTestServiceManager(session);
    }

    public TestOverviewDTO build() throws Exception {
        testOverview.setPass(true);  // will be updated by addSections()
        testOverview.setName(testId);
        ReadMe readme = testDefinition.getTestReadme();
        if (readme != null) {
            testOverview.setTitle(stripHeaderMarkup(readme.line1));
            testOverview.setDescription(Markdown.toHtml(readme.rest));
        }
        addSections();
        testOverview.setDependencies(testDependencies);
        return testOverview;
    }

    // Strip off markdown format header markings (#, ##, ###)
    private String stripHeaderMarkup(String in) {
        in = in.trim();
        if (in.charAt(0) != '#') return in;
        while (in.length() > 0 && in.charAt(0) == '#')
            in = in.substring(1);
        if (in.length() > 0)
            return in.trim();
        return in;
    }

    private void addSections() throws Exception {
        List<String> sectionNames = null;
        try {
            sectionNames = testServiceManager.getTestSections(testId);
        } catch (Exception e) {
            return;
        }
//        if (testId.equals("12360")) {
            // No test results available - scan
            // test definition to get section names
            try {
                List<String> sections = testDefinition.getSectionIndex();
                for (String section : sections) {
                    LogFileContentDTO logFileContentDTO = testLogDetails.sectionLogMapDTO.get(section);
                    if (logFileContentDTO == null) {
                        logFileContentDTO = new LogFileContentDTO();
                        logFileContentDTO.setHasRun(false);
                    } else {
                        logFileContentDTO.setHasRun(true);
                    }

                    SectionOverviewDTO sectionOverview = addSection(section, logFileContentDTO);

                    try {
                        SectionDefinitionDAO sectionDef = testDefinition.getSection(section);
                        sectionOverview.setSutInitiated(sectionDef.isSutInitiated());
                        testDependencies.addAll(sectionDef.getSectionDependencies());
                        for (String stepName : sectionDef.getStepNames()) {
                            StepOverviewDTO stepOverview = sectionOverview.getStep(stepName);
                            if (stepOverview==null) { // Probably not yet executed, pre-run state
                                stepOverview = new StepOverviewDTO();
                                StepDefinitionDAO stepSrc = sectionDef.getStep(stepName);
                                stepOverview.setName(stepSrc.getId());
                                stepOverview.setTransaction(stepSrc.getTransaction());
                                sectionOverview.getStepNames().add(stepName);
                                sectionOverview.getSteps().put(stepName,stepOverview);
                            }

                            stepOverview.setGoals(sectionDef.getStep(stepName).getGoals());
                            stepOverview.setInteractionSequence(sectionDef.getStep(stepName).getInteractionSequence());

                            TestStepLogContentDTO stepDTO = new TestStepLogContentDTO();
                            StepGoalsDTO goals = new StepGoalsDTO();
                            goals.setStepName(stepName);
                            goals.setGoals(sectionDef.getStep(stepName).getGoals());
                            stepDTO.setStepGoalsDTO(goals);
                            TestStepLogContentDTO existingStepLog = logFileContentDTO.getStepLog(stepName);
                            if (existingStepLog == null)
                                logFileContentDTO.addStep(stepName, stepDTO);
                            else
                                existingStepLog.setStepGoalsDTO(goals);
                        }
                    } catch (Exception e) {
                        System.out.println(e.toString());
                    }


                    sectionOverview.setRun(logFileContentDTO.isRun());
                    if (!sectionOverview.isPass())
                        testOverview.setPass(false);


                    try {
                        String sectionReadme = testDefinition.getFullSectionReadme(section);
                        sectionOverview.setDescription(Markdown.toHtml(sectionReadme));
                    } catch (IOException e) {
                        sectionOverview.setDescription("");
                    }
//                    testOverview.addSection(sectionOverview);
                }
                return;
            } catch (Exception e) {
                // no SECTIONS defined - maybe single un-named section
                if (testLogDetails.getTestPlanLogs().get("log.xml") != null) {
                    LogFileContentDTO logFileContentDTO = testLogDetails.getTestPlanLogs().get("log.xml");
                    SectionOverviewDTO sectionOverview = addSection("", logFileContentDTO);
                    if (!sectionOverview.isPass())
                        testOverview.setPass(false);
                }
                return;
            }
//        }
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
        sectionOverview.setHl7Time(logFileContentDTO.getHl7Time());
        sectionOverview.setSite(logFileContentDTO.getSiteName());

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
        stepOverview.setExpectedSuccess(stepContent.isExpectedSuccess());
        stepOverview.setDetails(stepContent.getDetails());
        stepOverview.addErrors(stepContent.getSoapFaults());
        stepOverview.addErrors(stepContent.getErrors());
        stepOverview.addErrors(stepContent.getAssertionErrors());
        stepOverview.setTransaction(stepContent.getTransaction()); // NOTE: This makes an assumption that only one transaction is allowed per step.

        sectionOverview.addStep(stepName, stepOverview);
    }

    public Collection<String> getTestDependencies() {
        return testDependencies;
    }
}
