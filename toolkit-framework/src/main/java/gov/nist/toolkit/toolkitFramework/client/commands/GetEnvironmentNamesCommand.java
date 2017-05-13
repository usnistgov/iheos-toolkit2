package gov.nist.toolkit.toolkitFramework.client.commands;


import gov.nist.toolkit.toolkitFramework.client.service.FrameworkServiceAsync;
import gov.nist.toolkit.toolkitFramework.client.toolSupport.command.CommandContext;

import javax.inject.Inject;
import java.util.List;

/**
 *
 */
public abstract class GetEnvironmentNamesCommand extends GenericCommand<CommandContext,List<String>>{

    @Inject
    FrameworkServiceAsync service;

    @Override
    public void run(CommandContext var1) {
        service.getEnvironmentNames(var1, this);
    }
}
