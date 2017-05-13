package gov.nist.toolkit.toolkitFramework.client.commands;


import gov.nist.toolkit.toolkitFramework.client.service.FrameworkServiceAsync;
import gov.nist.toolkit.toolkitFramework.client.toolSupport.command.CommandContext;

import javax.inject.Inject;

/**
 *
 */
public abstract class DeleteMesaTestSessionCommand extends GenericCommand<CommandContext,Boolean>{
    @Inject
    FrameworkServiceAsync service;
    @Override
    public void run(CommandContext var1) {
        service.delMesaTestSession(var1,this);
    }
}
