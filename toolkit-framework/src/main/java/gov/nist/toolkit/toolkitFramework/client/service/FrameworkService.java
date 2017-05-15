package gov.nist.toolkit.toolkitFramework.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import gov.nist.toolkit.toolkitFramework.client.toolSupport.command.CommandContext;
import gov.nist.toolkit.toolkitFramework.shared.InitializationResponse;

import java.util.List;

/**
 *
 */
@RemoteServiceRelativePath("framework")
public interface FrameworkService extends RemoteService {
    String getDefaultEnvironment(CommandContext context);
    List<String> getEnvironmentNames(CommandContext context);
    String setEnvironment(CommandContext context);
    List<String> getMesaTestSessionNames(CommandContext request);
    Boolean addMesaTestSession(CommandContext context);
    Boolean delMesaTestSession(CommandContext context);
    String getAdminPassword(CommandContext context);
    InitializationResponse getInitialization(CommandContext context);
    String getImplementationVersion(CommandContext context);

}
