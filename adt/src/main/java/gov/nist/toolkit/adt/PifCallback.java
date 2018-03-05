package gov.nist.toolkit.adt;

import gov.nist.toolkit.installation.shared.TestSession;

/**
 * Created by bill on 9/9/15.
 */
public interface PifCallback {
    void addPatient(String registrySimId, String patientId, TestSession testSession);
}
