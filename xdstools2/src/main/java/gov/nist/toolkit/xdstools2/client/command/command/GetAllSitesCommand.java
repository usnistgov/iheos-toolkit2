package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.xdstools2.client.command.CommandContext;

import java.util.Collection;

import static gov.nist.toolkit.xdstools2.client.ToolWindow.toolkitService;

/**
 *
 */
public abstract class GetAllSitesCommand extends GenericCommand<CommandContext, Collection<Site>> {
    public GetAllSitesCommand() {
        super();
    }

    @Override
    public void run(CommandContext var1) {
        toolkitService.getAllSites(var1, this);
    }
}
