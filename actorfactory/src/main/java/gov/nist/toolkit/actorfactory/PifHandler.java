package gov.nist.toolkit.actorfactory;

import gov.nist.toolkit.adt.PifCallback;
import gov.nist.toolkit.configDatatypes.client.Pid;
import gov.nist.toolkit.configDatatypes.client.PidBuilder;
import gov.nist.toolkit.installation.shared.TestSession;
import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.simcommon.server.SimDb;
import gov.nist.toolkit.xdsexception.client.ToolkitRuntimeException;
import org.apache.log4j.Logger;

/**
 *
 */
public class PifHandler implements PifCallback {
    static Logger logger = Logger.getLogger(PifHandler.class);

    @Override
    public void addPatient(String simId, String patientId, TestSession testSession) {
        try {
            Pid pid = PidBuilder.createPid(patientId);
            if (pid == null) {
                logger.info("Received Patient ID that cannot be parsed " + patientId);
                return;
            }
            new SimDb(new SimId(testSession, simId)).addPatientId(pid);
        } catch (Exception e) {
            throw new ToolkitRuntimeException("", e);
        }
    }
}
