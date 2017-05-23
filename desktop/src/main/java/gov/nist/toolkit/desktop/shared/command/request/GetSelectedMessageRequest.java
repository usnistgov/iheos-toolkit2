package gov.nist.toolkit.desktop.shared.command.request;


import gov.nist.toolkit.desktop.client.commands.util.CommandContext;

/**
 * calls to the server.
 * Created by onh2 on 10/31/16.
 */
public class GetSelectedMessageRequest extends CommandContext {
    private String filename;

    /**
     * Default constructor only here for serialization,
     * prefer using {@link #GetSelectedMessageRequest(CommandContext, String)}.
     */
    public GetSelectedMessageRequest(){}
    /**
     * Constructor that should be used for any GetSelectedMessage requests.
     * @param commandContext CommandContext containing the environment and the test session name.
     * @param filename
     */
    public GetSelectedMessageRequest(CommandContext commandContext, String filename) {
        copyFrom(commandContext);
        this.filename=filename;
    }

    public String getFilename() {
        return filename;
    }
}
