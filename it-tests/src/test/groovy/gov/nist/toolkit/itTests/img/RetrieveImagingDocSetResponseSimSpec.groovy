package gov.nist.toolkit.itTests.img

import gov.nist.toolkit.commondatatypes.MetadataSupport
import gov.nist.toolkit.errorrecording.ErrorRecorder
import gov.nist.toolkit.errorrecording.GwtErrorRecorder
import gov.nist.toolkit.itTests.support.ToolkitSpecification
import gov.nist.toolkit.simcommon.client.SimulatorConfig
import gov.nist.toolkit.simcommon.server.SimDb
import gov.nist.toolkit.simulators.sim.ids.RetrieveImagingDocSetResponseSim
import gov.nist.toolkit.simulators.support.DsSimCommon
import gov.nist.toolkit.simulators.support.SimCommon
import gov.nist.toolkit.simulators.support.StoredDocument
import gov.nist.toolkit.utilities.xml.XmlUtil
import gov.nist.toolkit.valregmsg.registry.RetrieveMultipleResponse
import gov.nist.toolkit.valsupport.client.ValidationContext
import org.apache.axiom.om.OMElement
import org.javatuples.Pair

/**
 * Created by rmoult01 on 3/14/17.
 */
class RetrieveImagingDocSetResponseSimSpec extends ToolkitSpecification{

    String repositoryUniqueId = '1.3.6.1.4.1.21367.13.80.110'
    static String success = MetadataSupport.status_success
    static String partial = MetadataSupport.status_partial_success
    static String failure = MetadataSupport.status_failure

    def 'test Response message creation' () {
        println(testName)

        given: 'StoredDocuments which match the document UIDs'
        List<StoredDocument> storedDocuments = new ArrayList<>()
        Map<String, StoredDocument> storedDocumentMap = new HashMap<>()
        for (Pair<String, String> imagingdocumentUid : imagingDocumentUids) {
            String compUid = imagingdocumentUid.getValue0()
            String[] uids = compUid.split(':')
            StoredDocument storedDocument = new StoredDocument()
            storedDocument.uid = uids[2]
            storedDocument.mimeType = 'application/dicom'
            storedDocument.charset = "UTF-8"
            storedDocument.content = "Stored document".bytes
            storedDocuments.add(storedDocument)
            storedDocumentMap.put(compUid, storedDocument)
        }

        when: 'A RetrieveImagingDocSetResponseSim instance is created'

        SimDb db = Mock()
        ValidationContext vc = Mock()
        SimulatorConfig simConfig = Mock()
        SimCommon common = Mock(SimCommon, constructorArgs: [db, simConfig, false, vc, null, null, null])

        DsSimCommon dsSimCommon = Stub(DsSimCommon, constructorArgs: [common, null, null])
        dsSimCommon.getAttachments() >> storedDocuments
        dsSimCommon.getStoredImagingDocument(_, _) >> {String key, List<String> xfers -> storedDocumentMap.get(key) }

        RetrieveImagingDocSetResponseSim retriveImagingDocSetResponseSim =
                new RetrieveImagingDocSetResponseSim(null, imagingDocumentUids,
                    transferSyntaxUids, common, dsSimCommon, repositoryUniqueId)

        then: 'An object is returned'
        retriveImagingDocSetResponseSim

        when: 'it is run'
        ErrorRecorder er = new GwtErrorRecorder()
        retriveImagingDocSetResponseSim.run(er, null)
        RetrieveMultipleResponse response = retriveImagingDocSetResponseSim.response
        org.apache.axiom.om.OMElement respMsg = response.root

        then: 'There is a response message'
        respMsg

        and: 'The status is correct'
        response.forcedStatus == status

        and: 'Everything is found'
        checkResponseMessage(respMsg, imagingDocumentUids)

        where: 'tests to run'
        testName | imagingDocumentUids | transferSyntaxUids || status
        'Simple Test' |
        [new Pair<String, String>('1.3.6.1.4.1.21367.201599.1.201604020954048:1.3.6.1.4.1.21367.201599.2.201604020954048:1.3.6.1.4.1.21367.201599.3.201604020954048.1', 'urn:oid:1.3.6.1.4.1.21367.13.70.200')] |
        ['1.2.840.10008.1.2.1'] |
        success
    }

    private boolean checkResponseMessage(OMElement root, List<Pair<String, String>> uids) {
        String p1 = "Retrieve Doc Set Response "
        boolean ok = true
        String t = root.getLocalName();
        if (!t.endsWith("RetrieveDocumentSetResponse")) {
            ok = false
            print(p1 + "expected root name RetrieveDocumentSetResponse, found [$t]")
        }
        List <OMElement> docs = XmlUtil.decendentsWithLocalName(root, "DocumentResponse");
        for (OMElement docReq : docs) {
            String instance = loadTxt(docReq, "DocumentUniqueId");
            String home = loadTxt(docReq, "HomeCommunityId");
            String repo = loadTxt(docReq, "RepositoryUniqueId");
            String mime = loadTxt(docReq, "mimeType");
            String pairInstance = null
            String pairHome = null
            Pair<String, String> p
            for (Pair<String, String> pair : uids) {
                p = pair
                String pi = pair.value0.split(":").last()
                if (pi == instance) {
                    pairInstance = pi
                    pairHome = pair.value1
                    break
                }
            }
            if (pairInstance == null) {
                println(p1 + "found unknown doc [$instance]")
                ok = false
                continue
            }
            if (home != pairHome) {
                println("$p1 document $instance home community id. Expected [$pairHome], found [$home]")
                ok = false
            }
            if (repo != repositoryUniqueId) {
                println("$p1 document $instance repository id. Expected [$repositoryUniqueId], found [$repo]")
                ok = false
            }
            if (mime != "application/dicom") {
                println("$p1 document $instance mime type. Expected [application/dicom], found [$mime]")

            }
            uids.remove(p)
        }
        for (Pair<String, String> pair : uids) {
            String pi = pair.value0.split(":").last()
            print("$p1 document $pi expected, not found")
            ok = false
        }
        ok
    }
    /*
   * helper for loadImgs. Gets text content of child tagName of element e.
   * Returns null if no such child or more than one such child.
   */
    private String loadTxt(OMElement e, String tagName) {
        try {
            OMElement child = XmlUtil.onlyChildWithLocalName(e, tagName);
            return child.getText().trim();
        } catch (Exception e1) {}
        return null;
    }
    private String last(String str) {
        return str.split(":").last()
    }

}
