package gov.nist.toolkit.fhir.simulators.sim.reg.mu;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.fhir.simulators.sim.reg.store.DocEntry;
import gov.nist.toolkit.fhir.simulators.sim.reg.store.MetadataCollection;

public class AcceptableUpdate {

    // error results in false return and messages in er
    public static boolean acceptableRMU(DocEntry de, MetadataCollection mc, ErrorRecorder er) {
        boolean ok = true;
        int rmuVersion = de.version;
        DocEntry prev = mc.docEntryCollection.getPreviousVersion(de);
        if (prev == null) {
            er.err(XdsErrorCode.Code.XDSMetadataVersionError, "Previous version " + String.valueOf(rmuVersion-1) + " not found", null, null);
            return false;
        }
        // home - structural - cannot check here
        // entryUUID - structural - cannot check here
        // objectType - structural - cannot check here
        if (!prev.pid.equals(de.pid)) {
            er.err(XdsErrorCode.Code.XDSPatientIDReconciliationError, "Illegal change made to PatientId - was " + prev.pid + " updated to " + de.pid, null, null);
            ok = false;
        }
        if (!prev.sourcePatientId.equals(de.sourcePatientId)) {
            er.err(XdsErrorCode.Code.UnmodifiableMetadataError, "Illegal change made to SourcePatientId - was " + prev.sourcePatientId + " updated to " + de.sourcePatientId, null, null);
            ok = false;
        }
        if (prev.documentAvailability == null)
            prev.documentAvailability = "urn:ihe:iti:2010:DocumentAvailability:Online";
        if (de.documentAvailability == null)
            de.documentAvailability = "urn:ihe:iti:2010:DocumentAvailability:Online";
        if (!prev.documentAvailability.equals(de.documentAvailability)) {
            er.err(XdsErrorCode.Code.UnmodifiableMetadataError, "Illegal change made to documentAvailability - was " + prev.documentAvailability + " updated to " + de.documentAvailability, null, null);
            ok = false;
        }
        if (!prev.uid.equals(de.uid)) {
            er.err(XdsErrorCode.Code.XDSMetadataIdentifierError, "Illegal change made to uniqueId - was " + prev.uid + " updated to " + de.uid, null, null);
            ok = false;
        }

        if (!prev.repositoryUniqueId.equals(de.repositoryUniqueId)) {
            er.err(XdsErrorCode.Code.UnmodifiableMetadataError, "Illegal change made to repositoryUniqueId - was " + prev.repositoryUniqueId + " updated to " + de.repositoryUniqueId, null, null);
            ok = false;
        }
        if (!prev.documentAvailability.equals(de.documentAvailability)) {
            er.err(XdsErrorCode.Code.UnmodifiableMetadataError, "documentAvailability cannot be changed through Restricted Metadata Update", null, null);
            ok = false;
        }
        return ok;
    }

    static public boolean acceptableMU(ErrorRecorder er) {
        return true;
    }
}
