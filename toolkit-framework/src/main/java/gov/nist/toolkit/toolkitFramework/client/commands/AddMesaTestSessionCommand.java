package gov.nist.toolkit.toolkitFramework.client.commands;


import gov.nist.toolkit.toolkitFramework.client.service.FrameworkServiceAsync;
import gov.nist.toolkit.toolkitFramework.client.toolSupport.command.CommandContext;

import javax.inject.Inject;

/**
 *
 */
public abstract class AddMesaTestSessionCommand extends GenericCommand<CommandContext,Boolean>{

    @Inject
    FrameworkServiceAsync service;

    @Override
    public void run(CommandContext context) {
        service.addMesaTestSession(context,this);
    }
}
