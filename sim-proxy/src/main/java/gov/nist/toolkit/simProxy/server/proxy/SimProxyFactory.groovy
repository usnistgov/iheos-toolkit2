package gov.nist.toolkit.simProxy.server.proxy

import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.actortransaction.client.ParamType
import gov.nist.toolkit.configDatatypes.server.SimulatorProperties
import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.envSetting.EnvSetting
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.simcommon.client.Simulator
import gov.nist.toolkit.simcommon.client.SimulatorConfig
import gov.nist.toolkit.simcommon.server.AbstractActorFactory
import gov.nist.toolkit.simcommon.server.GenericSimulatorFactory
import gov.nist.toolkit.simcommon.server.IActorFactory
import gov.nist.toolkit.simcommon.server.SimManager
import gov.nist.toolkit.sitemanagement.client.Site
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

    @Override
    ActorType getActorType() {
        return ActorType.SIM_PROXY
    }

    @Override
    protected Simulator buildNew(SimManager simm, SimId simId, boolean configureBase) throws Exception {
        ActorType actorType = ActorType.SIM_PROXY
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

        // link the two sims
        addFixedConfig(config, SimulatorProperties.proxyPartner, ParamType.SELECTION, simId2.toString())
        addFixedConfig(config2, SimulatorProperties.proxyPartner, ParamType.SELECTION, simId.toString())

        addEditableConfig(config, SimulatorProperties.proxyForwardSite, ParamType.SELECTION, "");

        return new Simulator([config, config2])
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
        if (locked) return aSite;
        locked = true
        Set<ActorType> types = ActorType.getAllActorTypes()
        for (ActorType actorType : types) {
            if (actorType.equals(ActorType.SIM_PROXY)) continue;
            int transactionCount = aSite.transactions().size()
            AbstractActorFactory af = new GenericSimulatorFactory().getActorFactory(actorType)
            if (af) {
                af.setTransactionOnly(true)  // don't include PIF - we will run out of ports - not needed for SimProxy
                Simulator sim = af.buildNew(new SimManager(EnvSetting.DEFAULTSESSIONID), asc.getId(), true)
                aSite = af.getActorFactory(actorType).getActorSite(sim.getConfig(0), (aSite) ? aSite : site)
                assert aSite.transactions().size() >= transactionCount, "ActorFactory ${af.getClass().getName()} does not maintain list of Transactions correctly"
            }
        }
        locked = false
        return aSite
    }

    @Override
    List<TransactionType> getIncomingTransactions() {
        return incomingTransactions
    }
}
