package gov.nist.toolkit.simulators.sim.ids;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode.Code;
import gov.nist.toolkit.registrymsg.registry.Response;
import gov.nist.toolkit.registrysupport.MetadataSupport;
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
import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.List;
import java.util.HashMap;

public class RetrieveImagingDocSetResponseSim extends TransactionSimulator implements RegistryResponseGeneratingSim{
	static Logger logger = Logger.getLogger(RetrieveImagingDocSetResponseSim.class);
	DsSimCommon dsSimCommon;
	//List<String> documentUids;
	List<String> imagingDocumentUids;
	List<String> transferSyntaxUids;
	// This is a map from an image instance UID to the composite UID (study:series:instace)
	HashMap<String, String> imagingUidMap;
	RetrieveMultipleResponse response;
	//RepIndex repIndex;
	String repositoryUniqueId;

	public RetrieveImagingDocSetResponseSim(ValidationContext vc, List<String> imagingDocumentUids, List<String> transferSyntaxUids, SimCommon common, DsSimCommon dsSimCommon, String repositoryUniqueId) {
		super(common, null);
		this.dsSimCommon = dsSimCommon;
		this.imagingDocumentUids = imagingDocumentUids;
		this.transferSyntaxUids = transferSyntaxUids;
		//this.documentUids = documentUids;
		//this.repIndex = dsSimCommon.repIndex;
		this.repositoryUniqueId = repositoryUniqueId;
		imagingUidMap = new HashMap<String, String>();
		for (String compositeUid: imagingDocumentUids) {
			String[] x = compositeUid.split(":");
			imagingUidMap.put(x[2], compositeUid);
		}
	}

	public void run(ErrorRecorder er, MessageValidatorEngine mvc) {
		try {
			response = new RetrieveMultipleResponse();

			dsSimCommon.addImagingDocumentAttachments(imagingDocumentUids, transferSyntaxUids, er);

			Collection<StoredDocument> documents = dsSimCommon.getAttachments();
			OMElement root = response.getRoot();
/*
			ArrayList<StoredDocument> documents = new ArrayList<StoredDocument>();

			for (String s : documentUids) {
				StoredDocument sdx = getStoredImageDocument(s);
				documents.add(sdx);
				logger.debug("Adding a document to list of documents: " + s);
			}
*/

			for (StoredDocument document : documents) {
				String uid = document.getUid();
				logger.debug("Adding document to response: " + uid);
				logger.debug("Repository Unique ID: " + repositoryUniqueId);

				//StoredDocument sd = repIndex.getDocumentCollection().getStoredDocument(uid);
				//StoredDocument sd = getStoredImageDocument(uid);
				String compositeUid = imagingUidMap.get(uid);
				StoredDocument sd = dsSimCommon.getStoredImagingDocument(compositeUid, transferSyntaxUids);

				OMElement docResponse = MetadataSupport.om_factory.createOMElement(MetadataSupport.document_response_qnamens);

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
		}
		catch (Exception e) {
			er.err(Code.XDSRepositoryError, e);
			return;
		}
	}

	public Response getResponse() {
		return response;
	}

/*
	private StoredDocument getStoredImageDocument(String uid) {
		StoredDocumentInt sdi = new StoredDocumentInt();
		sdi.pathToDocument = "/tmp/000000.dcm";
		sdi.uid = uid;
		sdi.mimeType = "application/dicom";
		sdi.charset = "UTF-8";
		sdi.hash="0000";
		sdi.size = "4";
		sdi.content = new byte[4];
		sdi.content[0] = 'a';
		sdi.content[1] = 'b';
		sdi.content[2] = 'c';
		sdi.content[3] = 'd';
		StoredDocument sd = new StoredDocument(sdi);
		sd.cid = mkCid(5);
		return sd;
	}
	private String mkCid(int i) {
		return "doc" + i + "@wustl.edu";
	}
*/

}
