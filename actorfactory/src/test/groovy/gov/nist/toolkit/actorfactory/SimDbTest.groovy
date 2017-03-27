package gov.nist.toolkit.actorfactory

import gov.nist.toolkit.actorfactory.client.SimId
import gov.nist.toolkit.actorfactory.testSupport.InitEC
import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.utilities.io.Io
import spock.lang.Specification
/**
 * Created by bill on 11/2/15.
 */
class SimDbTest extends Specification {

    def setup() {
        InitEC.init()
    }

    def 'Empty simdb has no simIds'() {
        when:
        SimDb.deleteAllSims()

        then:
        SimDb.getAllSimIds().isEmpty()
    }

    def 'Single sim found'() {
        when:
        SimDb.deleteAllSims()
        SimDb.mkSim(new SimId('bill', 'foo'), ActorType.REPOSITORY.name)

        then:
        SimDb.getAllSimIds().size() == 1
    }

    def 'Extra directory skipped'() {
        when:
        SimDb.deleteAllSims()
        SimDb.mkSim(new SimId('bill', 'foo'), ActorType.REPOSITORY.name)

        and:
        File sdb = Installation.instance().simDbFile()
        File file = new File(sdb, 'foo')
        file.mkdir()

        then:
        SimDb.getAllSimIds().size() == 1
    }

    def 'Extra file skipped'() {
        when:
        SimDb.deleteAllSims()
        SimDb.mkSim(new SimId('bill', 'foo'), ActorType.REPOSITORY.name)

        and:
        File sdb = Installation.instance().simDbFile()
        File file = new File(sdb, 'a_file.txt')
        Io.stringToFile(file, "hello")
        println file.toString()

        then:
        file.exists()
        SimDb.getAllSimIds().size() == 1
    }
}
