package gov.nist.toolkit.fhir.simulators.sim.reg.mu;

import gov.nist.toolkit.common.datatypes.Hl7Date;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode.Code;
import gov.nist.toolkit.fhir.simulators.sim.reg.store.*;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class ProcessMetadataForFolderUpdate implements ProcessMetadataInterface {
	static Logger log = Logger.getLogger(ProcessMetadataForFolderUpdate.class);
	ErrorRecorder er;
	MetadataCollection mc;
	MetadataCollection delta;
	String now;
	private boolean associationPropogationEnabled = true;

	public ProcessMetadataForFolderUpdate(ErrorRecorder er, MetadataCollection mc, MetadataCollection delta) {
		this.er = er;
		this.mc = mc;
		this.delta = delta;
		this.now = new Hl7Date().now();
	}

	// this check is part of the update process and follows different rules
	@Override
	public void checkUidUniqueness(Metadata m) {
	}

	// does not apply
	@Override
	public void setLidToId(Metadata m) {
	}

	// only initialize version on elements that we don't version
	// this is done from DocEntry - no need to repeat
	@Override
	public void setInitialVersion(Metadata m) {
	}

	@Override
	public void setNewFolderTimes(Metadata m) {
		// time copied from old version of folder
		for (OMElement folEle : m.getFolders()) {
			Fol fol = mc.folCollection.getLatestVersion(Metadata.getLid(folEle));
			if (fol != null) {
				String lastUpdateTime = fol.lastUpdateTime;
				m.setLastUpdateTime(folEle, lastUpdateTime);
			}
		}
	}

	// DocEntry class handles this
	@Override
	public void updateExistingFolderTimes(Metadata m) {
	}

	// only applies to DocEntries
	@Override
	public  void doRPLCDeprecations(Metadata m) {
	}

	@Override
	public void updateExistingFoldersWithReplacedDocs(Metadata m) {
	}

	// The DocEntry and Association must have status = Approved
	private List<DocEntry> docEntriesLinkedToFolderWithApprovedStatus(Fol fol) {
		List<DocEntry> docEntries = new ArrayList<>();

		for (Assoc assoc : delta.assocCollection.getAll()) {
			if (!assoc.from.equals(fol.id))
				continue;
			if (assoc.type != RegIndex.AssocType.HasMember)
				continue;
			if (assoc.getAvailabilityStatus() != StatusValue.APPROVED)
				continue;
			DocEntry docEntry = delta.docEntryCollection.getById(assoc.to);
			if (docEntry == null)
				continue;  // should never happen
			if (docEntry.getAvailabilityStatus() != StatusValue.APPROVED)
				continue;
			docEntries.add(docEntry);
		}

		return docEntries;
	}

	// verify that no associations are being added that:
	//     link objects with different patient ids (except for special cases)
	@Override
	public void associationPatientIdRules() {
		new ProcessMetadataForDocumentEntryUpdate(er, mc, delta).associationPatientIdRules();
	}

	// this implements 3.57.4.1.3.3.3.5 Association Propagation
	// when folder is updated, all the contents are linked to new version of folder
	//
	// When this runs the new objects will already have been added to delta
	@Override
	public void addDocsToUpdatedFolders(Metadata m) {
		for (OMElement updateFolEle : m.getFolders()) {
			String folLid = Metadata.getLid(updateFolEle);
			boolean associationPropagation = MuCommon.associationPropagation(m, updateFolEle, er);
			if (associationPropagation) {
				// latestFol is update, previousFol is the previous one (that is being replaced)
				Fol latestFol = delta.folCollection.getLatestVersion(folLid);
				// verify this and updateFolEle are the same
				if (!Metadata.getId(updateFolEle).equals(latestFol.id)) {
					er.err(Code.XDSMetadataUpdateError, "Unknown internal error #1 processing updates to Folder " + latestFol.id, "", "");
					return;
				}
				Fol previousFol = delta.folCollection.getPreviousVersion(latestFol);
				if (previousFol == null) {
					er.err(Code.XDSMetadataUpdateError, "Unknown internal error #2 processing updates to Folder " + latestFol.id, "", "");
					return;
				}
				if (previousFol.getAvailabilityStatus() != StatusValue.APPROVED) {
					er.err(Code.XDSMetadataUpdateError, "Folder being updated does not have Approved status " + latestFol.id, "", "");
					continue;
				}

				List<DocEntry> existingLinkedDocEntries = docEntriesLinkedToFolderWithApprovedStatus(previousFol);
				for (DocEntry docEntry : existingLinkedDocEntries) {
					try {
						delta.addDocEntryToFolAssoc(docEntry.id, latestFol.id);
					} catch (Exception e) {
						er.err(Code.XDSMetadataUpdateError, "Error with Association Propagation on Folder (lid) " + folLid + " -\n" + ExceptionUtil.exception_details(e), "", "");
						continue;
					}
				}
			}
		}
	}

	// use the default implementation
	@Override
	public void verifyAssocReferences(Metadata m) {
		new ProcessMetadataForRegister(er, mc, delta).verifyAssocReferences(m);
	}

}
