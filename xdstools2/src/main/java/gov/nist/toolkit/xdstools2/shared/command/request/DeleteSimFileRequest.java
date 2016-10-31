package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 * Request context for {@link gov.nist.toolkit.xdstools2.client.command.command.DeleteSimFileCommand DeleteSimFileCommand}
 * call to the server.
 * Created by onh2 on 10/31/16.
 */
public class DeleteSimFileRequest extends CommandContext{
    private String simFileSpec;

    /**
     * Default constructor only here for serialization,
     * prefer using {@link #DeleteSimFileRequest(CommandContext, String)}.
     */
    public DeleteSimFileRequest(){}
    /**
     * Constructor that should be used for any DeleteSimFile request.
     * @param context CommandContext containing the environment and the test session name.
     * @param simFileSpec name of the simulator file to delete.
     */
    public DeleteSimFileRequest(CommandContext context, String simFileSpec){
        copyFrom(context);
        this.simFileSpec=simFileSpec;
    }

    // ****** Getters and Setters ****** //
    public String getSimFileSpec() {
        return simFileSpec;
    }

    public void setSimFileSpec(String simFileSpec) {
        this.simFileSpec = simFileSpec;
    }
}
