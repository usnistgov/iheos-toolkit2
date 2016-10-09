package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.session.client.logtypes.TestOverviewDTO;

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
    private boolean allowRun = true;
    private boolean allowDelete = true;

    public TestDisplayGroup(TestContext testContext, TestContextDisplay testContextDisplay, TestRunner testRunner) {
        this.testContext = testContext;
        this.testContextDisplay = testContextDisplay;
        this.testRunner = testRunner;
    }

    public TestDisplay display(TestOverviewDTO testOverview) {
        TestDisplay testDisplay = get(testOverview.getTestInstance());
        if (testDisplay == null) {
            testDisplay = new TestDisplay(testOverview.getTestInstance(), this, testRunner, testContext, testContextDisplay);
            testDisplay.setAllowDelete(allowDelete);
            testDisplay.setAllowRun(allowRun);
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

    public void setAllowRun(boolean allowRun) {
        this.allowRun = allowRun;
    }

    public void setAllowDelete(boolean allowDelete) {
        this.allowDelete = allowDelete;
    }
}
