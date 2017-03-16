package gov.nist.toolkit.itTests.img

import gov.nist.toolkit.registrymsg.repository.RetImgDocSetReqParser
import gov.nist.toolkit.registrymsg.repository.RetrieveImageRequestModel
import gov.nist.toolkit.utilities.xml.XmlUtil
import org.apache.axiom.om.OMElement
import spock.lang.Specification

/**
 * Created by rmoult01 on 3/3/17.
 */
class RetImgDocSetReqParserSpec extends Specification {

    def 'Retrieve Image Document Set Request Parser Test -' () {
        println(testDesc)

        when: 'The test SOAP message is parsed'

        OMElement requestElement = XmlUtil.strToOM(requestString)

        then: 'There is an Element'
        requestElement

        when: 'the RetImgDocSetReqParser is instantiated'
        RetImgDocSetReqParser requestParser = new RetImgDocSetReqParser(requestElement);

        then: 'An object is created'
        requestParser

        when: 'the RetrieveImageRequestModel instance is retrieved'
        RetrieveImageRequestModel requestModel = requestParser.getRequest();

        // Steps used when testing ability to extract model for particular RIG or IDS
        if (!forHome.equalsIgnoreCase('all'))
            requestModel = requestModel.getModelForCommunity(forHome)
        if (!forIds.equalsIgnoreCase('all'))
            requestModel = requestModel.getModelForRepository(forIds)

        then: 'A request model instance is generated'
        requestModel

        then: 'The Transfer Syntax UIDs are correct'
        def xsul = requestModel.getTransferSyntaxUIDs()
        xsul
        xsul.size() == xferSyntaxUids.size()
        Collections.sort(xsul)
        xsul == xferSyntaxUids

        then: 'Home Community IDs are correct'
        def hcid = requestModel.getHomeCommunityIds()
        hcid
        hcid.size() == homeCommunityIds.size()
        hcid == homeCommunityIds

        then: "Repository UIDs are correct"
        def rid = requestModel.getIDSRepositoryUniqueIds()
        rid
        rid.size() == repositoryIds.size()
        rid == repositoryIds

        then: "Included image data is correct"
        def imgData = requestModel.getCompositeUids(true, true)
        imgData
        imgData.size() == compositeIds.size()
        imgData == compositeIds

        where: 'Tests to run'

        testDesc | requestString | forHome | forIds || xferSyntaxUids | homeCommunityIds  | repositoryIds | compositeIds
        'Request 1 object from 1 study from 1 repository' |
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
              </xdsiB:RetrieveImagingDocumentSetRequest>/$ |
                'all' | 'all' |
                ['1.2.840.10008.1.2.1'] |
                ['urn:oid:1.3.6.1.4.1.21367.13.70.200'] as Set |
                ['1.3.6.1.4.1.21367.13.80.110'] as Set |
                ['1.3.6.1.4.1.21367.201599.1.201604020954048:1.3.6.1.4.1.21367.201599.2.201604020954048:1.3.6.1.4.1.21367.201599.3.201604020954048.1:urn:oid:1.3.6.1.4.1.21367.13.70.200:1.3.6.1.4.1.21367.13.80.110']
        'Request 2 objects from each of 2 studies from 1 repository.' |
                $/\
              <xdsiB:RetrieveImagingDocumentSetRequest xmlns:xdsiB="urn:ihe:rad:xdsi-b:2009">
                 <xdsiB:StudyRequest studyInstanceUID="1.3.6.1.4.1.21367.201599.1.201604020956049">
                    <xdsiB:SeriesRequest seriesInstanceUID="1.3.6.1.4.1.21367.201599.2.201604020956049.11">
                       <xdsb:DocumentRequest xmlns:xdsb="urn:ihe:iti:xds-b:2007">
                          <xdsb:HomeCommunityId>urn:oid:1.3.6.1.4.1.21367.13.70.200</xdsb:HomeCommunityId>
                          <xdsb:RepositoryUniqueId>1.3.6.1.4.1.21367.13.80.110</xdsb:RepositoryUniqueId>
                          <xdsb:DocumentUniqueId>1.3.6.1.4.1.21367.201599.3.201604020956049.12</xdsb:DocumentUniqueId>
                       </xdsb:DocumentRequest>
                       <xdsb:DocumentRequest xmlns:xdsb="urn:ihe:iti:xds-b:2007">
                          <xdsb:HomeCommunityId>urn:oid:1.3.6.1.4.1.21367.13.70.200</xdsb:HomeCommunityId>
                          <xdsb:RepositoryUniqueId>1.3.6.1.4.1.21367.13.80.110</xdsb:RepositoryUniqueId>
                          <xdsb:DocumentUniqueId>1.3.6.1.4.1.21367.201599.3.201604020956049.13</xdsb:DocumentUniqueId>
                       </xdsb:DocumentRequest>
                    </xdsiB:SeriesRequest>
                 </xdsiB:StudyRequest>
                 <xdsiB:StudyRequest studyInstanceUID="1.3.6.1.4.1.21367.201599.1.201604020954059">
                    <xdsiB:SeriesRequest seriesInstanceUID="1.3.6.1.4.1.21367.201599.2.201604020954059.21">
                       <xdsb:DocumentRequest xmlns:xdsb="urn:ihe:iti:xds-b:2007">
                          <xdsb:HomeCommunityId>urn:oid:1.3.6.1.4.1.21367.13.70.200</xdsb:HomeCommunityId>
                          <xdsb:RepositoryUniqueId>1.3.6.1.4.1.21367.13.80.110</xdsb:RepositoryUniqueId>
                          <xdsb:DocumentUniqueId>1.3.6.1.4.1.21367.201599.3.201604020954059.22</xdsb:DocumentUniqueId>
                       </xdsb:DocumentRequest>
                       <xdsb:DocumentRequest xmlns:xdsb="urn:ihe:iti:xds-b:2007">
                          <xdsb:HomeCommunityId>urn:oid:1.3.6.1.4.1.21367.13.70.200</xdsb:HomeCommunityId>
                          <xdsb:RepositoryUniqueId>1.3.6.1.4.1.21367.13.80.110</xdsb:RepositoryUniqueId>
                          <xdsb:DocumentUniqueId>1.3.6.1.4.1.21367.201599.3.201604020954059.23</xdsb:DocumentUniqueId>
                       </xdsb:DocumentRequest>
                    </xdsiB:SeriesRequest>
                 </xdsiB:StudyRequest>
                 <xdsiB:TransferSyntaxUIDList>
                    <xdsiB:TransferSyntaxUID>1.2.840.10008.1.2.1</xdsiB:TransferSyntaxUID>
                 </xdsiB:TransferSyntaxUIDList>
              </xdsiB:RetrieveImagingDocumentSetRequest>/$ |
                'all' | 'all' |
                ['1.2.840.10008.1.2.1'] |
                ['urn:oid:1.3.6.1.4.1.21367.13.70.200'] as Set |
                ['1.3.6.1.4.1.21367.13.80.110'] as Set |
                ['1.3.6.1.4.1.21367.201599.1.201604020956049:1.3.6.1.4.1.21367.201599.2.201604020956049.11:1.3.6.1.4.1.21367.201599.3.201604020956049.12:urn:oid:1.3.6.1.4.1.21367.13.70.200:1.3.6.1.4.1.21367.13.80.110',
                 '1.3.6.1.4.1.21367.201599.1.201604020956049:1.3.6.1.4.1.21367.201599.2.201604020956049.11:1.3.6.1.4.1.21367.201599.3.201604020956049.13:urn:oid:1.3.6.1.4.1.21367.13.70.200:1.3.6.1.4.1.21367.13.80.110',
                 '1.3.6.1.4.1.21367.201599.1.201604020954059:1.3.6.1.4.1.21367.201599.2.201604020954059.21:1.3.6.1.4.1.21367.201599.3.201604020954059.22:urn:oid:1.3.6.1.4.1.21367.13.70.200:1.3.6.1.4.1.21367.13.80.110',
                 '1.3.6.1.4.1.21367.201599.1.201604020954059:1.3.6.1.4.1.21367.201599.2.201604020954059.21:1.3.6.1.4.1.21367.201599.3.201604020954059.23:urn:oid:1.3.6.1.4.1.21367.13.70.200:1.3.6.1.4.1.21367.13.80.110']
        'Request 1 object from 1 study from 2 different repositories.' |
                $/\
              <xdsiB:RetrieveImagingDocumentSetRequest xmlns:xdsiB="urn:ihe:rad:xdsi-b:2009">
                 <xdsiB:StudyRequest studyInstanceUID="1.3.6.1.4.1.21367.201599.1.201604020956049">
                    <xdsiB:SeriesRequest seriesInstanceUID="1.3.6.1.4.1.21367.201599.2.201604020956049.11">
                       <xdsb:DocumentRequest xmlns:xdsb="urn:ihe:iti:xds-b:2007">
                          <xdsb:HomeCommunityId>urn:oid:1.3.6.1.4.1.21367.13.70.200</xdsb:HomeCommunityId>
                          <xdsb:RepositoryUniqueId>1.3.6.1.4.1.21367.13.80.110.1</xdsb:RepositoryUniqueId>
                          <xdsb:DocumentUniqueId>1.3.6.1.4.1.21367.201599.3.201604020956049.12</xdsb:DocumentUniqueId>
                       </xdsb:DocumentRequest>
                    </xdsiB:SeriesRequest>
                 </xdsiB:StudyRequest>
                 <xdsiB:StudyRequest studyInstanceUID="1.3.6.1.4.1.21367.201599.1.201604020954059">
                    <xdsiB:SeriesRequest seriesInstanceUID="1.3.6.1.4.1.21367.201599.2.201604020954059.21">
                       <xdsb:DocumentRequest xmlns:xdsb="urn:ihe:iti:xds-b:2007">
                          <xdsb:HomeCommunityId>urn:oid:1.3.6.1.4.1.21367.13.70.200</xdsb:HomeCommunityId>
                          <xdsb:RepositoryUniqueId>1.3.6.1.4.1.21367.13.80.110.2</xdsb:RepositoryUniqueId>
                          <xdsb:DocumentUniqueId>1.3.6.1.4.1.21367.201599.3.201604020954059.22</xdsb:DocumentUniqueId>
                       </xdsb:DocumentRequest>
                    </xdsiB:SeriesRequest>
                 </xdsiB:StudyRequest>
                 <xdsiB:TransferSyntaxUIDList>
                    <xdsiB:TransferSyntaxUID>1.2.840.10008.1.2.1</xdsiB:TransferSyntaxUID>
                 </xdsiB:TransferSyntaxUIDList>
              </xdsiB:RetrieveImagingDocumentSetRequest>/$ |
                'all' | 'all' |
                ['1.2.840.10008.1.2.1'] |
                ['urn:oid:1.3.6.1.4.1.21367.13.70.200'] as Set |
                ['1.3.6.1.4.1.21367.13.80.110.1', '1.3.6.1.4.1.21367.13.80.110.2'] as Set |
                ['1.3.6.1.4.1.21367.201599.1.201604020956049:1.3.6.1.4.1.21367.201599.2.201604020956049.11:1.3.6.1.4.1.21367.201599.3.201604020956049.12:urn:oid:1.3.6.1.4.1.21367.13.70.200:1.3.6.1.4.1.21367.13.80.110.1',
                 '1.3.6.1.4.1.21367.201599.1.201604020954059:1.3.6.1.4.1.21367.201599.2.201604020954059.21:1.3.6.1.4.1.21367.201599.3.201604020954059.22:urn:oid:1.3.6.1.4.1.21367.13.70.200:1.3.6.1.4.1.21367.13.80.110.2']
        'Request 1 object from 1 study from 2 different repositories examine one repos result.' |
                $/\
              <xdsiB:RetrieveImagingDocumentSetRequest xmlns:xdsiB="urn:ihe:rad:xdsi-b:2009">
                 <xdsiB:StudyRequest studyInstanceUID="1.3.6.1.4.1.21367.201599.1.201604020956049">
                    <xdsiB:SeriesRequest seriesInstanceUID="1.3.6.1.4.1.21367.201599.2.201604020956049.11">
                       <xdsb:DocumentRequest xmlns:xdsb="urn:ihe:iti:xds-b:2007">
                          <xdsb:HomeCommunityId>urn:oid:1.3.6.1.4.1.21367.13.70.200</xdsb:HomeCommunityId>
                          <xdsb:RepositoryUniqueId>1.3.6.1.4.1.21367.13.80.110.1</xdsb:RepositoryUniqueId>
                          <xdsb:DocumentUniqueId>1.3.6.1.4.1.21367.201599.3.201604020956049.12</xdsb:DocumentUniqueId>
                       </xdsb:DocumentRequest>
                    </xdsiB:SeriesRequest>
                 </xdsiB:StudyRequest>
                 <xdsiB:StudyRequest studyInstanceUID="1.3.6.1.4.1.21367.201599.1.201604020954059">
                    <xdsiB:SeriesRequest seriesInstanceUID="1.3.6.1.4.1.21367.201599.2.201604020954059.21">
                       <xdsb:DocumentRequest xmlns:xdsb="urn:ihe:iti:xds-b:2007">
                          <xdsb:HomeCommunityId>urn:oid:1.3.6.1.4.1.21367.13.70.200</xdsb:HomeCommunityId>
                          <xdsb:RepositoryUniqueId>1.3.6.1.4.1.21367.13.80.110.2</xdsb:RepositoryUniqueId>
                          <xdsb:DocumentUniqueId>1.3.6.1.4.1.21367.201599.3.201604020954059.22</xdsb:DocumentUniqueId>
                       </xdsb:DocumentRequest>
                    </xdsiB:SeriesRequest>
                 </xdsiB:StudyRequest>
                 <xdsiB:TransferSyntaxUIDList>
                    <xdsiB:TransferSyntaxUID>1.2.840.10008.1.2.1</xdsiB:TransferSyntaxUID>
                 </xdsiB:TransferSyntaxUIDList>
              </xdsiB:RetrieveImagingDocumentSetRequest>/$ |
                'all' | '1.3.6.1.4.1.21367.13.80.110.2' |
                ['1.2.840.10008.1.2.1'] |
                ['urn:oid:1.3.6.1.4.1.21367.13.70.200'] as Set |
                ['1.3.6.1.4.1.21367.13.80.110.2'] as Set |
                ['1.3.6.1.4.1.21367.201599.1.201604020954059:1.3.6.1.4.1.21367.201599.2.201604020954059.21:1.3.6.1.4.1.21367.201599.3.201604020954059.22:urn:oid:1.3.6.1.4.1.21367.13.70.200:1.3.6.1.4.1.21367.13.80.110.2']
        'Request 1 object from 1 study from 2 different homecommunities.' |
                $/\
              <xdsiB:RetrieveImagingDocumentSetRequest xmlns:xdsiB="urn:ihe:rad:xdsi-b:2009">
                 <xdsiB:StudyRequest studyInstanceUID="1.3.6.1.4.1.21367.201599.1.201604020956049">
                    <xdsiB:SeriesRequest seriesInstanceUID="1.3.6.1.4.1.21367.201599.2.201604020956049.11">
                       <xdsb:DocumentRequest xmlns:xdsb="urn:ihe:iti:xds-b:2007">
                          <xdsb:HomeCommunityId>urn:oid:1.3.6.1.4.1.21367.13.70.200</xdsb:HomeCommunityId>
                          <xdsb:RepositoryUniqueId>1.3.6.1.4.1.21367.13.80.110.1</xdsb:RepositoryUniqueId>
                          <xdsb:DocumentUniqueId>1.3.6.1.4.1.21367.201599.3.201604020956049.12</xdsb:DocumentUniqueId>
                       </xdsb:DocumentRequest>
                    </xdsiB:SeriesRequest>
                 </xdsiB:StudyRequest>
                 <xdsiB:StudyRequest studyInstanceUID="1.3.6.1.4.1.21367.201599.1.201604020954059">
                    <xdsiB:SeriesRequest seriesInstanceUID="1.3.6.1.4.1.21367.201599.2.201604020954059.21">
                       <xdsb:DocumentRequest xmlns:xdsb="urn:ihe:iti:xds-b:2007">
                          <xdsb:HomeCommunityId>urn:oid:1.3.6.1.4.1.21367.13.70.222</xdsb:HomeCommunityId>
                          <xdsb:RepositoryUniqueId>1.3.6.1.4.1.21367.13.80.110.2</xdsb:RepositoryUniqueId>
                          <xdsb:DocumentUniqueId>1.3.6.1.4.1.21367.201599.3.201604020954059.22</xdsb:DocumentUniqueId>
                       </xdsb:DocumentRequest>
                    </xdsiB:SeriesRequest>
                 </xdsiB:StudyRequest>
                 <xdsiB:TransferSyntaxUIDList>
                    <xdsiB:TransferSyntaxUID>1.2.840.10008.1.2.1</xdsiB:TransferSyntaxUID>
                 </xdsiB:TransferSyntaxUIDList>
              </xdsiB:RetrieveImagingDocumentSetRequest>/$ |
                'urn:oid:1.3.6.1.4.1.21367.13.70.222' | 'all' |
                ['1.2.840.10008.1.2.1'] |
                ['urn:oid:1.3.6.1.4.1.21367.13.70.222'] as Set |
                ['1.3.6.1.4.1.21367.13.80.110.2'] as Set |
                ['1.3.6.1.4.1.21367.201599.1.201604020954059:1.3.6.1.4.1.21367.201599.2.201604020954059.21:1.3.6.1.4.1.21367.201599.3.201604020954059.22:urn:oid:1.3.6.1.4.1.21367.13.70.222:1.3.6.1.4.1.21367.13.80.110.2']
    }
}
