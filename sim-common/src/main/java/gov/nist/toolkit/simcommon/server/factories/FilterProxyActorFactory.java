package gov.nist.toolkit.simcommon.server.factories;

import gov.nist.toolkit.actortransaction.client.ParamType;
import gov.nist.toolkit.actortransaction.shared.ActorType;
import gov.nist.toolkit.adt.ListenerFactory;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.configDatatypes.server.FilterProxyProperties;
import gov.nist.toolkit.configDatatypes.server.SimulatorProperties;
import gov.nist.toolkit.installation.server.Installation;
import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.simcommon.client.Simulator;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement;
import gov.nist.toolkit.simcommon.server.*;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.sitemanagement.client.TransactionBean;
import gov.nist.toolkit.xdsexception.NoSimulatorException;

import java.util.logging.Level;
import java.util.logging.Logger;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class FilterProxyActorFactory extends AbstractActorFactory implements IActorFactory {
    static Logger logger = Logger.getLogger(FilterProxyActorFactory.class.getName());

    static final List<TransactionType> incomingTransactions =
            Arrays.asList(
                    TransactionType.ANY
            );

    @Override
    protected Simulator buildNew(SimManager simm, SimId simId, String environment, boolean configureBase) throws Exception {
        SimulatorConfig sc;
        if (configureBase)
            sc = configureBaseElements(getActorType(), simId, simId.getTestSession(), environment);
        else
            sc = new SimulatorConfig();

        return new Simulator(sc);
    }

    /**
     * Update the endpoints on the proxy to forward to the site.  This is called when
     * the site has been updated to keep the two in sync.
     * @param proxySimId
     * @param siteToProxy
     * @throws Exception
     */
    static public void updateEndpoints(SimId proxySimId, Site siteToProxy) throws Exception {
        FilterProxyActorFactory me = new FilterProxyActorFactory();
        SimulatorConfig proxyConfig = GenericSimulatorFactory.getSimConfig(proxySimId);

        me.addFixedConfig(proxyConfig, SimulatorProperties.relayToSiteName, ParamType.TEXT, siteToProxy.getName());

        // Delete old
        List<SimulatorConfigElement> endpointConfigs = proxyConfig.getEndpointConfigs();
        for (SimulatorConfigElement ele : endpointConfigs) {
            proxyConfig.deleteFixedByName(ele.getName());
        }

        if (proxyConfig.getEndpointConfigs().size() != 0) {
            logger.severe("Cannot delete endpoints for " + proxySimId);
        }

        ActorType actorType = ActorType.findActor(proxyConfig.getActorType());
        FilterProxyProperties fpProps = new FilterProxyProperties();
        // Add from Site
        for (TransactionBean tb : siteToProxy.transactions.transactions) {
            String name = tb.getName();
            TransactionType tt = tb.getTransactionType();
            String endpoint = tb.getEndpoint();
            if (!endpoint.trim().equals("")) {
                TransactionType type = tb.getTransactionType();

                String transactionEndpointLabel =
                        (tb.isSecure) ? tt.getTlsEndpointSimPropertyName() : tt.getEndpointSimPropertyName();
                String relayEndpointLabel =
                        FilterProxyProperties.getRelayEndpointName(transactionEndpointLabel);

                me.addFixedEndpoint(proxyConfig, transactionEndpointLabel, actorType, type, tb.isSecure);
                me.addFixedRelayEndpoint(proxyConfig, relayEndpointLabel, actorType, type, tb.isSecure, endpoint);
            }
        }

        me.addFixedConfig(proxyConfig, SimulatorProperties.PIF_PORT, ParamType.TEXT, siteToProxy.pifPort);
        me.addEditableConfig(proxyConfig, SimulatorProperties.homeCommunityId, ParamType.OID, siteToProxy.home);
        //me.addFixedConfig(proxyConfig, "pifHost", ParamType.TEXT, siteToProxy.pifHost);

        // Save
        try {
            new GenericSimulatorFactory().saveConfiguration(proxyConfig);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "saveSimConfig", e);
            throw e;
        }
        logger.fine("save complete");
    }

    @Override
    protected void verifyActorConfigurationOptions(SimulatorConfig config) throws Exception {

    }

    @Override
    public Site buildActorSite(SimulatorConfig asc, Site site) throws NoSimulatorException {
        String siteName = asc.getDefaultName();

        if (site == null)
            site = new Site(siteName, asc.getId().getTestSession());

        site.setTestSession(asc.getId().getTestSession());  // labels this site as coming from a sim

        boolean isAsync = false;

        for (TransactionType tt : TransactionType.values()) {
            String simProp = tt.getEndpointSimPropertyName();
            String simTlsProp = tt.getTlsEndpointSimPropertyName();
            SimulatorConfigElement ele;

            ele = asc.get(simProp);
            if (ele != null) {
                site.addTransaction(new TransactionBean(tt.getCode(),
                        TransactionBean.RepositoryType.NONE,
                        ele.asString(),
                        false,
                        isAsync));
            }

            ele = asc.get(simTlsProp);
            if (ele != null) {
                site.addTransaction(new TransactionBean(tt.getCode(),
                        TransactionBean.RepositoryType.NONE,
                        ele.asString(),
                        true,
                        isAsync));
            }
        }

        SimulatorConfigElement pifPortElement = asc.get(SimulatorProperties.PIF_PORT);
        if (pifPortElement != null)
            site.pifPort = pifPortElement.asString();
        site.pifHost = Installation.instance().propertyServiceManager().getToolkitHost();
        SimulatorConfigElement hciElement = asc.get(SimulatorProperties.homeCommunityId);
        if (hciElement != null)
            site.home = hciElement.asString();

        return site;
    }

    @Override
    public List<TransactionType> getIncomingTransactions() {
        return incomingTransactions;
    }

    @Override
    public ActorType getActorType() {
        return ActorType.FILTER_PROXY;
    }
}
