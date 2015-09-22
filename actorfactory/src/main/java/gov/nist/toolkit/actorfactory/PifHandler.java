package gov.nist.toolkit.actorfactory;

import gov.nist.toolkit.actorfactory.client.Pid;
import gov.nist.toolkit.actorfactory.client.PidBuilder;
import gov.nist.toolkit.actorfactory.client.SimId;
import gov.nist.toolkit.adt.PifCallback;
import gov.nist.toolkit.xdsexception.ToolkitRuntimeException;

/**
 * Created by bill on 9/1/15.
 */
public class PifHandler implements PifCallback {

    @Override
    public void addPatient(String simId, String patientId) {
        try {
            Pid pid = PidBuilder.createPid(patientId);
            if (pid == null) return;
            new SimDb(new SimId(simId)).addPatientId(pid);
        } catch (Exception e) {
            throw new ToolkitRuntimeException("", e);
        }
    }
}
