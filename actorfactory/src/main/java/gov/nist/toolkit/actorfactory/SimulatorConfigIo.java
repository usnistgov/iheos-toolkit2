package gov.nist.toolkit.actorfactory;

import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import org.apache.log4j.Logger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class SimulatorConfigIo {
    static Logger logger = Logger.getLogger(SimulatorConfigIo.class);

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


}
