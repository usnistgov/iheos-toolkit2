package gov.nist.toolkit.errorrecording.xml.assertions

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
            logger.debug("\n" + "Reset Assertions Library and load test assertions file...")
            ASSERTIONLIBRARY.loadAssertions(testFile)
            logger.debug(ASSERTIONLIBRARY.toString());

            then: 'Check size of Assertion Library, check contents'
            ASSERTIONLIBRARY.size() == 1
            Assertion testAssertion = ASSERTIONLIBRARY.getAssertion("TA001");
            testAssertion.errorMessage == "One or more required slots are missing in Minimal XDR"
            testAssertion.location == "ITI TF-3: Table 4.1-6"
            testAssertion.gazelleScheme == "gazelle scheme"
            testAssertion.gazelleAssertionID == "gazelle Assertion ID"
        }
}
