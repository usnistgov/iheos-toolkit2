package gov.nist.toolkit.soap.http

import gov.nist.toolkit.utilities.xml.OMFormatter
import spock.lang.Specification

/**
 *
 */
class SoapFaultTest extends Specification {

    def 'test 1' () {
        when:
        SoapFault fault = new SoapFault(SoapFault.FaultCodes.Sender, 'senderbad')
        String xml = new OMFormatter(fault.getXML()).toString()
        println xml
        SoapFault fault2 = new SoapFault(xml)

        then:
        fault == fault2
    }
}
