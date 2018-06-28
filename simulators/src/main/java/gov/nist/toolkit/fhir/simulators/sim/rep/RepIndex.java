package gov.nist.toolkit.fhir.simulators.sim.rep;

import gov.nist.toolkit.actortransaction.shared.ActorType;
import gov.nist.toolkit.simcommon.client.NoSimException;
import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.simcommon.client.SimulatorStats;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.Calendar;

public class RepIndex implements Serializable {
	static Logger logger = Logger.getLogger(RepIndex.class);

	private static final long serialVersionUID = 1L;
	public DocumentCollection dc;
	public String filename;
	public Calendar cacheExpires;
	SimId simId;

	public DocumentCollection getDocumentCollection() {
		return dc;
	}

	public RepIndex(String filename, SimId simId) {
		this.filename = filename;
		this.simId = simId;
		try {
			restore();
			dc.repIndex = this;
			dc.dirty = false;
		} catch (Exception e) {
			// no existing database - initialize instead
			dc = new DocumentCollection();
			dc.init();
			dc.repIndex = this;
			dc.dirty = false;
		}
	}


	public void restore() throws Exception, ClassNotFoundException {
		synchronized(this) {
			dc = RepIndex.restoreRepository(filename);
		}
	}

	static DocumentCollection restoreRepository(String filename) throws Exception {
		logger.debug("Restore Repository Index");
		FileInputStream fis = null;
		ObjectInputStream in = null;
		DocumentCollection dc;
		try {
			fis = new FileInputStream(filename);
			in = new ObjectInputStream(fis);
			dc = (DocumentCollection) in.readObject();
		} catch (Throwable e) {
			logger.fatal("Failed to restore Repository Index from " + filename);
			throw e;
		} finally {
			if (in!=null)
				in.close();
			if (fis!=null)
				fis.close();
		}

		return dc;
	}

	public void save() throws IOException {
		if (!dc.dirty)
			return;
		synchronized(this) {
			RepIndex.saveRepository(dc, filename);
			dc.dirty = false;
		}
	}

	private static void saveRepository(DocumentCollection dc, String filename) throws IOException {
		logger.debug("Save Repository Index");
		FileOutputStream fos = null;
		ObjectOutputStream out = null;

		fos = new FileOutputStream(filename);
		out = new ObjectOutputStream(fos);
		out.writeObject(dc);
		out.close();
	}

	public SimulatorStats getSimulatorStats() throws IOException, NoSimException {
		return getSimulatorStats(ActorType.REPOSITORY);
	}

	public SimulatorStats getSimulatorStats(ActorType actorType) throws IOException, NoSimException {
		SimulatorStats stats = new SimulatorStats();
		stats.actorType = actorType; // Should repository type be used here instead?
		stats.simId = simId;

		if (ActorType.REPOSITORY.equals(actorType))
			stats.put(SimulatorStats.DOCUMENT_COUNT, (dc!=null)?dc.size():0);
		return stats;
	}


}
