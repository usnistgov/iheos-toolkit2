/**
 *
 */
package gov.nist.toolkit.services.client;

import gov.nist.toolkit.actorfactory.client.SimulatorConfig;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * @author Matt Kelsey / MIR WUSTL IHE Development Project <a
 * href="mailto:kelseym@mir.wustl.edu">kelseym@mir.wustl.edu</a>
 *
 */
public class RSNAEdgeOrchestrationResponse extends RawResponse {
    private static final long serialVersionUID = 1L;

    SimulatorConfig simulatorConfig;

    public RSNAEdgeOrchestrationResponse() {}


    public void setSimulatorConfig(SimulatorConfig simulatorConfig) {
        this.simulatorConfig = simulatorConfig;
    }


    public SimulatorConfig getSimulatorConfig() {
        return simulatorConfig;
    }

}
