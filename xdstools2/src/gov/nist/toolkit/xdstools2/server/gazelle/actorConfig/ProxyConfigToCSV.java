package gov.nist.toolkit.xdstools2.server.gazelle.actorConfig;

import java.util.Properties;

/**
 * Created by bmajur on 1/20/15.
 */
public class ProxyConfigToCSV {

    public static String run(GazelleConfigs gConfigs) {
        StringBuilder builder = new StringBuilder();
        builder.append(ProxyConfig.getHeader()).append('\n');
        for (int i=0; i<gConfigs.size(); i++) {
            if (gConfigs.getDetail(i).contains("Async")) continue;
            ProxyConfig pConfig = new ProxyConfig();
            pConfig.setSystem(gConfigs.getSystem(i));
            pConfig.setWsType(gConfigs.getConfigDetail(i));
            pConfig.setProxyPort(gConfigs.getProxyPort(i));
            builder.append(pConfig.toCSV()).append('\n');
        }
        return builder.toString();
    }

    public static Properties buildProperties(GazelleConfigs gConfigs) {
        Properties props = new Properties();
        for (int i=0; i<gConfigs.size(); i++) {
            String wstype = gConfigs.getDetail(i);
            if (wstype.contains("Async")) continue;
            if (!wstype.startsWith("ITI-41")) continue;
            String hostname = gConfigs.getHost(i);
            String proxyPort = gConfigs.getProxyPort(i);
            if (proxyPort == null || proxyPort.equals("")) continue;
            props.setProperty(hostname + ".pnr.notls", proxyPort);
        }
        return props;
    }
}
