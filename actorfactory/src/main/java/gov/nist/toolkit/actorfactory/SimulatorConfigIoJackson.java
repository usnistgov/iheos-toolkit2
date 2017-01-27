package gov.nist.toolkit.actorfactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nist.toolkit.simcommon.shared.config.SimulatorConfig;
import org.apache.log4j.Logger;

import java.io.*;

/**
 *
 */
public class SimulatorConfigIoJackson implements SimulatorConfigIo{
        static Logger logger = Logger.getLogger(gov.nist.toolkit.actorfactory.SimulatorConfigIoJava.class);

        public void save(SimulatorConfig sc, String filename) throws Exception {
            try {
                logger.info("Saving sim config " + filename);
                ObjectMapper mapper = new ObjectMapper();
                mapper.writeValue(new File(filename), sc);
            } catch (Exception e) {
                throw new Exception("Simulator save failed - file name is " + filename + "\n", e);
            }
        }

        public SimulatorConfig restoreSimulator(String filename) throws Exception {
            try {
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(new File(filename), SimulatorConfig.class);
            } catch (Exception e) {
                throw new Exception("Simulator load failed - file name is " + filename + "\n", e);
            }
        }

}
