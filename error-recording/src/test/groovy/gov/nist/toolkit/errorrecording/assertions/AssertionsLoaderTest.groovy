package gov.nist.toolkit.errorrecording.assertions

import gov.nist.toolkit.errorrecording.client.assertions.AssertionLoader
import spock.lang.Specification

/**
 * Created by diane on 10/3/2016.
 */
class AssertionsLoaderTest extends Specification {

        def 'Load assertions'() {
            setup:
            // TODO need to define a test CSV file for the assertions
            AssertionLoader loader = new AssertionLoader()

            when: 'Load assertions from file into Map'
            Map<String, List<String>> assertionsMap = loader.loadAssertions()

            then: ''
            //ErrorRecorderUtil.errorRecorderChainAsList(erParent).size() == 4
        }
}
