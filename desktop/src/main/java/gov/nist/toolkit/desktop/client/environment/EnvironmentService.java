package gov.nist.toolkit.desktop.client.environment;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import gov.nist.toolkit.desktop.client.commands.util.CommandContext;
import gov.nist.toolkit.desktop.shared.NoServletSessionException;

import java.util.List;

/**
 *
 */
@RemoteServiceRelativePath("environment")

public interface EnvironmentService extends RemoteService {
    List<String> getEnvironmentNames(CommandContext context) throws Exception;
    String setEnvironment(CommandContext context) throws Exception;
    String getCurrentEnvironment() throws NoServletSessionException;
    String getDefaultEnvironment(CommandContext context) throws Exception;

}
