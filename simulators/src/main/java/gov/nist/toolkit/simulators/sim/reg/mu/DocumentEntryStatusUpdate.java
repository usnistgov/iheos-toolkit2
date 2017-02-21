package gov.nist.toolkit.simulators.sim.reg.mu;

import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.common.XdsErrorCode;
import gov.nist.toolkit.errorrecording.xml.assertions.Assertion;
import gov.nist.toolkit.errorrecording.xml.assertions.AssertionLibrary;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.simulators.sim.reg.RegRSim;
import gov.nist.toolkit.simulators.sim.reg.store.DocEntry;
import gov.nist.toolkit.simulators.sim.reg.store.RegIndex;
import gov.nist.toolkit.simulators.sim.reg.store.RegIndex.StatusValue;
import gov.nist.toolkit.simulators.support.DsSimCommon;
import gov.nist.toolkit.simulators.support.SimCommon;
import gov.nist.toolkit.valsupport.client.ValidationContext.MetadataPattern;

import org.apache.axiom.om.OMElement;

public class DocumentEntryStatusUpdate extends RegRSim {
	ErrorRecorder er;
	DsSimCommon dsSimCommon;
	private AssertionLibrary ASSERTIONLIBRARY = AssertionLibrary.getInstance();

	public DocumentEntryStatusUpdate(SimCommon common, DsSimCommon dsSimCommon, ErrorRecorder er, SimulatorConfig asc) {
		super(common, dsSimCommon, asc);
		this. dsSimCommon = dsSimCommon;
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
			Assertion assertion = ASSERTIONLIBRARY.getAssertion("TA101");
			String detail = "Found: '" + prefix + "'; " + "originalStatus value found: '" + origStatValue + "'; " +
					"availabilityStatus on DocumentEntry: '" + docEntry.getId() + " " + docEntry.getAvailabilityStatus() + "'";
			er.err(XdsErrorCode.Code.XDSMetadataUpdateError, assertion, this, "", detail);
		}

		String lid = docEntry.lid;

		DocEntry latest = muSim.delta.docEntryCollection.getLatestVersion(lid);

		String latestId = latest.getId();
		String targetId = m.getAssocTarget(assoc);

		if (!latestId.equals(targetId)) {
			Assertion assertion = ASSERTIONLIBRARY.getAssertion("TA102");
			String detail = "Found: '" + prefix + "'; " + "target DocumentEntry ID found: '" + latestId +"'";
			er.err(XdsErrorCode.Code.XDSMetadataUpdateError, assertion, this, "", detail);
		}

		if (!er.hasErrors()) {
			muSim.save(m, true);
			// perform status change
			muSim.delta.changeAvailabilityStatus(latest.getId(), RegIndex.getStatusValue(origStatus), RegIndex.getStatusValue(newStatus));
		}

	}

}
