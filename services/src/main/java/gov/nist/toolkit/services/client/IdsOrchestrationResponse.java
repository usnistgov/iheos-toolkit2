package gov.nist.toolkit.services.client;

import gov.nist.toolkit.actorfactory.client.SimulatorConfig;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class IdsOrchestrationResponse extends RawResponse {
   
   private static final long serialVersionUID = 1L;
   
   private List<SimulatorConfig> simulatorConfigs = new ArrayList<>();
   private SimulatorConfig idsSimulatorConfig;
   private SimulatorConfig regrepConfig;

    public IdsOrchestrationResponse() {}

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
    * @return the {@link #idsSimulatorConfig} value.
    */
   public SimulatorConfig getIdsSimulatorConfig() {
      return idsSimulatorConfig;
   }

   /**
    * @param idsSimulatorConfig the {@link #idsSimulatorConfig} to set
    */
   public void setIdsSimulatorConfig(SimulatorConfig idsSimulatorConfig) {
      this.idsSimulatorConfig = idsSimulatorConfig;
   }

   /**
    * @return the {@link #regrepConfig} value.
    */
   public SimulatorConfig getRegrepConfig() {
      return regrepConfig;
   }

   /**
    * @param regrepConfig the {@link #regrepConfig} to set
    */
   public void setRegrepConfig(SimulatorConfig regrepConfig) {
      this.regrepConfig = regrepConfig;
   }
    
    

}
