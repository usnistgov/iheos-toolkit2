package gov.nist.toolkit.actorfactory;

import gov.nist.toolkit.configDatatypes.client.Pid;
import gov.nist.toolkit.configDatatypes.client.PidBuilder;
import gov.nist.toolkit.actorfactory.client.SimId;
import gov.nist.toolkit.adt.PifCallback;
import gov.nist.toolkit.xdsexception.ToolkitRuntimeException;
import org.apache.log4j.Logger;

/**
 *
 */
public class PifHandler implements PifCallback {
    static Logger logger = Logger.getLogger(PifHandler.class);

    @Override
    public void addPatient(String simId, String patientId) {
        try {
            Pid pid = PidBuilder.createPid(patientId);
            if (pid == null) {
                logger.info("Received Patient ID that cannot be parsed " + patientId);
                return;
            }
            new SimDb(new SimId(simId)).addPatientId(pid);
        } catch (Exception e) {
            throw new ToolkitRuntimeException("", e);
        }
    }
}
