package gov.nist.toolkit.actorfactory

import gov.nist.toolkit.simcommon.server.InitEC
import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.installation.server.Installation
import gov.nist.toolkit.installation.shared.TestSession
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.simcommon.server.SimDb
import gov.nist.toolkit.utilities.io.Io
import spock.lang.Shared
import spock.lang.Specification
/**
 *
 */
class SimDbTest extends Specification {
    @Shared TestSession testSession = new TestSession('bill')
    def setup() {
        InitEC.init()
    }

    def 'Empty simdb has no simIds'() {
        when:
        new SimDb().deleteAllSims(testSession)

        then:
        new SimDb().getAllSimIds(testSession).isEmpty()
    }

    def 'Single sim found'() {
        when:
        new SimDb().deleteAllSims(testSession)
        new SimDb().mkSim(new SimId(testSession, 'foo'), ActorType.REPOSITORY.name)

        then:
        new SimDb().getAllSimIds(testSession).size() == 1
    }

    def 'Extra directory skipped'() {
        when:
        new SimDb().deleteAllSims(testSession)
        new SimDb().mkSim(new SimId(testSession, 'foo'), ActorType.REPOSITORY.name)

        and:
        File sdb = Installation.instance().simDbFile(testSession)
        File file = new File(sdb, 'foo')
        file.mkdir()

        then:
        new SimDb().getAllSimIds(testSession).size() == 1
    }

    def 'Extra file skipped'() {
        when:
        new SimDb().deleteAllSims(testSession)
        new SimDb().mkSim(new SimId(testSession, 'foo'), ActorType.REPOSITORY.name)

        and:
        File sdb = Installation.instance().simDbFile(testSession)
        File file = new File(sdb, 'a_file.txt')
        Io.stringToFile(file, "hello")
        println file.toString()

        then:
        file.exists()
        new SimDb().getAllSimIds(testSession).size() == 1
    }

    def pause() {
        sleep(5)
    }
}
