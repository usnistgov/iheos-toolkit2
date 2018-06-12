package gov.nist.toolkit.testengine.engine;

import gov.nist.toolkit.installation.shared.TestSession;

public interface ToolkitEnvironment {

    String getEnvironment();
    TestSession getTestSession();
}
