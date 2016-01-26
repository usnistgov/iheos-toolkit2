package gov.nist.toolkit.valregmetadata.xmlOutput

import spock.lang.Specification

/**
 * Created by diane on 1/26/2016.
 */
class HelloSpock extends Specification {
    def "can you figure out what I'm up to?"(){
        expect:
        name.size() == length

        where:
        name << ["Kirk", "Spock", "Scotty"]
        length << [4, 5, 7]
    }
}
