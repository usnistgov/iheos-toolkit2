package gov.nist.toolkit.toolkitServicesCommon

import spock.lang.Specification

/**
 *
 */
class SimConfigResourceTest extends Specification {

    def 'encode decode 2 element list'() {
        when:
        SimConfigResource config = new SimConfigResource()
        config.setProperty('name', ['a', 'b'] as List)

        then:
        config.asList('name') == ['a', 'b']
    }

    def 'encode decode 1 element list'() {
        when:
        SimConfigResource config = new SimConfigResource()
        config.setProperty('name', ['a'] as List)

        then:
        config.asList('name') == ['a']
    }

    def 'encode decode 0 element list'() {
        when:
        SimConfigResource config = new SimConfigResource()
        config.setProperty('name', new ArrayList<String>())
        println config.asList('name')

        then:
        config.asList('name').size() == 0
    }

}
