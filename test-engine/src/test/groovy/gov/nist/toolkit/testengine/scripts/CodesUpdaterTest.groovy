package gov.nist.toolkit.testengine.scripts

import spock.lang.Specification

/**
 * Created by oherrmann on 2/24/16.
 */
class CodesUpdaterTest extends Specification{
    def CodesUpdater updater
    File environmentFile
    File testkitFile

    def setup(){
        URL environmentMarker = getClass().getResource('/external_cache/environment/codeUpdateTest/codes.xml')
        environmentFile = new File(environmentMarker.toURI().path).parentFile
        URL testkitMarker= getClass().getResource('/war/war.txt')
        testkitFile=new File(new File(new File(testkitMarker.toURI().path).parentFile,"toolkitx"),"testkit")
        updater=new CodesUpdater()
    }

    def 'Test'(){
        when:
        updater.run(environmentFile.getAbsolutePath(),testkitFile.getAbsolutePath())
        then:
        !updater.hasErrors()
    }
}
