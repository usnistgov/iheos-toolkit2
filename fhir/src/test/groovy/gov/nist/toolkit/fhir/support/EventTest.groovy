package gov.nist.toolkit.fhir.support

import spock.lang.Specification

/**
 *
 */
class EventTest extends Specification {
    def sep = File.separator

    def 'from string'() {
        when:
        def path = "actor${sep}trans${sep}event${sep}Patient1"
        SimResource event = new SimResource(path)

        then:
        event.actor == 'actor'
        event.transaction == 'trans'
        event.event == 'event'
        event.filename == 'Patient1'
    }

    def 'from file'() {
        when:
        File file = new File("${sep}home${sep}bill${sep}ec${sep}simdb${sep}actor${sep}trans${sep}event${sep}Patient1")
        SimResource event = new SimResource(file)

        then:
        event.actor == 'actor'
        event.transaction == 'trans'
        event.event == 'event'
        event.filename == 'Patient1'
    }

    def 'from string to string'() {
        when:
        def path = "actor${sep}trans${sep}event${sep}Patient1"
        SimResource event = new SimResource(path)

        then:
        event.asPath() == path
    }
}
