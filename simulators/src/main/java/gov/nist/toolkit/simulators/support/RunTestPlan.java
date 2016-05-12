package gov.nist.toolkit.simulators.support;

import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.SiteSpec;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.session.server.serviceManager.XdsTestServiceManager;
import gov.nist.toolkit.testengine.engine.ResultPersistence;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Sunil Bhaskarla on 4/2/2016.
 */
public class RunTestPlan {
    static Logger logger = Logger.getLogger(RunTestPlan.class);

    static public List<Result> Transaction(String siteName, String sessionName, TestInstance testId, Map<String, String> params, boolean stopOnFirstError, Session myTestSession, XdsTestServiceManager xdsTestServiceManager, List<String> sections) {

//        UtilityRunner utilityRunner = new UtilityRunner(xdsTestServiceManager, TestRunType.TEST);
        if (myTestSession.getMesaSessionName() == null) myTestSession.setMesaSessionName(sessionName);

        SiteSpec siteSpec = new SiteSpec();

        logger.info("Transaction index 0 has:" + siteName); // This should always be the selected value
        siteSpec.setName(siteName);
        myTestSession.setSiteSpec(siteSpec);
//        myTestSession.transactionSettings.patientId

        List<Result> results = xdsTestServiceManager.runMesaTest(sessionName, siteSpec, testId, sections, params, null, stopOnFirstError); // This wrapper does two important things of interest: 1) set patient id 2) eventually calls the UtilityRunner


//        Result result = utilityRunner.run(myTestSession, params, null, sections, testId, null, stopOnFirstError);

        // Save results to external_cache.
        // Supports getTestResults tookit api call
        ResultPersistence rPer = new ResultPersistence();

            for (Result result : results) {
                try {
                    rPer.write(result, sessionName);
                } catch (Exception e) {
                    result.assertions.add(ExceptionUtil.exception_details(e), false);
                }
            }

        return results;
    }

    /**
     * Must have a default Register section in this test.
     * @param username
     * @param testInstance
     * @param pid
     * @param registry
     * @return
     */
    static public Result register(Session session, String username, TestInstance testInstance, String pid, SiteSpec registry) {
        String testPlanId = testInstance.getId();

        Map<String, String> params = new HashMap<>();
        // pid format "SKB1^^^&1.2.960&ISO";
        logger.debug("patientId is: " + pid);
        params.put("$patientid$", pid);
        boolean stopOnFirstError = true;
        Session myTestSession = session; //new Session(Installation.installation().warHome(), username);

        XdsTestServiceManager xdsTestServiceManager = new XdsTestServiceManager(myTestSession);
        List<Result> results = null;
        Result result = null;

        try {

            // NOTE: Make an assumption that the only (first) section is always the Register section so in this case run the Default section. This needs to be manually enforced when designing content bundles.

            List<String> sections = new ArrayList<>();

            results = RunTestPlan.Transaction(registry.getName(), username, testInstance, params, stopOnFirstError, myTestSession, xdsTestServiceManager, sections);

            result = results.get(0);
            if (result!=null)
                logger.info("passed? " + result.passed());
            else
                logger.info("Null result.");

        } catch (Exception ex) {
            logger.error(ex.toString());
        }

        return result;

    }

    /**
     * Store the ODDE UID in the ODDS index -- this is a localized purpose which means it is not directly related to testing.
     * @param username
     * @param testInstance
     * @param pid
     * @param registry
     * @param odds
     * @return
     */
    static public Map<String, String> registerWithLocalizedTrackingInODDS(Session session, String username, TestInstance testInstance, String pid, SiteSpec registry, SiteSpec odds) {

        // Step 1. Register an ODDE
        Result result = register(session, username,testInstance,pid,registry);

        if (result!=null && result.getStepResults()!=null)
            logger.info(" *** register result: " + result.getStepResults().size());

        if (result.passed()) {

            // Store the entry in the odds index
            // This is later used when a ODDS retrieve comes in and the repository index is checked to make sure it is a valid document OD
            // The document in ODDS for the UID will contain the content supply index which will be used for the PnR and content supply




            return null;

        }
        return null;
    }

}
