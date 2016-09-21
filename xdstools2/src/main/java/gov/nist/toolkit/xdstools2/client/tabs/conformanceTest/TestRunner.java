package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import gov.nist.toolkit.results.client.TestInstance;

/**
 *
 */
public interface TestRunner {
    void runTest(final TestInstance testInstance, TestDone testDone);
}
