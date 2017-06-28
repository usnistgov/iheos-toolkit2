package gov.nist.toolkit.simcommon.server;

/**
 *
 */
public class SimulatorConfigIoFactory {

//    static public SimulatorConfigIo impl() { return new SimulatorConfigIoJava(); }
    static public SimulatorConfigIo impl() { return new SimulatorConfigIoJackson(); }

}
