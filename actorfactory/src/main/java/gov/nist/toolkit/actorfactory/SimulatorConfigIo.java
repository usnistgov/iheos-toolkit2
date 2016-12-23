package gov.nist.toolkit.actorfactory;

import gov.nist.toolkit.actorfactory.client.SimulatorConfig;

import java.io.IOException;

/**
 *
 */
public interface SimulatorConfigIo {
    void save(SimulatorConfig sc, String filename) throws IOException;
    SimulatorConfig restoreSimulator(String filename) throws IOException, ClassNotFoundException;
}
