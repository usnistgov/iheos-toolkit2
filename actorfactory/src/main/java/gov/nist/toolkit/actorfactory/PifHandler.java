package gov.nist.toolkit.actorfactory;

import gov.nist.toolkit.actorfactory.client.NoSimException;
import gov.nist.toolkit.actorfactory.client.SimId;
import gov.nist.toolkit.adt.PifCallback;
import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.xdsexception.ToolkitRuntimeException;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;

/**
 * Created by bill on 9/1/15.
 */
public class PifHandler implements PifCallback {
    static Logger logger = Logger.getLogger(PifHandler.class);

    public boolean hasPatientId(String simId, String patientId)  {
        try {
            File pidFile = pidFile(simId, patientId);
            Io.stringFromFile(pidFile);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    static File pidFile(String simId, String patientId) throws IOException, NoSimException {
        logger.debug("patientID is " + patientId);
        SimDb simdb = new SimDb(new SimId(simId));
        logger.debug("simdir = " + simdb.getSimDir());
        String[] parts = patientId.split("\\^");
        if (parts.length != 4)
            return null;   // not valid pid
        String id = parts[0];
        logger.debug("id is " + id);
        String affinityDomain = parts[3];
        String[] parts2 = affinityDomain.split("&");
        if (parts2.length < 2)
            return null;
        String affinityDomainOid = parts2[1];
        logger.debug("oid is " + affinityDomainOid);
        File pidFile = simdb.getPidFile(affinityDomainOid, id);
        logger.debug("pidfile is " + pidFile);
        return pidFile;
    }

    @Override
    public void addPatient(String registrySimId, String patientId) {
        try {
            File pidFile = pidFile(registrySimId, patientId);
            Io.stringToFile(pidFile, patientId);
        } catch (Exception e) {
            throw new ToolkitRuntimeException("", e);
        }
    }
}
