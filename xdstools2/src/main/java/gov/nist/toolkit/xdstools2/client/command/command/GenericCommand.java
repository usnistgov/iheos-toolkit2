package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.xdstools2.client.ToolWindow;
import gov.nist.toolkit.xdstools2.client.command.CommandModule;
import gov.nist.toolkit.xdstools2.client.command.request.CommandRequest;

/**
 * Utility to compose commands to the server.
 * R is the request type.  This must inherit from CommandContext which carries
 * the environment and test session.
 * C is the callback type.
 */
abstract public class GenericCommand<R, C> extends CommandModule<C> implements CommandRequest<R, C> {
    public GenericCommand() {
        super();
    }
}
