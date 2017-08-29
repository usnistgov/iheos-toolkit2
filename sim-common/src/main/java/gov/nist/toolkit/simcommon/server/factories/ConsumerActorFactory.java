package gov.nist.toolkit.simcommon.server.factories;

import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.actortransaction.client.ParamType;
import gov.nist.toolkit.configDatatypes.server.SimulatorProperties;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.simcommon.client.Simulator;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.simcommon.server.AbstractActorFactory;
import gov.nist.toolkit.simcommon.server.IActorFactory;
import gov.nist.toolkit.simcommon.server.SimManager;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.xdsexception.NoSessionException;
import gov.nist.toolkit.xdsexception.NoSimulatorException;
import gov.nist.toolkit.xdsexception.client.EnvironmentNotSelectedException;

import java.util.ArrayList;
import java.util.List;

/**
 * XDS Document Consumer Simulator Factory
 */
public class ConsumerActorFactory  extends AbstractActorFactory implements IActorFactory {
    static final List<TransactionType> incomingTransactions = new ArrayList<>();

    @Override
    public Simulator buildNew(SimManager simm, SimId simId, boolean configureBase) throws EnvironmentNotSelectedException, NoSessionException {
        ActorType actorType = ActorType.DOC_CONSUMER;
        SimulatorConfig sc;
        if (configureBase)
            sc = configureBaseElements(actorType, simId);
        else
            sc = new SimulatorConfig();

        // placeholders - must be updated by client
        addEditableConfig(sc, SimulatorProperties.storedQueryEndpoint, ParamType.ENDPOINT, "http://host:port/service");
        addEditableConfig(sc, SimulatorProperties.storedQueryTlsEndpoint, ParamType.ENDPOINT, "https://host:port/service");
        addEditableConfig(sc, SimulatorProperties.retrieveEndpoint, ParamType.ENDPOINT, "http://host:port/service");
        addEditableConfig(sc, SimulatorProperties.retrieveTlsEndpoint, ParamType.ENDPOINT, "https://host:port/service");

        return new Simulator(sc);
    }

    @Override
    protected void verifyActorConfigurationOptions(SimulatorConfig config) {
        // nothing to do
    }

    /**
     * Does not apply to a source type actor
     * @param asc -
     * @param site -
     * @return -
     * @throws NoSimulatorException -
     */
    @Override
    public Site getActorSite(SimulatorConfig asc, Site site) throws NoSimulatorException {
        return (site == null) ? new Site(asc.getDefaultName()) : site;
    }

    @Override
    public List<TransactionType> getIncomingTransactions() {
        return incomingTransactions;
    }

    @Override
    public ActorType getActorType() {
        return ActorType.DOC_CONSUMER;
    }
}
