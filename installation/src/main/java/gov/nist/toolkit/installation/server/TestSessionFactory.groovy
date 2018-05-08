package gov.nist.toolkit.installation.server

import gov.nist.toolkit.installation.shared.TestSession
import gov.nist.toolkit.utilities.id.UuidAllocator
import groovy.transform.TypeChecked

@TypeChecked
class TestSessionFactory {

    // This does not test for prior existance - better to use TestSessionServiceManager
    static TestSession create() {
        return new TestSession(nonce())
    }

    static String nonce() {
        int size = Installation.instance().propertyServiceManager().nonceSize;
        if (size > 12) size = 12
        String base = UuidAllocator.allocate().reverse()
        String value = base.substring(0, size)
        return value.replaceAll('-', 'p')
    }

    // This does not test for prior existance - better to use TestSessionServiceManager
    static TestSession build() {
        TestSession testSession = create()
        initialize(testSession)
        return testSession
    }

    static void initialize(TestSession testSession) {
        Installation.instance().simDbFile(testSession).mkdirs()
        File testLogFile = Installation.instance().testLogCache(testSession)
        testLogFile.mkdirs()
        Installation.instance().actorsDir(testSession).mkdirs()
        Installation.instance().testSessionMgmtDir(testSession).mkdirs()
    }

    /*
    static createMarkerFile(File parent) {
        try {
            File userModeMarkerFile = getUserModeMarkerFile(parent)
            Io.stringToFile(userModeMarkerFile, "")
        } catch (Exception ex) {}
    }

    static File getUserModeMarkerFile(File parent) throws Exception {
       if (Installation.instance().propertyServiceManager().isMultiuserMode()) {
          return new File(parent, Installation.instance().propertyServiceManager().propertyManager.MULTIUSER_MODE + ".txt")
       } else if (Installation.instance().propertyServiceManager().isCasMode()) {
           return new File(parent, Installation.instance().propertyServiceManager().propertyManager.CAS_MODE + ".txt")
       }
        throw new Exception("Current user mode not supported!")
    }
    */
}
