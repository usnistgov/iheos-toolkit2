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
   private SimulatorConfig rrConfig;

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
    * @return the {@link #rrConfig} value.
    */
   public SimulatorConfig getRRConfig() {
      return rrConfig;
   }

   /**
    * @param regrepConfig the {@link #rrConfig} to set
    */
   public void setRRConfig(SimulatorConfig regrepConfig) {
      this.rrConfig = regrepConfig;
   }
    
    

}
