package gov.nist.toolkit.testkitutilities

import gov.nist.toolkit.installation.server.ExternalCacheManager
import gov.nist.toolkit.installation.server.Installation
import gov.nist.toolkit.installation.shared.TestCollectionCode
import gov.nist.toolkit.installation.shared.TestSession
import gov.nist.toolkit.xdsexception.client.ToolkitRuntimeException
import spock.lang.Specification

import java.nio.file.Path
import java.nio.file.Paths

/**
 *
 */
class SearchPathTest extends Specification {
    String environment = 'default'
    TestSession testSession = new TestSession('default')

    def setup() {
//        org.apache.log4j.BasicConfigurator.configure()
        Path externalCacheMarker = Paths.get(getClass().getResource('/').toURI()).resolve('external_cache/external_cache.txt')
        if (externalCacheMarker == null) {
            throw new ToolkitRuntimeException("Cannot locate external cache for test environment")
        }
        File externalCache = externalCacheMarker.toFile().parentFile

        // Important to set this before war home since it is overriding contents of toolkit.properties
        if (!externalCache || !externalCache.isDirectory())throw new ToolkitRuntimeException('External Cache not found')
        ExternalCacheManager.reinitialize(externalCache)
    }

    def 'find tests'() {
        when:
        TestKitSearchPath searchPath = new TestKitSearchPath(environment, testSession)

        then: 'should find default and internal'
        searchPath.testkits.size() == 2

        when:
        TestKit testKit = searchPath.getTestKitForTest('42')

        then:
        testKit

        when: 'default contains 42'
        TestDefinition t42 = testKit.getTestDef('42')

        then:
        t42

        when: 'internal contains 11990'
        testKit = searchPath.getTestKitForTest('11990')
        TestDefinition t11990 = testKit.getTestDef('11990')

        then:
        t11990
    }

    def 'get actor collection members'() {
        when:
        TestKitSearchPath searchPath = new TestKitSearchPath(environment, testSession)
        Collection<String> members = searchPath.getCollectionMembers(Installation.actorCollectionsDirName, new TestCollectionCode('reg'))
        List<String> tests = new ArrayList<>(members)

        then:
        tests.size() == 2
        tests.contains('42')
        tests.contains('11990')
    }

    def 'get members of test data set'() {
        when:
        TestKitSearchPath searchPath = new TestKitSearchPath(environment, testSession)
        Collection<String> items = searchPath.getTestdataSetListing('testdata-repository')
        List<String> list = new ArrayList<>(items)

        then:
        list.size() == 3
        list.contains('DoubleDocument')
        list.contains('DocumentInFolder')
        list.contains('SingleDocument')

    }

}
