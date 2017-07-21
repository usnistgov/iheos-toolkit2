import groovy.util.logging.Log4j
import spock.lang.Specification

@Log4j
class CodeValidationTestCase extends Specification{
    def m
    def codeValidation
    def er
    // Olivier Windows
    // String codePath="W:\\WorkEnvironment\\Workspaces\\IntelliJ-workspace\\toolkit_v2\\validators-registry-metadata\\src\\test\\resources\\codes.xml";
    // Olivier Mac
    String codePath="/Users/oherrmann/Development/nist-workspace/toolkit_v2/validators-registry-metadata/src/test/resources/codes.xml"
    String metadata

    def setup(){
        metadata = getClass().classLoader.getResource('PnR1Doc.xml').text
    }

//    def 'Validate'(){
//        when:
//        OMElement ele = Util.parse_xml(metadata);
//        m=MetadataParser.parse(ele);
//
//        er = new TextErrorRecorder();
//
//        codeValidation=new CodeValidation(m);
//        ValidationContext vc=new ValidationContext();
//        vc.setCodesFilename(codePath);
//        codeValidation.setValidationContext(vc);
//        codeValidation.run(er);
//
//        then:
//        !er.hasErrors();
//    }
}
