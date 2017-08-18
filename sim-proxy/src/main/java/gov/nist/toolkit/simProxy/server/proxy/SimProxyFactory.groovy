package gov.nist.toolkit.simProxy.server.proxy

import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.actortransaction.client.ParamType
import gov.nist.toolkit.configDatatypes.SimulatorProperties
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
        SimulatorConfig simulatorConfig
        if (configureBase)
            simulatorConfig = configureBaseElements(actorType, simId)
        else
            simulatorConfig = new SimulatorConfig()

        addEditableNullEndpoint(simulatorConfig, SimulatorProperties.proxyForwardEndpoint, ActorType.ANY , TransactionType.ANY, false);
        addEditableNullEndpoint(simulatorConfig, SimulatorProperties.proxyTlsForwardEndpoint, ActorType.ANY, TransactionType.ANY, true);


        return new Simulator(simulatorConfig)
    }

    @Override
    protected void verifyActorConfigurationOptions(SimulatorConfig config) {

    }

    // this only works because this is a singleton class
    private boolean locked = false;

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
