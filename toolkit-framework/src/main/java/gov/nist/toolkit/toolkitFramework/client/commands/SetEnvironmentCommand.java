package gov.nist.toolkit.toolkitFramework.client.commands;

import gov.nist.toolkit.toolkitFramework.client.service.FrameworkServiceAsync;
import gov.nist.toolkit.toolkitFramework.client.toolSupport.command.CommandContext;

import javax.inject.Inject;

/**
 * Created by onh2 on 10/19/16.
 */
public class SetEnvironmentCommand extends GenericCommand<CommandContext,String>{
    @Inject
    FrameworkServiceAsync service;

    @Override
    public void run(CommandContext context) {
        service.setEnvironment(context,this);
    }

    @Override
    public void onComplete(String result) {
        // nothing happens here
    }
}
