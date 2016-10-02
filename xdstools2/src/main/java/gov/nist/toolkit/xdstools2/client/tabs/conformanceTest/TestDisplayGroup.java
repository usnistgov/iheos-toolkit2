package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.session.client.TestOverviewDTO;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class TestDisplayGroup {
    // testId ==> TestDisplay
    private Map<String, TestDisplay> testDisplays = new HashMap<>();
    private TestRunner testRunner;
    private TestContext testContext;
    private TestContextDisplay testContextDisplay;

    public TestDisplayGroup(TestContext testContext, TestContextDisplay testContextDisplay, TestRunner testRunner) {
        this.testContext = testContext;
        this.testContextDisplay = testContextDisplay;
        this.testRunner = testRunner;
    }

    public TestDisplay display(TestOverviewDTO testOverview) {
        TestDisplay testDisplay = get(testOverview.getTestInstance());
        if (testDisplay == null) {
            testDisplay = new TestDisplay(testOverview.getTestInstance(), this, testRunner, testContext, testContextDisplay);
            put(testOverview.getTestInstance(), testDisplay);
        }
        testDisplay.display(testOverview);
        return testDisplay;
    }

    public void put(TestInstance testInstance, TestDisplay testDisplay) {
        testDisplays.put(testInstance.getId(), testDisplay);
    }

    public void remove(TestInstance testInstance) {
        testDisplays.remove(testInstance.getId());
    }

    public TestDisplay get(TestInstance testInstance) { return testDisplays.get(testInstance.getId()); }

    public boolean containsKey(TestInstance testInstance) { return testDisplays.containsKey(testInstance.getId()); }

    public void clear() { testDisplays.clear(); }
}
