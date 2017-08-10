package gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab;

import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.simcommon.client.SimulatorStats;

public class SimInfo {
   private SimulatorConfig simulatorConfig;
   private SimulatorStats simulatorStats;
   private String lastAccessedDt;
   private String lastAccessedDtHl7fmt;
   private String lastTransaction;

    public SimInfo() {
    }

    public SimInfo(SimulatorConfig simulatorConfig, SimulatorStats simulatorStats) {
        this.simulatorConfig = simulatorConfig;
        this.simulatorStats = simulatorStats;
    }

    public SimulatorConfig getSimulatorConfig() {
        return simulatorConfig;
    }

    public void setSimulatorConfig(SimulatorConfig simulatorConfig) {
        this.simulatorConfig = simulatorConfig;
    }

    public SimulatorStats getSimulatorStats() {
        return simulatorStats;
    }

    public void setSimulatorStats(SimulatorStats simulatorStats) {
        this.simulatorStats = simulatorStats;
    }

    public String getLastAccessedDt() {
        return lastAccessedDt;
    }

    public void setLastAccessedDt(String lastAccessedDt) {
        this.lastAccessedDt = lastAccessedDt;
    }

    public String getLastAccessedDtHl7fmt() {
        return lastAccessedDtHl7fmt;
    }

    public void setLastAccessedDtHl7fmt(String lastAccessedDtHl7fmt) {
        this.lastAccessedDtHl7fmt = lastAccessedDtHl7fmt;
    }

    public String getLastTransaction() {
        return lastTransaction;
    }

    public void setLastTransaction(String lastTransaction) {
        this.lastTransaction = lastTransaction;
    }
}
