package gov.nist.toolkit.errorrecording.factories

import gov.nist.toolkit.errorrecording.ErrorRecorder
import gov.nist.toolkit.errorrecording.GwtErrorRecorderBuilder
import spock.lang.Specification

/**
 * Created by bill on 6/20/15.
 */
class ErrorRecorderTest extends Specification {

    def 'Builder test'() {
        when: 'Build basic Error Recorder'
        ErrorRecorderBuilder builder = new GwtErrorRecorderBuilder()
        ErrorRecorder er = builder.buildNewErrorRecorder()

        then:
        er

        when: 'Build second generation Error Recorder'
        ErrorRecorder er2 = er.buildNewErrorRecorder()

        then:
        er2

        then: 'And they are different'
        er != er2
    }

    def 'Parent Child linkage'() {
        setup:
        ErrorRecorderBuilder builder = new GwtErrorRecorderBuilder()

        when: 'Build parent ER'
        ErrorRecorder erParent = builder.buildNewErrorRecorder()

        then:
        erParent

        when: 'Build child ER'
        ErrorRecorder erChild = erParent.buildNewErrorRecorder()

        then:
        erChild

        then: 'Parent and child linked'
        erParent.children.size() == 1
        erParent.children.get(0) == erChild
        erChild.children.size() == 0

        then: 'Parent depth should be 2'
        erParent.depth() == 2
        erChild.depth() == 1
    }
}
