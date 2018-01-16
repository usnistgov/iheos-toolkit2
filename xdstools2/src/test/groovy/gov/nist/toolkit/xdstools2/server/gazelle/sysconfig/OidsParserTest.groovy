package gov.nist.toolkit.xdstools2.server.gazelle.sysconfig

import spock.lang.Specification

class OidsParserTest extends Specification {

    def 'trim' () {
        setup:
        OidsParser p = new OidsParser()

        expect:
        p.trim('aaa') == 'aaa'
        p.trim(' aa ') == 'aa'
        p.trim('\naa') == 'aa'
        p.trim('aa  \t') == 'aa'
        p.trim(' a a ') == 'a a'
    }
}
