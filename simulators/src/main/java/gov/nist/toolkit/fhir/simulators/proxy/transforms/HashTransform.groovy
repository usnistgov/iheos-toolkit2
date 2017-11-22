package gov.nist.toolkit.fhir.simulators.proxy.transforms

import javax.xml.bind.DatatypeConverter

class HashTransform {

    static byte[] toByteArray(String hash) {
        DatatypeConverter.parseHexBinary(hash)
    }

    static String fromByteArray(byte[] bytes) {
        DatatypeConverter.printHexBinary(bytes).toLowerCase()
    }
}
