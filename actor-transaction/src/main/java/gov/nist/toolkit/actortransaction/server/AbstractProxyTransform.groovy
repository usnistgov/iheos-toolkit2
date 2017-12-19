package gov.nist.toolkit.actortransaction.server

import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.utilities.html.HeaderBlock
import org.apache.http.HttpHeaders

/**
 *
 */
abstract class AbstractProxyTransform {
    HeaderBlock inputHeaders
    String inputBody
    HeaderBlock outputHeaders
    String outputBody

    /**
     * takes inputHeader, inputBody and produces outputHeader, outputBody
     * A SimProxy can have multiple proxy transforms.  More than one can return
     * a TransactionType.  If none do then the input transaction type is used.
     * If multiple transforms return a non-null TransactionType then the last
     * one called (with a non-null return) gets used.
     * @return - transaction type to use in forwarding
     */
    abstract TransactionType run()

}
