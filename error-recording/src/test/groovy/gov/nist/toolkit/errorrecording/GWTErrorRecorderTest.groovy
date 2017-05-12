package gov.nist.toolkit.errorrecording

import gov.nist.toolkit.errorrecording.xml.ErrorRecorderUtil
import gov.nist.toolkit.errorrecording.gwt.GwtErrorRecorderBuilder
import spock.lang.Specification

/**
 * Created by bill on 6/20/15.
 */
class GWTErrorRecorderTest extends Specification {

    def 'Builder test'() {
        when: 'Build basic Error Recorder'
        IErrorRecorderBuilder builder = new GwtErrorRecorderBuilder()
        IErrorRecorder er = builder.buildNewErrorRecorder()

        then:
        er

        when: 'Build second generation Error Recorder'
        IErrorRecorder er2 = er.buildNewErrorRecorder()

        then:
        er2

        then: 'And they are different'
        er != er2

        when: 'Collect into list'
        List<IErrorRecorder> erl = ErrorRecorderUtil.errorRecorderChainAsList(er)

        then:
        erl.size() == 2
    }

    def 'Parent Child linkage'() {
        setup:
        IErrorRecorderBuilder builder = new GwtErrorRecorderBuilder()

        when: 'Build parent ER'
        IErrorRecorder erParent = builder.buildNewErrorRecorder()

        then:
        erParent

        when: 'Build child ER'
        IErrorRecorder erChild = erParent.buildNewErrorRecorder()

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
        List<IErrorRecorder> erl = ErrorRecorderUtil.errorRecorderChainAsList(erParent)

        then:
        erl.size() == 2
    }

    def 'List of three'() {
        setup:
        IErrorRecorderBuilder builder = new GwtErrorRecorderBuilder()

        when: 'Build chain of 3 ER'
        IErrorRecorder erParent = builder.buildNewErrorRecorder()
        IErrorRecorder erChild = erParent.buildNewErrorRecorder()
        IErrorRecorder erGChild = erChild.buildNewErrorRecorder()

        then:
        erParent.depth() == 3
        ErrorRecorderUtil.errorRecorderChainAsList(erParent).size() == 3
    }

    def 'Family tree'() {
        setup:
        IErrorRecorderBuilder builder = new GwtErrorRecorderBuilder()

        when: 'Build chain of 3 ER plus second grandkid'
        IErrorRecorder erParent = builder.buildNewErrorRecorder()
        IErrorRecorder erChild = erParent.buildNewErrorRecorder()
        IErrorRecorder erGChild1 = erChild.buildNewErrorRecorder()
        IErrorRecorder erGChild2 = erChild.buildNewErrorRecorder()

        then:
        ErrorRecorderUtil.errorRecorderChainAsList(erParent).size() == 4
    }
}
