package gov.nist.toolkit.simcommon.server;

import gov.nist.toolkit.configDatatypes.client.Pid;
import gov.nist.toolkit.configDatatypes.client.PidBuilder;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.utilities.io.Io;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Manager PIDs within a Simulator
 */
public class PidDb {
    static Logger logger = Logger.getLogger(PidDb.class);
    private final SimDb simDb;

    public PidDb(SimDb simDb) {
        this.simDb = simDb;
    }

    public File getPatientIDFeedDir() {
        File regDir = new File(simDb.getSimDir(), ActorType.REGISTRY.getShortName());
        File pifDir = new File(regDir, "pif");
        return pifDir;
    }

    public List<Pid> getAllPatientIds() {
        List<Pid> pids = new ArrayList<>();
        File pidDir = getPatientIDFeedDir();
        File[] aaDirs = pidDir.listFiles();
        if (aaDirs == null) return pids;
        for (int aai = 0; aai < aaDirs.length; aai++) {
            File aaDir = aaDirs[aai];
            if (!aaDir.isDirectory()) continue;
            String aaString = aaDir.getName();
            File[] pidFiles = aaDir.listFiles();
            if (pidFiles == null) continue;
            for (int pidi = 0; pidi < pidFiles.length; pidi++) {
                File pidFile = pidFiles[pidi];
                if (!pidFile.getName().endsWith(".txt")) continue;
                String pid = simDb.stripFileType(pidFile.getName(), "txt");
                pids.add(new Pid(aaString, pid));
            }
        }
        return pids;
    }

    public File getAffinityDomainDir(String adOid) {
        File regDir = new File(simDb.getSimDir(), ActorType.REGISTRY.getShortName());
        File pifDir = new File(regDir, "pif");
        File adDir = new File(pifDir, adOid);
        adDir.mkdirs();
        return adDir;
    }

    public File getPidFile(Pid pid) {
        File adFile = getAffinityDomainDir(pid.getAd());
        File pidFile = new File(adFile, pid.getId() + ".txt");
        return pidFile;
    }

    public void addPatientId(String patientId) throws IOException {
        addPatientId(PidBuilder.createPid(patientId));
    }

    public void addPatientId(Pid pid) throws IOException {
        logger.debug("storing Patient ID " + pid + " to " + getPidFile(pid));
        if (patientIdExists(pid)) return;
        Io.stringToFile(getPidFile(pid), pid.asString());
    }

    public boolean patientIdExists(Pid pid) {
        return getPidFile(pid).exists();
    }

    public boolean deletePatientIds(List<Pid> toDelete) {
        boolean ok = true;
        for (Pid pid : toDelete) {
            ok = ok & getPidFile(pid).delete();
        }
        return ok;
    }
}