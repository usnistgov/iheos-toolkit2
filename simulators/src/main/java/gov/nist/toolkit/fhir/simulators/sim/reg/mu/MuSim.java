package gov.nist.toolkit.fhir.simulators.sim.reg.mu;

import com.sun.ebxml.registry.util.Utility;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode.Code;
import gov.nist.toolkit.fhir.simulators.sim.reg.store.*;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.fhir.simulators.sim.reg.RegRSim;
import gov.nist.toolkit.fhir.simulators.support.DsSimCommon;
import gov.nist.toolkit.simcommon.server.SimCommon;
import gov.nist.toolkit.valregmetadata.coding.Uuid;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.xdsexception.client.MetadataException;
import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class MuSim extends RegRSim {
	static Logger log = Logger.getLogger(MuSim.class);
	String ssId;

	public MuSim(SimCommon common, DsSimCommon dsSimCommon, SimulatorConfig asc) {
		super(common, dsSimCommon, asc);
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
					"Update does not contain a SubmissionSet model",
					this,
			"ITI TF-2b:3.57.4.1.3.1");
			return;
		}

		ssId = m.getId(ssEle);
		if (ssId == null || ssId.equals("")) {
			er.err(Code.XDSMetadataUpdateError,
					"Update's SubmissionSet model has no id",
					this,
			"ITI TF-2b:3.57.4.1.3.1");
			return;
		}


		// Clone metadata so we can take it apart.
		// Each little update is handled as a separate operation
		// For each operation, a separate metadata model (operation) will
		// be created containing only the metadata relevant to that operation.
		// That way, if extra, unprocessable metadata is present, we will know
		// it in the end
		// Of course nothing gets committed unless ALL operations are
		// successful

		// This clones the Metadata model, not the individual chunks of XML
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
		// This updates delta but does not save delta to disk
		save(operation, true);


		//
		// process operations
		//
		documentEntryUpdateTrigger(clone);

		// if old DE status was Deprecated - new version should be the same
		DocEntry latestDE = null;  // latest version already in registry
		//List<DocEntry> existing = delta.docEntryCollection.parent.getAll();
		// delta objects are updates
		// delta.parent objects are the state of the registry
		for (DocEntry theDe : delta.docEntryCollection.getAllForUpdate()) {
			String uid = theDe.getUid();
			List<Ro> objs = delta.getParent().getObjectsByUid(uid);  // search registry for previous versions
			for (Ro obj : objs) {
				DocEntry de = (DocEntry) obj;
				if (latestDE == null) {
					latestDE = de;
				}
				else if (de.version > latestDE.version)
					latestDE = de;
			}
			if (latestDE != null && latestDE.getAvailabilityStatus() == StatusValue.DEPRECATED) {
				theDe.setAvailabilityStatus(StatusValue.DEPRECATED);
			}
		}

		docEntryUpdateStatusTrigger(clone);

		submittedAssociationsTrigger(clone);

		updateAssociationStatusTrigger(clone);

		folderUpdateTrigger(clone);
	}

	void updateAssociationStatusTrigger(Metadata m) {
		List<OMElement> updateAssocs = m.getAssociations(m.getSubmissionSetId(), null, "urn:ihe:iti:2010:AssociationType:UpdateAvailabilityStatus");
		for (OMElement updateAssocEle : updateAssocs) {
			String updateAssocId = Metadata.getAssocTarget(updateAssocEle);
			String originalStatus = m.getSlotValue(updateAssocEle, "OriginalStatus", 0);
			boolean originalStatusisDeprecated;
			if (originalStatus.equals(RegIndex.getStatusString(StatusValue.APPROVED))) {
				originalStatusisDeprecated = false;
			} else if (originalStatus.equals(RegIndex.getStatusString(StatusValue.DEPRECATED))) {
				originalStatusisDeprecated = true;
			} else {
				er.err(Code.XDSMetadataUpdateError, "UpdateAvailabilityStatus - invalid OriginalStatus in request - " + originalStatus, null, null);
				continue;
			}
			String newStatus = m.getSlotValue(updateAssocEle, "NewStatus", 0);
			boolean newStatusIsDeprecated;
			if (newStatus.equals(RegIndex.getStatusString(StatusValue.APPROVED))) {
				newStatusIsDeprecated = false;
			} else if (newStatus.equals(RegIndex.getStatusString(StatusValue.DEPRECATED))) {
				newStatusIsDeprecated = true;
			} else {
				er.err(Code.XDSMetadataUpdateError, "UpdateAvailabilityStatus - invalid requested new status value - " + newStatus, null, null);
				continue;
			}

			Ro ro = mc.getObjectById(updateAssocId);
			if (!(ro instanceof Assoc)) {
				er.err(Code.XDSRegistryError, "UpdateAssociation points to non-association " + ro.toString(), null, null);
				continue;
			}
			Assoc assoc = (Assoc) ro;
			boolean oldStatusIsDeprecated = assoc.isDeprecated();
			if (originalStatusisDeprecated != oldStatusIsDeprecated) {
				er.err(Code.XDSRegistryError, "UpdateAssociation OriginalStatus in request is " + originalStatus + " but current Association status is " + RegIndex.getStatusString(oldStatusIsDeprecated), null, null);
				continue;
			}
			if (!RegIndex.statusValues.contains(newStatus)) {
				er.err(Code.XDSRegistryError, "UpdateAssociation NewStatus is " + newStatus + " which is not understood", null, null);
				continue;
			}
			Ro sourceObject = mc.getObjectById(assoc.from);
			Ro targetObject = mc.getObjectById(assoc.to);
			if (assoc.type == RegIndex.AssocType.HasMember && !(sourceObject instanceof Fol)) {
				er.err(Code.XDSMetadataUpdateError, "UpdateAvailabilityStatus - HasMember Association attached to SubmissionSet cannot be updated", null, null);
				continue;
			}
			if (!RegIndex.isRelationshipAssoc(assoc.type.name())) {
				er.err(Code.XDSMetadataUpdateError, "UpdateAvailabilityStatus - Association being updated must be attached to Folder or be a Relationship AssociationHasMember Association - attempting to update Association of type " + assoc.type.name(), null, null);
				continue;
			}
			delta.changeAvailabilityStatus(assoc.id, RegIndex.getStatusValue(originalStatus), RegIndex.getStatusValue(newStatus));
		}
	}

	void submittedAssociationsTrigger(Metadata m) {
		List<OMElement> submitAssocs = m.getAssociations(m.getSubmissionSetId(), null, "urn:ihe:iti:2010:AssociationType:SubmitAssociation");
		for (OMElement submitAssocEle : submitAssocs) {
			String newAssocId = Metadata.getAssocTarget(submitAssocEle);
			if (!m.isAssociation(newAssocId)) {
				er.err(Code.XDSMetadataUpdateError, "SubmitAssociation references Association " + newAssocId + " which does not exist in submission", null, null);
				continue;
			}

			OMElement newAssocEle;
			try {
				newAssocEle = m.getObjectById(newAssocId);
			} catch (Exception e) {
				er.err(Code.XDSRegistryError, "Internal error - cannot find known object in input",null, null);
				return;
			}
			String src = Metadata.getAssocSource(newAssocEle);
			String tgt = Metadata.getAssocTarget(newAssocEle);
			String type = Metadata.getAssocType(newAssocEle);
			if (!m.isUuid(src)) {
				er.err(Code.XDSMetadataUpdateError, "SubmitAssociation - sourceObject attribute must be UUID format - found " + src,null, null);
				continue;
			}
			if (!m.isUuid(tgt)) {
				er.err(Code.XDSMetadataUpdateError, "SubmitAssociation - targetObject attribute must be UUID format - found " + tgt,null, null);
				continue;
			}
			Ro srcObject = mc.getObjectById(src);
			if (srcObject == null) {
				er.err(Code.XDSMetadataUpdateError, "SubmitAssociation - sourceObject attribute does not reference an object in the registry - value " + src,null, null);
				continue;
			}
			Ro tgtObject = mc.getObjectById(tgt);
			if (tgtObject == null) {
				er.err(Code.XDSMetadataUpdateError, "SubmitAssociation - targetObject attribute does not reference an object in the registry - value " + tgt,null, null);
				continue;
			}
			if (srcObject.isDeprecated()) {
				er.err(Code.XDSMetadataUpdateError, "SubmitAssociation - sourceObject attribute references deprecated object in the registry - value " + src,null, null);
				continue;
			}
			if (tgtObject.isDeprecated()) {
				er.err(Code.XDSMetadataUpdateError, "SubmitAssociation - targetObject attribute references deprecated object in the registry - value " + tgt,null, null);
				continue;
			}
			if (srcObject instanceof SubSet) {
				er.err(Code.XDSMetadataUpdateError, "SubmitAssociation - submitted association shall not reference a SubmissionSet ", null, null);
				continue;
			}
			if (tgtObject instanceof SubSet) {
				er.err(Code.XDSMetadataUpdateError, "SubmitAssociation - submitted association shall not reference a SubmissionSet ", null, null);
				continue;
			}

			if (RegIndex.isRelationshipAssoc(type) && (srcObject instanceof Fol || tgtObject instanceof Fol) ) {
				er.err(Code.XDSMetadataUpdateError, "SubmitAssociation - submitted association of type " + type + " references a Folder", null, null);
				continue;
			}
			if (type == RegIndex.AssocType.HasMember.name()) {
				boolean error = false;
				if (!(srcObject instanceof Fol)) {
					er.err(Code.XDSMetadataUpdateError, "SubmitAssociation - submitted association of type " + type + " must reference a Folder with its sourceObject attribute", null, null);
					error = true;
				}
				if (!(tgtObject instanceof DocEntry)) {
					er.err(Code.XDSMetadataUpdateError, "SubmitAssociation - submitted association of type " + type + " must reference a DocumentEntry with its targetObject attribute", null, null);
					error = true;
				}
				if (error)
					continue;
			}

			// cannot create approved Association if PatientId does not match
			if ((srcObject instanceof PatientObject) && (tgtObject instanceof PatientObject)) {
				String srcPid = ((PatientObject) srcObject).pid;
				String tgtPid = ((PatientObject) tgtObject).pid;
				if (!srcPid.equals(tgtPid)) {
					Code code = Code.XDSMetadataUpdateError;
					if (getCommon().vc.isRMU)
						code = Code.XDSPatientIDReconciliationError;
					er.err(code, "Attempting to submit Association linking two objects with different Patient IDs", null, null);
				}
			}

			// cannot create two approved Associations between a pair of objects
			// this is implied by the overall metadata model
			List<Assoc> as = mc.assocCollection.getBySourceDestAndType(srcObject.id, tgtObject.id, null);
			List<Assoc> approved = new ArrayList<>();
			for (Assoc a : as) {
				if (!a.isDeprecated())
					approved.add(a);
			}
			if (!approved.isEmpty()) {
				er.err(Code.XDSMetadataUpdateError, "There is already an Approved Association between " + srcObject.id + " and " + tgtObject.id, null, null);
			}
			if (!er.hasErrors()) {
				try {
					delta.addAssoc(srcObject.id, tgtObject.id, RegIndex.getAssocType(type));
				} catch (Exception e) {
					er.err(Code.XDSMetadataUpdateError, e.getMessage(), null, null);
					return;
				}
			}
		}
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
			String sourceId = Metadata.getAssocSource(assoc);
			String targetId = Metadata.getAssocTarget(assoc);

			String id = Metadata.getId(assoc);
			String prefix = "Update (trigger=Assoc(" + UUIDToSymbolic.get(id) +")) - cannot process - ";
			String updateDocEntryAvailStatusRef = "ITI TF-2b:3.57.4.1.3.3.2.2";


			// Association sourceObject must be the SubmissionSet
			if (!ssId.equals(sourceId)) {
				Code code = Code.XDSMetadataUpdateError;
				if (getCommon().vc.isRMU) {
					code = Code.XDSMetadataAnnotationError;
				}
				er.err(code,
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


			new DocumentEntryStatusUpdate(common, dsSimCommon, er, simulatorConfig).run(this, operation, assoc, delta.docEntryCollection.getById(targetId), originalStatus, newStatus);

			m.rmObject(assoc);
		}

		if (m.getAllObjects().size() == 1 && m.getSubmissionSet() != null) {
			m.rmObject(m.getSubmissionSet());
			m.clearSubmissionSet();
		}
	}

	void folderUpdateTrigger(Metadata m) {
		while (m.getFolders().size() > 0) {
			OMElement folEle = m.getFolder(0);

			String id = m.getId(folEle);
			String lid = m.getLid(folEle);

			OMElement ssAssoc = m.getAssociation(ssId, id, "HasMember");
			String prevVer = null;
			if (ssAssoc != null)
				prevVer = m.getSlotValue(ssAssoc, "PreviousVersion", 0);

			String prefix = "Update (trigger=" + UUIDToSymbolic.get(id) +") - cannot process - ";
			String updateDocEntryRef = "ITI TF-2b:3.57.4.1.3.3.3";

			boolean process = true;

			if (ssAssoc == null) {
				Code code = Code.XDSMetadataUpdateError;
				if (getCommon().vc.isRMU)
					code = Code.XDSMetadataAnnotationError;
				er.err(code, prefix + "no SubmissionSet HasMember Association found", this, updateDocEntryRef);
				process = false;
			}


			if (lid == null) {
				er.err(Code.XDSMetadataUpdateError, prefix + "logicalId not found, this cannot be an update (id=" + id + " lid=" + lid + ")", this, updateDocEntryRef);
				process = false;
			}

			if (id.equals(lid)) {
				Code code = Code.XDSMetadataUpdateError;
				if (getCommon().vc.isRMU)
					code = Code.XDSInvalidRequestException;
				er.err(code, prefix + "logicalId is same as id, this cannot be an update (id=" + id + " lid=" + lid + ")", this, updateDocEntryRef);
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
				new FolderUpdate().run(this, m, folEle, ssAssoc, prevVer);


			// so we don't process these again
			m.rmObject(folEle);
			if (ssAssoc != null)
				m.rmObject(ssAssoc);
		}

		if (m.getAllObjects().size() == 1 && m.getSubmissionSet() != null)
			m.rmObject(m.getSubmissionSet());
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
				Code code = Code.XDSMetadataUpdateError;
				if (getCommon().vc.isRMU)
					code  = Code.XDSMetadataAnnotationError;
				er.err(code, prefix + "no SubmissionSet HasMember Association found", this, updateDocEntryRef);
				process = false;
			}


			if (lid == null) {
				Code code = Code.XDSMetadataUpdateError;
				if (getCommon().vc.isRMU)
					code  = Code.XDSInvalidRequestException;
				er.err(code, prefix + "logicalId not found, this cannot be an update (id=" + id + " lid=" + lid + ")", this, updateDocEntryRef);
				process = false;
			}

			if (id.equals(lid)) {
				Code code = Code.XDSMetadataUpdateError;
				if (getCommon().vc.isRMU)
					code  = Code.XDSInvalidRequestException;
				er.err(code, prefix + "logicalId is same as id, this cannot be an update (id=" + id + " lid=" + lid + ")", this, updateDocEntryRef);
				process = false;
			}

			if (lid != null && !m.isUuid(lid)) {
				er.err(Code.XDSMetadataUpdateError, prefix + "logicalId is symbolic (not a UUID)  (id=" + id + " lid=" + lid + ")", this, updateDocEntryRef);
				process = false;
			}

			if (prevVer == null) {
				Code code = Code.XDSMetadataUpdateError;
				if (getCommon().vc.isRMU)
					code  = Code.XDSMetadataAnnotationError;
				er.err(code, prefix + "PreviousVersion Slot not found on SubmissionSet to DocumentEntry HasMember Association", this, updateDocEntryRef);
				process = false;
			}


			if (process)
				new DocumentEntryUpdate().run(this, m, docEle, ssAssoc, prevVer);

			// so we don't process these again
			m.rmObject(docEle);
			if (ssAssoc != null)
				m.rmObject(ssAssoc);

			// if SS is all there is left then delete it
			if (m.getAllObjects().size() == 1 && m.getSubmissionSet() != null)
				m.rmObject(m.getSubmissionSet());
				m.clearSubmissionSet();
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



