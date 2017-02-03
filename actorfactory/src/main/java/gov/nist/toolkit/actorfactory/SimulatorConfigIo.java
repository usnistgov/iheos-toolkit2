package gov.nist.toolkit.actorfactory;

import gov.nist.toolkit.actorfactory.client.SimulatorConfig;

/**
 *
 */
public interface SimulatorConfigIo {
    void save(SimulatorConfig sc, String filename) throws Exception;
    SimulatorConfig restoreSimulator(String filename) throws Exception;
}
