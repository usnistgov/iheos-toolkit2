package gov.nist.toolkit.testengine.scripts

import gov.nist.toolkit.installation.shared.TestSession
import spock.lang.Specification

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
        File testkitMarkerParent = Paths.get(this.getClass().getResource('/').toURI()).resolve('war/war.txt').toFile().parentFile
        testkitFile=new File(new File(testkitMarkerParent,"toolkitx"),"testkit")
        updater=new CodesUpdater()
    }

    def 'Test'(){
        when:
        updater.run(environmentFile.getAbsolutePath(),testkitFile.getAbsolutePath(), TestSession.DEFAULT_TEST_SESSION)
        then:
        !updater.hasErrors()
    }
}
