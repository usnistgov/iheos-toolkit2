package gov.nist.toolkit.adt;

import gov.nist.toolkit.actorfactory.SimDb;
import gov.nist.toolkit.actorfactory.client.NoSimException;
import gov.nist.toolkit.utilities.io.Io;

import java.io.File;
import java.io.IOException;

/**
 * Created by bill on 9/1/15.
 */
public class Adt {

    static public void addPatientId(String simId, String patientId) throws IOException, NoSimException {
        File pidFile = pidFile(simId, patientId);
        Io.stringToFile(pidFile, patientId);
    }

    static public boolean hasPatientId(String simId, String patientId)  {
        try {
            File pidFile = pidFile(simId, patientId);
            Io.stringFromFile(pidFile);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    static File pidFile(String simId, String patientId) throws IOException, NoSimException {
        SimDb simdb = new SimDb(simId);
        String[] parts = patientId.split("^");
        if (parts.length != 4)
            return null;   // not valid pid
        String id = parts[0];
        String ad = parts[3];
        String[] parts2 = ad.split("&");
        if (parts2.length < 2)
            return null;
        String oid = parts2[1];
        File pidFile = simdb.getPidFile(ad, id);
        return pidFile;
    }
}
