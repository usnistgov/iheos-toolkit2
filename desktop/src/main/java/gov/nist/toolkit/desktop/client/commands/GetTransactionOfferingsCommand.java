package gov.nist.toolkit.desktop.client.commands;

import gov.nist.toolkit.desktop.client.ClientUtils;
import gov.nist.toolkit.server.shared.command.CommandContext;
import gov.nist.toolkit.desktop.client.commands.util.GenericCommand;
import gov.nist.toolkit.sitemanagement.client.TransactionOfferings;

/**
 *
 */
public abstract class GetTransactionOfferingsCommand extends GenericCommand<CommandContext, TransactionOfferings> {
    @Override
    public void run(CommandContext var1) {
        ClientUtils.INSTANCE.getToolkitServices().getTransactionOfferings(var1, this);
    }
}
