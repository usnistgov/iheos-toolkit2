package gov.nist.toolkit.simProxy.server.proxy

import gov.nist.toolkit.actortransaction.client.ActorType
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
 *
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


        return new Simulator(simulatorConfig)
    }

    @Override
    protected void verifyActorConfigurationOptions(SimulatorConfig config) {

    }

    @Override
    Site getActorSite(SimulatorConfig asc, Site site) throws NoSimulatorException {
        Site aSite = null
        for (ActorType actorType : ActorType.getAllActorTypes()) {
            if (actorType.equals(ActorType.SIM_PROXY)) continue;
            AbstractActorFactory af = new GenericSimulatorFactory().getActorFactory(actorType)
            if (af) {
                af.setTransactionOnly(true)
                Simulator sim = af.buildNew(new SimManager(EnvSetting.DEFAULTSESSIONID), asc.getId(), true)
                aSite = af.getActorFactory(actorType).getActorSite(sim.getConfig(0), (aSite) ? aSite : site)
            }
        }
        return aSite
    }

    @Override
    List<TransactionType> getIncomingTransactions() {
        return incomingTransactions
    }
}
