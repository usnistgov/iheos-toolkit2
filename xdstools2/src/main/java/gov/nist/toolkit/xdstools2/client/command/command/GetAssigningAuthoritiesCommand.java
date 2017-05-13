package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.xdstools2.client.initialization.XdsTools2Presenter;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

import java.util.List;

/**
 * Request type (R) is CommandContext (base class is used because no other parms needed
 * Callback type (C) is List<String></String>
 *
 * onComplete is callback to user of this command. When creating new commands, IntelliJ
 * auto-completion will fill in an empty onComplete method because it is defined as abstract
 * in the base class.  To give user chance to capture callback, delete that auto-generated onComplete()
 * from this class.
 */
public abstract class GetAssigningAuthoritiesCommand extends GenericCommand<CommandContext, List<String>> {
    @Override
    public void run(CommandContext var1) {
        XdsTools2Presenter.data().getToolkitServices().getAssigningAuthorities(var1, this); // this because primary callback is offered by base class
    }

}
