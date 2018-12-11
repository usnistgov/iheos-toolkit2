package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 * Created by skb1 on 11/16/18.
 */
public class GetOrchestrationPifTypeRequest extends CommandContext {
    Site site;
    String actorShortName;

    public GetOrchestrationPifTypeRequest(){}
    public GetOrchestrationPifTypeRequest(CommandContext context, Site site, String actorShortName){
        copyFrom(context);
        this.site = site;
        this.actorShortName = actorShortName;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public String getActorShortName() {
        return actorShortName;
    }

    public void setActorShortName(String actorShortName) {
        this.actorShortName = actorShortName;
    }
}
