package gov.nist.toolkit.simulators.sim.rep.od;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode.Code;
import gov.nist.toolkit.registrymsg.registry.Response;
import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.simulators.sim.reg.RegistryResponseGeneratingSim;
import gov.nist.toolkit.simulators.sim.rep.RepIndex;
import gov.nist.toolkit.simulators.support.DsSimCommon;
import gov.nist.toolkit.simulators.support.SimCommon;
import gov.nist.toolkit.simulators.support.StoredDocument;
import gov.nist.toolkit.simulators.support.TransactionSimulator;
import gov.nist.toolkit.valregmsg.registry.RetrieveMultipleResponse;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.xdsexception.ToolkitRuntimeException;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Retrieve2DocumentResponseSim extends TransactionSimulator implements RegistryResponseGeneratingSim{
	DsSimCommon dsSimCommon;
	List<String> documentUids;
	List<String> dynamicDocumentUids = new ArrayList<String>();
	RetrieveMultipleResponse response;
	RepIndex repIndex;
	String repositoryUniqueId;

	public Retrieve2DocumentResponseSim(ValidationContext vc, List<String> documentUids, SimCommon common, DsSimCommon dsSimCommon, String repositoryUniqueId) {
		super(common, null);
		this.dsSimCommon = dsSimCommon;
		this.documentUids = documentUids;
		this.repIndex = dsSimCommon.repIndex;
		this.repositoryUniqueId = repositoryUniqueId;
	}

	public void run(ErrorRecorder er, MessageValidatorEngine mvc) {
		try {
			response = new RetrieveMultipleResponse();

			/***
			 * Document Response WBS:
			 * A) Check if persistence option is enabled for this sim, if it is on, then:
			 *
			 *  1) Get content state,
			 *  	a) if not in the last section, do a PnR (using api.runTest and replacing the prior document as needed) with the current section in the content bundle
			 *  	c)  return previous document if reached end of content state
			 *  3) Get the StoredDocument resulting from the PnR (store the document entry Uuid)
			 *  4) Set NewDocumentId in the response
			 * B) If no persistence, then just serve up the content from disk
			 */

			// At this point, there are no documents in this repository, so insert a fake one here.

			String dynamicDocumentUuid = "od-doc-uid";
			StoredDocument storedDocument = repIndex.getDocumentCollection().getStoredDocument(dynamicDocumentUuid);
			if (storedDocument==null) { 			// Begin insert a fake document here
				storedDocument =  new StoredDocument("nonexistent-od-file-path",dynamicDocumentUuid);
				storedDocument.setContent("This content is served on-demand.".getBytes());
				storedDocument.setMimetype("text/plain");

				repIndex.getDocumentCollection().add(storedDocument);
			}
			// End

			// Replace all OD promise Uuids with fake dynamic contents
			for (String uid : documentUids) {
				dynamicDocumentUids.add(dynamicDocumentUuid);
			}

			dsSimCommon.addDocumentAttachments(dynamicDocumentUids, er); // Old param was (documentUids)
			// End On-Demand update block

			Collection<StoredDocument> documents = dsSimCommon.getAttachments();

			OMElement root = response.getRoot();

			if (documents!= null && documentUids!=null)
			if (documents.size() != documentUids.size()) {// This should always be equal in this case and only for OD where documents are bogus
				er.err(Code.XDSRepositoryError, new ToolkitRuntimeException("The On-Demand StoredDocument collection size does not match with the requested number of Uids."));
				return;
			}

			int cx = 0;
			for (StoredDocument document : documents) {
				String uid = document.getUid();

				StoredDocument sd = repIndex.getDocumentCollection().getStoredDocument(uid);

				OMElement docResponse = MetadataSupport.om_factory.createOMElement(MetadataSupport.document_response_qnamens);

				OMElement repId = MetadataSupport.om_factory.createOMElement(MetadataSupport.repository_unique_id_qnamens);
				repId.setText(repositoryUniqueId);
				docResponse.addChild(repId);

				OMElement docId = MetadataSupport.om_factory.createOMElement(MetadataSupport.document_unique_id_qnamens);
				docId.setText(documentUids.get(cx));
				/* "sd.uid" is the value of the bogus StoredDocument inserted above.
				We are restoring the original promise Id. This is because OD changes are temporary in this class and changes elsewhere should not be needed.
				The order of the collection iterator doesn't matter here because the bogus content is exactly the same for any OD promise (for now).
				* */
				docResponse.addChild(docId);

				// Begin On-Demand
				OMElement newDocId = MetadataSupport.om_factory.createOMElement(MetadataSupport.newDocumentUniqueId);
				newDocId.setText(documentUids.get(cx++) + ".111" ); // TODO: Find out what needs to go here.
				docResponse.addChild(newDocId);

				// TODO: Find out if the NewRepositoryUniqueId needs to be added in. (Vol. 2b, 3.43.5.1.3).
				// A: Not required without the Persistence option. (Bill)
				// End On-Demand

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
