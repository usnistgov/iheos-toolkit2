import gov.nist.toolkit.actorfactory.client.SimId
import gov.nist.toolkit.simpleService.Main
import gov.nist.toolkit.toolkitServices.SimIdBean
import org.glassfish.grizzly.http.server.HttpServer
import spock.lang.Specification

import javax.ws.rs.client.Client
import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.client.Entity
import javax.ws.rs.client.WebTarget
import javax.ws.rs.core.Response

/**
 *
 */
class SimIdFakeTest extends Specification {
    private HttpServer server
    private WebTarget target

    def setupGrizzly() {
        server = Main.startServer();
        Client c = ClientBuilder.newClient();
        target = c.target(Main.BASE_URI);
    }

    def setupToolkit() {
        Client c = ClientBuilder.newClient();
        target = c.target('http://localhost:8888/xdstools2');
    }

    boolean toolkit = false

    def setup() {
        if (toolkit) setupToolkit()
        else setupGrizzly()
    }

    def 'Get SimId'() {
        when:
        SimIdBean bean = target.path("simulators/mike__reg").request().get(SimIdBean.class)

        then:
        bean.asSimId().toString() == 'mike__reg'
    }

    def 'Put SimId'() {
        given:
        String id = 'mike__reg'
        SimId simId = new SimId(id)
        when:
        SimIdBean bean = new SimIdBean(simId);
        Response response = target.path("simulators").request().put(Entity.xml(bean));

        then: response.status == 200

    }
}
