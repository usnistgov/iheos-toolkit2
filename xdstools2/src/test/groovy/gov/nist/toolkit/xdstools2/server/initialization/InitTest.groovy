package gov.nist.toolkit.xdstools2.server.initialization

import gov.nist.toolkit.installation.server.ExternalCacheManager
import gov.nist.toolkit.installation.server.Installation
import org.apache.commons.io.FileUtils
import spock.lang.Specification

import java.nio.file.Paths

/**
 *
 */
class InitTest extends Specification {

    def 'Basic test'() {
        when:
//        File initMarker = new File(getClass().getResource('/inittest/init.txt').file)
//        File warMarker = new File(getClass().getResource('/war/war.txt').file)
        File initMarker = Paths.get(getClass().getResource('/').toURI()).resolve('inittest/init.txt').toFile()
        File warMarker = Paths.get(getClass().getResource('/').toURI()).resolve('war/war.txt').toFile()

        then:
        initMarker.exists()
        warMarker.exists()

        when:
        File initDir = initMarker.parentFile
        File ec = new File(initDir, "EC")
        FileUtils.deleteDirectory(ec)

        then:
        !ec.exists()

        when:
        File warDir = warMarker.parentFile

        then:
        warDir.exists()
        warDir.isDirectory()

        when:
        ec.mkdir()
        String excuse = ExternalCacheManager.initialize(ec)
        Installation.instance().warHome(warDir)

        then:
        !excuse
        ec.exists()

        when:
        ExternalCacheManager.reinitialize(ec)
        File env = new File(ec, "environment")
        File dflt = new File(env, "default")
        File codes = new File(dflt, "codes.xml")

        then:
        env.exists()
        dflt.exists()
        codes.exists()
    }
}
