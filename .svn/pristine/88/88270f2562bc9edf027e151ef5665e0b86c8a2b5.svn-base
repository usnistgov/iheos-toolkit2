package gov.nist.toolkit.simulators.sim.reg.mu;

import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode.Code;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.simulators.sim.reg.RegRSim;
import gov.nist.toolkit.simulators.sim.reg.store.ProcessMetadataForRegister;
import gov.nist.toolkit.simulators.sim.reg.store.ProcessMetadataInterface;
import gov.nist.toolkit.simulators.sim.reg.store.RegIndex;
import gov.nist.toolkit.simulators.support.SimCommon;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.xdsexception.MetadataException;

import java.util.ArrayList;
import java.util.List;

import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;

public class MuSim extends RegRSim {
	Exception startUpException = null;
//	public ErrorRecorder er;
//	MessageValidatorContext mvc;
//	public MetadataCollection delta;
	static Logger log = Logger.getLogger(MuSim.class);
	String ssId;

	public MuSim(SimCommon common, SimulatorConfig asc) {
		super(common, asc);
	}


	public void run(ErrorRecorder er, MessageValidatorEngine mvc) {
		this.er = er;
		this.mvc = mvc;

		// These steps are common to Registry and Update.  They operate
		// on the entire metadata collection in both transactions.
		setup();

		// these two check should never fail, but just in case
		OMElement ssEle = m.getSubmissionSet();
		if (ssEle == null) {
			er.err(Code.XDSMetadataUpdateError, 
					"Update does not contain a SubmissionSet object", 
					this, 
			"ITI TF-2b:3.57.4.1.3.1");
			return;
		}

		ssId = m.getId(ssEle);
		if (ssId == null || ssId.equals("")) {
			er.err(Code.XDSMetadataUpdateError, 
					"Update's SubmissionSet object has no id", 
					this, 
			"ITI TF-2b:3.57.4.1.3.1");
			return;
		}


		// Clone metadata so we can take it apart.
		// Each little update is handled as a separate operation
		// For each operation, a separate metadata object (operation) will
		// be created containing only the metadata relevant to that operation.
		// That way, if extra, unprocessable metadata is present, we will know
		// it in the end
		// Of course nothing gets committed unless ALL operations are
		// successful

		// This clones the Metadata object, not the individual chunks of XML
		// (shallow copy)
		Metadata clone;
		try {
			clone = m.mkClone();
		} catch (Exception e) {
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, e);
			return;
		}
		OMElement ssClone = clone.getSubmissionSet();
		clone.rmObject(ssClone);

		Metadata operation = new Metadata();
		operation.addSubmissionSet(ssClone);
		
		allocateUUIDs(operation);
		
		ProcessMetadataInterface pmi = new ProcessMetadataForRegister(er, mc, delta);
		
		pmi.checkUidUniqueness(operation);

		// set logicalId to id 
		pmi.setLidToId(operation);

		// install version attribute in SubmissionSet, DocumentEntry and Folder objects
		// install default version in Association, Classification, ExternalIdentifier
		pmi.setInitialVersion(operation);



		// save metadata index and objects - seems too early
		// recheck this later
		save(operation, true);


		//
		// process operations
		//
		documentEntryUpdateTrigger(clone);

		docEntryUpdateStatusTrigger(clone);


	}


	void docEntryUpdateStatusTrigger(Metadata m) {
		List<OMElement> updateDocStatusAssocs = new ArrayList<OMElement>();
		
		List<OMElement> assocs = m.getAssociations();
		
		// find all updateDocStatus Associations
		for (OMElement assoc : assocs) {

			try {
				if (!m.getSimpleAssocType(assoc).equals("UpdateAvailabilityStatus"))
					continue;
			}
			catch (MetadataException e) {
				er.err(Code.XDSMetadataUpdateError, 
						"Error processing Association(" + UUIDToSymbolic.get(m.getId(assoc)) + "): " + e.getMessage(), 
						this, 
						null);
				assocs.remove(assoc);  // already recorded error - don't process again
				continue;
			}

			String targetId = m.getAssocTarget(assoc);

			// is target a DocumentEntry (new or existing)
			if (delta.docEntryCollection.getById(targetId) == null) {
				// not a DocEntry
				continue;
			}
			
			updateDocStatusAssocs.add(assoc);
		}
		
		// remove updateDocStatus Associations from main metadata
		assocs.removeAll(updateDocStatusAssocs);
		
		// process updateDocStatus associations
		for (OMElement assoc : updateDocStatusAssocs) {
			String sourceId = m.getAssocSource(assoc);
			String targetId = m.getAssocTarget(assoc);

			String id = m.getId(assoc);
			String prefix = "Update (trigger=Assoc(" + UUIDToSymbolic.get(id) +")) - cannot process - ";
			String updateDocEntryAvailStatusRef = "ITI TF-2b:3.57.4.1.3.3.2.2";

			
			// Association sourceObject must be the SubmissionSet
			if (!ssId.equals(sourceId)) {
				er.err(Code.XDSMetadataUpdateError, 
						prefix + "Association("  + getIdSubmittedValue(id) + "): sourceId does not reference the SubmissionSet", 
						this, 
						updateDocEntryAvailStatusRef);
			}

			// Association contains OriginalStatus Slot
			
			String originalStatus = verifySlotSingleValue(m, assoc,  "OriginalStatus",  prefix,  updateDocEntryAvailStatusRef);

			// Association contains NewStatus Slot
			String newStatus = verifySlotSingleValue(m, assoc,  "NewStatus",  prefix,  updateDocEntryAvailStatusRef);
			
			// newStatus is legal for DocumentEntry
			if (!RegIndex.docEntryLegalStatusValues.contains(RegIndex.getStatusValue(newStatus))) {
				er.err(Code.XDSMetadataUpdateError, 
						prefix + "New availabilityStatus for DocumentEntry, " + newStatus + " is not a legal status for a DocumentEntry: Association("  + getIdSubmittedValue(id) + ")", 
						this, 
						updateDocEntryAvailStatusRef);
			}
			
			Metadata operation = new Metadata();
			operation.add_association(assoc);
			
			
			new DocumentEntryStatusUpdate(common, er, asc).run(this, operation, assoc, delta.docEntryCollection.getById(targetId), originalStatus, newStatus);

		}
	}


	void documentEntryUpdateTrigger(Metadata m) {
		while (m.getExtrinsicObjects().size() > 0) {
			OMElement docEle = m.getExtrinsicObject(0);

			String id = m.getId(docEle);
			String lid = m.getLid(docEle);

			OMElement ssAssoc = m.getAssociation(ssId, id, "HasMember");
			String prevVer = null;
			if (ssAssoc != null)
				prevVer = m.getSlotValue(ssAssoc, "PreviousVersion", 0);

			String prefix = "Update (trigger=" + UUIDToSymbolic.get(id) +") - cannot process - ";
			String updateDocEntryRef = "ITI TF-2b:3.57.4.1.3.3.1.2";


			boolean process = true;

			if (ssAssoc == null) {
				er.err(Code.XDSMetadataUpdateError, prefix + "no SubmissionSet HasMember Association found", this, updateDocEntryRef);
				process = false;
			}


			if (lid == null) {
				er.err(Code.XDSMetadataUpdateError, prefix + "logicalId not found, this cannot be an update (id=" + id + " lid=" + lid + ")", this, updateDocEntryRef);
				process = false;
			}

			if (id.equals(lid)) {
				er.err(Code.XDSMetadataUpdateError, prefix + "logicalId is same as id, this cannot be an update (id=" + id + " lid=" + lid + ")", this, updateDocEntryRef);
				process = false;
			}

			if (lid != null && !m.isUuid(lid)) {
				er.err(Code.XDSMetadataUpdateError, prefix + "logicalId is symbolic (not a UUID)  (id=" + id + " lid=" + lid + ")", this, updateDocEntryRef);
				process = false;
			}

			if (prevVer == null) {
				er.err(Code.XDSMetadataUpdateError, prefix + "PreviousVersion Slot not found on SubmissionSet to DocumentEntry HasMember Association", this, updateDocEntryRef);
				process = false;
			}


			if (process) 
				new DocumentEntryUpdate(common, er).run(this, m, docEle, ssAssoc, prevVer);

			// so we don't process these again
			m.rmObject(docEle);
			if (ssAssoc != null)
				m.rmObject(ssAssoc);

		}
	}

	String verifySlotSingleValue(Metadata m, OMElement ele, String slotName, String prefix, String docRef) {
		OMElement slotEle = m.getSlot(ele, slotName);
		if (slotEle == null) {
			er.err(Code.XDSMetadataUpdateError, 
					prefix + "Association("  + getIdSubmittedValue(m.getId(ele)) + "): " + slotName + " Slot not present", 
					this, 
					docRef);
			return null;
		} 
		// must contain single value
		List<String> values = m.getSlotValues(ele, slotName);
		if (values.size() != 1) {
			er.err(Code.XDSMetadataUpdateError, 
					prefix + "Association("  + getIdSubmittedValue(m.getId(ele)) + "): " + slotName + " Slot must have one value", 
					this, 
					docRef);
		} 
		if (values.size() > 0)
			return values.get(0);
		return null;
	}


}



