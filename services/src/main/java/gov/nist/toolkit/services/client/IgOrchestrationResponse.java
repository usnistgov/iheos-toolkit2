package gov.nist.toolkit.services.client;

import gov.nist.toolkit.configDatatypes.client.Pid;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.sitemanagementui.client.Site;

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
    private Pid noAdOptionPid;
    private Site supportRG1;
    private Site supportRG2;
    private boolean externalStart;

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

    public Site getSupportRG1() {
        return supportRG1;
    }

    public void setSupportRG1(Site supportRG1) {
        this.supportRG1 = supportRG1;
    }

    public Site getSupportRG2() {
        return supportRG2;
    }

    public void setSupportRG2(Site supportRG2) {
        this.supportRG2 = supportRG2;
    }

    public Pid getNoAdOptionPid() {
        return noAdOptionPid;
    }

    public void setNoAdOptionPid(Pid noAdOptionPid) {
        this.noAdOptionPid = noAdOptionPid;
    }

    @Override
    public boolean isExternalStart() {
        return false;
    }

    public void setExternalStart(boolean externalStart) {
        this.externalStart = externalStart;
    }
}
