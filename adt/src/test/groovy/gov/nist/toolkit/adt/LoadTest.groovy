package gov.nist.toolkit.adt

import spock.lang.Specification

/**
 *
 */
class LoadTest extends Specification {
    def 'load request'() {
        when:
        File requestFile = new File(A01Sender.class.getResource("/adt/A01.txt").file)
        String content = new String(requestFile.bytes)
        println content

        then:
        notThrown Exception
    }


    def 'load response'() {
        when:
        File responseFile = new File(A01Sender.class.getResource("/adt/ACK.txt").file)
        String content = new String(responseFile.bytes)
        println content

        then:
        notThrown Exception
    }
}
