package gov.nist.toolkit.server.client;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import gov.nist.toolkit.server.shared.command.CommandContext;
import gov.nist.toolkit.server.shared.NoServletSessionException;

import java.util.List;

/**
 * Enable LocalService for isolated UI testing
 * Enable RemoteService for actually making calls to the back end
 *
 * See also ToolkitGinModule
 */
@RemoteServiceRelativePath("environment")
public interface EnvironmentService
//    extends LocalService
{
    List<String> getEnvironmentNames(CommandContext context) throws Exception;
    String getDefaultEnvironment(CommandContext context) throws Exception;
    List<String> getMesaTestSessionNames(CommandContext request) throws Exception;
    boolean addMesaTestSession(CommandContext context) throws Exception;
    boolean delMesaTestSession(CommandContext context) throws Exception;
    String getDefaultTestSession(CommandContext context) throws Exception;
}
