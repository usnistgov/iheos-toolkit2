package gov.nist.toolkit.desktop.client.commands;


import gov.nist.toolkit.desktop.client.ClientUtils;
import gov.nist.toolkit.desktop.client.commands.util.GenericCommand;
import gov.nist.toolkit.server.shared.command.request.GetStsSamlAssertionRequest;

/**
 *
 */
public abstract class GetStsSamlAssertionCommand extends GenericCommand<GetStsSamlAssertionRequest,String> {
    @Override
    public void run(GetStsSamlAssertionRequest var1) {
        ClientUtils.INSTANCE.getToolkitServices().getStsSamlAssertion(var1,this);
    }

}
