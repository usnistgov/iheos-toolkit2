package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.xdstools2.client.initialization.XdsTools2Presenter;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 * Created by onh2 on 12/8/16.
 */
public abstract class GenerateTestkitStructureCommand extends GenericCommand<CommandContext,Void>{
    @Override
    public void run(CommandContext var1) {
        XdsTools2Presenter.data().getToolkitServices().generateTestkitStructure(var1,this);
    }
}