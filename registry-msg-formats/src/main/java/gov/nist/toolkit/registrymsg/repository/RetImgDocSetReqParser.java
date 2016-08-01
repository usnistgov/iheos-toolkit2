/**
 * 
 */
package gov.nist.toolkit.registrymsg.repository;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;

import gov.nist.toolkit.commondatatypes.MetadataSupport;
import gov.nist.toolkit.utilities.xml.XmlUtil;

/**
 * RAD-69/75 Request parser
 * 
 * @author Ralph Moulton / MIR WUSTL IHE Development Project <a
 * href="mailto:moultonr@mir.wustl.edu">moultonr@mir.wustl.edu</a>
 *
 */
public class RetImgDocSetReqParser {
   
   private static final QName xferListQname = new QName(MetadataSupport.xdsiB_uri, "TransferSyntaxUIDList");
   
   RetrieveImageRequestModel model = new RetrieveImageRequestModel();
   OMElement body;
   
   public RetImgDocSetReqParser(OMElement element) {
      body = element;
   }
   
   public RetrieveImageRequestModel getRequest() {
      parse();
      return model;
   }
   
   void parse() {
      for (OMElement studyElement : XmlUtil.decendentsWithLocalName(body, "StudyRequest")) {
         RetrieveImageStudyRequestModel studyModel = new RetrieveImageStudyRequestModel();
         model.addStudyRequest(studyModel);
         studyModel.setStudyInstanceUID(XmlUtil.getAttributeValue(studyElement, "studyInstanceUID"));
         for (OMElement seriesElement : XmlUtil.decendentsWithLocalName(studyElement, "SeriesRequest")) {
            RetrieveImageSeriesRequestModel seriesModel = new RetrieveImageSeriesRequestModel();
            studyModel.addSeriesRequest(seriesModel);
            seriesModel.setSeriesInstanceUID(XmlUtil.getAttributeValue(seriesElement, "seriesInstanceUID"));
            for (OMElement documentElement : XmlUtil.decendentsWithLocalName(seriesElement, "DocumentRequest")) {
               RetrieveItemRequestModel documentModel = new RetrieveItemRequestModel();
               seriesModel.addDocumentRequest(documentModel);
               OMElement homeEle = documentElement.getFirstChildWithName(MetadataSupport.home_community_id_qname);
               if (homeEle != null) documentModel.setHomeId(homeEle.getText());
               OMElement repoEle = documentElement.getFirstChildWithName(MetadataSupport.repository_unique_id_qnamens);
               if (repoEle != null) documentModel.setRepositoryId(repoEle.getText());
               OMElement docEle = documentElement.getFirstChildWithName(MetadataSupport.document_unique_id_qnamens);
               if (docEle != null) documentModel.setDocumentId(docEle.getText());
            }
         }
      }
      OMElement xferListElement = body.getFirstChildWithName(xferListQname);
      if (xferListElement != null) {
         for (OMElement xferSyntaxElement : XmlUtil.decendentsWithLocalName(xferListElement, "TransferSyntaxUID")) {
            model.addTransferSyntaxUID(xferSyntaxElement.getText());
         }
      }
   }
   
   // XCAI_TODO written, need to test

}
