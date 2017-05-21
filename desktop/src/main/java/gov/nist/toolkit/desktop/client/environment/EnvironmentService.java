package gov.nist.toolkit.desktop.client.environment;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import gov.nist.toolkit.desktop.client.commands.util.CommandContext;
import gov.nist.toolkit.desktop.shared.NoServletSessionException;

import java.util.List;

/**
 * Enable LocalService for isolated UI testing
 * Enable RemoteService for actually making calls to the back end
 *
 * See also ToolkitGinModule
 */
@RemoteServiceRelativePath("environment")
public interface EnvironmentService
//        extends RemoteService
    extends LocalService
{
    List<String> getEnvironmentNames(CommandContext context) throws Exception;
    String setEnvironment(CommandContext context) throws Exception;
    String getCurrentEnvironment() throws NoServletSessionException;
    String getDefaultEnvironment(CommandContext context) throws Exception;
    List<String> getMesaTestSessionNames(CommandContext request) throws Exception;
    String setMesaTestSession(String name) throws Exception;
    boolean addMesaTestSession(CommandContext context) throws Exception;
    boolean delMesaTestSession(CommandContext context) throws Exception;
    String getDefaultTestSession(CommandContext context) throws Exception;
}
