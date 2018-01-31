package gov.nist.toolkit.installation.server

import gov.nist.toolkit.installation.shared.TestSession
import gov.nist.toolkit.utilities.id.UuidAllocator
import gov.nist.toolkit.utilities.io.Io
import groovy.transform.TypeChecked

@TypeChecked
class TestSessionFactory {

    static TestSession create() {
        int size = Installation.instance().propertyServiceManager().nonceSize;
        if (size > 12) size = 12
        String base = UuidAllocator.allocate().reverse()
        String value = base.substring(0, size)
        value = value.replaceAll('-', 'p')
        return new TestSession(value)
    }

    static TestSession build() {
        TestSession testSession = create()

        Installation.instance().simDbFile(testSession).mkdirs()
        File testLogFile = Installation.instance().testLogCache(testSession)
         if (testLogFile.mkdirs()) {
             createMarkerFile(testLogFile)
         }
        Installation.instance().actorsDir(testSession).mkdirs()

        return testSession
    }

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
}
