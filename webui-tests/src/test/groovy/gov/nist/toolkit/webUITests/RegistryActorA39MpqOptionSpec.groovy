package gov.nist.toolkit.webUITests


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
class RegistryActorA33MpqOptionSpec extends RegistryActorA1SimulatorSpec { // extends RegistryConformanceActorOption {

    @Override
    void setupSim() {
        simName = "reg4mpqtests"

        setActorPage(String.format(
                "%s/#ConfActor:env=default;testSession=%s;"
                        + "actor=reg;profile=xds;option=mpq;systemId=%s",
                toolkitBaseUrl,
                testSessionName,
                simName))

        deleteOldRegSim()
        sleep(5000) // Why we need this -- Problem here is that the Delete request via REST could be still running before we execute the next Create REST command. The PIF Port release timing will be off causing a connection refused error.
        regRepSim = createNewRegSim()
    }

    ///////////
    /*

    @Override
    void setRunButton() {
        runButton = RunButtonEnum.RUN_ALL_BUTTON
    }

    @Override
    void setOptions() {
        // Create New regmqp sim
        // Set sim in conformance tool
        // Initialize conformance orchestration
        // Run All
        simConfig = getSpi().get(getSimId())
        simConfig.setProperty(SimulatorProperties.UPDATE_METADATA_OPTION, false)
        simConfig.setProperty(SimulatorProperties.RESTRICTED_UPDATE_METADATA_OPTION, false)
        simConfig.setProperty(SimulatorProperties.REMOVE_METADATA, false)
        simConfig.setProperty(SimulatorProperties.REMOVE_DOCUMENTS, false)
        simConfig.setProperty(SimulatorProperties.requiresStsSaml, false)
        updateSimConfig(simConfig)
    }

    @Override
    void setActorPageUrl() {
        setActorPage(String.format(
                "%s/#ConfActor:env=default;testSession=%s;"
                        + "actor=reg;profile=xds;option=mpq;systemId=%s",
                toolkitBaseUrl,
                testSessionName,
                simName))

    }
*/

}