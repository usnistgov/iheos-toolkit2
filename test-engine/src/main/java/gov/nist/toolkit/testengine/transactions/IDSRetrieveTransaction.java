package gov.nist.toolkit.testengine.transactions;

import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrymsg.repository.RetrievedDocumentModel;
import gov.nist.toolkit.registrymsg.repository.RetrievedDocumentsModel;
import gov.nist.toolkit.testengine.engine.Linkage;
import gov.nist.toolkit.testengine.engine.Rad69;
import gov.nist.toolkit.testengine.engine.RetContext;
import gov.nist.toolkit.testengine.engine.StepContext;
import gov.nist.toolkit.utilities.io.Sha1Bean;
import gov.nist.toolkit.utilities.xml.OMFormatter;
import gov.nist.toolkit.utilities.xml.XmlUtil;
import gov.nist.toolkit.xdsexception.XdsPreparsedException;
import gov.nist.toolkit.xdsexception.client.MetadataException;
import gov.nist.toolkit.xdsexception.client.XdsException;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.xpath.AXIOMXPath;
import org.apache.log4j.Logger;

import javax.xml.namespace.QName;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class IDSRetrieveTransaction extends BasicTransaction {
   String uri = null;
   OMElement uri_ref = null;
   HashMap<String, String> referenced_documents = new HashMap<String, String>();  // uid, filename
   Metadata reference_metadata = null;
   boolean isXDSI;
   static Logger logger = Logger.getLogger(IDSRetrieveTransaction.class);
   @Override
   public String toString() {

      return "IDSRetrieveTransaction: *************" +
      "\nuri = " + uri +
      "\nuri_ref = " + isNull(uri_ref) +
      "\nreferenced_documents = " + referenced_documents +
      "\nuse_document_unique_id = " + use_repository_unique_id +
      "\nuse_id = " + use_id +
      "\nuse_xpath = " + use_xpath +
      "\nlinkage = " + local_linkage_data +
      "\nendpoint = " + endpoint +
      "\nisXDSI = " + isXDSI +
      "\nactor config = " + ((testConfig == null) ? "null" : testConfig.site) +
      "\n****************";
   }

   String metadataStructure(Metadata m) {
      try {
         return m.structure();
      } catch (Exception e) { }
      return null;
   }

   String isNull(Object thing) { return (thing == null) ? "null" : "not null"; }

   public IDSRetrieveTransaction(StepContext s_ctx, OMElement instruction, OMElement instruction_output, boolean isXDSI) {
      super(s_ctx, instruction, instruction_output);
      xds_version = BasicTransaction.xds_b;
      defaultEndpointProcessing = false;
      parse_metadata = false;
      noMetadataProcessing = true;
      this.isXDSI = isXDSI;
   }

   @Override
   public void run(OMElement request_ele) throws Exception {
      
      logger.debug("IDSRetrieveTransaction#run");
   
      if (request_ele == null)
         fatal("Retrieve transaction - request is null");
   
      logger.debug(" Request Element: " + request_ele.getText());

      applyLinkage(request_ele);

      reportManagerPreRun(request_ele);

      logger.debug(" Repository Unique ID: " + repositoryUniqueId);
         if (repositoryUniqueId == null) {
            // if managed by ReportManager then need to extract it from request
            try {
               AXIOMXPath xpathExpression = new AXIOMXPath ("//*[local-name()='RepositoryUniqueId']");
               repositoryUniqueId = xpathExpression.stringValueOf(request_ele);
            } catch (Exception e) {
               fatal("Error extracting repositoryUniqueId from Retrieve request - " + e.getMessage() + "\nRequest is..." +
                    new OMFormatter(request_ele).toString() + "\n...End of Request");
            }
         }

         if (s_ctx.getPlan().getExtraLinkage() != null)
            testLog.add_name_value(instruction_output, "TemplateParams", s_ctx.getPlan().getExtraLinkage());

         if (isXDSI) parseIDSIigEndpoint(repositoryUniqueId, testConfig.secure);
         else parseIDSEndpoint(repositoryUniqueId, testConfig.secure);

         RetContext r_ctx = null;
         try {
            // map from doc uid -> info about doc
            // RetInfo holds size, hash, home etc
            HashMap<String, RetrievedDocumentModel> request_info = build_request_info(request_ele /* retrieve request */);

            // Bean that holds the context of the retrieve operation
            r_ctx = new RetContext();
            r_ctx.setRequestInfo(new RetrievedDocumentsModel().setMap(request_info));
            r_ctx.setRequest(request_ele);
            r_ctx.setExpectedError(s_ctx.getExpectedErrorMessage());

            Rad69 rad69 = new Rad69(this, r_ctx, endpoint);
            rad69.setUseReportManager(useReportManager);
            rad69.setAsync(async);
            rad69.setExpectedMimeType("application/dicom");
            rad69.setStepContext(instruction_output);
            rad69.setIsXca(isXDSI);
            rad69.setSoap12(true);
            rad69.setReferenceMetadata(reference_metadata);
            logger.debug("ImagingDocSetRetrieveTransaction about to call ret_b.run()");
            OMElement result = rad69.run();
            logger.debug("ImagingDocSetRetrieveTransaction after rad69.run()");
//          testLog.add_name_value(instruction_output, "Result", result);
            rad69.validate();
         }
         catch (XdsPreparsedException e) {
            throw new XdsInternalException("Retrieve Error: endpoint was: " + endpoint + " " + e.getMessage(), e);
         }
         catch (Exception e) {
            throw new XdsInternalException("Retrieve Error: endpoint was: " + endpoint + " " + e.getMessage(), e);
         }

         add_step_status_to_output();

         // check that status == success
                String status = r_ctx.getRrp().get_registry_response_status();
                eval_expected_status(status, r_ctx.getRrp().get_error_code_contexts());

                String expErrorCode = s_ctx.getExpectedErrorCode();
                if (expErrorCode != null && !expErrorCode.equals("")) {
                    List<String> errCodesReturned = r_ctx.getRrp().get_error_codes();
                    if (!errCodesReturned.contains(expErrorCode)) {
                        s_ctx.set_error("Expected errorCode of " + expErrorCode + "\nDid getRetrievedDocumentsModel errorCodes of " +
                                errCodesReturned);
                        step_failure = true;
                    }
                }
                reportManagerPostRun();
            }

   @SuppressWarnings("unchecked")
   boolean containsVariable(OMElement ele) {
      String valueStr = ele.getText();
      if (containsVariable(valueStr)) return true;
      for (Iterator<OMElement> it=(Iterator<OMElement>)ele.getChildElements(); it.hasNext(); ) {
         OMElement child = (OMElement) it.next();
         if (containsVariable(child))
            return true;
      }
      return false;
   }

   boolean containsVariable(String str) {
      if (str == null) return false;
      int i = str.indexOf("$");
      if (i == -1)
         return false;
      return true;
   }

   void update_referenced_documents() {
      HashMap<String, String> new_entries = new HashMap<String, String>();
      for (String ref_id : referenced_documents.keySet()) {
         if (local_linkage_data.containsKey(ref_id)) {
            new_entries.put(local_linkage_data.get(ref_id), referenced_documents.get(ref_id));
         }
      }
      referenced_documents.putAll(new_entries);
   }

   HashMap<String, String> parse_rep_request(OMElement rdsr) {
      HashMap<String, String> map = new HashMap<String, String>();  // docuid -> repuid

      for (OMElement document_request : XmlUtil.childrenWithLocalName(rdsr, "DocumentRequest")) {
         OMElement doc_uid_ele = XmlUtil.firstChildWithLocalName(document_request, "DocumentUniqueId");
         String doc_uid = doc_uid_ele.getText();

         OMElement rep_uid_ele = XmlUtil.firstChildWithLocalName(document_request, "RepositoryUniqueId") ;
         String rep_uid = rep_uid_ele.getText();
         map.put(doc_uid, rep_uid);
      }
      return map;
   }





   private HashMap<String, RetrievedDocumentModel> build_request_info(OMElement metadata_ele) throws XdsException {
      HashMap<String, RetrievedDocumentModel> request;
      request = new HashMap<String, RetrievedDocumentModel>();
      for (OMElement document_request : XmlUtil.childrenWithLocalName(metadata_ele, "DocumentRequest")) {
         //       request_list.add(document_request);

         OMElement doc_uid_ele = XmlUtil.firstChildWithLocalName(document_request, "DocumentUniqueId");
         String doc_uid = doc_uid_ele.getText();

         OMElement rep_uid_ele = XmlUtil.firstChildWithLocalName(document_request, "RepositoryUniqueId") ;
         String rep_uid = rep_uid_ele.getText();

         RetrievedDocumentModel rqst = new RetrievedDocumentModel();
         rqst.setDocUid(doc_uid);
         rqst.setRepUid(rep_uid);

         request.put(doc_uid, rqst);

      }
      return request;
   }



   @Override
   protected void parseInstruction(OMElement part) throws XdsInternalException, MetadataException {
      String part_name = part.getLocalName();
      if (part_name.equals("MetadataFile")) {
         metadata_filename = testConfig.testplanDir + File.separator + part.getText();
         testLog.add_name_value(instruction_output, "MetadataFile", metadata_filename);
      }
      else if (part_name.equals("ExpectedContents")) {
         //expected_contents = part;
         testLog.add_name_value(instruction_output, "ExpectedContents", part);
      }
      else if (part_name.equals("ExpectedMimeType")) {
         //expected_mime_type = part.getText();
         testLog.add_name_value(instruction_output, "ExpectedMimeType", part);
      }
      else if (part_name.equals("ReferenceDocument")) {
         String filename = null;
         String uid = null;
         filename =testConfig.testplanDir + File.separator + part.getText();
         uid = part.getAttributeValue(new QName("uid"));
         referenced_documents.put(uid, filename);
         testLog.add_name_value(instruction_output, "ReferenceDocument", part);
      }
      else if (part_name.equals("ReferenceMetadata")) {
         String testdir = part.getAttributeValue(new QName("testdir"));
         String step = part.getAttributeValue(new QName("step"));
         if (testdir == null || testdir.equals("") || step == null || step.equals(""))
            throw new XdsInternalException("ReferenceMetadata instruction: both testdir and step are required attributes");
         reference_metadata = new Linkage(testConfig).getResult(testdir, step);
      }
      else if (part_name.equals("UseId")) {
         use_id.add(part);
         testLog.add_name_value(instruction_output, "UseId", part);
      }
      else if (part_name.equals("UseRepositoryUniqueId")) {
         this.use_repository_unique_id.add(part);
         testLog.add_name_value(instruction_output, "UseRepositoryUniqueId", part);
      }
      else if (part_name.equals("UseXPath")) {
         use_xpath.add(part);
         testLog.add_name_value(instruction_output, "UseXRef", part);
      }
      else if (part_name.equals("URI")) {
         uri = part.getText();
      }
      else if (part_name.equals("URIRef")) {
         uri_ref = part;
      } else {
         //          throw new XdsException("Don't understand instruction " + part_name + " inside step " + s_ctx.getId());
         parseBasicInstruction(part);
      }
   }


   String sha1(byte[] buf) throws Exception {
      Sha1Bean sb = new Sha1Bean();
      sb.setByteStream(buf);
      return sb.getSha1String();
   }

   @Override
   protected String getRequestAction() {
      return "urn:ihe:rad:2009:RetrieveImagingDocumentSet";
   }

   @Override
   protected String getBasicTransactionName() {
      if (isXDSI) return "ret.iig";
      return "ret";
   }



}