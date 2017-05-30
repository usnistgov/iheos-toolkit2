package gov.nist.toolkit.desktop.client.environment;

import com.google.gwt.core.client.GWT;
import gov.nist.toolkit.server.client.EnvironmentService;
import gov.nist.toolkit.server.shared.command.CommandContext;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class LocalEnvironmentServiceImpl implements EnvironmentService {
    private static List<String> envNames = new ArrayList<>();
    private static String envName = "default";

    private static List<String> sessionNames = new ArrayList<>();
    private static String sessionName = "default";

    static {
        envNames.add("default");
        envNames.add("CAT");
        sessionNames.add("default");
        sessionNames.add("CAT");
    }

    @Override
    public List<String> getEnvironmentNames(CommandContext context) throws Exception {
        return envNames;
    }

    @Override
    public String getDefaultEnvironment(CommandContext context) throws Exception {
        return "default";
    }

    // *****************************************************************************************

    @Override
    public List<String> getMesaTestSessionNames(CommandContext request) throws Exception {
        GWT.log("Get Test Sessions " + sessionNames);
        return sessionNames;
    }

//    @Override
//    public boolean addMesaTestSession(CommandContext context) throws Exception {
//        assert(context.getTestSessionName() != null);
//        sessionNames.add(context.getTestSessionName());
//        GWT.log("Add Test Session " + context.getTestSessionName());
//        return false;
//    }

//    @Override
//    public boolean delMesaTestSession(CommandContext context) throws Exception {
//        assert(context.getTestSessionName() != null);
//        int i = sessionNames.indexOf(context.getTestSessionName());
//        if (i >= 0)
//            sessionNames.remove(i);
//        GWT.log("Delete Test Session " + context.getTestSessionName());
//        return false;
//    }

    @Override
    public String getDefaultTestSession(CommandContext context) {
        return "default";
    }
}
