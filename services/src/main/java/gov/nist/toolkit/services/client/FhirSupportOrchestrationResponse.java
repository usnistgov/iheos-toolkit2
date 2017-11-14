package gov.nist.toolkit.services.client;

import java.util.ArrayList;
import java.util.List;

public class FhirSupportOrchestrationResponse extends AbstractOrchestrationResponse {
    private static final long serialVersionUID = 1L;
    private List<PatientDef> patients = new ArrayList<>();   // patientID ==> Name ; url

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

    static public class PatientDef {
        public String pid;
        public String given;
        public String family;
        public String url;

        public PatientDef(String pid, String given, String family, String url) {
            this.pid = pid;
            this.given = given;
            this.family = family;
            this.url = url;
        }
    }
}
