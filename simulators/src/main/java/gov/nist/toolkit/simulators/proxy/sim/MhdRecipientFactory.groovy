package gov.nist.toolkit.simulators.proxy.sim

import gov.nist.toolkit.actortransaction.client.ParamType
import gov.nist.toolkit.configDatatypes.server.SimulatorProperties
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.simcommon.client.Simulator
import gov.nist.toolkit.simcommon.client.SimulatorConfig
import gov.nist.toolkit.simcommon.server.SimManager
import gov.nist.toolkit.simcommon.server.factories.RepositoryRegistryActorFactory
import gov.nist.toolkit.sitemanagement.client.Site

class MhdRecipientFactory extends SimProxyFactory {

    MhdRecipientFactory() {
        super()
    }

    @Override
    buildExtensions(SimManager simm, SimulatorConfig config, SimulatorConfig config2) {
        SimId baseSimId = config.id
        String recSimIdName = baseSimId.toString() + '_regrep'
        SimId recSimId = new SimId(recSimIdName)

        Simulator regrep = new RepositoryRegistryActorFactory().buildNew(simm, recSimId, true)

        addEditableConfig(config, SimulatorProperties.proxyForwardSite, ParamType.SELECTION, recSimIdName);

        // add MHD -> XDS transforms

        // add response XDS -> MHD transforms

    }

    Site getActorSite(SimulatorConfig sc, Site site) {
        site = new RepositoryRegistryActorFactory().getActorSite(sc, site)
        site = new SimProxyFactory().getActorSite(sc, site)
        return site;
    }
}
