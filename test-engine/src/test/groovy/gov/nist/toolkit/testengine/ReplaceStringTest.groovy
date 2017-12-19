package gov.nist.toolkit.testengine

import gov.nist.toolkit.testengine.engine.Linkage
import gov.nist.toolkit.utilities.xml.Util
import org.apache.axiom.om.OMElement
import spock.lang.Specification

import java.util.regex.Matcher

class ReplaceStringTest extends Specification {

    def text1= '''
                    'submittedValue2' = 'returnedValue1' or 'submittedValue2' = 'returnedValue2\'
                '''
    def oldText1 = 'submittedValue2'
    def newText1 = 'urn:oid:1.2.3.4.567.8.2'
    OMElement e = Util.parse_xml("<foo>${text1}</foo>")

    def 'Test Replace'() {
        given:
        String oldstuff
        String replacement
        String text
        Linkage linkage = new Linkage(null)
        text = text1

        when:
        text = e.getText()
        oldstuff = linkage.escape_pattern('submittedValue2')
        replacement = Matcher.quoteReplacement('urn:oid:1.2.3.4.567.8.2')
        text = text.replaceAll(oldstuff, replacement)
        e.setText(text)

        then:
        true

        when:
        text = e.getText()
        oldstuff = linkage.escape_pattern('returnedValue1')
        replacement = Matcher.quoteReplacement('urn:oid:1.2.3.4.567.8.3')
        text = text.replaceAll(oldstuff, replacement)
        e.setText(text)

        then:
        true

        when:
        text = e.getText()
        oldstuff = linkage.escape_pattern('returnedValue2')
        replacement = Matcher.quoteReplacement('urn:oid:1.2.3.4.567.4.3')
        text = text.replaceAll(oldstuff, replacement)
        e.setText(text)

        then:
        true
    }
}
