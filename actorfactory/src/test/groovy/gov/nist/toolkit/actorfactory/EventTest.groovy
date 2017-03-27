package gov.nist.toolkit.actorfactory

import spock.lang.Specification

/**
 *
 */
class EventTest extends Specification {

    def 'from string'() {
        when:
        def path = 'actor/trans/event'
        Event event = new Event(path)

        then:
        event.actor == 'actor'
        event.transaction == 'trans'
        event.event == 'event'
    }

    def 'from file'() {
        when:
        File file = new File('/home/bill/ec/simdb/actor/trans/event')
        Event event = new Event(file)

        then:
        event.actor == 'actor'
        event.transaction == 'trans'
        event.event == 'event'
    }

    def 'from string to string'() {
        when:
        def path = 'actor/trans/event'
        Event event = new Event(path)

        then:
        event.asPath() == path
    }
}
