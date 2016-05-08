package gov.nist.toolkit.valregmsg.xdm

import gov.nist.toolkit.errorrecording.ErrorRecorder
import gov.nist.toolkit.errorrecording.TextErrorRecorder
import gov.nist.toolkit.errorrecording.factories.ErrorRecorderBuilder
import gov.nist.toolkit.errorrecording.factories.TextErrorRecorderBuilder
import gov.nist.toolkit.valsupport.client.ValidationContext
import gov.nist.toolkit.valsupport.engine.DefaultValidationContextFactory
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine
import spock.lang.Specification

/**
 *
 */
class XdmTest extends Specification {

    def 'ccda'() {
        setup:
        InputStream xdm = this.getClass().getResource('/xdm/Ccda.zip').openStream()

        when:
        ValidationContext vc = DefaultValidationContextFactory.validationContext();
        vc.isXDM = true;
        ErrorRecorderBuilder erBuilder = new TextErrorRecorderBuilder();
        TextErrorRecorder er = erBuilder.buildNewErrorRecorder();
        MessageValidatorEngine mvc = new MessageValidatorEngine();

        XdmDecoder xd = new XdmDecoder(vc, erBuilder, xdm);
        xd.er = er;

        then:
        xd.isXDM()

        when:
        xd.showContents = true;
        xd.run(er, mvc);
        println er.toString()
        OMap omap = xd.contents
        List<Path> items = omap.keySet()
        items.each { println it}

        then:
        true
    }
}
