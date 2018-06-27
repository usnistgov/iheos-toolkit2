package gov.nist.toolkit.actorfactory

import gov.nist.toolkit.actortransaction.shared.ActorType
import gov.nist.toolkit.actortransaction.client.ParamType
import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.configDatatypes.server.SimulatorProperties
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.simcommon.client.Simulator
import gov.nist.toolkit.simcommon.client.SimulatorConfig
import gov.nist.toolkit.simcommon.server.AbstractActorFactory
import gov.nist.toolkit.simcommon.server.SimManager
import gov.nist.toolkit.sitemanagement.client.Site
import gov.nist.toolkit.sitemanagement.client.TransactionBean
import gov.nist.toolkit.xdsexception.NoSimulatorException
import groovy.transform.TypeChecked

@TypeChecked
class FixedReplyFactory extends AbstractActorFactory {

    static final List<TransactionType> incomingTransactions =
        Arrays.asList(TransactionType.PROVIDE_AND_REGISTER,
        TransactionType.REGISTER,
        TransactionType.RETRIEVE,
        TransactionType.XC_RETRIEVE,
        TransactionType.XC_QUERY)


    @Override
    protected Simulator buildNew(SimManager simm, SimId simId, boolean configureBase) throws Exception {
        ActorType actorType = ActorType.FIXED_REPLY;
        SimulatorConfig sc;
        sc = configureBaseElements(actorType, simId, simId.getTestSession())
        addFixedEndpoint(sc, SimulatorProperties.pnrEndpoint,       actorType, TransactionType.PROVIDE_AND_REGISTER,     false);
        addFixedEndpoint(sc, SimulatorProperties.pnrTlsEndpoint,       actorType, TransactionType.PROVIDE_AND_REGISTER,     true);

        addFixedEndpoint(sc, SimulatorProperties.registerEndpoint,       actorType, TransactionType.REGISTER,     false);
        addFixedEndpoint(sc, SimulatorProperties.registerTlsEndpoint,       actorType, TransactionType.REGISTER,     true);

        addFixedEndpoint(sc, SimulatorProperties.retrieveEndpoint,       actorType, TransactionType.RETRIEVE,     false);
        addFixedEndpoint(sc, SimulatorProperties.retrieveTlsEndpoint,       actorType, TransactionType.RETRIEVE,     true);

        addFixedEndpoint(sc, SimulatorProperties.xcqEndpoint,       actorType, TransactionType.XC_QUERY,     false);
        addFixedEndpoint(sc, SimulatorProperties.xcqTlsEndpoint,       actorType, TransactionType.XC_QUERY,     true);

        addFixedEndpoint(sc, SimulatorProperties.xcrEndpoint,       actorType, TransactionType.XC_RETRIEVE,     false);
        addFixedEndpoint(sc, SimulatorProperties.xcrTlsEndpoint,       actorType, TransactionType.XC_RETRIEVE,     true);

        addEditableConfig(sc, SimulatorProperties.replyFile, ParamType.TEXT, "");
        addEditableConfig(sc, SimulatorProperties.mimeType, ParamType.TEXT, "text/plain");
        addEditableConfig(sc, SimulatorProperties.mtomResponse, ParamType.BOOLEAN, false);

        return new Simulator(sc);
    }

    @Override
    protected void verifyActorConfigurationOptions(SimulatorConfig config) {

    }

    @Override
    Site buildActorSite(SimulatorConfig asc, Site site) throws NoSimulatorException {
        String siteName = asc.getDefaultName();

        if (site == null)
            site = new Site(siteName, asc.getId().getTestSession());

        site.setTestSession(asc.getId().getTestSession());  // labels this site as coming from a sim
//        site.user = asc.getId().user;  // labels this site as coming from a sim

        site.addTransaction(new TransactionBean(
                TransactionType.PROVIDE_AND_REGISTER.getCode(),
                TransactionBean.RepositoryType.NONE,
                asc.get(SimulatorProperties.pnrEndpoint).asString(),
                false,
                false))
        site.addTransaction(new TransactionBean(
                TransactionType.PROVIDE_AND_REGISTER.getCode(),
                TransactionBean.RepositoryType.NONE,
                asc.get(SimulatorProperties.pnrTlsEndpoint).asString(),
                true,
                false))
        site.addTransaction(new TransactionBean(
                TransactionType.REGISTER.getCode(),
                TransactionBean.RepositoryType.NONE,
                asc.get(SimulatorProperties.registerEndpoint).asString(),
                false,
                false))
        site.addTransaction(new TransactionBean(
                TransactionType.REGISTER.getCode(),
                TransactionBean.RepositoryType.NONE,
                asc.get(SimulatorProperties.registerTlsEndpoint).asString(),
                true,
                false))
        site.addTransaction(new TransactionBean(
                TransactionType.RETRIEVE.getCode(),
                TransactionBean.RepositoryType.NONE,
                asc.get(SimulatorProperties.retrieveEndpoint).asString(),
                false,
                false))
        site.addTransaction(new TransactionBean(
                TransactionType.RETRIEVE.getCode(),
                TransactionBean.RepositoryType.NONE,
                asc.get(SimulatorProperties.retrieveTlsEndpoint).asString(),
                true,
                false))
        site.addTransaction(new TransactionBean(
                TransactionType.XC_QUERY.getCode(),
                TransactionBean.RepositoryType.NONE,
                asc.get(SimulatorProperties.xcqEndpoint).asString(),
                false,
                false))
        site.addTransaction(new TransactionBean(
                TransactionType.XC_QUERY.getCode(),
                TransactionBean.RepositoryType.NONE,
                asc.get(SimulatorProperties.xcqTlsEndpoint).asString(),
                true,
                false))
        site.addTransaction(new TransactionBean(
                TransactionType.XC_RETRIEVE.getCode(),
                TransactionBean.RepositoryType.NONE,
                asc.get(SimulatorProperties.xcrEndpoint).asString(),
                false,
                false))
        site.addTransaction(new TransactionBean(
                TransactionType.XC_RETRIEVE.getCode(),
                TransactionBean.RepositoryType.NONE,
                asc.get(SimulatorProperties.xcrTlsEndpoint).asString(),
                true,
                false)
        )

        return site;
    }

    @Override
    List<TransactionType> getIncomingTransactions() {
        return null;
    }
}
