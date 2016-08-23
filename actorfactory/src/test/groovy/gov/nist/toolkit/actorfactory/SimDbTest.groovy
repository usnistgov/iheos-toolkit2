package gov.nist.toolkit.actorfactory

import gov.nist.toolkit.actorfactory.client.SimId
import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.installation.ExternalCacheManager
import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.utilities.io.Io
import gov.nist.toolkit.xdsexception.client.ToolkitRuntimeException
import spock.lang.Specification

/**
 * Created by bill on 11/2/15.
 */
class SimDbTest extends Specification {

    def setup() {
        org.apache.log4j.BasicConfigurator.configure()
        URL externalCacheMarker = getClass().getResource('/external_cache/external_cache.txt')
        if (externalCacheMarker == null) {
            throw new ToolkitRuntimeException("Cannot locate external cache for test environment")
        }
        File externalCache = new File(externalCacheMarker.toURI().path).parentFile

        // Important to set this before war home since it is overriding contents of toolkit.properties
        if (!externalCache || !externalCache.isDirectory())throw new ToolkitRuntimeException('External Cache not found')
        ExternalCacheManager.reinitialize(externalCache)
    }

    def 'Empty simdb has no simIds'() {
        when:
        SimDb db = new SimDb()
        SimDb.deleteAllSims()

        then:
        db.getAllSimIds().isEmpty()
    }

    def 'Single sim found'() {
        when:
        SimDb db = new SimDb()
        SimDb.deleteAllSims()
        SimDb.mkSim(new SimId('bill', 'foo'), ActorType.REPOSITORY.name)

        then:
        db.getAllSimIds().size() == 1
    }

    def 'Extra directory skipped'() {
        when:
        SimDb db = new SimDb()
        SimDb.deleteAllSims()
        SimDb.mkSim(new SimId('bill', 'foo'), ActorType.REPOSITORY.name)

        and:
        File sdb = Installation.installation().simDbFile()
        File file = new File(sdb, 'foo')
        file.mkdir()

        then:
        db.getAllSimIds().size() == 1
    }

    def 'Extra file skipped'() {
        when:
        SimDb db = new SimDb()
        SimDb.deleteAllSims()
        SimDb.mkSim(new SimId('bill', 'foo'), ActorType.REPOSITORY.name)

        and:
        File sdb = Installation.installation().simDbFile()
        File file = new File(sdb, 'a_file.txt')
        Io.stringToFile(file, "hello")
        println file.toString()

        then:
        file.exists()
        db.getAllSimIds().size() == 1
    }
}
