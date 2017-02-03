package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;

import java.util.Collection;

/**
 *
 */
public abstract class GetAllSitesCommand extends GenericCommand<CommandContext, Collection<Site>> {

    @Override
    public void run(CommandContext var1) {
        ClientUtils.INSTANCE.getToolkitServices().getAllSites(var1, this);
    }
}
