package gov.nist.toolkit.fhir.support

import gov.nist.toolkit.actorfactory.SimDb
import gov.nist.toolkit.actorfactory.client.NoSimException
import gov.nist.toolkit.actorfactory.client.SimId
import gov.nist.toolkit.installation.Installation
import org.apache.log4j.Logger


/**
 * SibDb extensions for FHIR resources
 */
class ResDb extends SimDb {
    static private Logger logger = Logger.getLogger(ResDb.class);

    static private final String BASE_TYPE = "base"
    /**
     * Return base dir of SimDb storage
     * @return
     */
    @Override
    File getSimDbFile() {
        return Installation.instance().fhirSimDbFile();
    }

    static public boolean exists(SimId simId) {
        return new File(new ResDb().getSimDbFile(), simId.toString()).exists();
    }

    ResDb mkSim(SimId simid) throws IOException, NoSimException {
        return mkSimi(getSimDbFile(), simid, BASE_TYPE)
    }

    ResDb mkSim(SimId simid, String actor) throws IOException, NoSimException {
        return mkSimi(getSimDbFile(), simid, actor)
    }

    private static ResDb mkSimi(File dbRoot, SimId simid, String actor) throws IOException, NoSimException {
        validateSimId(simid);
        if (!dbRoot.exists())
            dbRoot.mkdir();
        if (!dbRoot.canWrite() || !dbRoot.isDirectory())
            throw new IOException("Resource Simulator database location, " + dbRoot.toString() + " is not a directory or cannot be written to");

        File simActorDir = new File(dbRoot.getAbsolutePath() + File.separatorChar + simid + File.separatorChar + actor);
        simActorDir.mkdirs();
        if (!simActorDir.exists()) {
            logger.error("Fhir Simulator " + simid + ", " + actor + " cannot be created");
            throw new IOException("Fhir Simulator " + simid + ", " + actor + " cannot be created");
        }

        ResDb db = new ResDb(simid);
        db.setSimulatorType(actor);
        return db;
    }

    ResDb(SimId simId) {
        super(simId, BASE_TYPE, null)
    }

    ResDb(SimId simId, String actor, String transaction) {
        super(simId, actor, transaction)
    }

    ResDb() {
        super()
    }

}
