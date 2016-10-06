package gov.nist.toolkit.errorrecording.assertions

import gov.nist.toolkit.errorrecording.client.assertions.AssertionLoader
import spock.lang.Specification

/**
 * Created by diane on 10/3/2016.
 */
class AssertionsLoaderTest extends Specification {

        def 'Load assertions'() {
            setup:
            String testFile = "/assertions/Toolkit_assertions_TEST.csv"
            AssertionLoader loader = new AssertionLoader()

            when: 'Load assertions from file into Map'
            Map<String, List<String>> assertionsMap = loader.loadAssertions(testFile)

            then: ''
            assertionsMap.size() == 1
            // TODO this test needs more detail to test the contents of the map, once the final map object is implemented
        }
}
