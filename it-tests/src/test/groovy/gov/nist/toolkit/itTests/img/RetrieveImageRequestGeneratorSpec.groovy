package gov.nist.toolkit.itTests.img

import edu.wustl.mir.erl.ihe.xdsi.validation.CAT
import gov.nist.toolkit.registrymsg.repository.RetrieveImageRequestGenerator
import gov.nist.toolkit.registrymsg.repository.RetrieveImageRequestModel
import gov.nist.toolkit.utilities.xml.XmlUtil
import gov.nist.toolkit.xdsexception.client.XdsInternalException
import org.apache.axiom.om.OMElement
import org.apache.commons.lang3.StringUtils;
import spock.lang.Shared
import spock.lang.Specification

import javax.xml.namespace.QName

/**
 * Created by rmoult01 on 3/3/17.
 */
class RetrieveImageRequestGeneratorSpec extends Specification {
    
    @Shared boolean failed;

    def 'Retrieve Image Request Generator test - ' () {
        println(testDesc)
        failed = false;

        given: 'A RetrieveImageRequestModel'
        RetrieveImageRequestModel requestModel = RetrieveImageRequestModel.buildModel(compositeIds, xferSyntaxUids)

        and: 'A std SOAP message'
        OMElement std = XmlUtil.strToOM(requestString)

        when: 'A SOAP message is generated'
        RetrieveImageRequestGenerator gen = new RetrieveImageRequestGenerator(requestModel)
        OMElement test = gen.get()

        and: 'They are compared'
        prsSameReqImgs(test, std)

        then: 'they should compare OK'
        failed == false
        
        where: 'Tests to run'
        testDesc |  xferSyntaxUids | compositeIds | requestString
        'Simple Test' | 
        ['1.2.840.10008.1.2.1'] |
        ['1.3.6.1.4.1.21367.201599.1.201604020954048,1.3.6.1.4.1.21367.201599.2.201604020954048,1.3.6.1.4.1.21367.201599.3.201604020954048.1,urn:oid:1.3.6.1.4.1.21367.13.70.200,1.3.6.1.4.1.21367.13.80.110'] |
                $/\
              <xdsiB:RetrieveImagingDocumentSetRequest xmlns:xdsiB="urn:ihe:rad:xdsi-b:2009">
                 <xdsiB:StudyRequest studyInstanceUID="1.3.6.1.4.1.21367.201599.1.201604020954048">
                    <xdsiB:SeriesRequest seriesInstanceUID="1.3.6.1.4.1.21367.201599.2.201604020954048">
                       <xdsb:DocumentRequest xmlns:xdsb="urn:ihe:iti:xds-b:2007">
                          <xdsb:HomeCommunityId>urn:oid:1.3.6.1.4.1.21367.13.70.200</xdsb:HomeCommunityId>
                          <xdsb:RepositoryUniqueId>1.3.6.1.4.1.21367.13.80.110</xdsb:RepositoryUniqueId>
                          <xdsb:DocumentUniqueId>1.3.6.1.4.1.21367.201599.3.201604020954048.1</xdsb:DocumentUniqueId>
                       </xdsb:DocumentRequest>
                    </xdsiB:SeriesRequest>
                 </xdsiB:StudyRequest>
                 <xdsiB:TransferSyntaxUIDList>
                    <xdsiB:TransferSyntaxUID>1.2.840.10008.1.2.1</xdsiB:TransferSyntaxUID>
                 </xdsiB:TransferSyntaxUIDList>
              </xdsiB:RetrieveImagingDocumentSetRequest>/$
    }

    private void prsSameReqImgs(OMElement test, OMElement std) {
        try {
            // Get gold standard request, validate
            
            Map <String, ReqImg> testImgs = loadReqImgs(test);
            Map <String, ReqImg> stdImgs = loadReqImgs(std);
            // load xfer syntax data for test/std
            Set <String> testSyntaxes = loadXferSyntaxes(test);
            Set <String> stdSyntaxes = loadXferSyntaxes(std);
            // pass test request docs against std
            Set <String> testKeys = testImgs.keySet();
            for (String testKey : testKeys) {
                if (stdImgs.containsKey(testKey) == false) {
                    store(CAT.ERROR, "test doc UID " + testKey + ", not found in standard.");
                    continue;
                }
                ReqImg testImg = testImgs.get(testKey);
                ReqImg stdImg = stdImgs.get(testKey);
                stdImgs.remove(testKey);
                // found and everything matches
                if (comp(stdImg, testImg)) {
                    store(CAT.SUCCESS, "test doc UID " + testKey + ", all values match.");
                    continue;
                }
                // found, but not everything matches
                store(CAT.SUCCESS, "test doc UID " + testKey + ", found in std.");
                if (!comp(stdImg.studyUID, testImg.studyUID)) store(CAT.ERROR, "for doc with UID: " + testKey
                        + " studyUID mismatch (std/test): (" + stdImg.studyUID + "/" + testImg.studyUID + ")");
                if (!comp(stdImg.seriesUID, testImg.seriesUID)) store(CAT.ERROR, "for doc with UID: " + testKey
                        + " seriesUID mismatch (std/test): (" + stdImg.seriesUID + "/" + testImg.seriesUID + ")");
                if (!comp(stdImg.homeCommunityUID, testImg.homeCommunityUID))
                    store(CAT.ERROR, "for doc with UID: " + testKey + " homeCommunityID mismatch (std/test): ("
                            + stdImg.homeCommunityUID + "/" + testImg.homeCommunityUID + ")");
                if (!comp(stdImg.repositoryUID, testImg.repositoryUID))
                    store(CAT.ERROR, "for doc with UID: " + testKey + " RepositoryUniqueID mismatch (std/test): ("
                            + stdImg.repositoryUID + "/" + testImg.repositoryUID + ")");
            }
            // list any std docs which weren't in test
            for (String key : stdImgs.keySet())
                store(CAT.ERROR, "std doc UID: " + key + " not found in test msg.");
            // match xfer syntaxes
            boolean errorFound = false;
            for (String syntax : testSyntaxes) {
                if (stdSyntaxes.contains(syntax)) {
                    stdSyntaxes.remove(syntax);
                    continue;
                }
                store(CAT.ERROR, "transfer syntax " + syntax + "in test, not found in standard.");
                errorFound = true;
            }
            for (String syntax : stdSyntaxes) {
                store(CAT.ERROR, "transfer syntax " + syntax + "in standard, not found in test.");
                errorFound = true;
            }
            if (!errorFound) store(CAT.SUCCESS, "transfer syntax lists match.");
        } catch (Exception e) {
            throw new XdsInternalException("sameRetImgs error: " + e.getMessage());
        }
    }
    private Map <String, ReqImg> loadReqImgs(OMElement msg) {
        Map <String, ReqImg> imgs = new LinkedHashMap <>();
        for (OMElement study : XmlUtil.decendentsWithLocalName(msg, "StudyRequest")) {
            String studyUID = study.getAttributeValue(new QName("studyInstanceUID"));
            for (OMElement series : XmlUtil.decendentsWithLocalName(study, "SeriesRequest")) {
                String seriesUID = series.getAttributeValue(new QName("seriesInstanceUID"));
                for (OMElement doc : XmlUtil.decendentsWithLocalName(series, "DocumentRequest")) {
                    String docUID = loadTxt(doc, "DocumentUniqueId");
                    if (StringUtils.isBlank(docUID)) {
                        String em = "Doc request with no UID. study=" + studyUID + ", series=" + seriesUID;
                        store(CAT.ERROR, em);
                        continue;
                    }
                    ReqImg reqImg = new ReqImg();
                    reqImg.studyUID = studyUID;
                    reqImg.seriesUID = seriesUID;
                    reqImg.homeCommunityUID = loadTxt(doc, "HomeCommunityId");
                    reqImg.repositoryUID = loadTxt(doc, "RepositoryUniqueId");
                    imgs.put(docUID, reqImg);
                }
            }
        }
        return imgs;
    }

    private Set <String> loadXferSyntaxes(OMElement msg) {
        Set <String> syntaxes = new HashSet <>();
        try {
            OMElement sList = XmlUtil.onlyChildWithLocalName(msg, "TransferSyntaxUIDList");
            for (OMElement s : XmlUtil.childrenWithLocalName(sList, "TransferSyntaxUID")) {
                String x = s.getText().trim();
                if (StringUtils.isNotBlank(x)) syntaxes.add(x);
            }
        } catch (Exception e) {
            store(CAT.ERROR, "no TransferSyntaxUIDList element found.");
        }
        return syntaxes;
    }

    class ReqImg {
        String studyUID;
        String seriesUID;
        String homeCommunityUID;
        String repositoryUID;
    }

    private boolean comp(ReqImg std, ReqImg test) {
        return comp(std.studyUID, test.studyUID) &&
               comp(std.seriesUID, test.seriesUID) &&
               comp(std.homeCommunityUID, test.homeCommunityUID) &&
               comp(std.repositoryUID, test.repositoryUID);
    }

    /*
  * helper method for string compares between std and test where an empty or
  * null std value means the value is not required. Used for home and
  * repository UIDs.
  */
    private boolean comp(String std, String test) {
        if (std == null || std.length() == 0) return true;
        if (test == null || test.length() == 0) return false;
        return std.equals(test);
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

    private void store(CAT cat, String msg) {
        if (cat != CAT.ERROR) return;
        failed = true;
        println("Error " + msg)
    }

}
