package gov.nist.toolkit.webUITests


import gov.nist.toolkit.configDatatypes.server.SimulatorProperties
import spock.lang.Stepwise
import spock.lang.Timeout
/**
 * Created by skb1 on 6/5/2017.
 */
@Stepwise
@Timeout(612) // Keep this to accommodate slow computers (Sunil's Windows 10 laptop).
/**
 * This OptionSpec class should only be queued to run after the Registry Required Spec tests since it will use the existing Orchestration and Simulator state.
 */
class RegistryActorA36XuaOptionSpec extends RegistryConformanceActorOption {

    @Override
    void setRunButton() {
        runButton = RunButtonEnum.RUN_ALL_BUTTON
    }

    @Override
    void setOptions() {
        simConfig = getSpi().get(getSimId())
        simConfig.setProperty(SimulatorProperties.UPDATE_METADATA_OPTION, false)
        simConfig.setProperty(SimulatorProperties.RESTRICTED_UPDATE_METADATA_OPTION, false)
        simConfig.setProperty(SimulatorProperties.REMOVE_METADATA, false)
        simConfig.setProperty(SimulatorProperties.REMOVE_DOCUMENTS, false)
        simConfig.setProperty(SimulatorProperties.requiresStsSaml, true)
        updateSimConfig(simConfig)
    }

    @Override
    void setActorPageUrl() {
        setActorPage(String.format(
                "%s/#ConfActor:env=default;testSession=%s;"
                        + "actor=reg;profile=xds;option=xua;systemId=%s",
                toolkitBaseUrl,
                testSessionName,
                simName))


        knownTestFailures = 3
        /*
        3 known test failures:
        11897_unknownaudience
        11990_unknownaudience
        11990eval_valid
         */
    }


}