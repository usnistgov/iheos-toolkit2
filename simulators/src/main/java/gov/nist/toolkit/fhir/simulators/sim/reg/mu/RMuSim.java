package gov.nist.toolkit.fhir.simulators.sim.reg.mu;

import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.fhir.simulators.support.DsSimCommon;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.simcommon.server.SimCommon;
import org.apache.axiom.om.OMElement;

import java.util.ArrayList;
import java.util.List;

public class RMuSim extends MuSim {
    public RMuSim(SimCommon common, DsSimCommon dsSimCommon, SimulatorConfig asc) {
        super(common, dsSimCommon, asc);
    }

    @Override
    void docEntryUpdateStatusTrigger(Metadata m) {
        List<OMElement> updateDocStatusAssocs = new ArrayList<OMElement>();

        List<OMElement> assocs = m.getAssociations();

        // find all updateDocStatus Associations
        XdsErrorCode.Code code = XdsErrorCode.Code.XDSMetadataUpdateError;
        if (getCommon().vc.isRMU)
            code = XdsErrorCode.Code.UnmodifiableMetadataError;
        for (OMElement assoc : assocs) {
            er.err(code,
                    "Error processing Association(" + UUIDToSymbolic.get(m.getId(assoc)) + ") - not updatable through RMU",
                    this,
                    null);
        }
    }

    @Override
    void submittedAssociationsTrigger(Metadata m) {
        List<OMElement> submitAssocs = m.getAssociations(m.getSubmissionSetId(), null, "urn:ihe:iti:2010:AssociationType:SubmitAssociation");
        for (OMElement submitAssocEle : submitAssocs) {
            er.err(XdsErrorCode.Code.XDSMetadataUpdateError, "SubmitAssociation  not updatable through RMU", null, null);
        }
    }

    @Override
    void updateAssociationStatusTrigger(Metadata m) {
        List<OMElement> updateAssocs = m.getAssociations(m.getSubmissionSetId(), null, "urn:ihe:iti:2010:AssociationType:UpdateAvailabilityStatus");
        for (OMElement updateAssocEle : updateAssocs) {
            er.err(XdsErrorCode.Code.XDSMetadataUpdateError, "UpdateAssociation  not updatable through RMU", null, null);
        }
    }

    @Override
    void folderUpdateTrigger(Metadata m) {
        while (m.getFolders().size() > 0) {
            er.err(XdsErrorCode.Code.XDSMetadataUpdateError, "FolderUpdate  not updatable through RMU", null, null);
        }
    }
}
