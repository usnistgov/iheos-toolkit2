package gov.nist.toolkit.simulators.support
import gov.nist.toolkit.actorfactory.SimDb
import gov.nist.toolkit.actorfactory.client.SimId
import gov.nist.toolkit.callbackService.TransactionLogBean
import groovy.util.logging.Log4j

import javax.ws.rs.client.Client
import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.client.Entity
import javax.ws.rs.client.WebTarget
import javax.ws.rs.core.Response
/**
 * Send toolkit simulator callback. This announces the arrival of a
 * message of interest from a SUT.
 */
@Log4j
class Callback {
    def callback(SimDb db, SimId simId, String callbackURI, String callbackClassName) {
        if (callbackURI) {
            assert callbackURI.startsWith("http")
            TransactionLogBean bean = new TransactionReportBuilder().asBean(db, simId, callbackClassName);
//            String payload = new TransactionReportBuilder().build(db, config);
            log.info("Callback to ${callbackURI}");
            try {
                Client client = ClientBuilder.newClient()
                WebTarget target = client.target(callbackURI)

                Response response = target
                        .request('text/xml')
                        .post(Entity.xml(bean))

                if (response.getStatus() != 200) {
                    throw new RuntimeException("Failed :\n...HTTP error code : "
                            + response.getStatus() + "\n..." +
                            response.getHeaderString("X-Toolkit-Error"));
                }
            } catch (Exception e) {
                log.error("Error on callback for simulator ${simId} to URI ${callbackURI} to class ${bean.callbackClassName}")
            }

        }
    }
}
