package gov.nist.toolkit.valregmsg.xdm

import gov.nist.toolkit.errorrecording.IErrorRecorder
import gov.nist.toolkit.errorrecording.gwt.GwtErrorRecorder
import spock.lang.Specification
/**
 *
 */
class Iso9660Test extends Specification {
    IErrorRecorder er = new GwtErrorRecorder()
    ValidateISO9660 v = new ValidateISO9660(er)

    def 'simple name' () {
        when: v.run('ABCDEF.ONL')
        then: !er.hasErrors()
    }

    def 'simple name - numbers ok' () {
        when: v.run('ABCD22.ONL')
        then: !er.hasErrors()
    }

    def 'simple name - _ ok' () {
        when: v.run('ABCD_2.ONL')
        then: !er.hasErrors()
    }

    def 'simple path name' () {
        when: v.run('/AB/CD/ABCDEF.ONL')
        then: !er.hasErrors()
    }

    def 'directory name can end in /' () {
        when: v.run('AB/')
        then: !er.hasErrors()
    }

    def 'long directory name' () {
        when: v.run('/ABNNNNNNNCD/ABCDEF.ONL')
        then:
        er.errMsgs.size() == 1
        er.errMsgs.get(0).msg.contains('limited to 8 characters')
    }

    def 'long directory name 2' () {
        when: v.run('CCDA/HTML/IMAGES/PAGEBACKGROUND.PNG')
        then:
        er.errMsgs.size() == 1
        er.errMsgs.get(0).msg.contains('limited to 8 characters')
    }

    def 'bad characters in name' () {
        when: v.run('ABC&DEF.ONL')
        then:
        er.hasErrors()
        er.errMsgs.get(0).msg.contains('upper case, digits, or _')
    }

    def 'directory name has dot' () {
        when: v.run('/AB.CD/ABCDEF.ONL')
        then:
        er.hasErrors()
        er.errMsgs.get(0).msg.contains('contains invalid characters')
    }

    def 'directory hierarchy depth 7 ok' () {
        when: v.run('/A/B/V/D/D/D/ABCDEF.ONL')
        then:
        !er.hasErrors()
    }

    def 'directory hierarchy depth 9 is bad' () {
        when: v.run('/A/B/V/D/D/D/M/B/ABCDEF.ONL')
        then:
        er.hasErrors()
        er.errMsgs.get(0).msg.contains('directory hierarchy must not exceed 8')
    }

    def 'lower case bad' () {
        when: v.run("abcdef.onl")
        then:
        er.hasErrors()
        er.errMsgs.get(0).msg.contains('must be upper case')
    }

    def 'name too long' () {
        when: v.run("ABCDEFGHIJK.XML")
        then:
        er.hasErrors()
        er.errMsgs.get(0).msg.contains('are limited to 8 characters')
    }

    def 'ext too long' () {
        when: v.run("AB.XMLUU")
        then:
        er.hasErrors()
        er.errMsgs.get(0).msg.contains('are limited to 8 characters')
    }

    def 'no ext' () {
        when: v.run("AB.")
        then:
        !er.hasErrors()
    }

    def 'two dots' () {
        when: v.run("ABC.X.XML")
        then:
        er.hasErrors()
        er.errMsgs.get(0).msg.contains('shall not have more than one dot')
    }
}
