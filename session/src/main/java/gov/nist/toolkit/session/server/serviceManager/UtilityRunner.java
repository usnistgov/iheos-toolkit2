package gov.nist.toolkit.session.server.serviceManager;

import gov.nist.toolkit.installation.server.Installation;
import gov.nist.toolkit.results.ResultBuilder;
import gov.nist.toolkit.results.client.*;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.simcommon.server.SimCache;
import gov.nist.toolkit.simcommon.server.SiteServiceManager;
import gov.nist.toolkit.sitemanagement.Sites;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.testengine.engine.TestCollection;
import gov.nist.toolkit.testengine.engine.TransactionSettings;
import gov.nist.toolkit.testenginelogging.logrepository.LogRepositoryFactory;
import gov.nist.toolkit.testkitutilities.TestKit;
import gov.nist.toolkit.testkitutilities.TestKitSearchPath;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.client.EnvironmentNotSelectedException;
import gov.nist.toolkit.xdsexception.client.ToolkitRuntimeException;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class UtilityRunner {
    private final XdsTestServiceManager xdsTestServiceManager;  // this is here only because of an automatic refactoring
    private static Logger logger = Logger.getLogger(UtilityRunner.class);
    private AssertionResults assertionResults = new AssertionResults();
    private final TestRunType testRunType;

    UtilityRunner(XdsTestServiceManager xdsTestServiceManager, TestRunType testRunType) {
        this.xdsTestServiceManager = xdsTestServiceManager;
        this.testRunType = testRunType;
    }

    /**
     * Run a testplan(s) as a utility within a session.  This is different from
     * run in that run stores logs in the external_cache and
     * this call stores the logs within the session so they go away at the
     * end of the end of the session.  Hence the label 'utility'.
     *
     * @param params
     * @param sections
     * @param testInstance
     * @param areas
     * @param stopOnFirstFailure
     * @return
     */
    public Result run(Session session, Map<String, String> params, Map<String, Object> params2, List<String> sections,
                      TestInstance testInstance, String[] areas, boolean stopOnFirstFailure) {
        if (testInstance.getTestSession() == null) throw new ToolkitRuntimeException("TestSession is null");

        xdsTestServiceManager.cleanupParams(params);

        try {
            // Initialize if necessary.  Used by TestRunner and it may have other settings
            // to enforce
            if (session.transactionSettings == null) {
                session.transactionSettings = new TransactionSettings();
            }

            // depending on the configuration, this could be null
            session.transactionSettings.patientIdAssigningAuthorityOid = session.currentCodesConfiguration().getAssigningAuthorityOid();

            if (session.xt == null)
                throw new Exception("UtilityRunner#run: session.xt not initialized");

            initializeLogRepository(session);

            session.xt.setLogRepository(session.transactionSettings.logRepository);
            logger.info("*** logRepository user (sessionName): " + session.transactionSettings.logRepository.getTestSession());

            try {
                if (testInstance.getId().startsWith("tc:")) {
                    String collectionName = testInstance.getId().split(":")[1];
//                    session.xt.addTestCollection(collectionName);
                    // all tests in the collection must be linked so the logs are linked
                    // we don't use the TestInstance we were given because it references a test
                    // collection.  We replace it with a list of linked TestInstances, one for each
                    // contained test.

                    TestCollection testCollection = new TestCollection(Installation.instance().internalTestkitFile(), collectionName);
                    List<String> testIds = testCollection.getTestIds();
                    for (String id : testIds) {
                        TestInstance ti = new TestInstance(id);
                        TestKitSearchPath searchPath = session.getTestkitSearchPath();
                        TestKit testKit = searchPath.getTestKitForTest(id);
                        if (testKit == null)
                            throw new Exception("Test " + ti + " not found");
                        session.xt.addTest(testKit, ti);
                    }
//                        session.xt.addTest(LogRepository.cloneTestInstance(ti, id)); // this cloning links them
                } else {
                    TestKitSearchPath searchPath = session.getTestkitSearchPath();
                    logger.info(searchPath.toString());
                    TestKit testKit = searchPath.getTestKitForTest(testInstance.getId());
                    if (testKit == null)
                        throw new Exception("Test " + testInstance + " not found");
                    session.xt.addTest(testKit, testInstance, sections, areas);
                }

                // force loading of site definitions
                SiteServiceManager.getSiteServiceManager().getAllSites(session.getId(), testInstance.getTestSession());

                Collection<Site> siteCollection = SimCache.getAllSites(testInstance.getTestSession());
                Sites theSites = new Sites(siteCollection);
                assignSite(session, theSites);
            } catch (Exception e) {
                logger.error(ExceptionUtil.exception_details(e));
                assertionResults.add(ExceptionUtil.exception_details(e), false);
                return ResultBuilder.RESULT(testInstance, assertionResults, null, null);
            }
            session.xt.setSecure(session.isTls());

            if (session.isSoap) {
                if (session.isTls())
                    assertionResults.add("Using TLS");
                else
                    assertionResults.add("Not using TLS");

                session.xt.setWssec(session.isSaml());
                if (session.isSaml())
                    assertionResults.add("Using SAML");
                else
                    assertionResults.add("Not using SAML");


                if (session.siteSpec != null)
                    assertionResults.add("Site: " + session.siteSpec.name);
            }

            return getResult(session, params, params2, sections, testInstance, stopOnFirstFailure);

        } catch (NullPointerException e) {
            logger.error(ExceptionUtil.exception_details(e));
            assertionResults.add(ExceptionUtil.exception_details(e), false);
            return ResultBuilder.RESULT(testInstance, assertionResults, null, null);
        } catch (Throwable e) {
            logger.error(ExceptionUtil.exception_details(e));
            assertionResults.add(ExceptionUtil.exception_details(e), false);
            return ResultBuilder.RESULT(testInstance, assertionResults, null, null);
        }
        finally {
            session.clear();
        }
    }

    private Result getResult(Session session, Map<String, String> params, Map<String, Object> params2, List<String> sections, TestInstance testInstance, boolean stopOnFirstFailure) {
        assertionResults.add("Parameters:");
        for (String param : params.keySet()) {
            assertionResults.add("..." + param + ": " + params.get(param));
        }

        if (session.siteSpec != null)
            System.out.println("Site is " + session.siteSpec.name);

        try {
            assertionResults.add("Starting");
            // s.assertionResults.add("Log Cache: " + s.getLogCount() + " entries");
            session.transactionSettings.securityParams = session;

            if (session.transactionSettings.environmentName==null)
                session.transactionSettings.environmentName=session.getCurrentEnvName();
            if (session.transactionSettings.testSession==null)
                session.transactionSettings.testSession=session.getTestSession();

            session.xt.run(params, params2, stopOnFirstFailure, session.transactionSettings);

            assertionResults.add(session.transactionSettings.res);

            // it writes a uuid named file to TestLogCache/${user}/
            if (!testInstance.isTestCollection())
                session.transactionSettings.logRepository.logOutIfLinkedToUser(testInstance, session.xt.getLogMap());

            Result result = xdsTestServiceManager.buildResult(session.xt.getTestSpecs(), testInstance);
            xdsTestServiceManager.scanLogs(session.xt, assertionResults, sections);
            assertionResults.add("Finished");
            result.assertions.add(assertionResults);
            return result;
        } catch (EnvironmentNotSelectedException e) {
            logger.error(ExceptionUtil.exception_details(e));
            assertionResults.add("Environment not selected", false);
            return ResultBuilder.RESULT(testInstance, assertionResults, null, null);
        } catch (Exception e) {
            logger.error(ExceptionUtil.exception_details(e));
            assertionResults.add(ExceptionUtil.exception_details(e), false);
            return ResultBuilder.RESULT(testInstance, assertionResults, null, null);
        }
    }

    // How is this useful since logRepository.id can never be updated?
    private void initializeLogRepository(Session session) throws IOException {
        if (session.transactionSettings.logRepository == null) {
            if (testRunType == TestRunType.UTILITY) {
                logger.info("*** logRepository user: " + session.getId());
                session.transactionSettings.logRepository =
                        LogRepositoryFactory.
                                getLogRepository(
                                        Installation.instance().sessionCache(),
                                        session.getId(),
                                        LogIdIOFormat.JAVA_SERIALIZATION,
                                        LogIdType.TIME_ID,
                                        null);
            } else if (testRunType == TestRunType.TEST) {
                logger.info("*** logRepository user (sessionName): " + session.getTestSession());
                session.transactionSettings.logRepository =
                        LogRepositoryFactory.
                                getLogRepository(
                                        Installation.instance().testLogCache(session.getTestSession()),
                                        session.getTestSession(),
                                        LogIdIOFormat.JAVA_SERIALIZATION,
                                        LogIdType.SPECIFIC_ID,
                                        null);
            }
        }
    }

    private void assignSite(Session session, Sites theSites) throws Exception {
        if (session.siteSpec != null && session.siteSpec.name != null && !session.siteSpec.name.equals("")) {
            Site site = theSites.getOrchestrationLinkedSites(session.siteSpec);

            if (site == null)
                throw new Exception("Cannot find site " + session.siteSpec.name);
            logger.info("Using site: " + site.describe());
            session.xt.setSite(site);
            session.xt.setSites(theSites);
        } else if (session.repUid != null) {
            Site site = theSites.getSite("allRepositories", session.getTestSession());
            if (site == null)
                throw new Exception("Cannot find site 'allRepositories'");
            session.xt.setSite(site);
            session.xt.setSites(theSites);
        }
    }

}