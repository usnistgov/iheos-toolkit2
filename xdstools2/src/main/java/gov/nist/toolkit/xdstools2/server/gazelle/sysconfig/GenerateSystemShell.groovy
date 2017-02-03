package gov.nist.toolkit.xdstools2.server.gazelle.sysconfig

import gov.nist.toolkit.sitemanagement.SeparateSiteLoader
import gov.nist.toolkit.sitemanagement.client.Site

/**
 *
 */
class GenerateSystemShell {
    File cache = new File('/Users/bill/tmp/actors')
//    def testingSession = '35'
    def gazelleBaseUrl // = 'https://gazelle.ihe.net/EU-CAT/systemConfigurations.seam?testingSessionId=' + testingSession
    GazellePull gazellePull

    GenerateSystemShell(File cache, String gazelleUrl) {
        this.cache = cache
        this.gazelleBaseUrl = gazelleUrl
        this.gazellePull = new GazellePull(gazelleBaseUrl)
    }

    String getLogContents(String systemName) {
        File f = new File(cache, "${systemName}.log.txt")
        return f.text
    }

    boolean run(String systemName) {
        GeneratedSystems generatedSystems = new GenerateSingleSystem(gazellePull, cache).generate(systemName)
        if (!generatedSystems)
            return true // nothing generated
        StringBuilder log = generatedSystems.log
        boolean status = !generatedSystems.hasErrors

//        if (!generatedSystems.hasErrors) {
            generatedSystems.systems.each { Site site ->
                try {
                    new SeparateSiteLoader().saveToFile(cache, site)
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
        Set<String> systemNames = new GenerateSystemNames(gazellePull, cache).getSystemNames()
        boolean status = true
        systemNames.each { String systemName ->
            boolean myStatus = run(systemName)
            if (!myStatus)
                status = false
        }
        return systemNames
    }
}
