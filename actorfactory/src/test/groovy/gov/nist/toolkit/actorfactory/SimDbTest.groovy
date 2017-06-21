package gov.nist.toolkit.actorfactory

import gov.nist.toolkit.actorfactory.client.SimId
import gov.nist.toolkit.actorfactory.testSupport.InitEC
import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.utilities.io.Io
import spock.lang.Specification
/**
 *
 */
class SimDbTest extends Specification {

    def setup() {
        InitEC.init()
    }

    def 'Empty simdb has no simIds'() {
        when:
        new SimDb().deleteAllSims()

        then:
        new SimDb().getAllSimIds().isEmpty()
    }

    def 'Single sim found'() {
        when:
        new SimDb().deleteAllSims()
        new SimDb().mkSim(new SimId('bill', 'foo'), ActorType.REPOSITORY.name)

        then:
        new SimDb().getAllSimIds().size() == 1
    }

    def 'Extra directory skipped'() {
        when:
        new SimDb().deleteAllSims()
        new SimDb().mkSim(new SimId('bill', 'foo'), ActorType.REPOSITORY.name)

        and:
        File sdb = Installation.instance().simDbFile()
        File file = new File(sdb, 'foo')
        file.mkdir()

        then:
        new SimDb().getAllSimIds().size() == 1
    }

    def 'Extra file skipped'() {
        when:
        new SimDb().deleteAllSims()
        new SimDb().mkSim(new SimId('bill', 'foo'), ActorType.REPOSITORY.name)

        and:
        File sdb = Installation.instance().simDbFile()
        File file = new File(sdb, 'a_file.txt')
        Io.stringToFile(file, "hello")
        println file.toString()

        then:
        file.exists()
        new SimDb().getAllSimIds().size() == 1
    }
}
