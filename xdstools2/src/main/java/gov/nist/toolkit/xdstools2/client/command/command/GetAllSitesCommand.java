package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.xdstools2.client.initialization.XdsTools2Presenter;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

import java.util.Collection;

/**
 *
 */
public abstract class GetAllSitesCommand extends GenericCommand<CommandContext, Collection<Site>> {

    @Override
    public void run(CommandContext var1) {
        XdsTools2Presenter.data().getToolkitServices().getAllSites(var1, this);
    }
}
