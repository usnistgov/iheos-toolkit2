package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.sitemanagement.client.TransactionOfferings;
import gov.nist.toolkit.xdstools2.client.initialization.FrameworkInitialization;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 *
 */
public abstract class GetTransactionOfferingsCommand extends GenericCommand<CommandContext, TransactionOfferings> {
    @Override
    public void run(CommandContext var1) {
        FrameworkInitialization.data().getToolkitServices().getTransactionOfferings(var1, this);
    }
}
