package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.sitemanagement.client.TransactionOfferings;
import gov.nist.toolkit.xdstools2.client.initialization.XdsTools2Presenter;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 *
 */
public abstract class GetTransactionOfferingsCommand extends GenericCommand<CommandContext, TransactionOfferings> {
    @Override
    public void run(CommandContext var1) {
        XdsTools2Presenter.data().getToolkitServices().getTransactionOfferings(var1, this);
    }
}