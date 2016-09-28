package gov.nist.toolkit.simulators.support.od;

import gov.nist.toolkit.actorfactory.SimDb;
import gov.nist.toolkit.actorfactory.client.SimId;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.commondatatypes.MetadataSupport;
import gov.nist.toolkit.configDatatypes.SimulatorProperties;
import gov.nist.toolkit.results.client.*;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.session.server.serviceManager.XdsTestServiceManager;
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement;
import gov.nist.toolkit.simulators.sim.rep.RepIndex;
import gov.nist.toolkit.simulators.support.StoredDocument;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.testengine.engine.ResultPersistence;
import gov.nist.toolkit.testkitutilities.TestDefinition;
import gov.nist.toolkit.testkitutilities.TestKitSearchPath;
import gov.nist.toolkit.utilities.xml.Util;
import gov.nist.toolkit.utilities.xml.XmlUtil;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Sunil Bhaskarla on 4/2/2016.
 */
public class TransactionUtil {
    public static final int ALL_OD_DOCS_SUPPLIED = -1;
    static Logger logger = Logger.getLogger(TransactionUtil.class);

    static public List<Result> Transaction(SiteSpec siteSpec, String environment, String sessionName, TestInstance testId, Map<String, String> params, boolean stopOnFirstError, Session myTestSession, XdsTestServiceManager xdsTestServiceManager, List<String> sections) {

//        UtilityRunner utilityRunner = new UtilityRunner(xdsTestServiceManager, TestRunType.TEST);


//        logger.info("Transaction index 0 has:" + siteName); // This should always be the selected value
//        myTestSession.transactionSettings.patientId

        List<Result> results; // This wrapper does two important things of interest: 1) set patient id 2) eventually calls the UtilityRunner
        try {
            results = xdsTestServiceManager.runMesaTest(environment, sessionName, siteSpec, testId, sections, params, null, stopOnFirstError);
        } catch (Exception e) {
            results = new ArrayList<>();
            Result result = new Result();
            result.assertions.add(ExceptionUtil.exception_details(e), false);
            results.add(result);
            return results;
        }


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
    static public Result register(Session session, String username, TestInstance testInstance, SiteSpec registry, Map<String, String> params, List<String> sections) {

        // pid format "SKB1^^^&1.2.960&ISO";

        boolean stopOnFirstError = true;
        //new Session(Installation.instance().warHome(), username);

        XdsTestServiceManager xdsTestServiceManager = new XdsTestServiceManager(session);



        try {

            // NOTE: Make an assumption that there is only one ( and the first) section is always the Register section so in this case run the Default section. This needs to be manually enforced when designing content bundles.

            List<Result> results = null;

            if (session.getMesaSessionName() == null) session.setMesaSessionName(username);
            session.setSiteSpec(registry);

            results = Transaction(registry, session.getCurrentEnvironment(), username, testInstance, params, stopOnFirstError, session, xdsTestServiceManager, sections);

            printResult(results);
            return results.get(0);

        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex.toString());
        }

        return null;

    }

    private static boolean printResult(List<Result> results) {
        if (results!=null) {
            Result result = results.get(0);
            if (result!=null) {
                boolean passed = result.passed();
                logger.info("tx passed? " + passed);
                return passed;
            } else
                logger.info("Null result.");
        } else
            logger.info("Null results.");
        return false;
    }

    /**
     *
     * @return
     */
    static public Map<String, String> registerWithLocalizedTrackingInODDS(Session session, String username, TestInstance testInstance, SiteSpec registry, SimId oddsSimId, Map<String, String> params) throws Exception {

        if (oddsSimId==null)
            throw new Exception("ODDS Sim Id cannot be null.");

        Map<String, String> rs = new HashMap<>();
        String oddeUid = null;

        // Part 1. Register an ODDE
        Result result = register(session, username,testInstance,registry,params, new ArrayList<String>(){{add("Register_OD");}});

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
                t.printStackTrace();
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

            // When a Retrieve is triggered, we have to detect an ODDS-recognized UID from a bogus or non-existent UID, to do this, we Store the UID in the ODDS.
            // The document in ODDS for the UID will contain the content supply index which will be used for the PnR and content supply
            // 0. Implement an internal-purpose-only "fakePnR" in ODDS that will only store document
            // 1. Create an internal-purpose-only Store_OD section and test plan that will only store the document with the document Uid from Step 1.
            // 2. Run that test plan transaction
            // 3. Return a map with UID plus the index


            if (oddeUid!=null) { // An indicator that registration was successful

                try {


                    XdsTestServiceManager xdsTestServiceManager = new XdsTestServiceManager(session);
                    List<String> testPlanSections = xdsTestServiceManager.getTestSections(testInstance.getId());
                    String registerSection = testPlanSections.get(0); // IMPORTANT NOTE: In a Content Bundle: Make an assumption that the only (first) section is always the Register section which has the ContentBundle
                    String contentBundle = testInstance.getId() + "/" + registerSection + "/" + "ContentBundle";
                    List<String> contentBundleSections = xdsTestServiceManager.getTestSections(contentBundle);

                    // Save document entry detail to the repository index
                    SimDb simDb = new SimDb(oddsSimId);

                    DocumentEntryDetail ded = new DocumentEntryDetail();
                    ded.setUniqueId(result.stepResults.get(0).getMetadata().docEntries.get(0).uniqueId);
                    ded.setId(result.stepResults.get(0).getMetadata().docEntries.get(0).id);
                    ded.setEntryType("urn:uuid:34268e47-fdf5-41a6-ba33-82133c465248");
                    ded.setTimestamp(result.getTimestamp());
                    ded.setTestInstance(testInstance);
                    ded.setPatientId(result.stepResults.get(0).getMetadata().docEntries.get(0).patientId);
                    ded.setRegSiteSpec(registry);
                    SimulatorConfig simulatorConfig = simDb.getSimulator(oddsSimId);
                    if (simulatorConfig.get(SimulatorProperties.PERSISTENCE_OF_RETRIEVED_DOCS).asBoolean()) {
                        SimulatorConfigElement sce = simulatorConfig.get(SimulatorProperties.oddsRepositorySite);
                        if (sce!=null && sce.asList()!=null && sce.asList().size()>0) {
                            SiteSpec reposSite = new SiteSpec(sce.asList().get(0), ActorType.REPOSITORY, null);
                            ded.setReposSiteSpec(reposSite);
                        }
                    }
                    ded.setContentBundleSections(contentBundleSections);


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
                    ex.printStackTrace();
                    rs.put("error",ex.toString());
                }

            }



        }
        return rs;
    }

    static public List<DocumentEntryDetail> getOnDemandDocumentEntryDetails(SimId oddsSimId) {
        List<DocumentEntryDetail> result = null;

        try {
            SimDb simDb = new SimDb(oddsSimId);

            RepIndex repIndex = new RepIndex(simDb.getRepositoryIndexFile().toString(), oddsSimId);

            if (repIndex.getDocumentCollection() != null) {
                List<StoredDocument> documents = repIndex.getDocumentCollection().getDocuments();
                if (documents != null) {
                    result = new ArrayList<>();
                    for (StoredDocument sd : documents) {
                        result.add(sd.getEntryDetail());
                    }
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return result;
    }


    static File getDocumentFile(File testPlanFile) {

        OMElement testplanEle;
        try {
                testplanEle = Util.parse_xml(testPlanFile);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        List<OMElement> testSteps = XmlUtil.decendentsWithLocalName(testplanEle, "TestStep");
        for (OMElement testStep : testSteps) {
            String stepName = testStep.getAttributeValue(MetadataSupport.id_qname);
            OMElement documentFileEle = XmlUtil.firstDecendentWithLocalName(testStep, "Document");
            if (documentFileEle != null) {
                String documentName = documentFileEle.getText();
                return new File(testPlanFile.getParent(), documentName);
            }

        }

        return null;
    }


    /**
     * Maps Content file and if being persisted, its new snapshot UUID
     */
    static public Map<String,String> getOdContentFile(boolean persistenceOption, Session session, String username, SiteSpec repository, DocumentEntryDetail ded, SimId oddsSimId, Map<String, String> params)  {

        try {
            XdsTestServiceManager xdsTestServiceManager = new XdsTestServiceManager(session);
            TestInstance testInstance = ded.getTestInstance();
            List<String> testPlanSections = xdsTestServiceManager.getTestSections(testInstance.getId());
            String registerSection = testPlanSections.get(0); // IMPORTANT NOTE: In a Content Bundle: Make an assumption that the only (first) section is always the Register section which has the ContentBundle
            String contentBundle = testInstance.getId() + "/" + registerSection + "/" + "ContentBundle";
            List<String> contentBundleSections = xdsTestServiceManager.getTestSections(contentBundle);

            int contentBundleIdx = ded.getSupplyStateIndex();
            String section = registerSection + "/" + "ContentBundle" + "/" + contentBundleSections.get(contentBundleIdx);

            int lastBundleIdx = contentBundleSections.size() - 1;
            int nextContentIdx = (contentBundleIdx < lastBundleIdx) ? contentBundleIdx + 1 : lastBundleIdx;


//            TestLogDetails ts = xdsTestServiceManager.getTestDetails(testInstance, section);
            TestKitSearchPath searchPath = session.getTestkitSearchPath();
            TestDefinition testDefinition = searchPath.getTestDefinition(testInstance.getId());
            File testPlanFile = testDefinition.getTestplanFile(section);
            File documentFile = getDocumentFile(testPlanFile);  // IMPORTANT NOTE: In a Content Bundle: Make an assumption of only step per section
            String snapshotUniqueId = "";


            logger.info("Selecting contentBundle section: " + section + " file: " + documentFile);

            Result result = null;
            DocumentEntryDetail snapshotDed = null;
            Map<String, String> rs = new HashMap<>();

            if (persistenceOption) {
                List<String> sections = new ArrayList<String>() {
                };
                sections.add(section);

                List<Result> results = null;
                boolean stopOnFirstError = true;

                // pnr -- only happens in persistence mode and until the first time the last document in the content bundle is retrieved.
                // Example: A content bundle has 2 documents
                // ODD is the localized StoredDocument tracking detail in the ODDS repository
                // Ret 1: ODD has 0, Snapshot has 0
                // Ret 2: ODD has 1, Snapshot has -1
                // Ret 3: ODD has 1, Snapshot has -1 to indicate end of documents
                if (ded.getSnapshot() == null /* first retrieve attempt */ || ALL_OD_DOCS_SUPPLIED != ded.getSnapshot().getSupplyStateIndex() /* subsequent attempt until the last */ ) {
                    results = Transaction(repository, session.getCurrentEnvName(), username, testInstance, params, stopOnFirstError, session, xdsTestServiceManager, sections);

                    printResult(results);

                    if (results != null && results.size() > 0) {

                        result = results.get(0);
                        StepResult stepResult = null;
                        for (StepResult sr : result.getStepResults()) {
                            if (section.equals(sr.section)) {
                                stepResult = sr;
                            }
                        }

                        if (result.passed()) {
                            String repUid = stepResult.getMetadata().docEntries.get(0).repositoryUniqueId;
                            rs.put("newRepsoitoryUniqueId", repUid);

                            snapshotDed = new DocumentEntryDetail();
                            snapshotUniqueId = stepResult.getMetadata().docEntries.get(0).uniqueId;
                            snapshotDed.setUniqueId(snapshotUniqueId);
                            rs.put("snapshotUniqueId", snapshotUniqueId);
                            snapshotDed.setId(stepResult.getMetadata().docEntries.get(0).id);
                            snapshotDed.setEntryType("urn:uuid:7edca82f-054d-47f2-a032-9b2a5b5186c1");
                            snapshotDed.setTimestamp(result.getTimestamp());
                            snapshotDed.setTestInstance(testInstance);
                            snapshotDed.setPatientId(stepResult.getMetadata().docEntries.get(0).patientId);
                            repository.homeId = repUid;
                            snapshotDed.setReposSiteSpec(repository);

                            if (contentBundleIdx == nextContentIdx)
                                snapshotDed.setSupplyStateIndex(ALL_OD_DOCS_SUPPLIED);
                            else
                                snapshotDed.setSupplyStateIndex(contentBundleIdx);

                            SimDb simDb = new SimDb(oddsSimId);
                            RepIndex repIndex = new RepIndex(simDb.getRepositoryIndexFile().toString(), oddsSimId);
                            StoredDocument sd = repIndex.getDocumentCollection().getStoredDocument(ded.getUniqueId());
                            if (sd!=null) {
                                sd.getEntryDetail().setSupplyStateIndex(nextContentIdx);
                                sd.getEntryDetail().setSnapshot(snapshotDed);
                                repIndex.getDocumentCollection().update(sd);
                                repIndex.save();
                            } else {
                                logger.error("PersistenceOption Error: SD is null! Id:" + ded.getUniqueId());
                            }

                        }

                    }
                } else if (ded.getSnapshot() != null && ALL_OD_DOCS_SUPPLIED == ded.getSnapshot().getSupplyStateIndex()) { // ALL OD documents already supplied
                    rs.put("newRepsoitoryUniqueId", ded.getSnapshot().getReposSiteSpec().homeId);
                    rs.put("snapshotUniqueId", ded.getSnapshot().getUniqueId());
                }


            } else {
                // Non-Persistence option

                SimDb simDb = new SimDb(oddsSimId);
                RepIndex repIndex = new RepIndex(simDb.getRepositoryIndexFile().toString(), oddsSimId);
                StoredDocument sd = repIndex.getDocumentCollection().getStoredDocument(ded.getUniqueId());

                if (sd != null) {
                    sd.getEntryDetail().setSupplyStateIndex(nextContentIdx);
                    repIndex.getDocumentCollection().update(sd);
                    repIndex.save();
                } else {
                    logger.error("SD is null! Id:" + ded.getUniqueId());
                }


            }

            rs.put("file", documentFile.toString());
            rs.put("mimeType", "text/plain");

            return rs;

            } catch(Exception ex){
                ex.printStackTrace();
                return null;
            }

        }

}
