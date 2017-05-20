package gov.nist.toolkit.desktop.client.environment;

import gov.nist.toolkit.desktop.client.commands.util.CommandContext;
import gov.nist.toolkit.desktop.shared.NoServletSessionException;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class LocalEnvironmentServiceImpl implements EnvironmentService {
    private List<String> envNames = new ArrayList<>();
    private String envName = "default";

    public LocalEnvironmentServiceImpl() {
        envNames.add("default");
        sessionNames.add("default");
    }

    @Override
    public List<String> getEnvironmentNames(CommandContext context) throws Exception {
        return envNames;
    }

    @Override
    public String setEnvironment(CommandContext context) throws Exception {
        assert(context.getEnvironmentName() != null);
        envName = context.getEnvironmentName();
        return "";
    }

    @Override
    public String getCurrentEnvironment() throws NoServletSessionException {
        return envName;
    }

    @Override
    public String getDefaultEnvironment(CommandContext context) throws Exception {
        return "default";
    }

    private List<String> sessionNames = new ArrayList<>();
    private String sessionName = "default";

    @Override
    public List<String> getMesaTestSessionNames(CommandContext request) throws Exception {
        return sessionNames;
    }

    @Override
    public String setMesaTestSession(String name) throws Exception {
        assert(name != null);
        sessionName = name;
        return "";
    }

    @Override
    public boolean addMesaTestSession(CommandContext context) throws Exception {
        assert(context.getTestSessionName() != null);
        sessionNames.add(context.getTestSessionName());
        return false;
    }

    @Override
    public boolean delMesaTestSession(CommandContext context) throws Exception {
        assert(context.getTestSessionName() != null);
        int i = sessionNames.indexOf(context.getTestSessionName());
        if (i >= 0)
            sessionNames.remove(i);
        return false;
    }
}
