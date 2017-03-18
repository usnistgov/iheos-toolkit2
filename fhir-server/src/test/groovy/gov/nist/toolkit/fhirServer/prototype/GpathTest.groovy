package gov.nist.toolkit.fhirServer.prototype

import com.jayway.restassured.path.json.JsonPath
import spock.lang.Specification

/**
 * Uses JsonPath by REST Assured
 * requires
 * <dependency>
 <groupId>com.jayway.restassured</groupId>
 <artifactId>json-path</artifactId>
 <version>2.4.0</version>
 </dependency>
 */
class GpathTest extends Specification {

    def 'simple gpath test'() {

        when:  '''load a Patient resource'''
        String patientJsonString = this.getClass().getResource('/Patient1.json').text
        def familyName = JsonPath.from(patientJsonString).get('name[0].family[0]')

        then:
        familyName == 'Chalmers'
    }
}
