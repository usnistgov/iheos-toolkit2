/**
 * 
 */
package gov.nist.toolkit.registrymsg.repository;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;

import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.utilities.xml.XmlUtil;

/**
 * Generates {@code <RetrieveImagingDocumentRequest> xml document from model.
 * 
 * @author Ralph Moulton / MIR WUSTL IHE Development Project <a
 * href="mailto:moultonr@mir.wustl.edu">moultonr@mir.wustl.edu</a>
 *
 */
public class RetrieveImageRequestGenerator {

   RetrieveImageRequestModel model;
   
   public RetrieveImageRequestGenerator(RetrieveImageRequestModel model) {
      this.model = model;
   }
   
   public OMElement get() {
      
      OMElement request = 
         XmlUtil.createElement("RetrieveImagingDocumentSetRequest", MetadataSupport.xdsiB);
      
      for (RetrieveImageStudyRequestModel eModel : model.getStudyRequests()) {
         
         OMElement studyReq = XmlUtil.createElement("StudyRequest", MetadataSupport.xdsiB);
         OMAttribute studyUID = 
            MetadataSupport.om_factory.createOMAttribute("studyInstanceUID", 
               null, eModel.getStudyInstanceUID());
         studyReq.addAttribute(studyUID);
         request.addChild(studyReq);
         
         for (RetrieveImageSeriesRequestModel sModel : eModel.getSeriesRequests()) {
            
            OMElement seriesReq = XmlUtil.createElement("SeriesRequest", MetadataSupport.xdsiB);
            OMAttribute seriesUID = 
               MetadataSupport.om_factory.createOMAttribute("seriesInstanceUID", 
                  null, sModel.getSeriesInstanceUID());
            seriesReq.addAttribute(seriesUID);
            studyReq.addChild(seriesReq);
            
            for (RetrieveItemRequestModel documentRequest : sModel.getDocumentRequests()) {

               OMElement documentReq = XmlUtil.createElement("DocumentRequest", MetadataSupport.xdsB);
               seriesReq.addChild(documentReq);

               if (documentRequest.hasHomeId()) {
                   OMElement homeEle = MetadataSupport.om_factory.createOMElement(MetadataSupport.home_community_id_qname);
                   documentReq.addChild(homeEle);
                   homeEle.setText(documentRequest.getHomeId());
               }

               OMElement repoEle = MetadataSupport.om_factory.createOMElement(MetadataSupport.repository_unique_id_qnamens);
               documentReq.addChild(repoEle);
               repoEle.setText(documentRequest.getRepositoryId());

               OMElement docEle = MetadataSupport.om_factory.createOMElement(MetadataSupport.document_unique_id_qnamens);
               documentReq.addChild(docEle);
               docEle.setText(documentRequest.getDocumentId());
               
            } // EO document loop
         } // EO series loop
      } // EO study loop
      
      OMElement syntaxUIDList = XmlUtil.createElement("TransferSyntaxUIDList", MetadataSupport.xdsiB);
      request.addChild(syntaxUIDList);
      
      for (String syntaxStr : model.getTransferSyntaxUIDs()) {
         OMElement syntaxUID = XmlUtil.createElement("TransferSyntaxUID", MetadataSupport.xdsiB);
         syntaxUIDList.addChild(syntaxUID);
         syntaxUID.setText(syntaxStr);
      }
      return request;
   } // EO get method
}
