package gov.nist.toolkit.xdstools2.client.util;

import gov.nist.toolkit.xdstools2.client.initialization.FrameworkInitialization;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 *
 */
public class CommandContextFactory {

    static public CommandContext getCommandContext(){
        // this is a horrible hack until the initialization is cleaned up
        String env = FrameworkInitialization.data().getEnvironmentState().getEnvironmentName();
        if (env == null || env.equals("null")) env = "default";
        return new CommandContext(env, FrameworkInitialization.data().getTestSessionManager().getCurrentTestSession());
    }

}
