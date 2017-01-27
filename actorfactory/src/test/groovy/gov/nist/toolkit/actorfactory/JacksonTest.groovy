package gov.nist.toolkit.actorfactory

import com.fasterxml.jackson.databind.ObjectMapper
import gov.nist.toolkit.actorfactory.client.SimulatorConfig
import gov.nist.toolkit.configDatatypes.client.PatientErrorMap
import gov.nist.toolkit.simcommon.shared.config.SimulatorConfigElement
import spock.lang.Specification
/**
 *
 */
class JacksonTest extends Specification {
    ObjectMapper mapper = new ObjectMapper() //.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)

    def 'serialize patienterrormap'() {
        when:
        PatientErrorMap map = new PatientErrorMap()
        byte[] bytes = mapper.writeValueAsBytes(map)
        println new String(bytes)
        def replica = mapper.readValue(bytes, PatientErrorMap)

        then:
        map == replica
    }

    def 'serialize simulatorconfigelement'() {
        when:
        SimulatorConfigElement ele = new SimulatorConfigElement()
        byte[] bytes = mapper.writeValueAsBytes(ele)
        println new String(bytes)
        def replica = mapper.readValue(bytes, SimulatorConfigElement)

        then:
        ele == replica
    }

    def 'serialize simulatorconfig'() {
        when:
        SimulatorConfig config = new SimulatorConfig()
        byte[] bytes = mapper.writeValueAsBytes(config)
        println new String(bytes)
        def replica = mapper.readValue(bytes, SimulatorConfig)

        then:
        config == replica
    }
}
