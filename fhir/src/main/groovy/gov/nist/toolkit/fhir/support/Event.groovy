package gov.nist.toolkit.fhir.support

/**
 *
 */
class Event {
    private File eventDir

    Event(File eventDir) {
        this.eventDir = eventDir
    }

    File getEventDir() {
        return eventDir
    }
}
