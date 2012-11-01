package gov.nist.toolkit.simulators.sim.rep;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Calendar;

import org.apache.log4j.Logger;

public class RepIndex implements Serializable {
	static Logger logger = Logger.getLogger(RepIndex.class);

	private static final long serialVersionUID = 1L;
	public DocumentCollection dc;
	String filename;
	public Calendar cacheExpires;

	public DocumentCollection getDocumentCollection() {
		return dc;
	}

	public RepIndex(String filename) {
		this.filename = filename;
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



}
