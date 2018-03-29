package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 * Created by onh2 on 10/14/16.
 */
public class GetSiteNamesRequest extends CommandContext {
    private boolean reload;
    private boolean simAlso;
    private boolean qualified = false;

    public GetSiteNamesRequest() {
    }

    public GetSiteNamesRequest(CommandContext context,boolean reload,boolean simAlso){
        copyFrom(context);
        this.reload=reload;
        this.simAlso=simAlso;
    }

    public boolean getReload(){
        return reload;
    }

    public void setReload(boolean reload){
        this.reload=reload;
    }

    public boolean getSimAlso(){
        return simAlso;
    }

    public void setSimAlso(boolean simAlso){
        this.simAlso=simAlso;
    }

    public boolean isQualified() {
        return qualified;
    }

    public void setQualified(boolean qualified) {
        this.qualified = qualified;
    }

    public GetSiteNamesRequest withQualified(boolean qualified) {
        this.qualified = qualified;
        return this;
    }
}
