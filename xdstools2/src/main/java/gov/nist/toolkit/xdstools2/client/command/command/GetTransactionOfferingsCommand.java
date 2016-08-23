package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.sitemanagement.client.TransactionOfferings;
import gov.nist.toolkit.xdstools2.client.ToolWindow;
import gov.nist.toolkit.xdstools2.client.command.CommandContext;

import static gov.nist.toolkit.xdstools2.client.ToolWindow.toolkitService;

/**
 *
 */
abstract public class GetTransactionOfferingsCommand extends GenericCommand<CommandContext, TransactionOfferings> {
    public GetTransactionOfferingsCommand(ToolWindow toolWindow) {
        super(toolWindow);
    }

    @Override
    public void run(CommandContext var1) {
        toolkitService.getTransactionOfferings(var1, this);
    }
}
