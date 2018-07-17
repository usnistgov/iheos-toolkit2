package gov.nist.toolkit.fhir.simulators.sim.reg.mu;

import gov.nist.toolkit.common.datatypes.Hl7Date;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode.Code;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.fhir.simulators.sim.reg.store.*;
import gov.nist.toolkit.fhir.simulators.sim.reg.store.RegIndex.AssocType;
import gov.nist.toolkit.fhir.simulators.sim.reg.store.StatusValue;
import gov.nist.toolkit.xdsexception.client.MetadataException;
import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;

import java.util.List;

public class ProcessMetadataForDocumentEntryUpdate implements ProcessMetadataInterface {
	static Logger log = Logger.getLogger(ProcessMetadataForDocumentEntryUpdate.class);
	ErrorRecorder er;
	MetadataCollection mc;
	MetadataCollection delta;
	String now;

	public ProcessMetadataForDocumentEntryUpdate(ErrorRecorder er, MetadataCollection mc, MetadataCollection delta) {
		this.er = er;
		this.mc = mc;
		this.delta = delta;
		this.now = new Hl7Date().now();
	}

	// this check is part of the update process and follows different rules
	// this is important for submission but does not apply to MU
	@Override
	public void checkUidUniqueness(Metadata m) {
		
	}
	
	// does not apply
	@Override
	public void setLidToId(Metadata m) {
		
	}
	
	// only initialize version on elements that we don't version
	@Override
	public void setInitialVersion(Metadata m) {
		try {
			m.setDefaultVersionOfUnversionedElements();
		} catch (MetadataException e) {
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, e);
		}
	}

	@Override
	public void setNewFolderTimes(Metadata m) {
	}

	@Override
	public void updateExistingFolderTimes(Metadata m) {
		for (OMElement eo : m.getExtrinsicObjects()) {
			String eoLid = m.getLid(eo);
			DocEntry de = delta.docEntryCollection.getLatestVersion(eoLid);
			List<Assoc> assocs = delta.assocCollection.getBySourceDestAndType(null, de.getId(), AssocType.HASMEMBER);
			for (Assoc a : assocs) {
				String fid = a.getFrom();
				Fol fol = delta.folCollection.getById(fid);
				if (fol == null)
					continue;
				fol.setLastUpdateTime(now);
			}
		}
	}

	@Override
	public  void doRPLCDeprecations(Metadata m) {
		for (OMElement a : m.getAssociations()) {
			try {
				if (!"RPLC".equals(m.getSimpleAssocType(a)))
					continue;
			} catch (Exception e) {
				continue;
			}
			String docId = m.getAssocTarget(a);
			DocEntry de = delta.docEntryCollection.getById(docId);
			
			DocEntry prevDE = delta.docEntryCollection.getPreviousVersion(de);
			if (prevDE != null)
				prevDE.setAvailabilityStatus(StatusValue.DEPRECATED);
		}
	}

	@Override
	public void updateExistingFoldersWithReplacedDocs(Metadata m) {
		for (OMElement a : m.getAssociations()) {
			try {
				if (!"RPLC".equals(m.getSimpleAssocType(a)))
					continue;
			} catch (Exception e) {
				continue;
			}
			String docId = m.getAssocSource(a);
			String origDocId = m.getAssocTarget(a);
			DocEntry de = delta.docEntryCollection.getById(docId);
			DocEntry origDE = delta.docEntryCollection.getById(origDocId);
			
			List<Fol> origDEFols = mc.getFoldersContaining(origDE);
			for (Fol f : origDEFols) {
				try {
					delta.addDocEntryToFolAssoc(de, f);
					f.setLastUpdateTime(now);
				} catch (Exception e) {
					er.err(Code.XDSMetadataUpdateError, e);
				}
			}
			
		}
	}

	@Override
	// verify that no associations are being added that:
	//     link objects with different patient ids (except for special cases)
	public void associationPatientIdRules() {

		for (Assoc a : delta.assocCollection.assocs) {
			String fromId = a.getFrom();
			String toId = a.getTo();

			Ro sourceObject = delta.getObjectById(fromId);
			if (sourceObject == null) sourceObject = mc.getObjectById(fromId);

			Ro targetObject = delta.getObjectById(toId);
			if (targetObject == null) targetObject = mc.getObjectById(toId);

			if (sourceObject == null || targetObject == null) continue;  // checked elsewhere

			PatientObject src = null;
			if (sourceObject instanceof PatientObject) src = (PatientObject) sourceObject;
			PatientObject tgt = null;
			if (targetObject instanceof PatientObject) tgt = (PatientObject) targetObject;

			if (src == null || tgt == null) continue;

			if (src.pid != null && src.pid.equals(tgt.pid)) continue;  // all is good

			// if is Reference type HasMember then ok
			if (a.isReference) continue;
			er.err(Code.XDSPatientIdDoesNotMatch, "Association " +
					a.getType() + "(" + a.getId() + ")" +
					" links two objects with different Patient IDs: " +
					src.getType() + "(" + src.getId() + ") and " +
					tgt.getType() + "(" + tgt.getId() + ") "
					, this, null);
		}
	}

	@Override
	public void addDocsToUpdatedFolders(Metadata m) {

	}

	// use the default implementation
	@Override
	public void verifyAssocReferences(Metadata m) {
		new ProcessMetadataForRegister(er, mc, delta).verifyAssocReferences(m);
	}

}
