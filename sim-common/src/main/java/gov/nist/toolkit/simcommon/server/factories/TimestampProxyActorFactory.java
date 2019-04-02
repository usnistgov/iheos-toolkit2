package gov.nist.toolkit.simcommon.server.factories;

import gov.nist.toolkit.actortransaction.shared.ActorType;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.configDatatypes.server.SimulatorProperties;
import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.simcommon.client.Simulator;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.simcommon.server.AbstractActorFactory;
import gov.nist.toolkit.simcommon.server.IActorFactory;
import gov.nist.toolkit.simcommon.server.SimManager;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.sitemanagement.client.TransactionBean;
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
        ActorType actorType = ActorType.TIMESTAMP_PROXY;
        SimulatorConfig sc;
        if (configureBase)
            sc = configureBaseElements(getActorType(), simId, simId.getTestSession(), environment);
        else
            sc = new SimulatorConfig();

        addFixedEndpoint(sc, SimulatorProperties.pnrEndpoint, actorType, TransactionType.ANY, false);
        addFixedEndpoint(sc, SimulatorProperties.storedQueryEndpoint, actorType, TransactionType.ANY, false);
        addFixedEndpoint(sc, SimulatorProperties.retrieveEndpoint, actorType, TransactionType.ANY, false);
        addFixedEndpoint(sc, SimulatorProperties.igqEndpoint, actorType, TransactionType.ANY, false);
        addFixedEndpoint(sc, SimulatorProperties.igrEndpoint, actorType, TransactionType.ANY, false);
        addFixedEndpoint(sc, SimulatorProperties.xcrEndpoint, actorType, TransactionType.ANY, false);
        addFixedEndpoint(sc, SimulatorProperties.xcqEndpoint, actorType, TransactionType.ANY, false);
        addFixedEndpoint(sc, SimulatorProperties.xcpdEndpoint, actorType, TransactionType.ANY, false);

        return new Simulator(sc);
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

        site.addTransaction(new TransactionBean(
                TransactionType.XDR_PROVIDE_AND_REGISTER.getCode(),
                TransactionBean.RepositoryType.NONE,
                asc.get(SimulatorProperties.pnrEndpoint).asString(),
                false,
                isAsync));
        site.addTransaction(new TransactionBean(
                TransactionType.PROVIDE_AND_REGISTER.getCode(),
                TransactionBean.RepositoryType.NONE,
                asc.get(SimulatorProperties.pnrEndpoint).asString(),
                false,
                isAsync));
        site.addTransaction(new TransactionBean(
                TransactionType.STORED_QUERY.getCode(),
                TransactionBean.RepositoryType.NONE,
                asc.get(SimulatorProperties.storedQueryEndpoint).asString(),
                false,
                isAsync));
        site.addTransaction(new TransactionBean(
                TransactionType.RETRIEVE.getCode(),
                TransactionBean.RepositoryType.NONE,
                asc.get(SimulatorProperties.retrieveEndpoint).asString(),
                false,
                isAsync));
        site.addTransaction(new TransactionBean(
                TransactionType.IG_QUERY.getCode(),
                TransactionBean.RepositoryType.NONE,
                asc.get(SimulatorProperties.igqEndpoint).asString(),
                false,
                isAsync));
        site.addTransaction(new TransactionBean(
                TransactionType.IG_RETRIEVE.getCode(),
                TransactionBean.RepositoryType.NONE,
                asc.get(SimulatorProperties.igrEndpoint).asString(),
                false,
                isAsync));
        site.addTransaction(new TransactionBean(
                TransactionType.XC_RETRIEVE.getCode(),
                TransactionBean.RepositoryType.NONE,
                asc.get(SimulatorProperties.xcrEndpoint).asString(),
                false,
                isAsync));
        site.addTransaction(new TransactionBean(
                TransactionType.XC_QUERY.getCode(),
                TransactionBean.RepositoryType.NONE,
                asc.get(SimulatorProperties.xcqEndpoint).asString(),
                false,
                isAsync));
        site.addTransaction(new TransactionBean(
                TransactionType.XC_PATIENT_DISCOVERY.getCode(),
                TransactionBean.RepositoryType.NONE,
                asc.get(SimulatorProperties.xcpdEndpoint).asString(),
                false,
                isAsync));
        site.addTransaction(new TransactionBean(
                TransactionType.XCPD.getCode(),
                TransactionBean.RepositoryType.NONE,
                asc.get(SimulatorProperties.xcpdEndpoint).asString(),
                false,
                isAsync));


        return site;
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
