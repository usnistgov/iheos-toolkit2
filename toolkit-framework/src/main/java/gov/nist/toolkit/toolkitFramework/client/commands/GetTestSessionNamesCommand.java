package gov.nist.toolkit.toolkitFramework.client.commands;


import gov.nist.toolkit.toolkitFramework.client.service.FrameworkServiceAsync;
import gov.nist.toolkit.toolkitFramework.client.toolSupport.command.CommandContext;

import javax.inject.Inject;
import java.util.List;


/**
 *
 */
public abstract class GetTestSessionNamesCommand extends GenericCommand<CommandContext, List<String>> {

    @Inject
    FrameworkServiceAsync frameworkService;

    @Override
    public void run(CommandContext var1) {
        frameworkService.getMesaTestSessionNames(var1, this);
    }
}
