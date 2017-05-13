package gov.nist.toolkit.toolkitFramework.client.util;

import gov.nist.toolkit.toolkitFramework.client.environment.EnvironmentState;
import gov.nist.toolkit.toolkitFramework.client.testSession.TestSessionManager;
import gov.nist.toolkit.toolkitFramework.client.toolSupport.command.CommandContext;

import javax.inject.Inject;

/**
 *
 */
public class CurrentCommandContext {


    static public CommandContext GET() { return new CurrentCommandContext().getCommandContext();}

    @Inject
    private EnvironmentState environmentState;

    @Inject
    private TestSessionManager testSessionManager;

    public CommandContext getCommandContext() {
        return new CommandContext(environmentState.getEnvironmentName(), testSessionManager.getCurrentTestSession());
    }

}
