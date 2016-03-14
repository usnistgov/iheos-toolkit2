package gov.nist.toolkit.simulators.sim.rep;

import gov.nist.toolkit.actorfactory.client.NoSimException;
import gov.nist.toolkit.actorfactory.client.SimId;
import gov.nist.toolkit.actorfactory.client.SimulatorStats;
import gov.nist.toolkit.actortransaction.client.ActorType;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.Calendar;

public class RepIndex implements Serializable {
	static Logger logger = Logger.getLogger(RepIndex.class);

	private static final long serialVersionUID = 1L;
	public DocumentCollection dc;
	String filename;
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

	public void restore() throws IOException, ClassNotFoundException {
		synchronized(this) {
			dc = RepIndex.restoreRepository(filename);
		}
	}

	static DocumentCollection restoreRepository(String filename) throws IOException, ClassNotFoundException {
		logger.debug("Restore Repository Index");
		FileInputStream fis = null;
		ObjectInputStream in = null;
		DocumentCollection dc;
		fis = new FileInputStream(filename);
		in = new ObjectInputStream(fis);
		dc = (DocumentCollection)in.readObject();
		in.close();
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

	static void saveRepository(DocumentCollection dc, String filename) throws IOException {
		logger.debug("Save Repository Index");
		FileOutputStream fos = null;
		ObjectOutputStream out = null;

		fos = new FileOutputStream(filename);
		out = new ObjectOutputStream(fos);
		out.writeObject(dc);
		out.close();
	}

	public SimulatorStats getSimulatorStats() throws IOException, NoSimException {
		SimulatorStats stats = new SimulatorStats();
		stats.actorType = ActorType.REPOSITORY;
		stats.simId = simId;

		stats.put(SimulatorStats.DOCUMENT_COUNT, dc.size());
		return stats;
	}


}
