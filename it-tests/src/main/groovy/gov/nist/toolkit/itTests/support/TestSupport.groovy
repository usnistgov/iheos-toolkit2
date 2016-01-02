package gov.nist.toolkit.itTests.support
import gov.nist.toolkit.services.server.UnitTestEnvironmentManager
/**
 *
 */

class TestSupport {

    /**
     * Initialized local toolkit and api for local toolkit
     * @return [ Session, ToolkitApi ]
     */
    public static INIT() {
        [
                UnitTestEnvironmentManager.setupLocalToolkit(),
                UnitTestEnvironmentManager.localToolkitApi()
        ]
    }
}
