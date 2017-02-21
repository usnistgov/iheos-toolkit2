package gov.nist.toolkit.simulators.sim.reg.mu;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.common.XdsErrorCode;
import gov.nist.toolkit.errorrecording.xml.assertions.Assertion;
import gov.nist.toolkit.errorrecording.xml.assertions.AssertionLibrary;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.simulators.sim.reg.store.DocEntry;
import gov.nist.toolkit.simulators.sim.reg.store.RegIndex.StatusValue;
import gov.nist.toolkit.simulators.support.SimCommon;
import gov.nist.toolkit.valsupport.client.ValidationContext.MetadataPattern;
import gov.nist.toolkit.xdsexception.MetadataException;

import org.apache.axiom.om.OMElement;

public class DocumentEntryUpdate  {
	ErrorRecorder er;
	private AssertionLibrary ASSERTIONLIBRARY = AssertionLibrary.getInstance();


	public DocumentEntryUpdate(SimCommon common, ErrorRecorder er) {
//		super(common);
//		this.er = er;
	}

	public void run(MuSim muSim, Metadata m, OMElement docEle, OMElement ssAssoc, String prevVer) {
		er = muSim.er;
		String lid = m.getLid(docEle);
		String id = m.getId(docEle);
		String prefix = "Update (trigger=" + muSim.UUIDToSymbolic.get(id) +") - ";

		muSim.getCommon().vc.addMetadataPattern(MetadataPattern.UpdateDocumentEntry);

		DocEntry latest = muSim.delta.docEntryCollection.getLatestVersion(lid);

		if (latest == null) {
			Assertion assertion = ASSERTIONLIBRARY.getAssertion("TA103");
			String detail = "DocumentEntry found: '" + prefix + "'";
			er.err(XdsErrorCode.Code.XDSMetadataUpdateError, assertion, this, "", detail);
			return;
		}

		if (latest.getAvailabilityStatus() != StatusValue.APPROVED) {
			Assertion assertion = ASSERTIONLIBRARY.getAssertion("TA104");
			String detail = "DocumentEntry found: '" + prefix + "'; availabilityStatus found (should be \'Approved\'): '" + latest.getAvailabilityStatus() + "'";
			er.err(XdsErrorCode.Code.XDSMetadataUpdateError, assertion, this, "", detail);
		}
		String submittedUid = "";
		try {
			submittedUid = m.getUniqueIdValue(docEle);
		} catch (MetadataException e) {
			Assertion assertion = ASSERTIONLIBRARY.getAssertion("TA105");
			String detail = "DocumentEntry found: '" + prefix + "'";
			er.err(XdsErrorCode.Code.XDSMetadataUpdateError, assertion, this, "", detail);
		}
		if (!latest.getUid().equals(submittedUid)) {
			Assertion assertion = ASSERTIONLIBRARY.getAssertion("TA106");
			String detail = "DocumentEntry found: '" + prefix + "'; the previous version of the DocumentEntry has a uniqueID value of: '" +
					latest.getUid() + "'; the update has: '" + submittedUid + "'";
			er.err(XdsErrorCode.Code.XDSMetadataUpdateError, assertion, this, "", detail);
		}
		String objectType = "";
		try {
			objectType = m.getObjectTypeById(id);
		} catch (MetadataException e) {
			Assertion assertion = ASSERTIONLIBRARY.getAssertion("TA107");
			String detail = "DocumentEntry found: '" + prefix + "'";
			er.err(XdsErrorCode.Code.XDSMetadataUpdateError, assertion, this, "", detail);
		}

		if (!latest.objecttype.equals(objectType)) {
			Assertion assertion = ASSERTIONLIBRARY.getAssertion("TA108");
			String detail = "DocumentEntry found: '" + prefix + "'; the previous version of the DocumentEntry has an objectType value of: '" +
					latest.objecttype + "'; the update has: '" + objectType + "'";
			er.err(XdsErrorCode.Code.XDSMetadataUpdateError, assertion, this, "", detail);
		}

		String latestVerStr = String.valueOf(latest.version);
		if (!latestVerStr.equals(prevVer)) {
			Assertion assertion = ASSERTIONLIBRARY.getAssertion("TA109");
			String detail = "DocumentEntry found: '" + prefix + "'; the previousVersion value for this DocumentEntry is: '" +
					prevVer + "'; the latest version in the Registry is: '" + latestVerStr + "'";
			er.err(XdsErrorCode.Code.XDSMetadataVersionError, assertion, this, "", detail);
		}
		m.setVersion(docEle, String.valueOf(latest.version + 1));

		// install default version in Association, Classification, ExternalIdentifier
		try {
			m.setDefaultVersionOfUnversionedElements();
		} catch (MetadataException e) {
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, e);
		}

		Metadata operation = new Metadata();

		operation.addExtrinsicObject(docEle);
		operation.add_association(ssAssoc);

		// run normal processing for a Register
		// Note that this method farms out all the work to other
		// worker methods that can be overridden by this class to
		// control the processing. (That's why this class inherits
		// from RegRSim).
		muSim.processMetadata(operation, new ProcessMetadataForDocumentEntryUpdate(er, muSim.mc, muSim.delta));

		if (!muSim.hasErrors()) {
			muSim.save(operation, false);
			// deprecate last version
			muSim.delta.changeAvailabilityStatus(latest.getId(), StatusValue.APPROVED, StatusValue.DEPRECATED);
		}

	}


}
