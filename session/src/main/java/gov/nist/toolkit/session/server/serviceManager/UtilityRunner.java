package gov.nist.toolkit.session.server.serviceManager;

import gov.nist.toolkit.actorfactory.SimCache;
import gov.nist.toolkit.actorfactory.SiteServiceManager;
import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.results.ResultBuilder;
import gov.nist.toolkit.results.client.*;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.sitemanagement.Sites;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.testengine.engine.TestCollection;
import gov.nist.toolkit.testengine.engine.TransactionSettings;
import gov.nist.toolkit.testenginelogging.logrepository.LogRepository;
import gov.nist.toolkit.testenginelogging.logrepository.LogRepositoryFactory;
import gov.nist.toolkit.xdsexception.EnvironmentNotSelectedException;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class UtilityRunner {
    private final XdsTestServiceManager xdsTestServiceManager;  // this is here only because of an automatic refactoring
    static Logger logger = Logger.getLogger(UtilityRunner.class);
    AssertionResults assertionResults = new AssertionResults();
    final TestRunType testRunType;

    public UtilityRunner(XdsTestServiceManager xdsTestServiceManager, TestRunType testRunType) {
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
    // TODO add environment name as param? (what about session? will it contain the mesa session info?
    public Result run(Session session, Map<String, String> params, Map<String, Object> params2, List<String> sections,
                      TestInstance testInstance, String[] areas, boolean stopOnFirstFailure) {

        xdsTestServiceManager.cleanupParams(params);

        // TODO find test in the right testkit using Session object (contain mesaSession and environment)
        // TODO add the location (or File) in TestInstance (could also be done at a higher level)

        try {
            // Initialize if necessary.  Used by TestRunner and it may have other settings
            // to enforce
            if (session.transactionSettings == null) {
                session.transactionSettings = new TransactionSettings();
            }

            // depending on the configuration, this could be null
            session.transactionSettings.patientIdAssigningAuthorityOid = session.currentCodesConfiguration().getAssigningAuthorityOid();

            if (session.xt == null)
                session.xt = xdsTestServiceManager.getNewXt();

//            if (assertionResults == null)
//                assertionResults = new AssertionResults();
//            assertionResults = assertionResults;



            if (session.transactionSettings.logRepository == null) {
                if (testRunType == TestRunType.UTILITY) {
                    logger.info("*** logRepository user: " + session.getId());
                    session.transactionSettings.logRepository =
                            LogRepositoryFactory.
                                    getRepository(
                                            Installation.installation().sessionCache(),
                                            session.getId(),
                                            LogIdIOFormat.JAVA_SERIALIZATION,
                                            LogIdType.TIME_ID,
                                            null);
                } else if (testRunType == TestRunType.TEST) {
                    logger.info("*** logRepository user (sessionName): " + session.getMesaSessionName());
                    session.transactionSettings.logRepository =
                            LogRepositoryFactory.
                                    getRepository(
                                            Installation.installation().testLogCache(),
                                            session.getMesaSessionName(),
                                            LogIdIOFormat.JAVA_SERIALIZATION,
                                            LogIdType.SPECIFIC_ID,
                                            null);
                }
            }
            session.xt.setLogRepository(session.transactionSettings.logRepository);
            logger.info("*** logRepository user (sessionName): " + session.transactionSettings.logRepository.getUser());

            try {
                if (testInstance.getId().startsWith("tc:")) {
                    String collectionName = testInstance.getId().split(":")[1];
//                    session.xt.addTestCollection(collectionName);
                    // all tests in the collection must be linked so the logs are linked
                    // we don't use the TestInstance we were given because it references a test
                    // collection.  We replace it with a list of linked TestInstances, one for each
                    // contained test.

                    session.xt.setTestkits(Installation.installation().testkitFiles(session.getCurrentEnvironment(),session.getMesaSessionName()));
                    // TODO change testkitFile() for testkitFiles?
                    TestCollection testCollection = new TestCollection(Installation.installation().testkitFile(), collectionName);
                    List<String> testIds = testCollection.getTestIds();
                    TestInstance ti = null;
                    for (String id : testIds) {
                        if (ti == null) {
                            ti = new TestInstance(id);
                            session.xt.addTest(ti);
                            continue;
                        }
                        session.xt.addTest(LogRepository.cloneTestInstance(ti, id)); // this cloning links them
                    }
                } else {
                    session.xt.addTest(testInstance, sections, areas);
                }

                // force loading of site definitions
                SiteServiceManager.getSiteServiceManager().getAllSites(session.getId());

//                Sites theSites = new Sites(SiteServiceManager.getSiteServiceManager().getAllSites(session.getId()));
                Collection<Site> siteCollection = SimCache.getAllSites();
                logger.debug("UtilityRunner - defined sites - " + SimCache.describe());
                Sites theSites = new Sites(siteCollection);
                // Only for SOAP messages will siteSpec.name be filled in.  For Direct it is not expected
                if (session.siteSpec != null && session.siteSpec.name != null && !session.siteSpec.name.equals("")) {
                    Site site = theSites.getSite(session.siteSpec.name);
                    if (site == null)
                        throw new Exception("Cannot find site " + session.siteSpec.name);
                    logger.info("Using site: " + site.describe());
                    session.xt.setSite(site);
                    session.xt.setSites(theSites);
                } else if (session.repUid != null) {
                    Site site = theSites.getSite("allRepositories");
                    if (site == null)
                        throw new Exception("Cannot find site 'allRepositories'");
                    session.xt.setSite(site);
                    session.xt.setSites(theSites);
                }
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

                // TODO set testkit in xt
                session.xt.run(params, params2, stopOnFirstFailure, session.transactionSettings);

                assertionResults.add(session.transactionSettings.res);

                // Save the created logs in the SessionCache
//                TestId testId1 = xdsTestServiceManager.newTestLogId();

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

}