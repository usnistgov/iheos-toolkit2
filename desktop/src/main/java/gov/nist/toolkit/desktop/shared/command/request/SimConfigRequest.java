package gov.nist.toolkit.desktop.shared.command.request;

import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.desktop.client.commands.util.CommandContext;


/**
 * Created by onh2 on 11/7/16.
 */
public class SimConfigRequest extends CommandContext {
    private SimulatorConfig config;

    public SimConfigRequest(){}
    public SimConfigRequest(CommandContext context, SimulatorConfig config){
        copyFrom(context);
        this.config=config;
    }

    public SimulatorConfig getConfig() {
        return config;
    }

    public void setConfig(SimulatorConfig config) {
        this.config = config;
    }
}
