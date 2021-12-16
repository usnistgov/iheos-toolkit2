package gov.nist.toolkit.xdstools2.server;

import gov.nist.toolkit.common.datatypes.Hl7Date;
import gov.nist.toolkit.commondatatypes.MetadataSupport;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrymetadata.MetadataParser;
import gov.nist.toolkit.registrymetadata.client.AnyId;
import gov.nist.toolkit.registrymetadata.client.AnyIds;
import gov.nist.toolkit.registrymetadata.client.Association;
import gov.nist.toolkit.registrymetadata.client.Difference;
import gov.nist.toolkit.registrymetadata.client.DocumentEntry;
import gov.nist.toolkit.registrymetadata.client.DocumentEntryDiff;
import gov.nist.toolkit.registrymetadata.client.MetadataCollection;
import gov.nist.toolkit.registrymetadata.client.SubmissionSet;
import gov.nist.toolkit.results.MetadataToMetadataCollectionParser;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.testengine.engine.Linkage;
import gov.nist.toolkit.testengine.engine.SourceIdAllocator;
import gov.nist.toolkit.testengine.engine.TestConfig;
import gov.nist.toolkit.testengine.engine.UniqueIdAllocator;
import gov.nist.toolkit.testenginelogging.client.LogMapDTO;
import gov.nist.toolkit.testenginelogging.client.TestStepLogContentDTO;
import gov.nist.toolkit.testenginelogging.logrepository.LogRepository;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.client.NoDifferencesException;
import gov.nist.toolkit.xdsexception.client.ToolkitRuntimeException;
import gov.nist.toolkit.xdstools2.shared.command.request.GetSubmissionSetsRequest;
import gov.nist.toolkit.xdstools2.shared.command.request.UpdateDocumentEntryRequest;
import org.apache.axiom.om.OMElement;
import java.util.logging.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetadataUpdate {
    Session session;
    static Logger logger = Logger.getLogger(MetadataUpdate.class.getName());

    public MetadataUpdate(Session session) {
        this.session = session;
    }

    public Result updateDocumentEntry(UpdateDocumentEntryRequest request) throws Exception {
        // 1. get Mc and M from request.originalGetDcocs
        // 2. compare Mc with Mc' where Mc' is request.toBeUpdatedMc
        //   Skip. replace M with differences from Mc and Mc'. This method is not necessary.
        // 3. Translate entire Mc' to new M', warn when there are no changes.
        // 4. run test plan (needs to be created)
        // 5. return test logs
        // 6. Client: wire the run command (needs to be created)
        // 7. Client: add new UI controls to allow 0-1-many value list



        TestInstance originalQueryTi = request.getOriginalQueryTestInstance();

        LogMapDTO logMapDTO;
        try {
            logMapDTO = LogRepository.logIn(originalQueryTi);
        } catch (Exception e) {
            String message = "Logs not available for " + originalQueryTi;
            logger.severe(ExceptionUtil.exception_details(e, message));
            throw new ToolkitRuntimeException(message, e);
        }

        /*
        TestLogs testLogs = null;
        try {
            testLogs = TestLogsBuilder.build(logMapDTO);
            testLogs.testInstance = originalGetDocsTi;
        } catch (Exception e) {
            String details = ExceptionUtil.exception_details(e);
            logger.severe(details);
            throw new ToolkitRuntimeException(e);
        }
        */

        if (!logMapDTO.getItems().get(0).getLog().isSuccess())
            throw new ToolkitRuntimeException("originalQueryTi was not successfully run.");

//        if (request.getLogEntryindex()>testLogs.size()-1)
//            throw new ToolkitRuntimeException("originalGetDocsTi testLogs index mismatched.");

        // transform original query log to M
        Metadata m = null;
        try {

            for (String key : logMapDTO.getKeys()) {
                if (key.equals(request.getQueryOrigin().getTestInstance().getId() + "/" +request.getQueryOrigin().getSectionName())) {
                    int idx = logMapDTO.getKeys().indexOf(key);
                    TestStepLogContentDTO step = logMapDTO.getItems().get(idx).getLog().getStep(request.getQueryOrigin().getStepName());
                    m = MetadataParser.parseNonSubmission(step.getRootString());
                    break;
                }
            }
            if (m==null) {
                throw new ToolkitRuntimeException("Was expecting " + request.getQueryOrigin().getTestInstance().getId() + ". Unsupported query: " + logMapDTO.getItems().get(0).getTestName() + ". Log fetch failed for QueryOrigin: " + request.getQueryOrigin().toString());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new ToolkitRuntimeException(e);
        }

        // compare

        MetadataCollection mcOrig = MetadataToMetadataCollectionParser.buildMetadataCollection(m, "de");
        DocumentEntryDiff diff = new DocumentEntryDiff(true);
        DocumentEntry deOrig = null;
        if (mcOrig.docEntries.size()>0) {
            for (DocumentEntry de : mcOrig.docEntries) {
                if (de.id.equals(request.getToBeUpdated().id)) {
                    deOrig = de;
                }
            }
            if (! request.isNoCompare()) {
                List<Difference> diffs = diff.compare(deOrig, request.getToBeUpdated());

                if (diffs.size()==0) {
                    // No differences
                    throw new NoDifferencesException(deOrig.id);
                } else {
//                    for (Difference d : diffs) {
//                        logger.info("Found difference: " + d.getMetadataAttributeName());
//                    }
                }
            }

        } else {
            throw new ToolkitRuntimeException("No documentEntries in log.xml. EventDir: " + request.getOriginalQueryTestInstance().getEventDir());
        }

        MetadataCollection finalMc = new MetadataCollection();
        SubmissionSet ss = null;
        // get submission set and association if it is not in the metadatacollection that was passed into the request object
        if (request.getMc().submissionSets==null ||  (request.getMc().submissionSets!=null && request.getMc().submissionSets.isEmpty())) {
            AnyIds ids = new AnyIds(new AnyId(deOrig.id));
            GetSubmissionSetsRequest getSsRequest = new GetSubmissionSetsRequest(request, request.getSiteSpec(), ids);
            List<Result> results = getSubmissionSets(getSsRequest);
            if (results!=null && !results.isEmpty()) {
                Result getSsResult = results.get(0);
                LogMapDTO getSslogMapDTO;
                try {
                    getSslogMapDTO = LogRepository.logIn(getSsResult.logId);
                } catch (Exception e) {
                    String message = "Logs not available for " + getSsResult.logId;
                    logger.severe(ExceptionUtil.exception_details(e,message));
                    throw new ToolkitRuntimeException(message, e);
                }

                Metadata getSsM = null;
                try {
                    if (getSslogMapDTO.getItems().get(0).getTestName().equals("GetSubmissionSets/XDS")) {
                        getSsM = MetadataParser.parseNonSubmission(getSslogMapDTO.getItems().get(0).getLog().getStep("GetSubmissionSetsByUUID").getRootString());
                    } else
                        throw new ToolkitRuntimeException("Was expecting GetSubmissionSets/XDS. Unsupported query: " + getSslogMapDTO.getItems().get(0).getTestName());
                }
                catch (Exception e) {
                    e.printStackTrace();
                    throw new ToolkitRuntimeException(e);
                }

                ss = MetadataToMetadataCollectionParser.buildMetadataCollection(getSsM, "ss").submissionSets.get(0);

            } else {
                throw new ToolkitRuntimeException("GetSubmissionSet returned a null result.");
            }
        } else {
            ss = request.getMc().submissionSets.get(0);
        }


        UniqueIdAllocator allocator = UniqueIdAllocator.getInstance();
        ss.uniqueId = allocator.allocate();

        try {
            ss.sourceId = new SourceIdAllocator(TestConfig.testConfigWithTestMgmt()).allocate();
        } catch (Exception ex) {
            ss.sourceId = "1.3.6.1.4.1.21367.4";
        }
        ss.submissionTime = new Hl7Date().now();

        /* This is needed because the linkage string replacer will replace all attributes
        including the lid to the same value as new Document id so the original lid is lost.
        Temporarily change this lid and restore it to the original value through another string replace. */
        String tempLid = "$templid01$";
        request.getToBeUpdated().lid = "$templid01$";
        finalMc.docEntries.add(request.getToBeUpdated());
        finalMc.submissionSets.add(ss);

        String newDeId = "Document01";
        Association assoc = new Association();
        assoc.id = "Association01";

        String newSsId = "SubmissionSet01";
        assoc.source = newSsId;
        assoc.type = "HasMember";
        assoc.target = newDeId;
        assoc.previousVersion = deOrig.version;
        assoc.ssStatus = "Original";

        finalMc.assocs.add(assoc);

        Metadata m2 = MetadataCollectionToMetadata.buildMetadata(finalMc, true);
        List<OMElement> eles = m2.getV3();

        // Need to replace ids in the finalMc
        Linkage linkage = new Linkage(null);
        if (eles.size()==3) {
            // DocumentEntry
            linkage.replace_string_in_text_and_attributes(eles.get(0), request.getToBeUpdated().id, newDeId);
            linkage.replace_string_in_text_and_attributes(eles.get(0), tempLid, deOrig.lid);
            // SS
            linkage.replace_string_in_text_and_attributes(eles.get(1), ss.id, newSsId);
            // Assocs
            // Should be ok since we are adding a new one which already has a symbolic id
        } else {
            throw new ToolkitRuntimeException("MU error: Expecting 3 elements, found: " + eles.size());
        }
/*
        <lcm:SubmitObjectsRequest xmlns:lcm="urn:oasis:names:tc:ebxml-regrep:xsd:lcm:3.0">
    <rim:RegistryObjectList xmlns:rim="urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0">
    </rim:RegistryObjectList>
</lcm:SubmitObjectsRequest>
*/

        OMElement registryObjectList = MetadataSupport.om_factory.createOMElement("RegistryObjectList", MetadataSupport.ebRIMns3);
        for (OMElement e : eles) {
            registryObjectList.addChild(e);
        }

        OMElement submitObjectsRequest = MetadataSupport.om_factory.createOMElement("SubmitObjectsRequest", MetadataSupport.ebLcm3);
        submitObjectsRequest.addChild(registryObjectList);

        // Run the test plan with dynamic metadata content as a param
//        StringBuilder metadataSb = new StringBuilder();
//        for (OMElement e : eles) {
//            metadataSb.append(new OMFormatter(e).toString());
//        }

        // begin debug
//        File outFile = new File("/home/skb1/tmpTest/out.xml");
//        try {
//            Io.stringToFile(outFile, metadataSb.toString());
//        } catch (IOException e1) {
//            e1.printStackTrace();
//        }
        // end debug

        TestInstance testInstance = new TestInstance("MU-Simple",request.getTestSession());
//
//        TestKitSearchPath searchPath = session().getTestkitSearchPath();
//        logger.info(searchPath.toString());
//        TestKit testKit = searchPath.getTestKitForTest(testInstance.getId());
//        TestDefinition testDef = testKit.getTestDef(testInstance.getId());
//
//        List<String> sections = testDef.getSectionIndex();
//        for (String s : sections) {
//            if (s.equals("update")) {
//
//            }
//        }


        List<String> sections = new ArrayList<>();
        Map<String, String> params = new HashMap<>();
        Map<String, Object> params2 = new HashMap<>();
//        params.put("$myfile$","/home/skb1/tmpTest/out.xml");
//        params.put("$metadata_update$", metadataSb.toString());
        params2.put("MUObject", submitObjectsRequest);
        Result updateResult = session.xdsTestServiceManager().xdstest(
                testInstance
                , sections
                , params
                , params2
                , null
                ,true);



        return updateResult;
    }

    private List<Result> getSubmissionSets(GetSubmissionSetsRequest request) {
        return session.queryServiceManager().getSubmissionSets(request.getSite(), request.getIds());
    }
}
