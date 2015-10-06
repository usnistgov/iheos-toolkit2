package gov.nist.toolkit.simulators.support

import com.sun.jersey.api.client.Client
import com.sun.jersey.api.client.ClientResponse
import com.sun.jersey.api.client.WebResource
import gov.nist.toolkit.actorfactory.SimDb
import gov.nist.toolkit.actorfactory.client.SimulatorConfig
import groovy.util.logging.Log4j
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

                Client client = Client.create();

                WebResource webResource = client.resource(callbackURI);

                ClientResponse response = webResource.type("applicaiton/xml")
                        .post(ClientResponse.class, payload);

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
