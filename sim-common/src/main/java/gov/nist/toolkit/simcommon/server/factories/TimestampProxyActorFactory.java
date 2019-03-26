package gov.nist.toolkit.simcommon.server.factories;

import gov.nist.toolkit.actortransaction.shared.ActorType;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.simcommon.client.Simulator;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.simcommon.server.AbstractActorFactory;
import gov.nist.toolkit.simcommon.server.IActorFactory;
import gov.nist.toolkit.simcommon.server.SimManager;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.xdsexception.NoSimulatorException;

import java.util.Arrays;
import java.util.List;

public class TimestampProxyActorFactory extends AbstractActorFactory implements IActorFactory {

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

    @Override
    protected void verifyActorConfigurationOptions(SimulatorConfig config) throws Exception {

    }

    @Override
    public Site buildActorSite(SimulatorConfig asc, Site site) throws NoSimulatorException {
        return null;
    }

    @Override
    public List<TransactionType> getIncomingTransactions() {
        return incomingTransactions;
    }

    @Override
    public ActorType getActorType() {
        return ActorType.TIMESTAMP_PROXY;
    }
}
