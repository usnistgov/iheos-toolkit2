package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 * Created by skb1 on 7/20/17.
 */
public class GetTabConfigRequest extends CommandContext{
    private String toolId;

    public GetTabConfigRequest(){}

    public GetTabConfigRequest(CommandContext commandContext, String toolId){
        copyFrom(commandContext);
        this.toolId = toolId;
    }

    public String getToolId() {
        return toolId;
    }

    public void setToolId(String toolId) {
        this.toolId = toolId;
    }
}
