package gov.nist.toolkit.simulators.sim.rig

import gov.nist.toolkit.actorfactory.SimDb
import gov.nist.toolkit.actorfactory.client.SimulatorConfig
import gov.nist.toolkit.actortransaction.TransactionErrorCodeDbLoader
import gov.nist.toolkit.actortransaction.client.TransactionErrorCodesDb
import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.simulators.support.DsSimCommon
import gov.nist.toolkit.simulators.support.SimCommon
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine
import spock.lang.Specification
import spock.lang.Subject

/**
 * Created by davidmaffitt on 2/27/17.
 */
class RigActorSimulatorTest extends Specification {

    def 'RigActorSimulator handles RAD-75 request'() {
        given: "a RigActorSimulator and collaborators SimCommon, DsSimCommon, SimDb, and SimulatorConfig"
        SimCommon common = Mock(SimCommon)
        DsSimCommon dsSimCommon = Mock(DsSimCommon)
        SimDb db = Mock( SimDb)
        SimulatorConfig simulatorConfig = Mock( SimulatorConfig)
        MessageValidatorEngine mvc
        String validation

        @Subject
        RigActorSimulator rigActorSimulator = new RigActorSimulator( common, dsSimCommon, db, simulatorConfig)

        when: 'RigActorSimulator runs RAD-75 message'
        rigActorSimulator.run( TransactionType.XC_RET_IMG_DOC_SET, mvc, validation)

        then:
        rigActorSimulator != null
        // something cool.
    }
}
