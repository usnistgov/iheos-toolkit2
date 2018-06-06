package gov.nist.toolkit.services.client;


import gov.nist.toolkit.simcommon.client.SimulatorConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Orchestration Response for Image Document Source SUT
 */
public class IdsOrchestrationResponse extends AbstractOrchestrationResponse {
   
   private static final long serialVersionUID = 1L;
   
   private List<SimulatorConfig> simulatorConfigs = new ArrayList<>();
   private SimulatorConfig idsSimulatorConfig;
   private SimulatorConfig repConfig;
   private SimulatorConfig regConfig;

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
    * @return the {@link #repConfig} value.
    */
   public SimulatorConfig getRepConfig() {
      return repConfig;
   }
   
   /**
    * @param repConfig the {@link #repConfig} to set
    */
   public void setRepConfig(SimulatorConfig repConfig) {
      this.repConfig = repConfig;
   }

   /**
    * @return the {@link #regConfig} value.
    */
   public SimulatorConfig getRegConfig() {
      return regConfig;
   }

   /**
    * @param regConfig the {@link #regConfig} to set
    */
   public void setRegConfig(SimulatorConfig regConfig) {
      this.regConfig = regConfig;
   }

   /* (non-Javadoc)
    * @see gov.nist.toolkit.services.client.AbstractOrchestrationResponse#isExternalStart()
    */
   @Override
   public boolean isExternalStart() {
      // TODO Auto-generated method stub
      return false;
   }



}
