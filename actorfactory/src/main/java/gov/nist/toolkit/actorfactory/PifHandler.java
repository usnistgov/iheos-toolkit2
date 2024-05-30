package gov.nist.toolkit.actorfactory;

import gov.nist.toolkit.adt.PifCallback;
import gov.nist.toolkit.configDatatypes.client.Pid;
import gov.nist.toolkit.configDatatypes.client.PidBuilder;
import gov.nist.toolkit.installation.shared.TestSession;
import gov.nist.toolkit.simcommon.client.SimIdFactory;
import gov.nist.toolkit.simcommon.server.SimDb;
import gov.nist.toolkit.xdsexception.client.ToolkitRuntimeException;
import java.util.logging.Logger;

/**
 *
 */
public class PifHandler implements PifCallback {
    static Logger logger = Logger.getLogger(PifHandler.class.getName());

    @Override
    public void addPatient(String simId, String patientId, TestSession testSession) {
        try {
            Pid pid = PidBuilder.createPid(patientId);
            if (pid == null) {
                logger.info("Received Patient ID that cannot be parsed " + patientId);
                return;
            }
            new SimDb(SimIdFactory.simIdBuilder(simId)).addPatientId(pid);
        } catch (Exception e) {
            throw new ToolkitRuntimeException("", e);
        }
    }

    @Override
    public void addhl7v2Msg(String simId, String msg, String msh9, String dateDir, boolean inboundMsg) {
        try {
            new SimDb(SimIdFactory.simIdBuilder(simId)).addhl7v2Msg(msg, msh9, dateDir, inboundMsg);
        } catch (Exception e) {
            throw new ToolkitRuntimeException("add hl7v2 msg error: ", e);
        }
    }
}
