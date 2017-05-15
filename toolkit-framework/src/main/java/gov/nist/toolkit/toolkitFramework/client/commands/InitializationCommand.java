package gov.nist.toolkit.toolkitFramework.client.commands;


import gov.nist.toolkit.toolkitFramework.client.service.FrameworkServiceAsync;
import gov.nist.toolkit.toolkitFramework.client.toolSupport.command.CommandContext;
import gov.nist.toolkit.toolkitFramework.shared.InitializationResponse;

import javax.inject.Inject;

/**
 *
 */
public abstract class InitializationCommand extends GenericCommand<CommandContext, InitializationResponse> {

    @Inject
    FrameworkServiceAsync service;

    @Override
    public void run(CommandContext var1) {
        service.getInitialization(var1,this);
    }
}
