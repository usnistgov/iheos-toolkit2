package gov.nist.toolkit.fhirServer.support

import gov.nist.toolkit.actorfactory.SimDb
import gov.nist.toolkit.actorfactory.client.NoSimException
import gov.nist.toolkit.actorfactory.client.SimId
import gov.nist.toolkit.installation.Installation
import org.apache.log4j.Logger

/**
 * SibDb extensions for FHIR resources
 */
class FhirSimDb extends SimDb {
    static private Logger logger = Logger.getLogger(FhirSimDb.class);

    static private final String BASE_TYPE = "base"
    /**
     * Return base dir of SimDb storage
     * @return
     */
    public File getSimDbFile() {
        return Installation.instance().fhirSimDbFile();
    }

    static public boolean exists(SimId simId) {
        return new File(new FhirSimDb().getSimDbFile(), simId.toString()).exists();
    }

    static public FhirSimDb mkSim(SimId simid) throws IOException, NoSimException {
        return mkSimi(Installation.instance().fhirSimDbFile(), simid, BASE_TYPE)
    }

    static public FhirSimDb mkSim(SimId simid, String actor) throws IOException, NoSimException {
        return mkSimi(Installation.instance().fhirSimDbFile(), simid, actor)
    }

    private static FhirSimDb mkSimi(File dbRoot, SimId simid, String actor) throws IOException, NoSimException {
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

        FhirSimDb db = new FhirSimDb(simid);
        db.setSimulatorType(actor);
        return db;
    }

    public FhirSimDb(SimId simId) {
        super(simId, BASE_TYPE, null)
    }

    public FhirSimDb(SimId simId, String actor) {
        super(simId, actor, null)
    }

}
