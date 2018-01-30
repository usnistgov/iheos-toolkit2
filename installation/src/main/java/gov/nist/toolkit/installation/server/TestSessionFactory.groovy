package gov.nist.toolkit.installation.server

import gov.nist.toolkit.installation.shared.TestSession
import gov.nist.toolkit.utilities.id.UuidAllocator
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
        Installation.instance().testLogCache(testSession).mkdirs()
        Installation.instance().actorsDir(testSession).mkdirs()

        return testSession
    }
}
