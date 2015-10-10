package gov.nist.toolkit.simulators.support

import gov.nist.toolkit.actorfactory.SimDb
import gov.nist.toolkit.actorfactory.client.SimulatorConfig
import groovy.util.logging.Log4j
import org.glassfish.jersey.client.ClientResponse

import javax.ws.rs.client.Client
import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.client.Entity
import javax.ws.rs.client.WebTarget

/**
 * Created by bill on 10/6/15.
 */
@Log4j
class Callback {
    def callback(SimDb db, SimulatorConfig config, String callbackURI) {
        if (callbackURI) {
            assert callbackURI.startsWith("http")
            String payload = new TransactionReportBuilder().build(db, config);
            log.info("Callback to ${callbackURI}");
            try {
                Client client = ClientBuilder.newClient()
                WebTarget target = client.target(callbackURI)

                ClientResponse response = target
                        .request('text/xml')
                        .post(Entity.entity(payload, 'text/xml'),
                                ClientResponse.class)

                if (response.getStatus() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : "
                            + response.getStatus());
                }
            } catch (Exception e) {
                log.error("Error on callback for simulator ${config.id} to URI ${callbackURI}", e)
            }

        }
    }
}
