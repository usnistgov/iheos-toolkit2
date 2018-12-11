package gov.nist.toolkit.valregmetadata.field
import gov.nist.toolkit.errorrecording.ErrorRecorder
import gov.nist.toolkit.errorrecording.GwtErrorRecorder
import gov.nist.toolkit.registrymetadata.Metadata
import gov.nist.toolkit.registrymetadata.MetadataParser
import gov.nist.toolkit.utilities.io.Io
import gov.nist.toolkit.valregmetadata.top.MetadataValidator
import gov.nist.toolkit.valsupport.client.ValidationContext
import org.apache.log4j.BasicConfigurator
import spock.lang.Specification

import java.nio.file.Paths

/**
 *
 */
class SimpleMetadataValidatorTest extends Specification {

    def 'Run'() {
        setup:
        BasicConfigurator.configure()

        when:
        InputStream metadataStream = getClass().getResourceAsStream('/PnR1Doc.xml')
        String metadataString = Io.getStringFromInputStream(metadataStream)
        Metadata m = MetadataParser.parse(metadataString)

        ValidationContext vc = new ValidationContext()
        vc.isRequest = true
        vc.isPnR = true
        vc.codesFilename = Paths.get(this.getClass().getResource('/').toURI()).resolve('codes.xml').toFile()

        ErrorRecorder er = new GwtErrorRecorder()
//        ErrorRecorder er = new TextErrorRecorder()

        MetadataValidator val = new MetadataValidator(m, vc, null)
        val.run(er)

        println er.toString()

        then:
        true
    }
}
