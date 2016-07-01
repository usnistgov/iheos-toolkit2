package gov.nist.toolkit.errorrecording

import gov.nist.toolkit.errorrecording.ErrorRecorder
import gov.nist.toolkit.errorrecording.ErrorRecorderUtil
import gov.nist.toolkit.errorrecording.GwtErrorRecorderBuilder
import gov.nist.toolkit.errorrecording.factories.ErrorRecorderBuilder
import spock.lang.Specification

/**
 * Created by bill on 6/20/15.
 */
class GWTErrorRecorderTest extends Specification {

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

        when: 'Collect into list'
        List<ErrorRecorder> erl = ErrorRecorderUtil.errorRecorderChainAsList(er)

        then:
        erl.size() == 2
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

        when: 'Collect into list'
        List<ErrorRecorder> erl = ErrorRecorderUtil.errorRecorderChainAsList(erParent)

        then:
        erl.size() == 2
    }

    def 'List of three'() {
        setup:
        ErrorRecorderBuilder builder = new GwtErrorRecorderBuilder()

        when: 'Build chain of 3 ER'
        ErrorRecorder erParent = builder.buildNewErrorRecorder()
        ErrorRecorder erChild = erParent.buildNewErrorRecorder()
        ErrorRecorder erGChild = erChild.buildNewErrorRecorder()

        then:
        erParent.depth() == 3
        ErrorRecorderUtil.errorRecorderChainAsList(erParent).size() == 3
    }

    def 'Family tree'() {
        setup:
        ErrorRecorderBuilder builder = new GwtErrorRecorderBuilder()

        when: 'Build chain of 3 ER plus second grandkid'
        ErrorRecorder erParent = builder.buildNewErrorRecorder()
        ErrorRecorder erChild = erParent.buildNewErrorRecorder()
        ErrorRecorder erGChild1 = erChild.buildNewErrorRecorder()
        ErrorRecorder erGChild2 = erChild.buildNewErrorRecorder()

        then:
        ErrorRecorderUtil.errorRecorderChainAsList(erParent).size() == 4
    }
}
