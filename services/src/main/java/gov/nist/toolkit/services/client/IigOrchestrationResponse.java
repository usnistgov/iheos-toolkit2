/**
 * 
 */
package gov.nist.toolkit.services.client;

import gov.nist.toolkit.simcommon.client.SimulatorConfig;

import java.util.ArrayList;
import java.util.List;


/**
 *
 * 
 * @author Ralph Moulton / MIR WUSTL IHE Development Project <a
 * href="mailto:moultonr@mir.wustl.edu">moultonr@mir.wustl.edu</a>
 *
 */
public class IigOrchestrationResponse extends AbstractOrchestrationResponse {
   private static final long serialVersionUID = 1L;
   
   List<SimulatorConfig> simulatorConfigs = new ArrayList<>();
   SimulatorConfig iigSimulatorConfig;
   
   public IigOrchestrationResponse() {}

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
    * @return the {@link #iigSimulatorConfig} value.
    */
   public SimulatorConfig getIigSimulatorConfig() {
      return iigSimulatorConfig;
   }

   /**
    * @param iigSimulatorConfig the {@link #iigSimulatorConfig} to set
    */
   public void setIigSimulatorConfig(SimulatorConfig iigSimulatorConfig) {
      this.iigSimulatorConfig = iigSimulatorConfig;
   }

   /* (non-Javadoc)
    * @see gov.nist.toolkit.services.client.AbstractOrchestrationResponse#isExternalStart()
    */
   @Override
   public boolean isExternalStart() {
      // TODO Auto-generated method stub
      return false;
   };
   
   

}
