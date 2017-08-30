package gov.nist.toolkit.actortransaction.server

/**
 *
 */
abstract class ProxyTransform {
    String inputHeader
    byte[] inputBody
    String outputHeader
    byte[] outputBody

    /**
     * takes inputHeader, inputBody and produces outputHeader, outputBody
     */
    abstract void run()

}
