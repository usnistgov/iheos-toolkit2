package gov.nist.toolkit.testkitutilities

import gov.nist.toolkit.results.client.TestInstance
import spock.lang.Specification

/**
 *
 */
class TestCollectionBuilderTest extends Specification {
    def envName = 'default'
    def testSession = 'default'

    def 'all tests for collection'() {
        when:
        TestCollection testCollection = TestCollectionBuilder.allTests(envName, testSession, new TestCollectionId(TestCollectionType.ACTOR_COLLECTION, 'reg'))
        Set expected = ['42', '11990']
        List<TestInstance> results = testCollection.tests
        Set results1 = results.collect { ti -> ti.id }

        then:
        expected == results1
    }

    def 'all tests for actor collection'() {

    }

    def 'include statuses in actor collection'() {

    }
}
