package gov.nist.toolkit.session.server.sort

import gov.nist.toolkit.session.client.sort.SortableTest
import gov.nist.toolkit.session.client.sort.TestSorter
import spock.lang.Specification

/**
 *
 */
class TestSorterTest extends Specification {

    class Test implements SortableTest {
        String name
        List<String> dependencies

        Test(String _name, List<String> _dependencies) {
            name = _name
            dependencies = _dependencies
        }

        String toString() { name }
    }

    def 'presorted numeric'() {
        given:
        def tests = [new Test('1', []), new Test('2', []), new Test('5', [])]

        when:
        def sorted = new TestSorter().sort(tests)

        then:
        sorted == tests
        ids(sorted) == ids(tests)
    }

    def 'reverse numeric'() {
        given:
        def tests = [new Test('9', []), new Test('3', []), new Test('2', [])]

        when:
        def sorted = new TestSorter().sort(tests)

        then:
        ids(sorted) == ['2', '3', '9']
    }

    def 'alpha numeric'() {
        given:
        def tests = [new Test('9', []), new Test('a', []), new Test('2', [])]

        when:
        def sorted = new TestSorter().sort(tests)

        then:
        ids(sorted) == ['2', '9', 'a']
    }

    def 'in order dependency'() {
        given:
        def tests = [new Test('2', []), new Test('5', []), new Test('7', ['5'])]

        when:
        def sorted = new TestSorter().sort(tests)

        then:
        ids(sorted) == ['2', '5', '7']
    }

    def 'out of order dependency'() {
        given:
        def tests = [new Test('2', []), new Test('5', ['7']), new Test('7', [])]

        when:
        def sorted = new TestSorter().sort(tests)

        then:
        ids(sorted) == ['2', '7', '5']
    }

    def 'multi level dependency'() {
        given:
        def tests = [new Test('2', []), new Test('3', ['9']), new Test('7', []), new Test('9', ['7'])]

        when:
        def sorted = new TestSorter().sort(tests)

        then:
        ids(sorted) == ['2', '7', '9', '3']
    }

    def ids(List<SortableTest> tests) {
        tests.collect { it.name }
    }
}
