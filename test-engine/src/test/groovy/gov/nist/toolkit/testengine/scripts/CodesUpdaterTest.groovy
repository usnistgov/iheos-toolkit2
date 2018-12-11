package gov.nist.toolkit.testengine.scripts

import gov.nist.toolkit.installation.shared.TestSession
import spock.lang.Specification

import java.nio.file.Path
import java.nio.file.Paths

/**
 * Created by oherrmann on 2/24/16.
 */
class CodesUpdaterTest extends Specification{
    def CodesUpdater updater
    File environmentFile
    File testkitFile

    def setup(){
        environmentFile = Paths.get(this.getClass().getResource('/').toURI()).resolve('external_cache/environment/codeUpdateTest/codes.xml').toFile().parentFile
        Path testkitMarkerParent = Paths.get(this.getClass().getResource('/').toURI()).resolve('war/war.txt').toFile().parentFile.toPath()
        testkitFile = testkitMarkerParent.resolve("toolkitx/testkit").toFile()
        updater=new CodesUpdater()
    }

    def 'Test'(){
        when:
        updater.run(environmentFile.getAbsolutePath(),testkitFile.getAbsolutePath(), TestSession.DEFAULT_TEST_SESSION)
        then:
        !updater.hasErrors()
    }
}
