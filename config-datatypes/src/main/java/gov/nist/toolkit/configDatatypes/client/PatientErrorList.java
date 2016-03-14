package gov.nist.toolkit.configDatatypes.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class PatientErrorList implements Serializable {
    List<PatientError> list = new ArrayList<>();

    public PatientErrorList() {}

    public void add(PatientError pe) {
        if (list.contains(pe)) return;
        list.add(pe);
    }

    public void remove(PatientError pe) {
        for (PatientError e : list) {
            if (e.equals(pe)) {
                list.remove(pe);
                return;
            }
        }
    }

    public List<PatientError> values() { return list; }

    public String getErrorName(Pid pid) {
        for (PatientError pe : list) {
            if (pe.patientId.equals(pid)) return pe.errorCode;
        }
        return null;
    }

    public boolean isEmpty() { return list.isEmpty(); }

    @Override
    public String toString() {
        return list.toString();
    }
}
