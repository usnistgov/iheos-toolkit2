package gov.nist.toolkit.fhir.simulators.proxy.transforms

import spock.lang.Specification

class HashTransformTest extends Specification {

    def 'hash test'() {
        when:
        def hexBinary = 'da39a3ee5e6b4b0d3255bfef95601890afd80709'
        byte[] bytes = HashTransform.toByteArray(hexBinary)
        def hexBinary2 = HashTransform.fromByteArray(bytes)

        then:
        hexBinary == hexBinary2
    }
}
