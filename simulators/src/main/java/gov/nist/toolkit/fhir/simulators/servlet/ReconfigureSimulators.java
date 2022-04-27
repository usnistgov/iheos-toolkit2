package gov.nist.toolkit.fhir.simulators.servlet;

import gov.nist.toolkit.actortransaction.shared.ActorType;
import gov.nist.toolkit.actortransaction.server.EndpointParser;
import gov.nist.toolkit.configDatatypes.server.SimulatorProperties;
import gov.nist.toolkit.installation.server.Installation;
import gov.nist.toolkit.installation.shared.TestSession;
import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement;
import gov.nist.toolkit.simcommon.server.AbstractActorFactory;
import gov.nist.toolkit.simcommon.server.GenericSimulatorFactory;
import gov.nist.toolkit.simcommon.server.SimDb;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;

/**
 * Reconfigure simulators based on updates to
 *   Toolkit Host
 *   Toolkit Port
 *   Toolkit TLS Port
 *   Simulator ID
 */
public class ReconfigureSimulators extends HttpServlet {
    private String configuredHost;
    private String configuredPort;
    private String configuredTlsPort;
    private String configuredProxyPort;
    private String configuredContext;

    // These are used for testing only
    private String overrideHost = null;
    private String overridePort = null;
    private String overrideTlsPort = null;
    private String overrideProxyPort = null;
    private String overrideContext = null;

    private static Logger logger = Logger.getLogger(ReconfigureSimulators.class.getName());

    public void init(ServletConfig sConfig) {
        configuredHost = Installation.instance().propertyServiceManager().getToolkitHost();
        configuredPort = Installation.instance().propertyServiceManager().getToolkitPort();
        configuredTlsPort = Installation.instance().propertyServiceManager().getToolkitTlsPort();
        configuredProxyPort = Installation.instance().propertyServiceManager().getProxyPort();
        configuredContext = Installation.instance().getServletContextName();
        if (configuredContext.startsWith("/"))
            configuredContext = configuredContext.substring(1);

        logger.info("Reconfiguring Simulators to host " + getConfiguredHost() + " port " + getConfiguredPort() + " context " + getConfiguredContext());

        logger.info("Reconfiguring simulators in " + Installation.instance().getTestSessions());

        for (TestSession testSession : Installation.instance().getTestSessions()) {

            for (SimId simId : SimDb.getAllSimIds(testSession)) {
                try {
                    reconfigure(simId);
                } catch (Throwable e) {
                    logger.severe("Reconfigure of sim " + simId + " failed - " + ExceptionUtil.exception_details(e));
                }
            }
        }
    }

    public void reconfigure(SimId simId) {
        String simIdString = simId.toString();
        boolean error = false;
        boolean updated = false;
        SimulatorConfig config;
        logger.info("Reconfiguring Simulator " + simIdString);

        try {
            AbstractActorFactory.updateSimConfiguration(simId);
        } catch (Exception e) {
            logger.severe("    updateSimConfiguration failed: " + ExceptionUtil.exception_details(e, 5));
        }

        try {
            config = new SimDb().getSimulator(simId);
        } catch (Exception e) {
            logger.severe("    Cannot load " + ExceptionUtil.exception_details(e, 5));
            return;
        }

        ActorType actorType = ActorType.findActor(config.getActorType());
        if (actorType == null) {
            logger.severe("ERROR: Simulator " + simId + " of ActorType " + config.getActorType() + " - actor type does not exist");
            return;
        }

//        boolean isProxy = actorType.isProxy();

        for (SimulatorConfigElement ele : config.getEndpointConfigs()) {
            boolean isTls = SimulatorProperties.isTlsEndpoint(ele.getName());
            String existingEndpoint = ele.asString();
            EndpointParser ep = new EndpointParser(existingEndpoint);
            logger.info("From " + existingEndpoint);
            if (!ep.validate()) {
                error = true;
                logger.severe("    " + ele.getName() + ": " + existingEndpoint + " - does not validate - " + ep.getError());
                continue;
            }

            String host = ep.getHost();
            String port = ep.getPort();
            String context = ep.getContext();

            /*
            if (isProxy) {
                if (!isTls) {
                    if (!port.equals(getConfiguredPort())) {
                        ep.updateHostAndPort(getConfiguredHost(), getConfiguredProxyPort());
                        ele.setStringValue(ep.getEndpoint());
                        logger.info("...to " + ep.getEndpoint());
                        updated = true;
                    }
                }
            } else {
             */
            if (actorType.isFilterProxy()) {
                // Issue #547
                logger.info("...endpoint not updated.");
            } else {
                if (isTls) {
                    if (!host.equals(getConfiguredHost()) || !port.equals(getConfiguredTlsPort())) {
                        ep.updateHostAndPort(getConfiguredHost(), getConfiguredTlsPort());
                        ele.setStringValue(ep.getEndpoint());
                        logger.info("...to " + ep.getEndpoint());
                        updated = true;
                    }
                } else {
                    if (!host.equals(getConfiguredHost()) || !port.equals(getConfiguredPort())) {
                        ep.updateHostAndPort(getConfiguredHost(), getConfiguredPort());
                        ele.setStringValue(ep.getEndpoint());
                        logger.info("...to " + ep.getEndpoint());
                        updated = true;
                    }
                }

                if (!context.equals(getConfiguredContext())) {
                    ep.setContext(getConfiguredContext());
                    logger.info("...to " + ep.getEndpoint());
                    ele.setStringValue(ep.getEndpoint());
                    updated = true;
                }

            /* Fix Issue #436: This block is not needed since simulators cannot be renamed.
            if (!ep.getSimId().equals(simIdString)) {
                ep.setSimId(simIdString);
                logger.info("...to " + ep.getEndpoint());
                ele.setStringValue(ep.getEndpoint());
                updated = true;
            }
            */
            }
        }

        try {
            if (updated)
                new GenericSimulatorFactory(null).saveConfiguration(config);
        } catch (Exception e) {
            logger.severe("    Error saving updates: " + e.getMessage());
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

    public void setOverrideContext(String service) {
        this.overrideContext = service;
    }

    public String getConfiguredContext() {
        if (overrideContext != null) return overrideContext;
        return configuredContext;
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
