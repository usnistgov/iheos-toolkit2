package gov.nist.toolkit.actortransaction

import spock.lang.Specification

/**
 *
 */
class EndpointParserTest extends Specification {

    def 'null ok'() {
        when: new EndpointParser(null)

        then: notThrown Exception
    }

    def 'empty ok'() {
        when: new EndpointParser('')

        then: notThrown Exception
    }

    def 'space ok'() {
        when: new EndpointParser(' ')

        then: notThrown Exception
    }

    def 'good endpoint ok'() {
        expect: new EndpointParser('http://host:port/foo').validate()
    }

    def 'http required'() {
        expect: !new EndpointParser('ssh://host:port/foo').validate()
    }

    def 'no port ok'() {
        expect: new EndpointParser('http://host/foo').validate()
    }

    def 'double slash is error'() {
        expect: !new EndpointParser('http://host:port//foo').validate()
    }

    def 'parms is error'() {
        expect: !new EndpointParser('http://host:port/foo?').validate()
    }

    def 'update hostname'() {
        when: EndpointParser ep = new EndpointParser('http://host:port/foo')

        then: ep.validate()

        and: ep.updateHostAndPort('myhost', 'myport').getEndpoint() == 'http://myhost:myport/foo'
    }

    def 'get host'() {
        expect: new EndpointParser('http://host:port/foo').getHost() == 'host'
    }

    def 'get port'() {
        expect: new EndpointParser('http://host:port/foo').getPort() == 'port'
    }

    def 'get default port'() {
        expect: new EndpointParser('http://host/foo').getPort() == '80'
    }
}
