package gov.nist.toolkit.pluginSupport

import gov.nist.toolkit.pluginSupport.loader.DynamicClassLoader
import spock.lang.Specification

class LoaderTest extends Specification {

    def 'A Test'() {
        when:
        def topDir = System.getProperty("user.dir")
        println topDir

        def testDir = topDir + '/target/test-classes'
        println testDir

        DynamicClassLoader loader
        loader = new DynamicClassLoader(testDir)
        Class<?> claz = loader.load('gov.nist.toolkit.pluginSupport.testClasses.A')

        then:
        claz
        claz.name

        when:
        println "Got $claz.name"

        then:
        true

        when:
        Class<?> claz2 = loader.load('gov.nist.toolkit.pluginSupport.testClasses.A')

        then:
        claz2

        when:
        println "Got $claz2.name"

        then:
        true

        when:
        println "resetting classloader"
        loader = new DynamicClassLoader(testDir)
        claz2 = loader.load('gov.nist.toolkit.pluginSupport.testClasses.A')

        then:
        claz2

        when:
        println "Got $claz2.name"

        then:
        true

    }
}
