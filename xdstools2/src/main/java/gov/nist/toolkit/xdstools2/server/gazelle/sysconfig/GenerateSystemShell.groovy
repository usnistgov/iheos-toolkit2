package gov.nist.toolkit.xdstools2.server.gazelle.sysconfig

import gov.nist.toolkit.installation.shared.TestSession
import gov.nist.toolkit.sitemanagement.SeparateSiteLoader
import gov.nist.toolkit.sitemanagement.Sites
import gov.nist.toolkit.sitemanagement.client.Site
import groovy.transform.TypeChecked

/**
 *
 */
@TypeChecked
class GenerateSystemShell {
    File cache = new File('/Users/bill/tmp/actors')
//    def testingSession = '35'
    String gazelleBaseUrl // = 'https://gazelle.ihe.net/EU-CAT/systemConfigurations.seam?testingSessionId=' + testingSession
    GazellePull gazellePull
    TestSession testSession
    Sites existingSites
    SeparateSiteLoader siteLoader

    GenerateSystemShell(File cache, String gazelleUrl, TestSession testSession) {
        this.cache = cache
        this.gazelleBaseUrl = gazelleUrl
        this.testSession = testSession
        this.gazellePull = new GazellePull(gazelleBaseUrl)
        siteLoader = new SeparateSiteLoader(testSession)
        this.existingSites = siteLoader.load(cache, null)
    }

    String getLogContents(String systemName) {
        File f = new File(cache, "${systemName}.log.txt")
        return f.text
    }

    boolean run(String systemName) {
        GeneratedSystems generatedSystems = new GenerateSingleSystem(gazellePull, cache, testSession).generate(systemName)
        if (!generatedSystems)
            return true // nothing generated
        StringBuilder log = generatedSystems.log
        boolean status = !generatedSystems.hasErrors

//        if (!generatedSystems.hasErrors) {
            generatedSystems.systems.each { Site site ->
                site.setOwner(TestSession.GAZELLE_TEST_SESSION.value)
                try {
                    Site existingSite
                    try {
                        existingSite = existingSites.getSite(site.name, testSession)
                    } catch (Exception e) {
                        // site does not exist
                        existingSite = null
                    }
                    if (!existingSite || existingSite.owner == TestSession.GAZELLE_TEST_SESSION.value)
                        new SeparateSiteLoader(testSession).saveToFile(cache, site)
                    else
                        log.append("Site ${site.name} not over written, owned by Test Session ${existingSite.owner}")
                } catch (Exception e) {
                    log.append("Site ${site.name} cannot be saved - conflicts:\n")
                    log.append(e.getMessage())
                    status = false
                }
            }
//        }
        new File(cache, "${systemName}.log.txt").text = log.toString()
        return status
    }

    Collection<String> run() {
        Set<String> systemNames = new GenerateSystemNames(gazellePull, cache, testSession).getSystemNames()
        boolean status = true
        systemNames.each { String systemName ->
            try {
                boolean myStatus = run(systemName)
                if (!myStatus)
                    status = false
            } catch (Throwable t) {
                println("Cannot load - ${systemName} - ${t.message}")
            }
        }
        return systemNames
    }
}
