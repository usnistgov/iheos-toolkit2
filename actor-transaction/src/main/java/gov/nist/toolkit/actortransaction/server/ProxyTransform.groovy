package gov.nist.toolkit.actortransaction.server

/**
 *
 */
abstract class ProxyTransform {
    String inputHeader
    byte[] inputBody
    String outputHeader
    byte[] outputBody

    abstract void run()

}
