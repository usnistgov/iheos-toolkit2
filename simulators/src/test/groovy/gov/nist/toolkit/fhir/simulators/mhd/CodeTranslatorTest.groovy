package gov.nist.toolkit.fhir.simulators.mhd

import gov.nist.toolkit.simcoresupport.mhd.Code
import gov.nist.toolkit.simcoresupport.mhd.CodeTranslator
import gov.nist.toolkit.valsupport.client.ValidationContext
import spock.lang.Specification

import java.nio.file.Paths

class CodeTranslatorTest extends Specification {
    File codesFile
    ValidationContext vc = new ValidationContext()

    def setup() {
        codesFile = Paths.get(this.getClass().getResource('/').toURI()).resolve('codes.xml').toFile()
        assert codesFile.exists()
        vc.codesFilename = codesFile
    }

    def 'load test'() {
        when:
        CodeTranslator xlator = new CodeTranslator(vc)
        def codeTypes = xlator.codeTypes

        then:
        codeTypes.find { it.name == CodeTranslator.CONFCODE }.codes.find { it.code == 'GRANT' && it.codingScheme == 'RSNA ISN'}
    }

    def 'find code by system'() {
        when:
        CodeTranslator xlator = new CodeTranslator(vc)
        Code code = xlator.findCodeBySystem(CodeTranslator.CONFCODE, 'urn:oid:1.3.6.1.4.1.21367.2017.3', 'GRANT')

        then:
        code.codingScheme == 'RSNA ISN'
    }

    def 'find by classification and system'() {
        when:
        CodeTranslator xlator = new CodeTranslator(vc)
        Code code = xlator.findCodeByClassificationAndSystem('urn:uuid:f4f85eac-e6cb-4883-b524-f2705394840f', 'urn:oid:1.3.6.1.4.1.21367.2017.3', 'GRANT')

        then:
        code.codingScheme == 'RSNA ISN'

    }




}
