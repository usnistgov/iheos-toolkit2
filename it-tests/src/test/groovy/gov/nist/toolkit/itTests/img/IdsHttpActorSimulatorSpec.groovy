package gov.nist.toolkit.itTests.img

import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.errorrecording.ErrorRecorder
import gov.nist.toolkit.errorrecording.GwtErrorRecorder
import gov.nist.toolkit.http.HttpMessageBa
import gov.nist.toolkit.http.HttpParserBa
import gov.nist.toolkit.simcommon.client.SimulatorConfig
import gov.nist.toolkit.simcommon.server.SimDb
import gov.nist.toolkit.fhir.simulators.sim.ids.IdsHttpActorSimulator
import gov.nist.toolkit.fhir.simulators.support.DsSimCommon
import gov.nist.toolkit.simcommon.server.SimCommon
import gov.nist.toolkit.valsupport.client.ValidationContext
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine
import spock.lang.Specification

/**
 * Created by rmoult01 on 3/16/17.
 */
class IdsHttpActorSimulatorSpec extends Specification {

    static TransactionType type = TransactionType.WADO_RETRIEVE
    static HttpParserBa hparser

    def 'Test WADO 55 message parsing & validation'() {
        println(testName)

        when: 'An IdsHttpActorSimulator instance is created'

        ErrorRecorder er = new GwtErrorRecorder()
        SimDb db = Mock()
        ValidationContext vc = Mock()
        SimulatorConfig simConfig = Mock()
        SimCommon common = Mock(SimCommon, constructorArgs: [db, simConfig, false, vc, null, null, null])
        common.getCommonErrorRecorder() >> er

        HttpMessageBa httpMsg = Mock()
        httpMsg.getHeaderValue("Accept") >> acceptHeaderValue
        httpMsg.getQueryParameterValue("requestType") >> 'WADO'
        httpMsg.getQueryParameterValue("studyUID") >> studyUID
        httpMsg.getQueryParameterValue("seriesUID") >> seriesUID
        httpMsg.getQueryParameterValue("objectUID") >> objectUID
        httpMsg.getQueryParameterValue("contentType") >> objectUID

        hparser = Mock()
        hparser.getHttpMessage() >> httpMsg
        IdsHttpActorSimulator sim = new IdsHttpActorSimulator();
        sim.init(common)

        MessageValidatorEngine mvc = Mock()

        sim.setDsSimCommon(new MyDs(common, mvc))


        boolean ret = sim.run(type, mvc)

        then: 'It worked'
        er.hasErrors() == result

        where: 'tests to run'
        testName | acceptHeaderValue | studyUID | seriesUID | objectUID | contentType || result
        'Simple Test' | 'application/dicom' |
        '1.3.6.1.4.1.21367.201599.1.201604020954048' |
        '1.3.6.1.4.1.21367.201599.2.201604020954048' |
        '1.3.6.1.4.1.21367.201599.3.201604020954048.1' |
        'application/dicom' | false
    }

    public class MyDs extends DsSimCommon {

        public MyDs(SimCommon simCommon, MessageValidatorEngine mvc) {
            super(simCommon, mvc)
        }
         public HttpParserBa getHttpParserBa() {
             return hparser
         }
    }
}
