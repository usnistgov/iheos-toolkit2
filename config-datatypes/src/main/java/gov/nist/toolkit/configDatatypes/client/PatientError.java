package gov.nist.toolkit.configDatatypes.client;

import java.io.Serializable;

/**
 *
 */
public class PatientError implements Serializable {
    Pid patientId;
    String errorCode;

    public PatientError() {}

    public Pid getPatientId() {
        return patientId;
    }

    public void setPatientId(Pid patientId) {
        this.patientId = patientId;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
