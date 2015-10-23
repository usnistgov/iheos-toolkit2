import gov.nist.toolkit.errorrecording.TextErrorRecorder
import gov.nist.toolkit.registrymetadata.MetadataParser
import gov.nist.toolkit.utilities.xml.Util
import gov.nist.toolkit.valregmetadata.field.CodeValidation
import gov.nist.toolkit.valsupport.client.ValidationContext
import groovy.util.logging.Log4j
import org.apache.axiom.om.OMElement
import spock.lang.Specification

@Log4j
class CodeValidationTestCase extends Specification{
    def m
    def codeValidation
    def er
    // Olivier Windows
    // String codePath="W:\\WorkEnvironment\\Workspaces\\IntelliJ-workspace\\toolkit_v2\\validators-registry-metadata\\src\\test\\resources\\codes.xml";
    // Olivier Mac
    String codePath="/Users/oherrmann/Development/nist-workspace/toolkit_v2/validators-registry-metadata/src/test/resources/codes.xml";
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
