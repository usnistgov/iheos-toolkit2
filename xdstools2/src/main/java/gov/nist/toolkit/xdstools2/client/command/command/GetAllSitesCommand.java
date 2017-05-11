package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.xdstools2.client.initialization.FrameworkInitialization;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

import java.util.Collection;

/**
 *
 */
public abstract class GetAllSitesCommand extends GenericCommand<CommandContext, Collection<Site>> {

    @Override
    public void run(CommandContext var1) {
        FrameworkInitialization.data().getToolkitServices().getAllSites(var1, this);
    }
}
