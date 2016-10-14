package gov.nist.toolkit.errorrecording.assertions

import gov.nist.toolkit.errorrecording.client.assertions.AssertionLibrary
import spock.lang.Specification

/**
 * Created by diane on 10/3/2016.
 */
class AssertionsLoaderTest extends Specification {

        def 'Load assertions'() {
            setup:
            String testFile = "/assertions/Toolkit_assertions_TEST.csv"
            AssertionLibrary ASSERTIONLIBRARY = AssertionLibrary.getInstance();

            when: 'Load assertions from file into Assertion Library'
            ASSERTIONLIBRARY.loadAssertions(testFile)

            then: 'Check size of Assertion Library, check contents'
            ASSERTIONLIBRARY.size() == 1
            // TODO this test needs more detail to test the contents of the list
        }
}
