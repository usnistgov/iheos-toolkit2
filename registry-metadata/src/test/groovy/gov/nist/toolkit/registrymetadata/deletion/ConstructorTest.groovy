package gov.nist.toolkit.registrymetadata.deletion

import gov.nist.toolkit.registrymetadata.deletion.objects.RO
import spock.lang.Specification

/**
 *
 */
class ConstructorTest extends Specification {

    def 'test'() {
        when:
        RO ro = new RO('id')

        then:
        ro.id == new Uuid('id')
    }
}
