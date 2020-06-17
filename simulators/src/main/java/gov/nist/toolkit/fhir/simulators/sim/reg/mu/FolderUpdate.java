package gov.nist.toolkit.fhir.simulators.sim.reg.mu;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.fhir.simulators.sim.reg.store.Fol;
import gov.nist.toolkit.fhir.simulators.sim.reg.store.ProcessMetadataInterface;
import gov.nist.toolkit.fhir.simulators.sim.reg.store.StatusValue;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.xdsexception.client.MetadataException;
import org.apache.axiom.om.OMElement;

public class FolderUpdate {
    ErrorRecorder er;

    public void run(MuSim muSim, Metadata m, OMElement folEle, OMElement ssAssoc, String prevVer) {
        er = muSim.er;
        String lid = m.getLid(folEle);
        String id = m.getId(folEle);
        String prefix = "Update (trigger=" + muSim.UUIDToSymbolic.get(id) +") - ";

        muSim.getCommon().vc.addMetadataPattern(ValidationContext.MetadataPattern.UpdateFolder);

        Fol latest = muSim.delta.folCollection.getLatestVersion(lid);

        if (latest == null) {
            XdsErrorCode.Code code = XdsErrorCode.Code.XDSMetadataUpdateError;
            if (muSim.getCommon().vc.isRMU)
                code = XdsErrorCode.Code.XDSInvalidRequestException;
            er.err(code, prefix + "existing Folder not present in Registry", this, "ITI TF-2b:3.57.4.1.3.3.3.2");
            return;
        }

        if (latest.getAvailabilityStatus() != StatusValue.APPROVED) {
            XdsErrorCode.Code code = XdsErrorCode.Code.XDSMetadataUpdateError;
            if (!muSim.getCommon().vc.isRMU) {
                //code = XdsErrorCode.Code.XDSMetadataVersionError;
                er.err(code,
                        prefix + "previous version does not have availabilityStatus of Approved, " + latest.getAvailabilityStatus() + " found instead",
                        this, "ITI TF-2b:3.57.4.1.3.3.3.2");
            }
        }

        String submittedUid = "";
        try {
            submittedUid = m.getUniqueIdValue(folEle);
        } catch (MetadataException e) {
            XdsErrorCode.Code code = XdsErrorCode.Code.XDSMetadataUpdateError;
            if (muSim.getCommon().vc.isRMU)
                code = XdsErrorCode.Code.XDSMetadataIdentifierError;
            er.err(code,
                    prefix + "cannot extract uniqueId from submitted metadata",
                    this, "ITI TF-2b:3.57.4.1.3.3.3.2");
        }

        if (!latest.getUid().equals(submittedUid)) {
            XdsErrorCode.Code code = XdsErrorCode.Code.XDSMetadataUpdateError;
            if (muSim.getCommon().vc.isRMU)
                code = XdsErrorCode.Code.XDSMetadataIdentifierError;
            er.err(code,
                    prefix + "previous version does not have same value for uniqueId: " +
                            " previous version has " + latest.getUid() +
                            " update has " + submittedUid,
                    this, "ITI TF-2b:3.57.4.1.3.3.3.2");
        }
        String latestVerStr = String.valueOf(latest.version);
        if (!latestVerStr.equals(prevVer))
            er.err(XdsErrorCode.Code.XDSMetadataVersionError,
                    prefix + "PreviousVersion from submission and latest Registry version do not match - " +
                            " PreviousVersion is " + prevVer +
                            " and latest version in Registry is " + latestVerStr,
                    this, "ITI TF-2b:3.57.4.1.3.3.3.2");

        m.setVersion(folEle, String.valueOf(latest.version + 1));

        // install default version in Association, Classification, ExternalIdentifier
        try {
            m.setDefaultVersionOfUnversionedElements();
        } catch (MetadataException e) {
            er.err(XdsErrorCode.Code.XDSRegistryMetadataError, e);
        }

        Metadata operation = new Metadata();

        operation.addFolder(folEle);
        operation.add_association(ssAssoc);
        operation.addSubmissionSet(m.getSubmissionSet());

        boolean associationPropagation = true;


        // run normal processing for a Register
        // Note that this method farms out all the work to other
        // worker methods that can be overridden by this class to
        // control the processing. (That's why this class inherits
        // from RegRSim).
        ProcessMetadataInterface pmi = new ProcessMetadataForFolderUpdate(er, muSim.mc, muSim.delta);
        try {
            muSim.processMetadata(operation, pmi);
        } catch (Exception e) {
            er.err(XdsErrorCode.Code.XDSRegistryMetadataError, e);
        }

        if (!muSim.hasErrors()) {
            muSim.save(operation, false);
            // deprecate last version
            muSim.delta.changeAvailabilityStatus(latest.getId(), StatusValue.APPROVED, StatusValue.DEPRECATED);
        }


    }
}
