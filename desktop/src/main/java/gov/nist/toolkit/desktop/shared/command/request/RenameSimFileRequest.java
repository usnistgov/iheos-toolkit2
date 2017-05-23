package gov.nist.toolkit.desktop.shared.command.request;


import gov.nist.toolkit.desktop.client.commands.util.CommandContext;

/**
 * Created by onh2 on 11/1/16.
 */
public class RenameSimFileRequest extends CommandContext {
    private String newSimFileName;
    private String oldSimFileName;

    public RenameSimFileRequest(){}
    public RenameSimFileRequest(CommandContext context, String oldSimFileName, String newSimFileName){
        copyFrom(context);
        this.oldSimFileName=oldSimFileName;
        this.newSimFileName=newSimFileName;
    }

    public String getNewSimFileName() {
        return newSimFileName;
    }

    public void setNewSimFileName(String newSimFileName) {
        this.newSimFileName = newSimFileName;
    }

    public String getOldSimFileName() {
        return oldSimFileName;
    }

    public void setOldSimFileName(String oldSimFileName) {
        this.oldSimFileName = oldSimFileName;
    }
}
