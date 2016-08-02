package gov.nist.toolkit.toolkitServicesCommon

import gov.nist.toolkit.toolkitServicesCommon.resource.Mapping
import spock.lang.Specification

/**
 *
 */
class MappingTest extends Specification {

    def 'Create with separate'() {
        when:
        Mapping m = new Mapping('a', 'b')

        then:
        m.key == 'a'
        m.value == 'b'
    }

    def 'Create with array'() {
        when:
        String[] arr = ['a', 'b']
        Mapping m = new Mapping(arr)

        then:
        m.key == 'a'
        m.value == 'b'
    }

    def 'Create from single string'() {
        when:
        Mapping m = new Mapping('a=b')

        then:
        m.key == 'a'
        m.value == 'b'
    }

    def 'Create from partial string'() {
        when:
        Mapping m = new Mapping('a=')

        then:
        m.key == 'a'
        m.value == ''
    }
}
