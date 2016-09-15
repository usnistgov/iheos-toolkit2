/**
 * 
 */
package gov.nist.toolkit.services.client;

import java.util.ArrayList;
import java.util.List;

import gov.nist.toolkit.actorfactory.client.SimulatorConfig;


public class RigOrchestrationResponse extends RawResponse {
   private static final long serialVersionUID = 1L;
   
   List<SimulatorConfig> simulatorConfigs = new ArrayList<>();
   SimulatorConfig rigSimulatorConfig;
   
   public RigOrchestrationResponse() {}

   /**
    * @return the {@link #simulatorConfigs} value.
    */
   public List <SimulatorConfig> getSimulatorConfigs() {
      return simulatorConfigs;
   }

   /**
    * @param simulatorConfigs the {@link #simulatorConfigs} to set
    */
   public void setSimulatorConfigs(List <SimulatorConfig> simulatorConfigs) {
      this.simulatorConfigs = simulatorConfigs;
   }

   /**
    * @return the {@link #rigSimulatorConfig} value.
    */
   public SimulatorConfig getRigSimulatorConfig() {
      return rigSimulatorConfig;
   }

   /**
    * @param rigSimulatorConfig the {@link #rigSimulatorConfig} to set
    */
   public void setRigSimulatorConfig(SimulatorConfig rigSimulatorConfig) {
      this.rigSimulatorConfig = rigSimulatorConfig;
   };
   
   

}