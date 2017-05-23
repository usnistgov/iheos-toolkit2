package gov.nist.toolkit.desktop.shared.command.request;


import gov.nist.toolkit.desktop.client.commands.util.CommandContext;

/**
 * Created by onh2 on 10/14/16.
 */
public class SetAssignedSiteForTestSessionRequest extends CommandContext {
    private String selecetedTestSession;
    private String selectedSite;

    public SetAssignedSiteForTestSessionRequest() {
    }

    public SetAssignedSiteForTestSessionRequest(CommandContext context, String selectedTestSession, String selectedSite) {
        copyFrom(context);
        this.selecetedTestSession=selectedTestSession;
        this.selectedSite=selectedSite;
    }

    public String getSelecetedTestSession(){
        return selecetedTestSession;
    }

    public void setSelecetedTestSession(String selecetedTestSession){
        this.selecetedTestSession=selecetedTestSession;
    }

    public String getSelectedSite(){
        return selectedSite;
    }

    public void setSelectedSite(String selectedSite){
        this.selectedSite=selectedSite;
    }
}
