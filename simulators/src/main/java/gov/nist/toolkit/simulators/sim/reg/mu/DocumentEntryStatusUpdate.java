package gov.nist.toolkit.simulators.sim.reg.mu;

import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode.Code;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.simulators.sim.reg.RegRSim;
import gov.nist.toolkit.simulators.sim.reg.store.DocEntry;
import gov.nist.toolkit.simulators.sim.reg.store.RegIndex;
import gov.nist.toolkit.simulators.sim.reg.store.RegIndex.StatusValue;
import gov.nist.toolkit.simulators.support.SimCommon;
import gov.nist.toolkit.valsupport.client.ValidationContext.MetadataPattern;

import org.apache.axiom.om.OMElement;

public class DocumentEntryStatusUpdate extends RegRSim {
	ErrorRecorder er;
	
	public DocumentEntryStatusUpdate(SimCommon common, ErrorRecorder er, SimulatorConfig asc) {
		super(common, asc);
		this.er = er;
	}

	public void run(MuSim muSim, Metadata m, OMElement assoc, DocEntry docEntry, String origStatus, String newStatus) {
		
		try {
			er.detail("Update trigger is Association(" + m.getId(assoc) + ") " + m.getSimpleAssocType(assoc) + "\n" +
					"\t\tsourceObject=" + m.getAssocSource(assoc)  +
					"\t\ttargetObject=" + m.getAssocTarget(assoc)  
			);
		} catch (Exception e) {}
		
		String prefix = "Update (trigger=Association(" + muSim.UUIDToSymbolic.get(m.getId(assoc)) +")) - ";

		
		vc.addMetadataPattern(MetadataPattern.UpdateDocumentEntryStatus);
		
		StatusValue origStatValue = RegIndex.getStatusValue(origStatus);
		if (origStatValue != docEntry.getAvailabilityStatus()) {
			er.err(Code.XDSMetadataUpdateError, 
					prefix + "OriginalStatus Slot on UpdateAvailabilityStatus Association (" + origStatValue + " does not match existing availabilityStatus on DocumentEntry(" +
					docEntry.getId() + ") " + docEntry.getAvailabilityStatus(),
					this, "ITI TF-2b:3.57.4.1.3.3.2.3");

		}
		
		String lid = docEntry.lid;
		
		DocEntry latest = muSim.delta.docEntryCollection.getLatestVersion(lid);
		
		String latestId = latest.getId();
		String targetId = m.getAssocTarget(assoc);
		
		if (!latestId.equals(targetId)) {
			er.err(Code.XDSMetadataUpdateError, 
					prefix + "target DocumentEntry(" + latestId +") is not the most recent version",
					this, "ITI TF-2b:3.57.4.1.3.3.2.3");
		}

		
		if (!er.hasErrors()) {
			muSim.save(m, true);
			// perform status change
			muSim.delta.changeAvailabilityStatus(latest.getId(), RegIndex.getStatusValue(origStatus), RegIndex.getStatusValue(newStatus));
		}

	}

}
