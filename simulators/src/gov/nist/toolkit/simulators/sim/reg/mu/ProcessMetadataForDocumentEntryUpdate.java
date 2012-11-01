package gov.nist.toolkit.simulators.sim.reg.mu;

import gov.nist.toolkit.common.datatypes.Hl7Date;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode.Code;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.simulators.sim.reg.store.Assoc;
import gov.nist.toolkit.simulators.sim.reg.store.DocEntry;
import gov.nist.toolkit.simulators.sim.reg.store.Fol;
import gov.nist.toolkit.simulators.sim.reg.store.MetadataCollection;
import gov.nist.toolkit.simulators.sim.reg.store.ProcessMetadataForRegister;
import gov.nist.toolkit.simulators.sim.reg.store.ProcessMetadataInterface;
import gov.nist.toolkit.simulators.sim.reg.store.RegIndex.AssocType;
import gov.nist.toolkit.simulators.sim.reg.store.RegIndex.StatusValue;
import gov.nist.toolkit.xdsexception.MetadataException;

import java.util.List;

import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;

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
	public void checkUidUniqueness(Metadata m) {
		
	}
	
	// does not apply
	public void setLidToId(Metadata m) {
		
	}
	
	// only initialize version on elements that we don't version
	public void setInitialVersion(Metadata m) {
		try {
			m.setDefaultVersionOfUnversionedElements();
		} catch (MetadataException e) {
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, e);
		}
	}
	
	public void setNewFolderTimes(Metadata m) {
	}

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
				} catch (Exception e) {
					er.err(Code.XDSMetadataUpdateError, e);
				}
			}
			
		}
	}

	// use the default implementation
	public void verifyAssocReferences(Metadata m) {
		new ProcessMetadataForRegister(er, mc, delta).verifyAssocReferences(m);
	}

}
