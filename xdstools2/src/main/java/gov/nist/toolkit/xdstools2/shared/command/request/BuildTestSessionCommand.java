package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.installation.shared.TestSession;
import gov.nist.toolkit.xdstools2.client.command.command.GenericCommand;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 * Created by skb1 on 1/30/18.
 */
public abstract class BuildTestSessionCommand extends GenericCommand<CommandContext, TestSession>{
    @Override
    public void run(CommandContext request) {
        ClientUtils.INSTANCE.getToolkitServices().buildTestSession(this);
    }
}
