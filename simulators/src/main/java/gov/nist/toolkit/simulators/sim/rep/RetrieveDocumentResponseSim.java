package gov.nist.toolkit.simulators.sim.rep;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.common.XdsErrorCode.Code;
import gov.nist.toolkit.registrymsg.registry.Response;
import gov.nist.toolkit.commondatatypes.MetadataSupport;
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

import java.util.Collection;
import java.util.List;

public class RetrieveDocumentResponseSim extends TransactionSimulator implements RegistryResponseGeneratingSim{
	DsSimCommon dsSimCommon;
	List<String> documentUids;
	RetrieveMultipleResponse response;
	RepIndex repIndex;
	String repositoryUniqueId;

	public RetrieveDocumentResponseSim(ValidationContext vc, List<String> documentUids, SimCommon common, DsSimCommon dsSimCommon, String repositoryUniqueId) {
		super(common, null);
		this.dsSimCommon = dsSimCommon;
		this.documentUids = documentUids;
		this.repIndex = dsSimCommon.repIndex;
		this.repositoryUniqueId = repositoryUniqueId;
	}

	public void run(ErrorRecorder er, MessageValidatorEngine mvc) {
		try {
			response = new RetrieveMultipleResponse();

			dsSimCommon.addDocumentAttachments(documentUids, er);

			Collection<StoredDocument> documents = dsSimCommon.getAttachments();

			OMElement root = response.getRoot();

			for (StoredDocument document : documents) {
				String uid = document.getUid();

				StoredDocument sd = repIndex.getDocumentCollection().getStoredDocument(uid);

				OMElement docResponse = MetadataSupport.om_factory.createOMElement(MetadataSupport.document_response_qnamens);

				OMElement repId = MetadataSupport.om_factory.createOMElement(MetadataSupport.repository_unique_id_qnamens);
				repId.setText(repositoryUniqueId);
				docResponse.addChild(repId);

				OMElement docId = MetadataSupport.om_factory.createOMElement(MetadataSupport.document_unique_id_qnamens);
				docId.setText(sd.getUid());
				docResponse.addChild(docId);

				OMElement mimeType = MetadataSupport.om_factory.createOMElement(MetadataSupport.mimetype_qnamens);
				mimeType.setText(sd.getMimeType());
				docResponse.addChild(mimeType);

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

}
