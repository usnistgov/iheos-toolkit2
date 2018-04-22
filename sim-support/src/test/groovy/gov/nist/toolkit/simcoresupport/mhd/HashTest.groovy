package gov.nist.toolkit.simcoresupport.mhd

import spock.lang.Specification

import java.security.MessageDigest

class HashTest extends Specification {

    def 'x' () {
        when:
        String content = 'Hola Mundo'
        MessageDigest sha1er = MessageDigest.getInstance("SHA1")
        byte[] digest = sha1er.digest(content.getBytes())
        String sha1 = '48124d6dc3b2e693a207667c32ac672414913994'
        byte[] binary = sha1.decodeHex()

        then:
        digest == binary

        when:
        String base64 = binary.encodeBase64().toString()
        print base64
        String sha1a = base64.decodeBase64().encodeHex().toString()

        then:
        sha1 == sha1a
    }
}
