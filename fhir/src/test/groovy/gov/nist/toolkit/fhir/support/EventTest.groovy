package gov.nist.toolkit.fhir.support

import spock.lang.Specification

/**
 *
 */
class EventTest extends Specification {

    def 'from string'() {
        when:
        def path = 'actor/trans/event/Patient1'
        SimResource event = new SimResource(path)

        then:
        event.actor == 'actor'
        event.transaction == 'trans'
        event.event == 'event'
        event.filename == 'Patient1'
    }

    def 'from file'() {
        when:
        File file = new File('/home/bill/ec/simdb/actor/trans/event/Patient1')
        SimResource event = new SimResource(file)

        then:
        event.actor == 'actor'
        event.transaction == 'trans'
        event.event == 'event'
        event.filename == 'Patient1'
    }

    def 'from string to string'() {
        when:
        def path = 'actor/trans/event/Patient1'
        SimResource event = new SimResource(path)

        then:
        event.asPath() == path
    }
}
