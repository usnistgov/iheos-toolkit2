package gov.nist.toolkit.xdstools2.server.gazelle.actorConfig;

/**
 * Created by bmajur on 1/20/15.
 */
public class ProxyConfigToCSV {

    public String run(GazelleConfigs gConfigs) {
        StringBuilder builder = new StringBuilder();
        builder.append(ProxyConfig.getHeader()).append('\n');
        for (int i=0; i<gConfigs.size(); i++) {
            ProxyConfig pConfig = new ProxyConfig();
            pConfig.setSystem(gConfigs.getSystem(i));
            pConfig.setWsType(gConfigs.getProxyPort(i));
            builder.append(pConfig.toCSV()).append('\n');
        }
        return builder.toString();
    }
}
