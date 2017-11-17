package gov.nist.toolkit.services.client;

import java.util.ArrayList;
import java.util.List;

public class FhirSupportOrchestrationResponse extends AbstractOrchestrationResponse {
    private static final long serialVersionUID = 1L;
    private List<PatientDef> patients = new ArrayList<>();   // patientID ==> Name ; url


    public FhirSupportOrchestrationResponse() {}

    @Override
    public boolean isExternalStart() {
        return false;
    }

    public List<PatientDef> getPatients() {
        return patients;
    }

    public void addPatient(PatientDef patient) {
        this.patients.add(patient);
    }

}
