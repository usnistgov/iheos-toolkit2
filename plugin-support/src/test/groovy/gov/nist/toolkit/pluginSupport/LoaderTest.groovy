package gov.nist.toolkit.pluginSupport

import gov.nist.toolkit.pluginSupport.loader.Context
import spock.lang.Specification

class LoaderTest extends Specification {

    def 'A Test'() {
        setup:
        Context context = new Context()
        def topDir = System.getProperty("user.dir")
        def testDir = topDir + '/target/test-classes'
        println testDir

        when:
        context.setDynamicClassPath(testDir)
        Class<?> claz = context.load('gov.nist.toolkit.pluginSupport.testClasses.A')

        then:
        claz
        claz.name

        when:
        println "Got $claz.name"

        then:
        true

        when:
        Class<?> claz2 = context.load('gov.nist.toolkit.pluginSupport.testClasses.A')

        then:
        claz2

        when:
        println "Got $claz2.name"

        then:
        true

        when:
        println "resetting classloader"
        context.setDynamicClassPath(testDir)
        claz2 = context.load('gov.nist.toolkit.pluginSupport.testClasses.A')

        then:
        claz2

        when:
        println "Got $claz2.name"

        then:
        true

    }
}
