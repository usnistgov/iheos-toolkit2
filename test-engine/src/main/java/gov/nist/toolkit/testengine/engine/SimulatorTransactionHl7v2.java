package gov.nist.toolkit.testengine.engine;

import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.simcommon.server.SimDb;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Encapsulates hl7 v2 transaction sent to a simulator.
 */
public class SimulatorTransactionHl7v2 {

    private SimId simId;
    private String transType;

    private Map<String, String> msgs = new HashMap<>();

    /**
     * Creates an instance for the most recent transaction of a given type received
     * by a simulator. Once the instance is created, a particular result can be
     * retrieved by "piece", using {@link #getMsg(String)}.
     * @param simId id for the simulator
     * @param transType hl7 v2 transaction type, for example "ADT^A01". Supported types
     *                  are those handled by {@link gov.nist.toolkit.adt.AdtSocketListener}
     * @throws Exception on error, for example, an IO error.
     */
    public SimulatorTransactionHl7v2 (SimId simId, String transType) throws Exception {
        this.simId = simId;
        this.transType = transType;
        loadTransaction();
    }

    private void loadTransaction() throws Exception {
        SimDb simDb = new SimDb(simId);
        // Directory for hl7v2 transactions of this transType, for example "ADT^A01
        File dir = Paths.get(simDb.getSimDir().getPath(), "hl7v2", transType).toFile();
        String[] transDirs = dir.list();
        if (transDirs == null || transDirs.length == 0) return;
        Arrays.sort(transDirs);
        File transDir = new File(dir, transDirs[transDirs.length -1]);
        String[] transFiles = transDir.list();
        for (String fn : transFiles) {
            File pfn = new File(transDir, fn);
            if (pfn.isFile() && pfn.canRead()) {
                String msg = new String(Files.readAllBytes(pfn.toPath()), "UTF-8");
                msgs.put(fn.toUpperCase(), msg);
            }
        }
    }

    /**
     * Return one of the message/formats for the transaction.
     * @param piece the message/format to retrieve. Valid entries are:
     *              Request.xml, Request.txt, Response.xml and Response.txt
     *              where the .txt files are in ANSI X.22 format, and the
     *              .xml files are converted to XML format.
     *              For Windows compatibility, not case sensitive.
     * @return The requested piece, or null if no matching piece was found.
     */
    public String getMsg(String piece) {
        return msgs.get(piece.toUpperCase());
    }
    public SimId getSimId() {
        return simId;
    }

    public String getTransType() {
        return transType;
    }
}