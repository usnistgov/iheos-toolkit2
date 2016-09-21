package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.sitemanagement.client.TransactionOfferings;
import gov.nist.toolkit.xdstools2.client.command.CommandContext;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;

/**
 *
 */
abstract public class GetTransactionOfferingsCommand extends GenericCommand<CommandContext, TransactionOfferings> {
    public GetTransactionOfferingsCommand() {
        super();
    }

    @Override
    public void run(CommandContext var1) {
        ClientUtils.INSTANCE.getToolkitServices().getTransactionOfferings(var1, this);
    }
}
