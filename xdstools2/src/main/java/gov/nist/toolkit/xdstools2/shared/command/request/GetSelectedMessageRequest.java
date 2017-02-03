package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 * Request context for {@link gov.nist.toolkit.xdstools2.client.command.command.GetSelectedMessageCommand GetSelectedMessageCommand}
 * and {@link gov.nist.toolkit.xdstools2.client.command.command.GetSelectedMessageResponseCommand GetSelectedMessageResponseCommand}
 * calls to the server.
 * Created by onh2 on 10/31/16.
 */
public class GetSelectedMessageRequest extends CommandContext{
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
