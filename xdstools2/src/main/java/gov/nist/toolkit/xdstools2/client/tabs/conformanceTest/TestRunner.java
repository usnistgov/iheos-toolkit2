package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;

import java.util.Map;

/**
 *
 */
public interface TestRunner {
    void runTest(TestInstance testInstance, Map<String, String> sectionParms, TestIterator testIterator);
    void removeTestDetails(TestInstance testInstance);
//    void displayTest(FlowPanel testsPanel, TestDisplayGroup testDisplayGroup, TestOverviewDTO testOverview);
    SiteSpec getSiteToIssueTestAgainst();
    ActorOption getCurrentActorOption();
}
