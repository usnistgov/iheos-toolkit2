package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import gov.nist.toolkit.results.client.TestInstance;

/**
 *
 */
public interface TestRunner {
    void runTest(TestInstance testInstance, TestIterator testIterator);
    void removeTestDetails(TestInstance testInstance);
//    void displayTest(FlowPanel testsPanel, TestDisplayGroup testDisplayGroup, TestOverviewDTO testOverview);
}
