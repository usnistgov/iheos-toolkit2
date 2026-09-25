package gov.nist.toolkit.simcommon.server;


import gov.nist.toolkit.simcommon.client.SimulatorConfig;

/**
 *
 */
public interface SimulatorConfigIo {
    void save(SimulatorConfig sc, String filename) throws Exception;
    SimulatorConfig restoreSimulator(String filename) throws Exception;
}
