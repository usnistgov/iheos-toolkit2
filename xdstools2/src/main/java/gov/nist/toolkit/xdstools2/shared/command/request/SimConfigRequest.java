package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

import java.util.List;

/**
 * Created by onh2 on 11/7/16.
 */
public class SimConfigRequest extends CommandContext{
    private SimulatorConfig config;
    private List<SimulatorConfig> configList;

    public SimConfigRequest(){}
    public SimConfigRequest(CommandContext context, SimulatorConfig config){
        copyFrom(context);
        this.config=config;
    }

    public SimConfigRequest(CommandContext context, List<SimulatorConfig> configs){
        copyFrom(context);
        this.configList=configs;
    }


    public SimulatorConfig getConfig() {
        return config;
    }

    public List<SimulatorConfig> getConfigList() {
        return configList;
    }

    public void setConfig(SimulatorConfig config) {
        this.config = config;
    }
}
