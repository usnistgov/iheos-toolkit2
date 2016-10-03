package gov.nist.toolkit.services.client;

import gov.nist.toolkit.configDatatypes.client.Pid;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class IgOrchestrationResponse extends AbstractOrchestrationResponse {
    private List<SimulatorConfig> simulatorConfigs = new ArrayList<>()  ;
    private SimulatorConfig igSimulatorConfig;
    private Pid oneDocPid;
    private Pid twoDocPid;
    private Pid twoRgPid;
    private Pid unknownPid;

    public IgOrchestrationResponse() {}

    public List<SimulatorConfig> getSimulatorConfigs() {
        return simulatorConfigs;
    }

    public void setSimulatorConfigs(List<SimulatorConfig> simulatorConfigs) {
        this.simulatorConfigs = simulatorConfigs;
    }

    public Pid getOneDocPid() {
        return oneDocPid;
    }

    public void setOneDocPid(Pid oneDocPid) {
        this.oneDocPid = oneDocPid;
    }

    public Pid getTwoDocPid() {
        return twoDocPid;
    }

    public void setTwoDocPid(Pid twoDocPid) {
        this.twoDocPid = twoDocPid;
    }

    public Pid getTwoRgPid() {
        return twoRgPid;
    }

    public void setTwoRgPid(Pid twoRgPid) {
        this.twoRgPid = twoRgPid;
    }

    public Pid getUnknownPid() {
        return unknownPid;
    }

    public void setUnknownPid(Pid unknownPid) {
        this.unknownPid = unknownPid;
    }

    public SimulatorConfig getIgSimulatorConfig() {
        return igSimulatorConfig;
    }

    public void setIgSimulatorConfig(SimulatorConfig igSimulatorConfig) {
        this.igSimulatorConfig = igSimulatorConfig;
    }
}
