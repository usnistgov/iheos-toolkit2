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

    static final String BASE_TYPE = "base"
    final static String STORE_TRANSACTION = "store"

    File storeNewResource(String resourceType, String resourceContents) {
        File file = newResourceFile(resourceType)
        file.text = resourceContents
        return file
    }

    File newResourceFile(String resourceType) {
        File eventDir = getEventDir()
        for (int i=1; i<200; i++) {  // no more than 200 resources in an event
            File resourceFile = new File(eventDir, resourceType + i)
            if (!resourceFile.exists())
                return resourceFile
        }
        return null
    }

    /**
     * Return base dir of SimDb storage for FHIR resources (all FHIR simulators)
     * @return
     */
    @Override
    File getSimDbFile() {
        return Installation.instance().fhirSimDbFile();
    }

    /**
     * Get location of Lucene index for this simulator
     * @param simId
     * @return
     */
    static File getIndexFile(SimId simId) {
        return new File(getSimBase(simId), 'simindex')
    }

    /**
     * Does simulator exist?
     * @param simId
     * @return
     */
    static  boolean exists(SimId simId) {
        return getSimBase(simId).exists();
    }

    /**
     * Base location of simulator
     * @param simId - which simulator
     * @return
     */
    static File getSimBase(SimId simId) {
        return new File(new ResDb().getSimDbFile(), simId.toString())
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

        ResDb db = new ResDb(simid, BASE_TYPE, null);
        db.setSimulatorType(actor);
        return db;
    }

    ResDb(SimId simId) {
        super(simId)
    }

    ResDb(SimId simId, String actor, String transaction) {
        super(simId, actor, transaction)
    }

    ResDb() {
        super()
    }

}
