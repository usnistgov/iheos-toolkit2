package gov.nist.toolkit.webUITests

import gov.nist.toolkit.toolkitApi.DocumentRegRep
import gov.nist.toolkit.toolkitServicesCommon.SimConfig
import gov.nist.toolkit.toolkitServicesCommon.SimId
import gov.nist.toolkit.toolkitServicesCommon.ToolkitFactory
import spock.lang.Shared

abstract class RegistryConformanceActor extends ConformanceActor {


    public static final String simName = "reg" /* Sim names should be lowered cased */

    @Shared
    protected DocumentRegRep regRepSim

    @Shared
    protected SimConfig simConfig

    @Override
    String getSimIdAsString() {
        return ToolkitWebPage.testSessionName + "__" + simName
    }

    SimId getSimId() {
        return ToolkitFactory.newSimId(simName, ToolkitWebPage.testSessionName, null, null)
    }

    // Registry actor option specific
    void updateSimConfig(SimConfig simConfig) {
        getSpi().update(simConfig)
        sleep(5000) // Why we need this -- Problem here is that the request via REST could be still running before we execute the next command.
    }


}
