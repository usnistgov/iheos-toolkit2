package gov.nist.toolkit.validatorsSoapMessage.factories

import gov.nist.toolkit.errorrecording.ErrorRecorder
import gov.nist.toolkit.errorrecording.ErrorRecorderUtil
import gov.nist.toolkit.errorrecording.GwtErrorRecorder
import gov.nist.toolkit.errorrecording.GwtErrorRecorderBuilder
import gov.nist.toolkit.errorrecording.factories.ErrorRecorderBuilder
import gov.nist.toolkit.utilities.xml.Util
import gov.nist.toolkit.valsupport.client.ValidationContext
import gov.nist.toolkit.valsupport.engine.DefaultValidationContextFactory
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine
import gov.nist.toolkit.valsupport.engine.ValidationStep
import gov.nist.toolkit.valsupport.registry.RegistryValidationInterface
import org.apache.axiom.om.OMElement
import spock.lang.Specification

/**
 *
 */
class SoapRootElementValidatorFactoryTest extends Specification {
    def pnrXmlString
    OMElement xml
    MessageValidatorEngine mvc
    RegistryValidationInterface rvi
    ValidationContext vc
    ErrorRecorderBuilder erb

    def init(def message) {
        pnrXmlString = this.getClass().getResource(message).text
        xml = Util.parse_xml(pnrXmlString)
        mvc = new MessageValidatorEngine()
        rvi = null
        vc = DefaultValidationContextFactory.validationContext()
        vc.codesFilename = this.getClass().getResource('/codes.xml').file
        erb = new GwtErrorRecorderBuilder().buildNewErrorRecorder()
    }

    def printErrors() {
        List<ErrorRecorder> erl = ErrorRecorderUtil.errorRecorderChainAsList(erb)
        println "${erl.size()} ERs"
        List<String> errors = erl.collect() { ((GwtErrorRecorder) it).toString()}
        println errors
    }

    def 'Pnr with SOAP validation based on validation context'() {
        setup:
        init('/messages/pnr_soap.xml')
        erb.sectionHeading('Pnr validation test')

        vc.isPnR = true
        vc.isRequest = true
        vc.hasHttp = false
        vc.hasSoap = true

        when: 'Run validations'

        mvc = SoapMessageValidatorFactory.validateBasedOnValidationContext(
                erb, xml, mvc, vc, rvi
        )
        mvc.run()
        ValidationStep rootVS = mvc.getRootValidationStep()
        ErrorRecorder er1 = rootVS.errorRecorder

        then:
        er1.class.name.endsWith('GwtErrorRecorder')

        when: 'Collect ER'
        printErrors()

        then:
        !ErrorRecorderUtil.hasErrors(erb)
    }

    def 'Pnr with soap validation based on root element'() {
        setup:
        init('/messages/pnr_soap.xml')
        erb.sectionHeading('Pnr validation test')

        when: 'Run validations'
        mvc = SoapMessageValidatorFactory.validateBasedOnRootElement(
                erb, pnrXmlString, mvc, vc, rvi
        )
        mvc.run()
        ValidationStep rootVS = mvc.getRootValidationStep()
        ErrorRecorder er1 = rootVS.errorRecorder

        then:
        er1.class.name.endsWith('GwtErrorRecorder')

        when: 'Collect ER'
        printErrors()

        then:
        !ErrorRecorderUtil.hasErrors(erb)
    }

}
