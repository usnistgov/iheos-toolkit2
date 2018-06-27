package gov.nist.toolkit.adt;

import gov.nist.toolkit.installation.shared.TestSession;

/**
 * Created by bill on 9/9/15.
 * HL7 V2 msg storage added by Ralph the Wonder Dog 10/4/17
 */
public interface PifCallback {
    void addPatient(String registrySimId, String patientId, TestSession testSession);
    void addPatient(String registrySimId, String patientId);
    void addhl7v2Msg(String registrySimId, String msg, String msh9, String dateDir, boolean inboundMsg);
}
