package gov.nist.toolkit.pluginSupport

import gov.nist.toolkit.pluginSupport.loader.PluginClassLoader
import spock.lang.Specification

class GroovyLoaderTest extends Specification {

    def 'Loader Test'() {
        setup:
        def topDir = System.getProperty("user.dir")
        def testDir = topDir + '/src/test/groovy'
        println testDir
        PluginClassLoader loader = new PluginClassLoader(testDir)

        when:
        def className = 'gov.nist.toolkit.pluginSupport.testClasses.B.groovy'
        Class claz = loader.loadFile(className)

        then:
        claz

        when:
        Object b = claz.newInstance()
        println b.getClass().name
        println b.hi()

        then:
        true  // if no exceptions thrown then we're good
    }

}
