package gov.nist.toolkit.actorfactory;

import java.util.ArrayList;
import java.util.List;

import gov.nist.toolkit.actorfactory.client.SimId;
import gov.nist.toolkit.actorfactory.client.Simulator;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.actortransaction.client.ParamType;
import gov.nist.toolkit.configDatatypes.SimulatorProperties;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.xdsexception.EnvironmentNotSelectedException;
import gov.nist.toolkit.xdsexception.NoSessionException;
import gov.nist.toolkit.xdsexception.NoSimulatorException;

/**
 * XDSI Document Consumer Simulator Factory
 */
public class ImgConsumerActorFactory  extends AbstractActorFactory {
    static final List<TransactionType> incomingTransactions = new ArrayList<>();

    @Override
    public Simulator buildNew(SimManager simm, SimId simId, boolean configureBase) throws EnvironmentNotSelectedException, NoSessionException {
        ActorType actorType = ActorType.IMAGING_DOC_CONSUMER;
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
        return null;
    }

    @Override
    public List<TransactionType> getIncomingTransactions() {
        return incomingTransactions;
    }
}
