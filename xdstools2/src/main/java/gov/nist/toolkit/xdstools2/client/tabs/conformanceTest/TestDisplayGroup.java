package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.session.client.logtypes.TestOverviewDTO;
import gov.nist.toolkit.testkitutilities.client.ConfTestPropertyName;

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
    private TestContextView testContextView;
    private boolean allowRun = true;
    private boolean allowValidate = false;
    private boolean allowDelete = true;
    private Controller controller;

    public TestDisplayGroup(TestContext testContext, TestContextView testContextView, TestRunner testRunner, Controller controller) {
        this.testContext = testContext;
        this.testContextView = testContextView;
        this.testRunner = testRunner;
        this.controller = controller;
    }

    public TestDisplay add(TestOverviewDTO testOverview) {
        TestDisplay testDisplay = get(testOverview.getTestInstance());
        if (testDisplay == null) {
            testDisplay = new TestDisplay(testOverview.getTestInstance(), testRunner, testContext, testContextView, controller);
            testDisplay.allowDelete(allowDelete);
            testDisplay.allowRun(allowRun);
            testDisplay.showValidate(allowValidate);
            // Override Run/Validation option by ConfTest.properties
            if (testOverview.getConfTestPropertyMap()!=null) {
               if (testOverview.getConfTestPropertyMap().containsKey(ConfTestPropertyName.RUN_ENTIRE_TEST)) {
                  String externalStartVal =  testOverview.getConfTestPropertyMap().get(ConfTestPropertyName.RUN_ENTIRE_TEST);
                  if ("false".equals(externalStartVal)) {
                     testDisplay.allowRun(false);
                     testDisplay.allowValidate(false);
                  }
               }
            }
            put(testOverview.getTestInstance(), testDisplay);
        }
        return testDisplay;
    }

    private void put(TestInstance testInstance, TestDisplay testDisplay) {
        testDisplays.put(testInstance.getId(), testDisplay);
    }

    private void remove(TestInstance testInstance) {
        testDisplays.remove(testInstance.getId());
    }

    public TestDisplay get(TestInstance testInstance) { return testDisplays.get(testInstance.getId()); }

    public boolean containsKey(TestInstance testInstance) { return testDisplays.containsKey(testInstance.getId()); }

    public void clear() { testDisplays.clear(); }

    public void allowRun(boolean allowRun) {
        this.allowRun = allowRun;
    }

    public void allowDelete(boolean allowDelete) {
        this.allowDelete = allowDelete;
    }

    public void allowValidate(boolean allowValidate) {
        this.allowValidate = allowValidate;
    }

}
