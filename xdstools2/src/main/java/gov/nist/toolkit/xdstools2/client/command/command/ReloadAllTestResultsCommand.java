package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.results.shared.Test;
import gov.nist.toolkit.xdstools2.client.initialization.FrameworkInitialization;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

import java.util.List;

/**
 * Created by onh2 on 11/7/16.
 */
public abstract class ReloadAllTestResultsCommand extends GenericCommand<CommandContext,List<Test>>{
    @Override
    public void run(CommandContext var1) {
        FrameworkInitialization.data().getToolkitServices().reloadAllTestResults(var1,this);
    }
}
