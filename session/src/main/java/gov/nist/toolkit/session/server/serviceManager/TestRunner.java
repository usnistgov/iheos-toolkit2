package gov.nist.toolkit.session.server.serviceManager;

import gov.nist.toolkit.installation.server.Installation;
import gov.nist.toolkit.installation.shared.TestSession;
import gov.nist.toolkit.results.CommonService;
import gov.nist.toolkit.results.client.LogIdIOFormat;
import gov.nist.toolkit.results.client.LogIdType;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.session.server.TestSessionNotSelectedException;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.testengine.engine.FhirContentFormat;
import gov.nist.toolkit.testengine.engine.ResultPersistence;
import gov.nist.toolkit.testenginelogging.logrepository.LogRepositoryFactory;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.client.EnvironmentNotSelectedException;
import org.apache.log4j.Logger;
import org.hl7.fhir.dstu3.elementmodel.Manager;

import java.util.List;
import java.util.Map;

public class TestRunner {
    private final XdsTestServiceManager xdsTestServiceManager;
    private static Logger logger = Logger.getLogger(TestRunner.class);

    public TestRunner(XdsTestServiceManager xdsTestServiceManager) {
        this.xdsTestServiceManager = xdsTestServiceManager;
    }

    public List<Result> run(Session session, TestSession mesaTestSession, SiteSpec siteSpec, TestInstance testInstance, List<String> sections,
                            Map<String, String> params, Map<String, Object> params2, boolean stopOnFirstFailure) {
        XdsTestServiceManager.logger.info(session.id() + ": " + "run" + " " + mesaTestSession + " " + testInstance + " " + sections + " " + siteSpec + " " + params + " " + stopOnFirstFailure);
        try {

            if (session.getEnvironment() == null)
                throw new EnvironmentNotSelectedException("");

            if ((mesaTestSession == null || mesaTestSession.toString().equals("")))
                throw new TestSessionNotSelectedException("Must choose test session");
            session.setSiteSpec(siteSpec);
            session.transactionSettings.testSession = session.getTestSession();
            session.transactionSettings.environmentName = session.getCurrentEnvName();

            // if testId is actually a test collection then let a lower level fill in the logRepository
            // for the individual test - no logRepository should be created for the test collection
            // itself
            if (session.transactionSettings.logRepository == null && !testInstance.getId().startsWith("tc:")) {
                session.transactionSettings.logRepository = LogRepositoryFactory.
                        getLogRepository(
                                Installation.instance().testLogCache(mesaTestSession),
                                mesaTestSession,
                                LogIdIOFormat.JAVA_SERIALIZATION,
                                LogIdType.SPECIFIC_ID,
                                testInstance);
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

            String format = params.get("format");
            if (format != null && !format.equals("")) {
                if (format.equalsIgnoreCase(Manager.FhirFormat.XML.name()))
                    session.transactionSettings.fhirContentFormat = FhirContentFormat.XML;
                else
                    session.transactionSettings.fhirContentFormat = FhirContentFormat.JSON;
            }


            // This sets result.testId so it looks like a session-based utility usage
            // of the test engine.  Need to re-label it so the logs can later
            // be properly pulled from the external_cache.
//            Result result = xdsTestServiceManager.xdstest(testId, SECTIONS, params, params2, null, stopOnFirstFailure);
            UtilityRunner utilityRunner = new UtilityRunner(xdsTestServiceManager, TestRunType.TEST);
            Result result = utilityRunner.run(session, params, params2, sections, testInstance, null, stopOnFirstFailure);
//			ResultSummary summary = new ResultSummary(result);

            // Save results to external_cache.
            // Supports getTestResults tookit api call
            ResultPersistence rPer = new ResultPersistence();
            try {
                logger.info("Persist results to EC");
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