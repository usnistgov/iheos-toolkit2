package gov.nist.toolkit.utilities.io

import spock.lang.Specification

/**
 *
 */
class HashTest extends Specification {

    def 'ByteBuffer grow'() {
        when:
        ByteBuffer byteBuffer = new ByteBuffer()
        byte[] data = new byte[4001]
        byteBuffer.append(data, 0, data.length)
        byte[] result = byteBuffer.get()

        then:
        byteBuffer.fill == 4001
        data.length == result.length
    }
}
