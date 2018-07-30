package gov.nist.toolkit.fhir.simulators.sim.reg.mu;

import gov.nist.toolkit.commondatatypes.MetadataSupport;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.fhir.simulators.sim.reg.store.RegIndex;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.xdsexception.client.ToolkitRuntimeException;
import org.apache.axiom.om.OMElement;

class MuCommon {

    // is Association Propagation requested for this object?
    static boolean associationPropagation(Metadata m, OMElement focusObject, ErrorRecorder er) {
        OMElement ssEle = m.getSubmissionSet();
        if (ssEle == null) {
            er.err(XdsErrorCode.Code.XDSMetadataUpdateError, "No SubmissionSet object found in request", null, null);
            return false;
        }
        OMElement hasMemberEle = m.getAssociation(Metadata.getId(ssEle), Metadata.getId(focusObject), RegIndex.AssocType.HasMember.name());
        if (hasMemberEle == null) {
            er.err(XdsErrorCode.Code.XDSMetadataUpdateError, "No HasMember Association found linking Folder " + Metadata.getId(focusObject) + " to SubmissionSet", "", "");
            return false;
        }
        String aprop = m.getSlotValue(hasMemberEle, MetadataSupport.AssociationPropagation, 0);
        return aprop == null || aprop.equals("yes");
    }
}
