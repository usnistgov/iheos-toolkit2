package gov.nist.toolkit.configDatatypes.client;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class PatientErrorList implements Serializable, IsSerializable {
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

    public boolean empty() { return list.isEmpty(); }

    @Override
    public String toString() {
        return list.toString();
    }
}
