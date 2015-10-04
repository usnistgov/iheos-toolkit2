package gov.nist.toolkit.session.server.serviceManager;

import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.results.CommonService;
import gov.nist.toolkit.results.client.*;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.session.server.TestSessionNotSelectedException;
import gov.nist.toolkit.testengine.engine.ResultPersistence;
import gov.nist.toolkit.testenginelogging.logrepository.LogRepositoryFactory;
import gov.nist.toolkit.xdsexception.EnvironmentNotSelectedException;
import gov.nist.toolkit.xdsexception.ExceptionUtil;

import java.util.List;
import java.util.Map;

public class TestRunner {
    private final XdsTestServiceManager xdsTestServiceManager;

    public TestRunner(XdsTestServiceManager xdsTestServiceManager) {
        this.xdsTestServiceManager = xdsTestServiceManager;
    }

    public List<Result> run(Session session, String mesaTestSession, SiteSpec siteSpec, TestId testId, List<String> sections,
                            Map<String, String> params, Map<String, Object> params2, boolean stopOnFirstFailure) {
        XdsTestServiceManager.logger.info(session.id() + ": " + "run" + " " + mesaTestSession + " " + testId + " " + sections + " " + siteSpec + " " + params + " " + stopOnFirstFailure);
        try {

            if (session.getEnvironment() == null)
                throw new EnvironmentNotSelectedException("");

            if ((mesaTestSession == null || mesaTestSession.equals("")))
                throw new TestSessionNotSelectedException("Must choose test session");
            session.setSiteSpec(siteSpec);

            // if testId is actualy a test collection then let a lower leve fill in the logRepository
            // for the individual test - no logRepository should be created for the test collection
            // itself
            if (session.transactionSettings.logRepository == null && !testId.getId().startsWith("tc:")) {
                session.transactionSettings.logRepository = new LogRepositoryFactory().
                        getRepository(
                                Installation.installation().testLogCache(),
                                mesaTestSession,
                                LogIdIOFormat.JAVA_SERIALIZATION,
                                LogIdType.SPECIFIC_ID,
                                testId);
                session.transactionSettings.writeLogs = true;
            }



            // this PatientId override is necessary because the patientid.txt file
            // is shared by all threads
            String pid = params.get("$patientid$");
            if (pid != null && !pid.equals("")) {
                session.transactionSettings.patientId = pid;
            }

            String altPid = params.get("$altpatientid$");
            if (altPid != null && !altPid.equals("")) {
                session.transactionSettings.altPatientId = altPid;
            } else
                session.transactionSettings.altPatientId = null;


            // This sets result.testId so it looks like a session-based utility usage
            // of the test engine.  Need to re-label it so the logs can later
            // be properly pulled from the external_cache.
//            Result result = xdsTestServiceManager.xdstest(testId, sections, params, params2, null, stopOnFirstFailure);
            UtilityRunner utilityRunner = new UtilityRunner(xdsTestServiceManager, TestRunType.TEST);
            Result result = utilityRunner.run(session, params, params2, sections, testId, null, stopOnFirstFailure);
//			ResultSummary summary = new ResultSummary(result);

            // Save results to external_cache.
            // Supports getTestResults tookit api call
            ResultPersistence rPer = new ResultPersistence();
            try {
                rPer.write(result, mesaTestSession);
            } catch (Exception e) {
                result.assertions.add(ExceptionUtil.exception_details(e), false);
            }

            return CommonService.asList(result);
        } catch (Exception e) {
            return CommonService.buildExtendedResultList(e);
        } finally {
            session.clear();
        }
    }
}