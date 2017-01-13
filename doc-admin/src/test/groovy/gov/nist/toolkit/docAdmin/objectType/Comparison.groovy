package gov.nist.toolkit.docAdmin.objectType

import gov.nist.toolkit.docAdmin.attType.Identifier
import gov.nist.toolkit.docAdmin.attType.Slot
import gov.nist.toolkit.docAdmin.util.ListUtil
import spock.lang.Specification

/**
 *
 */
class Comparison extends Specification {

    def 'identifier equals'() {
        when:
        def id1 = new Identifier([name:'id', value:'Document01'])
        def id2 = new Identifier([name:'id', value:'Document01'])
        def id3 = new Identifier([name:'id', value:'Document02'])

        then:
        id1 == id2
        id1 != id3
    }

    def 'identifier sorts'() {
        when:
        def id1 = new Identifier([name:'id', value:'Document01'])
        def id2 = new Identifier([name:'id2', value:'Document02'])
        def id3 = new Identifier([name:'id2', value:'Document01'])
        def lst = [id1, id2, id3]
        def sorted = lst.sort()

        then:
        sorted == [id1, id3, id2]
        sorted != [id1, id2, id3]
    }

    def 'list compare'() {
        expect:
        ListUtil.compare([], []) == 0
        ListUtil.compare([1], [1]) == 0
        ListUtil.compare([], [1]) == -1
        ListUtil.compare([1], []) == 1

        ListUtil.compare([1], [2]) == -1
        ListUtil.compare([2], [1]) == 1

        ListUtil.compare([1], [1,2]) == -1
        ListUtil.compare([1,2], [1]) == 1

        ListUtil.compare([1,2,3], [1,3,4]) == -1
    }

    def 'slot equals'() {
        when:
        def s1 = new Slot([name:'a', values:['a']])
        def s1a = new Slot([name:'a', values:['a']])
        def s2 = new Slot([name:'b', values:['ab']])
        def s2a = new Slot([name:'b', values:['ab']])

        then:
        s1 == s1a
        s2 == s2a
        s1 != s2
    }


    def 'slot sorts'() {
        when:
        def s1 = new Slot([name:'a', values:['a']])
        def s2 = new Slot([name:'a', values:['ab']])
        def s3 = new Slot([name:'b', values:['a']])

        then:
        s1 < s2
        s1 < s3
        s2 < s3
    }

    def 'MObject.contains'() {
        when:
        MObject o = new RObject()
        o
                .add(new Identifier([name:'id', value:'Document1']))
                .add(new Slot([name:'size', value:42]))

        then:
        true
    }
}
