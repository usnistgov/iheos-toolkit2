import gov.nist.toolkit.tookitSpi.SimulatorBuilder
import gov.nist.toolkit.toolkitServicesCommon.SimId
import gov.nist.toolkit.toolkitServicesCommon.SimIdBean
import gov.nist.toolkit.toolkitServicesCommon.ToolkitFactory
import spock.lang.Specification

import javax.ws.rs.client.Client
import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.client.Entity
import javax.ws.rs.client.WebTarget
import javax.ws.rs.core.Response
/**
 *
 */
class SimIdTest extends Specification {
    private WebTarget target

    def setup() {
        Client c = ClientBuilder.newClient();
        target = c.target('http://localhost:8888/xdstools2/rest/');
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
        SimId simId = ToolkitFactory.newSimId('reg', 'mike', 'reg', 'NA2015');
//        SimId simId = new SimId(id)
//        simId.setActorType(ActorType.REGISTRY.getName());
//        simId.setEnvironmenName('NA2015')
        when:
        SimIdBean bean = new SimIdBean(simId);
        Response response = target.path("simulators").request().put(Entity.xml(bean));

        then: response.status == 200

    }

    def 'Put SimId via SPI'() {
        given:
        SimulatorBuilder builder = new SimulatorBuilder('localhost', '8888');

        when:
        SimId simId = builder.create('reg', 'mike', 'reg', 'NA2015')

        then:
        simId.getId() == 'reg'
    }
}
