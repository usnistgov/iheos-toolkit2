package gov.nist.toolkit.server.shared.command.request;

import gov.nist.toolkit.server.shared.command.CommandContext;
import gov.nist.toolkit.sitemanagement.client.Site;

/**
 * This class represent the command request containing all
 * the information required to save a site.
 *
 * @see CommandContext
 * Created by onh2 on 10/18/16.
 */
public class SaveSiteRequest extends CommandContext {

    private Site site;

    /**
     * Default constructor required by Serialization.
     * Be careful when using it.
     * Rather use {@link #SaveSiteRequest(CommandContext, Site)} instead.
     */
    public SaveSiteRequest() {
    }

    /**
     * @param commandContext context of the request.
     * @param site site that needs to be saved
     */
    public SaveSiteRequest(CommandContext commandContext, Site site) {
        copyFrom(commandContext);
        this.site=site;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }
}
