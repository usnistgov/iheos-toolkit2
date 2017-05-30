package gov.nist.toolkit.server.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.server.client.EnvironmentService;
import gov.nist.toolkit.server.shared.command.CommandContext;

import java.util.List;

/**
 *
 */
@SuppressWarnings("serial")
public class EnvironmentServiceImpl extends RemoteServiceServlet implements EnvironmentService {
    @Override
    public List<String> getEnvironmentNames(CommandContext context) throws Exception {
        return Installation.instance().getEnvironmentNames();
    }

    @Override
    public String getDefaultEnvironment(CommandContext context) throws Exception {
            String defaultEnvironment = Installation.instance().propertyServiceManager().getDefaultEnvironment();
            if (!Installation.instance().environmentExists(defaultEnvironment))
                throw new Exception("Default environment does not exist.");
            return defaultEnvironment;
    }

    @Override
    public List<String> getMesaTestSessionNames(CommandContext request) throws Exception {
        return Installation.instance().getMesaTestSessionNames();
    }

//    @Override
//    public boolean addMesaTestSession(CommandContext context) throws Exception {
//        return new XdsTestServiceManager(null).addMesaTestSession(context.getTestSessionName());
//    }
//
//    @Override
//    public boolean delMesaTestSession(CommandContext context) throws Exception {
//        return new XdsTestServiceManager(null).delMesaTestSession(context.getTestSessionName());
//    }

    @Override
    public String getDefaultTestSession(CommandContext context) throws Exception {
        return null;
    }
}
