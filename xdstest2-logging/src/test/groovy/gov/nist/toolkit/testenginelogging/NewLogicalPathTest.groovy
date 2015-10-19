package gov.nist.toolkit.testenginelogging
import spock.lang.Specification

import java.nio.file.Path

/**
 * Created by bill on 10/19/15.
 */
class NewLogicalPathTest extends Specification {

    def testKit = '/Users/bill/tomcat1/webapps/xdstools2/toolkitx/testkit'
    def testpath = '/Users/bill/tomcat1/webapps/xdstools2/toolkitx/testkit/tests/11991/submit'

    def 'Extract relative path'() {
        when:
        def relPath = TestDetails.getLogicalPath(new File(testpath), new File(testKit))

        then:
        relPath == 'tests/11991/submit'
    }

    def 'New stuff'() {
        when:
        Path relPath = TestDetails.getLogicalPath(new File(testpath).toPath(), new File(testKit).toPath())

        then:
        relPath == new File('tests/11991/submit').toPath()
    }
}
