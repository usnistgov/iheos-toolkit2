package gov.nist.toolkit.desktop.shared.command.request;


import gov.nist.toolkit.desktop.client.commands.util.CommandContext;

/**
 * Created by onh2 on 10/19/16.
 */
public class ReloadSystemFromGazelleRequest extends CommandContext {
    private String system;

    /**
     * Use {@link #ReloadSystemFromGazelleRequest(CommandContext, String)} instead
     * or use all setters along with the default constructor.
     */
    public ReloadSystemFromGazelleRequest() {
    }

    public ReloadSystemFromGazelleRequest(CommandContext commandContext, String system) {
        copyFrom(commandContext);
        this.system=system;
    }

    public String getSystem(){
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }
}
