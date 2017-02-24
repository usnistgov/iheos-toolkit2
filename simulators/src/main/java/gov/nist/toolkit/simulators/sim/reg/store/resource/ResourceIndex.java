package gov.nist.toolkit.simulators.sim.reg.store.resource;

import gov.nist.toolkit.actorfactory.SimDb;
import gov.nist.toolkit.actorfactory.client.SimId;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.Calendar;

/**
 * Manages ResourceCollection into and out of memory.
 */
public class ResourceIndex implements Serializable {
    private static Logger logger = Logger.getLogger(ResourceIndex.class);
    private static final long serialVersionUID = 1L;
    private String filename;
    public Calendar cacheExpires;
    transient private SimDb db;
    private SimId simId;
    private ResourceCollection rc;

    public ResourceIndex() {}

    public ResourceIndex(String filename, SimId simId) {
        this.filename = filename;
        this.simId = simId;
        try {

        } catch (Exception e) {
            logger.debug("No existing - creating new");
            rc = new ResourceCollection();
            rc.init();
            rc.setIndex(this);
            rc.setDirty(false);
            rc.clear();
        }
    }

    public ResourceIndex(ResourceCollection rc) {
        this.rc = rc;
    }

    public void setSimDb(SimDb db) {
        this.db = db;
    }

    public SimDb getSimDb() {
        return db;
    }

    public void setExpiration(Calendar expires) {
        this.cacheExpires = expires;
    }

    private static void save(ResourceCollection mc, String filename) throws IOException {
        logger.debug("Save Resource Index");
        FileOutputStream fos = null;
        ObjectOutputStream out = null;

        fos = new FileOutputStream(filename);
        out = new ObjectOutputStream(fos);
        out.writeObject(mc);
        out.close();
    }

    // This must be called from a synchronize block
    private static ResourceCollection restore(String filename) throws IOException, ClassNotFoundException {
        FileInputStream fis = null;
        ObjectInputStream in = null;
        ResourceCollection rc;
        try {
            fis = new FileInputStream(filename);
            in = new ObjectInputStream(fis);
            rc = (ResourceCollection)in.readObject();
        } finally {
            if (in != null)
                in.close();
            if (fis!=null)
                fis.close();
        }
        return rc;
    }

    // caller takes responsiblity for sync, must be on this
    public void save() throws IOException {
        save(rc, filename);
        rc.setDirty(false);
    }

    private void restore() throws IOException, ClassNotFoundException {
        synchronized(this) {
            rc = restore(filename);
        }
    }

}
