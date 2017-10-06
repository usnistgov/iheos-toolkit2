package gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab;

import gov.nist.toolkit.actortransaction.client.TransactionInstance;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.simcommon.client.SimulatorStats;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by skb1 on 08/11/17.
 */
public class SimInfo {
   private SimulatorConfig simulatorConfig;
   private SimulatorStats simulatorStats;
   private String createdDtHl7fmt;
   public static final int TOP_TRANSACTION_CT  = 3;
   private final List<TransactionInstance> topThreeTransInstances = new ArrayList<TransactionInstance>();

    public SimInfo() {
    }

    public SimInfo(SimulatorConfig simulatorConfig, SimulatorStats simulatorStats) {
        this.simulatorConfig = simulatorConfig;
        this.simulatorStats = simulatorStats;
    }

    public SimulatorConfig getSimulatorConfig() {
        return simulatorConfig;
    }

    public SimulatorStats getSimulatorStats() {
        return simulatorStats;
    }
    public String getCreatedDtHl7fmt() {
        return createdDtHl7fmt;
    }

    public void setCreatedDtHl7fmt(String createdDtHl7fmt) {
        this.createdDtHl7fmt = createdDtHl7fmt;
    }

    public List<TransactionInstance> getTopThreeTransInstances() {
        return topThreeTransInstances;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimInfo simInfo = (SimInfo) o;

        return simulatorConfig != null && simulatorConfig.getId() != null ? simulatorConfig.getId().toString().equals(simInfo.simulatorConfig.getId().toString()) : simInfo.simulatorConfig == null;
    }

    @Override
    public int hashCode() {
        return simulatorConfig != null  && simulatorConfig.getId() != null ? simulatorConfig.getId().toString().hashCode() : 0;
    }
}
