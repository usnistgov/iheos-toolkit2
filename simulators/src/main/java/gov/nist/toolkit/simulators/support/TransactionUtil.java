package gov.nist.toolkit.simulators.support;

import gov.nist.toolkit.actorfactory.SimDb;
import gov.nist.toolkit.actorfactory.client.SimId;
import gov.nist.toolkit.results.client.AssertionResult;
import gov.nist.toolkit.results.client.DocumentEntryDetail;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.SiteSpec;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.session.server.serviceManager.XdsTestServiceManager;
import gov.nist.toolkit.simulators.sim.rep.RepIndex;
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
public class TransactionUtil {
    static Logger logger = Logger.getLogger(TransactionUtil.class);

    static public List<Result> Transaction(SiteSpec siteSpec, String sessionName, TestInstance testId, Map<String, String> params, boolean stopOnFirstError, Session myTestSession, XdsTestServiceManager xdsTestServiceManager, List<String> sections) {

//        UtilityRunner utilityRunner = new UtilityRunner(xdsTestServiceManager, TestRunType.TEST);
        if (myTestSession.getMesaSessionName() == null) myTestSession.setMesaSessionName(sessionName);


//        logger.info("Transaction index 0 has:" + siteName); // This should always be the selected value
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
     */
    static public Result register(Session session, String username, TestInstance testInstance, SiteSpec registry, Map<String, String> params) {

        // pid format "SKB1^^^&1.2.960&ISO";

        boolean stopOnFirstError = true;
        //new Session(Installation.installation().warHome(), username);

        XdsTestServiceManager xdsTestServiceManager = new XdsTestServiceManager(session);
        List<Result> results = null;
        Result result = null;

        try {

            // NOTE: Make an assumption that there is only one ( and the first) section is always the Register section so in this case run the Default section. This needs to be manually enforced when designing content bundles.

            List<String> sections = new ArrayList<>();

            results = Transaction(registry, username, testInstance, params, stopOnFirstError, session, xdsTestServiceManager, sections);

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
     *
     * @return
     */
    static public Map<String, String> registerWithLocalizedTrackingInODDS(Session session, String username, TestInstance testInstance, SiteSpec registry, SimId oddsSimId, Map<String, String> params) {

        Map<String, String> rs = new HashMap<>();
        String oddeUid = null;

        // Part 1. Register an ODDE
        Result result = register(session, username,testInstance,registry,params);

        if (result!=null && result.getStepResults()!=null)
            logger.info(" *** register result size: " + result.getStepResults().size());

        if (result==null) {
            rs.put("error","Null result.");
            return rs;
        }

        if (!result.passed()) {
            rs.put("error","Failed.");
            if (result.assertions!=null && result.assertions.assertions!=null) {
                List<AssertionResult> ars = result.assertions.assertions;
                for (int cx=0; cx < ars.size(); cx++) {
                    rs.put("assertion" + cx, ars.get(cx).toString());
                }
            }
        } else {

            try {
                oddeUid = result.stepResults.get(0).getMetadata().docEntries.get(0).uniqueId;
                rs.put("key", oddeUid);
            } catch (Throwable t) {
                rs.put("error", t.toString());
            }


            // Part 2. Store the UID to recognize it in future
            // Store all this
            /* The Document External Identifier

             result.stepResults.get(0).getMetadata().docEntries.get(0).uniqueId
             The EO object ref

             result.stepResults.get(0).getMetadata().docEntries.get(0).id

             Created timestamp

             result.getTimestamp()

             * result.timestamp
             result.stepResults.get(0).metadata.docEntries.get(0).patientId

             testplan?
             testInstance.id

             reg site?
             registry.name
             */

            // TODO:
            // When a Retrieve is triggered, we have to detect an ODDS-recognized UID from a bogus or non-existent UID, to do this, we Store the UID in the ODDS.
            // The document in ODDS for the UID will contain the content supply index which will be used for the PnR and content supply
            // 0. Implement an internal-purpose-only "fakePnR" in ODDS that will only store document
            // 1. Create an internal-purpose-only Store_OD section and test plan that will only store the document with the document Uid from Step 1.
            // 2. Run that test plan transaction
            // 3. Return a map with UID plus the index


            if (oddeUid!=null) { // An indicator that registration was successful

                try {

                    DocumentEntryDetail ded = new DocumentEntryDetail();
                    ded.setUniqueId(result.stepResults.get(0).getMetadata().docEntries.get(0).uniqueId);
                    ded.setId(result.stepResults.get(0).getMetadata().docEntries.get(0).id);
                    ded.setEntryType("urn:uuid:34268e47-fdf5-41a6-ba33-82133c465248");
                    ded.setTimestamp(result.getTimestamp());
                    ded.setTestInstanceId(testInstance.getId());
                    ded.setPatientId(result.stepResults.get(0).getMetadata().docEntries.get(0).patientId);
                    ded.setRegistrySiteName(registry.getName());


                    // Save document entry detail to the repository index
                    SimDb simDb = new SimDb(oddsSimId);

                    RepIndex repIndex = new RepIndex(simDb.getRepositoryIndexFile().toString(), oddsSimId);

                    // Another place the StoredDocument gets touched is the Retrieve -- to update the supply state index
                    // StoredDocument storedDocument = repIndex.getDocumentCollection().getStoredDocument(oddeUid);
                    // Begin insert details on the on-demand document entry
                    StoredDocument storedDocument = new StoredDocument();
                    storedDocument.setUid(oddeUid);
                    storedDocument.setEntryDetail(ded);
//                    storedDocument.setPathToDocument(simDb.getRepositoryDocumentFile(oddeUid).toString());
//                    storedDocument.setContent("This content is served on-demand.".getBytes());
//                    storedDocument.setMimetype("text/plain");
                    repIndex.getDocumentCollection().add(storedDocument);
                    repIndex.save();

                } catch (Exception ex) {
                    rs.put("error",ex.toString());
                }

            }



        }
        return rs;
    }

}
