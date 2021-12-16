package gov.nist.toolkit.simcommon.server;

import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import java.util.logging.Logger;

import java.io.*;

class SimulatorConfigIoJava implements SimulatorConfigIo {
    static Logger logger = Logger.getLogger(SimulatorConfigIoJava.class.getName());

	// Should only be called from ActorFactory which figures
	// out where to save it
	public void save(SimulatorConfig sc, String filename) throws IOException {
        logger.info("Saving sim config " + filename);
		FileOutputStream fos = null;
		ObjectOutputStream out = null;

		fos = new FileOutputStream(filename);
		out = new ObjectOutputStream(fos);
		out.writeObject(sc);
		out.close();
	}

	public SimulatorConfig restoreSimulator(String filename) throws IOException, ClassNotFoundException {
		FileInputStream fis = null;
		ObjectInputStream in = null;
		SimulatorConfig config;
		fis = new FileInputStream(filename);
		in = new ObjectInputStream(fis);
		config = (SimulatorConfig)in.readObject();
		in.close();

		return config;
	}


}
