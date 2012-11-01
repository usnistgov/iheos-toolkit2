package gov.nist.toolkit.simulators.sim.rep;

import gov.nist.toolkit.actorfactory.ActorFactory;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.docref.Mtom;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode.Code;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.simulators.support.MetadataGeneratingSim;
import gov.nist.toolkit.simulators.support.SimCommon;
import gov.nist.toolkit.simulators.support.StoredDocument;
import gov.nist.toolkit.simulators.support.TransactionSimulator;
import gov.nist.toolkit.soap.axis2.Soap;
import gov.nist.toolkit.utilities.io.Hash;
import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.valregmsg.message.DocumentAttachmentMapper;
import gov.nist.toolkit.valregmsg.message.MetadataContainer;
import gov.nist.toolkit.valregmsg.message.MultipartContainer;
import gov.nist.toolkit.valregmsg.message.StoredDocumentInt;
import gov.nist.toolkit.valregmsg.service.SoapActionFactory;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.xdsexception.XDSMissingDocumentException;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.axiom.om.OMElement;

public class RepPnRSim extends TransactionSimulator implements MetadataGeneratingSim {
	Metadata m = null;
	SimulatorConfig asc;

	public RepPnRSim(SimCommon common, SimulatorConfig asc) {
		super(common);
		this.asc = asc;
	}

	public Metadata getMetadata() {
		return m;
	}

	public void run(ErrorRecorder er, MessageValidatorEngine mvc) {
		this.er = er;

		// if request didn't validate, return so errors can be reported
		if (common.hasErrors()) {
			return;
		}

		try {

			MetadataContainer metaCon = (MetadataContainer) common.getMessageValidator(MetadataContainer.class);
			m = metaCon.getMetadata();

			DocumentAttachmentMapper dam = (DocumentAttachmentMapper) common.getMessageValidator(DocumentAttachmentMapper.class);
			MultipartContainer mc = (MultipartContainer) common.getMessageValidator(MultipartContainer.class);

			Map<String, StoredDocument> sdMap = new HashMap<String, StoredDocument>();

			for (OMElement eo : m.getExtrinsicObjects()) {
				String eoId = m.getId(eo);
				String uid = m.getUniqueIdValue(eo);
				File repositoryFile = common.db.getRepositoryDocumentFile(uid);
				int size;
				String hash;
				StoredDocument storedDocument = null;
				try {
					storedDocument = new StoredDocument(dam.getStoredDocumentForDocumentId(eoId));  // exception if not found
					storedDocument.pathToDocument = common.db.getRepositoryDocumentFile(uid).toString();
					sdMap.put(uid, storedDocument);
					storedDocument.pathToDocument = repositoryFile.toString();
					storedDocument.uid = uid;
					byte[] contents = storedDocument.content;

					size = contents.length;
					hash = new Hash().compute_hash(contents);

					sdMap.put(uid, storedDocument);

				} catch (Exception e) {
					// wasn't available through DocumentAttachmentMapper, try MultipartContainer
					String docContentId = dam.getDocumentContentsIdForDocumentId(eoId);
					StoredDocumentInt sdi = mc.getContent(docContentId);
					if (sdi != null) {
						storedDocument = new StoredDocument(sdi);
						storedDocument.uid = uid;
						storedDocument.pathToDocument = common.db.getRepositoryDocumentFile(uid).toString();
						storedDocument.content = mc.getContent(docContentId).content;
						sdMap.put(uid, storedDocument);
						size = storedDocument.content.length;
						hash = new Hash().compute_hash(storedDocument.content);
					} else {
						throw new XDSMissingDocumentException("Document contents for document " + eoId + " not available in message", Mtom.XOP_example2);
					}
				}

				// add size and hash attributes. Error if they exist and disagree.

				String sizeStr = Integer.toString(size);

				String existingSize = m.getSlotValue(eo, "size", 0);
				boolean hasSize = existingSize != null && !existingSize.equals("");
				String existingHash = m.getSlotValue(eo, "hash", 0);
				boolean hasHash = existingHash != null && !existingHash.equals(""); 

				if (hasSize && !existingSize.equals(sizeStr)) {
					er.err(XdsErrorCode.Code.XDSRepositoryMetadataError, "DocumentEntry(" + m.getId(eo) + ") has size slot with value " + existingSize + " which disagrees with computed value of " + sizeStr, this, "");
				}
				if (hasHash && !existingHash.equals(hash)) {
					er.err(XdsErrorCode.Code.XDSRepositoryMetadataError, "DocumentEntry(" + m.getId(eo) + ") has hash slot with value " + existingHash + " which disagrees with computed value of " + hash, this, "");
				}

				String mimeType = m.getMimeType(eo);

				storedDocument.setHash(hash);
				storedDocument.setSize(sizeStr);
				storedDocument.setMimetype(mimeType);

				// add size and hash to metadata, overwrite if necessary
				if (hasSize) 
					m.setSlotValue(eo, "size", 0, sizeStr);
				else {
					OMElement slot = m.mkSlot("size", sizeStr);
					m.insertSlot(eo, slot);
				}
				if (hasHash)
					m.setSlotValue(eo, "hash", 0, hash);
				else {
					OMElement slot = m.mkSlot("hash", hash);
					m.insertSlot(eo, slot);
				}

				String repUID = asc.get(ActorFactory.repositoryUniqueId).asString();
				OMElement rid = m.mkSlot("repositoryUniqueId", repUID);
				m.insertSlot(eo, rid);
			}

			// flush documents to repository
			for (String uid : sdMap.keySet()) {
				StoredDocument sd = sdMap.get(uid);
				common.repIndex.getDocumentCollection().add(sd);
				Io.bytesToFile(sd.getPathToDocument(), sdMap.get(uid).content);
			}
			
			// issue soap call to registry
			String endpoint = asc.get(ActorFactory.registerEndpoint).asString();
			
			Soap soap = new Soap();
			try {
				OMElement result = soap.soapCall(m.getV3SubmitObjectsRequest(), endpoint, false, true, true, SoapActionFactory.r_b_action, SoapActionFactory.getResponseAction(SoapActionFactory.r_b_action));
				ErrorRecorder rrEr = common.registryResponseAsErrorRecorder(result);
				mvc.addErrorRecorder("RegistryResponse", rrEr);
			} catch (Exception e) {
				er.err(Code.XDSRepositoryError, e);
			}


		}
		// these are all un-recoverable errors
		// make entries in the transaction log and give up
		catch (Exception e) {
			er.err(XdsErrorCode.Code.XDSRepositoryError, e);
		}
	}

}
