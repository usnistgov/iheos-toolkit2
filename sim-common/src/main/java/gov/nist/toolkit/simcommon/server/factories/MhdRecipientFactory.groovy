package gov.nist.toolkit.simcommon.server.factories

import gov.nist.toolkit.actortransaction.client.ParamType
import gov.nist.toolkit.configDatatypes.server.SimulatorProperties
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.simcommon.client.Simulator
import gov.nist.toolkit.simcommon.client.SimulatorConfig
import gov.nist.toolkit.simcommon.server.SimManager

class MhdRecipientFactory extends SimProxyFactory {

    MhdRecipientFactory() {
        super()
    }

    @Override
    List<SimulatorConfig> buildExtensions(SimManager simm, SimulatorConfig config, SimulatorConfig config2) {
        SimId baseSimId = config.id
        String recSimIdName = baseSimId.toString() + '_regrep'
        SimId recSimId = new SimId(recSimIdName)

        Simulator regrep = new RepositoryRegistryActorFactory().buildNew(simm, recSimId, true)

        addEditableConfig(config, SimulatorProperties.proxyForwardSite, ParamType.SELECTION, recSimIdName);

        return [config, config2, regrep.getConfig(0)]

    }

}
