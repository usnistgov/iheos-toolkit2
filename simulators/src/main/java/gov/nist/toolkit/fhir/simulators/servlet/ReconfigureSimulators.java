package gov.nist.toolkit.fhir.simulators.servlet;

import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.actortransaction.server.EndpointParser;
import gov.nist.toolkit.configDatatypes.server.SimulatorProperties;
import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement;
import gov.nist.toolkit.simcommon.server.GenericSimulatorFactory;
import gov.nist.toolkit.simcommon.server.SimDb;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import org.apache.log4j.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;

/**
 * Reconfigure simulators based on updates to
 *   Toolkit Host
 *   Toolkit Port
 *   Toolkit TLS Port
 */
public class ReconfigureSimulators extends HttpServlet {
    private String configuredHost;
    private String configuredPort;
    private String configuredTlsPort;
    private String configuredProxyPort;

    // These are used for testing only
    private String overrideHost = null;
    private String overridePort = null;
    private String overrideTlsPort = null;
    private String overrideProxyPort = null;

    private static Logger logger = Logger.getLogger(ReconfigureSimulators.class);

    public void init(ServletConfig sConfig) {
        configuredHost = Installation.instance().propertyServiceManager().getToolkitHost();
        configuredPort = Installation.instance().propertyServiceManager().getToolkitPort();
        configuredTlsPort = Installation.instance().propertyServiceManager().getToolkitTlsPort();
        configuredProxyPort = Installation.instance().propertyServiceManager().getProxyPort();

        for (SimId simId : SimDb.getAllSimIds()) {
            reconfigure(simId);
        }
    }

    public void reconfigure(SimId simId) {
        boolean error = false;
        boolean updated = false;
        SimulatorConfig config;
        logger.info("Reconfiguring Simulator " + simId.toString());
        try {
            config = new SimDb().getSimulator(simId);
        } catch (Exception e) {
            logger.error("    Cannot load " + ExceptionUtil.exception_details(e, 5));
            return;
        }

        ActorType actorType = ActorType.findActor(config.getActorType());
        if (actorType == null) {
            logger.error("ERROR: Simulator " + simId + " of ActorType " + config.getActorType() + " - actor type does not exist");
            return;
        }

        boolean isProxy = actorType.isProxy();

        for (SimulatorConfigElement ele : config.getEndpointConfigs()) {
            boolean isTls = SimulatorProperties.isTlsEndpoint(ele.getName());
            String existingEndpoint = ele.asString();
            EndpointParser ep = new EndpointParser(existingEndpoint);
            if (!ep.validate()) {
                error = true;
                logger.error("    " + ele.getName() + ": " + existingEndpoint + " - does not validate - " + ep.getError());
                continue;
            }

            String host = ep.getHost();
            String port = ep.getPort();

            if (isProxy) {
                if (!isTls) {
                    if (!port.equals(getConfiguredPort())) {
                        ep.updateHostAndPort(getConfiguredHost(), getConfiguredProxyPort());
                        ele.setStringValue(ep.getEndpoint());
                        updated = true;
                    }
                }
            } else {
                if (isTls) {
                    if (!host.equals(getConfiguredHost()) || !port.equals(getConfiguredTlsPort())) {
                        ep.updateHostAndPort(getConfiguredHost(), getConfiguredTlsPort());
                        ele.setStringValue(ep.getEndpoint());
                        updated = true;
                    }
                } else {
                    if (!host.equals(getConfiguredHost()) || !port.equals(getConfiguredPort())) {
                        ep.updateHostAndPort(getConfiguredHost(), getConfiguredPort());
                        ele.setStringValue(ep.getEndpoint());
                        updated = true;
                    }
                }
            }
        }

        try {
            if (updated)
                new GenericSimulatorFactory(null).saveConfiguration(config);
        } catch (Exception e) {
            logger.error("    Error saving updates: " + e.getMessage());
        }

        if (!error && !updated)
            logger.info("    ok");
        if (!error && updated)
            logger.info("    updated");
    }

    public void setOverrideHost(String overrideHost) {
        this.overrideHost = overrideHost;
    }

    public void setOverridePort(String overridePort) {
        this.overridePort = overridePort;
    }

    public void setOverrideTlsPort(String overrideTlsPort) {
        this.overrideTlsPort = overrideTlsPort;
    }

    private String getConfiguredHost() {
        if (overrideHost != null) return overrideHost;
        return configuredHost;
    }

    private String getConfiguredPort() {
        if (overridePort != null) return overridePort;
        return configuredPort;
    }

    private String getConfiguredTlsPort() {
        if (overrideTlsPort != null) return overrideTlsPort;
        return configuredTlsPort;
    }

    private String getConfiguredProxyPort() {
        if (overrideProxyPort != null) return overrideProxyPort;
        return configuredProxyPort;
    }

}
