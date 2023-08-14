package gov.nist.toolkit.simcoresupport.mhd

import groovy.transform.TypeChecked

import javax.xml.bind.DatatypeConverter

@TypeChecked
class HashTranslator {

    static byte[] toByteArray(String hash) {
        DatatypeConverter.parseHexBinary(hash)
    }

    static byte[] toByteArrayFromBase64Binary(String hash) {
        DatatypeConverter.parseBase64Binary(hash)
    }

    static String fromByteArray(byte[] bytes) {
        DatatypeConverter.printHexBinary(bytes).toLowerCase()
    }
}
