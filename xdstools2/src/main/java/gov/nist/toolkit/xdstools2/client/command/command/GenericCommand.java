package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.xdstools2.client.command.CommandModule;
import gov.nist.toolkit.xdstools2.client.command.CommandRequest;

/**
 * Utility to compose commands to the server.
 * @param <R></R> is the request type.  This must inherit from CommandContext which carries
 * the environment and test session.
 * @param <C></C> is the callback type.
 */
public abstract class GenericCommand<R, C> extends CommandModule<C> implements CommandRequest<R, C> {
    public GenericCommand() {
        super();
    }
}
