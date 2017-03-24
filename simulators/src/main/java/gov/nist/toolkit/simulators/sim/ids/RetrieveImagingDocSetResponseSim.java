package gov.nist.toolkit.simulators.sim.ids;

import gov.nist.toolkit.commondatatypes.MetadataSupport;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode.Code;
import gov.nist.toolkit.registrymsg.registry.Response;
import gov.nist.toolkit.simulators.sim.reg.RegistryResponseGeneratingSim;
import gov.nist.toolkit.simulators.support.DsSimCommon;
import gov.nist.toolkit.simulators.support.SimCommon;
import gov.nist.toolkit.simulators.support.StoredDocument;
import gov.nist.toolkit.simulators.support.TransactionSimulator;
import gov.nist.toolkit.valregmsg.registry.RetrieveMultipleResponse;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class RetrieveImagingDocSetResponseSim extends TransactionSimulator implements RegistryResponseGeneratingSim {
   static Logger logger = Logger.getLogger(RetrieveImagingDocSetResponseSim.class);
   DsSimCommon dsSimCommon;
   // List<String> documentUids;
   List <Pair<String, String>> imagingDocumentUids;
   List <String> compositeUids = new ArrayList<>();
   List <String> transferSyntaxUids;
   // This is a map from an image instance UID to the composite UID
   // (study:series:instace)
   HashMap <String, String> imagingUidMap;
   RetrieveMultipleResponse response;
   // RepIndex repIndex;
   String repositoryUniqueId;

   public RetrieveImagingDocSetResponseSim(ValidationContext vc, List <Pair<String, String>> imagingDocumentUids,
      List <String> transferSyntaxUids, SimCommon common, DsSimCommon dsSimCommon, String repositoryUniqueId) {
      super(common, null);
      this.dsSimCommon = dsSimCommon;
      this.imagingDocumentUids = new ArrayList<>();
      for (Pair<String, String> p : imagingDocumentUids) this.imagingDocumentUids.add(p);
      this.transferSyntaxUids = new ArrayList<>();
      for (String s : transferSyntaxUids) this.transferSyntaxUids.add(s.trim());
      this.repositoryUniqueId = repositoryUniqueId.trim();
      imagingUidMap = new HashMap <String, String>();
      for (Pair<String, String> p : this.imagingDocumentUids) {
         String compositeUid = p.getValue0();
         compositeUids.add(compositeUid);
         String[] x = compositeUid.split(":");
         imagingUidMap.put(x[2], compositeUid);
      }
   }

   @Override
   public void run(@SuppressWarnings("hiding") ErrorRecorder er, MessageValidatorEngine mvc) {
      try {
         response = new RetrieveMultipleResponse();

         dsSimCommon.addImagingDocumentAttachments(compositeUids, transferSyntaxUids, er);

         Collection <StoredDocument> documents = dsSimCommon.getAttachments();
         OMElement root = response.getRoot();
         
         /*
          * use forced status to set response status to:
          * + Success if all documents were successfully retrieved.
          * + Partial success if some documents were successfully retrieved.
          * + Failure is no documents were successfully retrieved.
          * Reference RAD TF-3 4.69.5 and ITI TF-3 table 4.2.4.2-4
          */
         int req = imagingDocumentUids.size();
         int ret = documents.size();
         response.setForcedStatus(MetadataSupport.status_success);
         if (req > ret) 
            if (ret > 0) response.setForcedStatus(MetadataSupport.status_partial_success);
            else response.setForcedStatus(MetadataSupport.status_failure);

         for (StoredDocument document : documents) {
            String uid = document.getUid();
            logger.debug("Adding document to response: " + uid);
            logger.debug("Repository Unique ID: " + repositoryUniqueId);

            String compositeUid = imagingUidMap.get(uid);
            String hci = null;
            for (Pair<String, String> p : imagingDocumentUids) {
               if (compositeUid.equals(p.getValue0())) {
                  hci = p.getValue1();
                  break;
               }
            }
            StoredDocument sd = dsSimCommon.getStoredImagingDocument(compositeUid, transferSyntaxUids);

            OMElement docResponse =
               MetadataSupport.om_factory.createOMElement(MetadataSupport.document_response_qnamens);
            
            if (StringUtils.isNotBlank(hci)) {
               OMElement hciEle = MetadataSupport.om_factory.createOMElement(MetadataSupport.home_community_id_qname);
               hciEle.setText(hci);
               docResponse.addChild(hciEle);
            }

            OMElement repId = MetadataSupport.om_factory.createOMElement(MetadataSupport.repository_unique_id_qnamens);
            repId.setText(repositoryUniqueId);
            docResponse.addChild(repId);

            OMElement docId = MetadataSupport.om_factory.createOMElement(MetadataSupport.document_unique_id_qnamens);
            docId.setText(sd.getUid());
            docResponse.addChild(docId);
            logger.debug("Setting document ID: " + sd.getUid());

            OMElement mimeType = MetadataSupport.om_factory.createOMElement(MetadataSupport.mimetype_qnamens);
            mimeType.setText(sd.getMimeType());
            docResponse.addChild(mimeType);
            logger.debug("Setting mimeType: " + sd.getMimeType());

            OMElement doc = MetadataSupport.om_factory.createOMElement(MetadataSupport.document_qnamens);
            docResponse.addChild(doc);

            OMElement include = MetadataSupport.om_factory.createOMElement(MetadataSupport.xop_include_qnamens);
            OMAttribute href = MetadataSupport.om_factory.createOMAttribute("href", null, "cid:" + document.cid);
            include.addAttribute(href);
            doc.addChild(include);

            root.addChild(docResponse);
         }
         
      } catch (Exception e) {
         er.err(Code.XDSRepositoryError, e);
         e.printStackTrace();
         return;
      }
   }

   @Override
   public Response getResponse() {
      return response;
   }
   
   
}
