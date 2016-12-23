package gov.nist.toolkit.actorfactory;

/**
 *
 */
public class SimulatorConfigIoFactory {

    static public SimulatorConfigIo impl() { return new SimulatorConfigIoJava(); }
}
