package gov.nist.toolkit.desktop.client;

import com.google.gwt.core.client.GWT;
import gov.nist.toolkit.desktop.client.commands.util.CommandContext;
import gov.nist.toolkit.desktop.client.environment.EnvironmentService;
import gov.nist.toolkit.desktop.client.environment.EnvironmentServiceAsync;

/**
 * Client Utilities singleton.
 */
public class ClientUtils {
    public static final ClientUtils INSTANCE=new ClientUtils();
//    private ClientFactory clientFactory=GWT.create(ClientFactory.class);
    public EnvironmentServiceAsync environmentService = GWT.create(EnvironmentService.class);

    public ClientUtils(){}

    public EnvironmentServiceAsync getEnvironmentServices(){
        return environmentService;
    }

    public CommandContext getCommandContext(){
        // this is a horrible hack until the initialization is cleaned up
        String env = "default";
        String testSession = "default";
        return new CommandContext(env, testSession);
    }

}
