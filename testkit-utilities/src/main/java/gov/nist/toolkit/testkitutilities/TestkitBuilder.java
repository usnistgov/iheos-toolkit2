package gov.nist.toolkit.testkitutilities;

import gov.nist.toolkit.installation.Installation;

import java.io.File;

/**
 *
 */
public class TestkitBuilder {
    static private TestKit testkit = null;

    static public TestKit getTestKit() {
        if (testkit == null)
            testkit = new TestKit(Installation.installation().testkitFile());
        return testkit;
    }

    static public File getTestDir(String testId) throws Exception {
        return getTestKit().getTestDir(testId);
    }
}
