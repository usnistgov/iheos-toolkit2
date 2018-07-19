package gov.nist.toolkit.fhir.simulators.sim.reg.mu;

import gov.nist.toolkit.common.datatypes.Hl7Date;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode.Code;
import gov.nist.toolkit.fhir.simulators.sim.reg.store.*;
import gov.nist.toolkit.registrymetadata.Metadata;
import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;

import java.util.List;

public class ProcessMetadataForFolderUpdate implements ProcessMetadataInterface {
	static Logger log = Logger.getLogger(ProcessMetadataForFolderUpdate.class);
	ErrorRecorder er;
	MetadataCollection mc;
	MetadataCollection delta;
	String now;

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
		for (OMElement a : m.getAssociations()) {
			try {
				if (!"RPLC".equals(m.getSimpleAssocType(a)))
					continue;
			} catch (Exception e) {
				continue;
			}
			String docId = Metadata.getAssocSource(a);
			String origDocId = Metadata.getAssocTarget(a);
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

	// verify that no associations are being added that:
	//     link objects with different patient ids (except for special cases)
	@Override
	public void associationPatientIdRules() {
		new ProcessMetadataForDocumentEntryUpdate(er, mc, delta).associationPatientIdRules();
	}

	// when folder is updated, all the contents are linked to new version of folder
	@Override
	public void addDocsToUpdatedFolders(Metadata m) {
		for (OMElement folEle : m.getFolders()) {
			Fol fol = mc.folCollection.getLatestVersion(Metadata.getLid(folEle));
			if (fol != null) {

			}
		}
	}

	// use the default implementation
	@Override
	public void verifyAssocReferences(Metadata m) {
		new ProcessMetadataForRegister(er, mc, delta).verifyAssocReferences(m);
	}

}
