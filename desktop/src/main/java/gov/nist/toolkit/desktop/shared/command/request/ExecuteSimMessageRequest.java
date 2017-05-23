package gov.nist.toolkit.desktop.shared.command.request;


import gov.nist.toolkit.desktop.client.commands.util.CommandContext;

/**
 * Created by onh2 on 10/21/16.
 */
public class ExecuteSimMessageRequest extends CommandContext {
    private String fileName;

    public ExecuteSimMessageRequest() {}

    public ExecuteSimMessageRequest(CommandContext commandContext, String filename) {
        copyFrom(commandContext);
        this.fileName=filename;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
