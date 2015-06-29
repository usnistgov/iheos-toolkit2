package gov.nist.toolkit.valregmsg.validation.factories

import gov.nist.toolkit.errorrecording.ErrorRecorder
import gov.nist.toolkit.errorrecording.ErrorRecorderUtil
import gov.nist.toolkit.errorrecording.GwtErrorRecorder
import gov.nist.toolkit.errorrecording.GwtErrorRecorderBuilder
import gov.nist.toolkit.errorrecording.factories.ErrorRecorderBuilder
import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.utilities.xml.Util
import gov.nist.toolkit.valsupport.client.ValidationContext
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine
import gov.nist.toolkit.valsupport.engine.ValidationStep
import gov.nist.toolkit.valsupport.registry.RegistryValidationInterface
import org.apache.axiom.om.OMElement
import spock.lang.Specification


/**
 * Created by bill on 6/25/15.
 */
class MessageValidatorFactoryTest extends Specification {

    def 'Pnr validation test'() {
        setup:
        def pnrXmlString = this.getClass().getResource('/messages/pnr.xml').text
        ErrorRecorderBuilder erb = new GwtErrorRecorderBuilder().buildNewErrorRecorder()
        erb.sectionHeading('Pnr validation test')
        OMElement xml = Util.parse_xml(pnrXmlString)
        MessageValidatorEngine mvc = new MessageValidatorEngine()
        RegistryValidationInterface rvi = null

        ValidationContext vc = new ValidationContext()
        vc.isPnR = true
        vc.isRequest = true
        vc.hasHttp = false
        vc.hasSoap = false
        vc.codesFilename = this.getClass().getResource('/codes.xml').file

        when: 'Run validations'

        mvc = MessageValidatorFactory.validateBasedOnValidationContext(
                erb, xml, mvc, vc, rvi
        )
        mvc.run()
        ValidationStep rootVS = mvc.getRootValidationStep()
        int stepCount = mvc.getValidationStepCount()

        then: 'Count steps run'
        rootVS
        stepCount == 6

        when:
        ErrorRecorder er1 = rootVS.errorRecorder

        then:
        er1.class.name.endsWith('GwtErrorRecorder')

        when: 'Collect ER'
        List<ErrorRecorder> erl = ErrorRecorderUtil.errorRecorderChainAsList(erb)
        println "${erl.size()} ERs"
        List<String> errors = erl.collect() { ((GwtErrorRecorder) it).toString()}
        println errors

        then:
        !ErrorRecorderUtil.hasErrors(erb)
    }
}
