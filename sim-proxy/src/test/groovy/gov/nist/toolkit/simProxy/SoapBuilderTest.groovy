package gov.nist.toolkit.simProxy

import gov.nist.toolkit.simulators.proxy.util.PartSpec
import gov.nist.toolkit.simulators.proxy.util.SoapBuilder
import spock.lang.Specification

/**
 *
 */
class SoapBuilderTest extends Specification {

    def service = '/home/free'
    def host = 'localhost'
    def port = 'any'
    def action = 'now'
    def bodyXml = 'builder'

    def 'template test' () {
        when:
        def (hdr, body) = new SoapBuilder().simpleSoap(service, host, port, action, bodyXml)
        String header = hdr

        then:
        header.split('\n')[0].trim() == 'POST /home/free HTTP/1.1'
    }

    def 'mtom test' () {
        given:
        def referenceMsg = getClass().getResource('/sample_mtom_message.txt').text
        def part1 = getClass().getResource('/sample_part_1.txt').text   // start part
        def part2 = getClass().getResource('/sample_part_2.txt').text   // text attachment

        def part1Spec = new PartSpec(PartSpec.SOAPXOP, part1)
        def part2Spec = new PartSpec(PartSpec.PLAINTEXT, part2)
        SoapBuilder builder = new SoapBuilder()

        when:
        def (header, body) = builder.mtomSoap(service, host, port, action, [part1Spec, part2Spec])

        then:
        body == referenceMsg
    }

}
