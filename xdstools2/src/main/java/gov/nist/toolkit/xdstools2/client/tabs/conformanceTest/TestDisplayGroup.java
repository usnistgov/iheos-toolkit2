package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import gov.nist.toolkit.results.client.TestInstance;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class TestDisplayGroup {
    private Map<String, TestDisplay> testDisplays = new HashMap<>();

    public void put(TestInstance testInstance, TestDisplay testDisplay) {
        testDisplays.put(testInstance.getId(), testDisplay);
    }

    public TestDisplay get(TestInstance testInstance) { return testDisplays.get(testInstance.getId()); }

    public boolean containsKey(TestInstance testInstance) { return testDisplays.containsKey(testInstance.getId()); }

    public void clear() { testDisplays.clear(); }
}
