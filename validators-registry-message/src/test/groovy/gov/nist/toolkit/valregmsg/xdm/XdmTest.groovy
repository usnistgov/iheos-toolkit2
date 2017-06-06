package gov.nist.toolkit.valregmsg.xdm

import gov.nist.toolkit.errorrecording.IErrorRecorder
import gov.nist.toolkit.errorrecording.gwt.GwtErrorRecorderBuilder
import gov.nist.toolkit.errorrecording.IErrorRecorderBuilder
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
        IErrorRecorderBuilder erBuilder = new GwtErrorRecorderBuilder();
        IErrorRecorder er = ErrorRecorderFactory.getErrorRecorderFactory().getNewErrorRecorder();
        MessageValidatorEngine mvc = new MessageValidatorEngine();

        XdmDecoder xd = new XdmDecoder(vc, erBuilder, xdm);
        xd.er = er;

        then:
        true //xd.isXDM()

        when:
        xd.showContents = false;
        xd.run(er, mvc);
//        println er.toString()
//        OMap omap = xd.contents
//        List<Path> items = omap.keySet()
//        items.each { println it}
        int errCnt = 0
        er.errMsgs.each {
            if (it.isError()) {
                println 'ERROR: ' + it.msg + ' ' + it.location
                errCnt++
            }
        }

        then:
        er.hasErrors()
        errCnt == 3
    }
}
