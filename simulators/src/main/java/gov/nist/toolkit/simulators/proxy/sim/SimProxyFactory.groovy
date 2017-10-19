package gov.nist.toolkit.simulators.proxy.sim

import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.actortransaction.client.ParamType
import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.configDatatypes.server.SimulatorProperties
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.simcommon.client.Simulator
import gov.nist.toolkit.simcommon.client.SimulatorConfig
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement
import gov.nist.toolkit.simcommon.server.AbstractActorFactory
import gov.nist.toolkit.simcommon.server.IActorFactory
import gov.nist.toolkit.simcommon.server.SimManager
import gov.nist.toolkit.sitemanagement.client.Site
import gov.nist.toolkit.sitemanagement.client.TransactionBean
import gov.nist.toolkit.xdsexception.NoSimulatorException
/**
 * A singleton factory for creating Sim Proxy
 *
 * This actually creates a pair of simulators.  First, using the SimId provided,
 * is the sim that receives the input message.  The second simulator sends the
 * message on to the configured endpoint.
 *
 * That is the theory, it is not coded that way.  The first simulator does all the work
 * and has all the configuration information.  The second simulator is just the container
 * for the logs holding the messages to the configured endpoint.
 */
class SimProxyFactory extends AbstractActorFactory implements IActorFactory{
    static final List<TransactionType> incomingTransactions = TransactionType.asList()  // accepts all known transactions

//    @Override
//    ActorType getActorType() {
//        return ActorType.SIM_PROXY
//    }

    SimProxyFactory() {}

    @Override
    protected Simulator buildNew(SimManager simm, SimId simId, boolean configureBase) throws Exception {
        ActorType actorType = getActorType(); //ActorType.SIM_PROXY
        SimulatorConfig config
        if (configureBase)
            config = configureBaseElements(actorType, simId)
        else
            config = new SimulatorConfig()


//        addEditableNullEndpoint(config, SimulatorProperties.proxyForwardEndpoint, ActorType.ANY , TransactionType.ANY, false);
//        addEditableNullEndpoint(config, SimulatorProperties.proxyTlsForwardEndpoint, ActorType.ANY, TransactionType.ANY, true);

        SimId simId2 = new SimId(simId.user, simId.id + '_be')   // 'be' for back end
        SimulatorConfig config2
        if (configureBase)
            config2 = configureBaseElements(actorType, simId2)
        else
            config2 = new SimulatorConfig()

        addFixedConfig(config, SimulatorProperties.isProxyFrontEnd, ParamType.BOOLEAN, true)
        addFixedConfig(config2, SimulatorProperties.isProxyFrontEnd, ParamType.BOOLEAN, false)

        actorType.transactions.each { TransactionType transactionType ->
            if (transactionType.isFhir()) {
                if (transactionType.endpointSimPropertyName)
                    addFixedFhirEndpoint(config, transactionType.endpointSimPropertyName, actorType, transactionType, false, true)
//                addFixedFhirEndpoint(config, transactionType.tlsEndpointSimPropertyName, actorType, transactionType, true)
            } else {
                if (transactionType.endpointSimPropertyName) {
                    addFixedEndpoint(config, transactionType.endpointSimPropertyName, actorType, transactionType, false)
                    addFixedEndpoint(config, transactionType.tlsEndpointSimPropertyName, actorType, transactionType, true)
                }
            }
        }

        // link the two sims making up the front end and the back end of the simproxy
        addFixedConfig(config, SimulatorProperties.proxyPartner, ParamType.SELECTION, simId2.toString())
        addFixedConfig(config2, SimulatorProperties.proxyPartner, ParamType.SELECTION, simId.toString())
        addEditableConfig(config, SimulatorProperties.simProxyRequestTransformations, ParamType.LIST, actorType.proxyTransformClassNames)
        addEditableConfig(config, SimulatorProperties.simProxyResponseTransformations, ParamType.LIST, actorType.proxyResponseTransformClassNames)

        List<SimulatorConfig> configs = buildExtensions(simm, config, config2)
//        addEditableConfig(config, SimulatorProperties.proxyForwardSite, ParamType.SELECTION, "");

        isSimProxy = true;

        return new Simulator(configs)
    }

    // This is separate so it can be overriden by an extension class
    List<SimulatorConfig> buildExtensions(SimManager simm, SimulatorConfig config, SimulatorConfig config2) {
        addEditableConfig(config, SimulatorProperties.proxyForwardSite, ParamType.SELECTION, "");
    }

    @Override
    protected void verifyActorConfigurationOptions(SimulatorConfig config) {

    }

    // this only works because this is a singleton class
    private boolean locked = false;

    /**
     * Build Site with ALL transactions defined so we don't have to know
     * what it will be used for
     * @param asc
     * @param site
     * @return
     * @throws NoSimulatorException
     */
    @Override
    Site getActorSite(SimulatorConfig asc, Site site) throws NoSimulatorException {
        Site aSite = (site) ? site : new Site(asc.defaultName)

        if (!asc.get(SimulatorProperties.isProxyFrontEnd).asBoolean())
            return aSite  // back end gets no transactions

        boolean isAsync = false

        ActorType actorType = ActorType.findActor(asc.actorType)
        actorType.transactions.each { TransactionType transactionType ->
            asc.elements.findAll { SimulatorConfigElement sce ->
                sce.type == ParamType.ENDPOINT && sce.transType == transactionType
            }.each { SimulatorConfigElement sce ->
                aSite.addTransaction(new TransactionBean(
                        transactionType.getCode(),
                        TransactionBean.RepositoryType.NONE,
                        sce.asString(),   // endpoint
                        false,
                        isAsync
                ))
            }
//            aSite.addTransaction(new TransactionBean(
//                    transactionType.getCode(),
//                    TransactionBean.RepositoryType.NONE,
//                    asc.get(transactionType.tlsEndpointSimPropertyName).asString(),
//                    true,
//                    isAsync
//            ))
        }

        return aSite

//        if (locked) return aSite;
//        locked = true
//        Set<ActorType> types = ActorType.getAllActorTypes()
//        for (ActorType actorType : types) {
//            if (actorType.equals(ActorType.SIM_PROXY)) continue;
//            int transactionCount = aSite.transactions().size()
//            AbstractActorFactory af = new GenericSimulatorFactory().getActorFactory(actorType)
//            if (af) {
//                af.asSimProxy()
//                af.setTransactionOnly(true)  // don't include PIF - we will run out of ports - not needed for SimProxy
//                Simulator sim = af.buildNew(new SimManager(EnvSetting.DEFAULTSESSIONID), asc.getId(), true)
////                AbstractActorFactory factory  = af.getActorFactory(actorType).asSimProxy()
//                SimulatorConfig config = sim.getConfig(0)
//                aSite = af.getActorSite(config, (aSite) ? aSite : site)
//                assert aSite.transactions().size() >= transactionCount, "ActorFactory ${af.getClass().getName()} does not maintain list of Transactions correctly"
//            }
//        }
//        SiteEndpointFactory.updateNonTlsTransactionsToPort(aSite, Installation.instance().propertyServiceManager().getProxyPort());
//        locked = false
//        return aSite
    }

    @Override
    List<TransactionType> getIncomingTransactions() {
        return incomingTransactions
    }
}
