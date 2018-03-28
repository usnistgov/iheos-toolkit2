package gov.nist.toolkit.fhir.simulators.proxy.transforms

import gov.nist.toolkit.simcoresupport.mhd.HashTranslator
import spock.lang.Specification

class HashTransformTest extends Specification {

    def 'hash test'() {
        when:
        def hexBinary = 'da39a3ee5e6b4b0d3255bfef95601890afd80709'
        byte[] bytes = HashTranslator.toByteArray(hexBinary)
        def hexBinary2 = HashTranslator.fromByteArray(bytes)

        then:
        hexBinary == hexBinary2
    }
}
