package gov.nist.toolkit.actorfactory;

import gov.nist.toolkit.simcommon.shared.config.SimulatorConfig;

/**
 *
 */
public interface SimulatorConfigIo {
    void save(SimulatorConfig sc, String filename) throws Exception;
    SimulatorConfig restoreSimulator(String filename) throws Exception;
}
