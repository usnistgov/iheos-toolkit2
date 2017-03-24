import gov.nist.toolkit.actorfactory.client.SimId
import gov.nist.toolkit.fhir.support.ResDb
import gov.nist.toolkit.installation.Installation
import org.apache.log4j.ConsoleAppender
import org.apache.log4j.Logger
import org.apache.log4j.PatternLayout

/**
 *
 */
class Tk {

    static init() {
        Logger rootLogger = Logger.getRootLogger()
        rootLogger.setLevel(org.apache.log4j.Level.DEBUG)
        PatternLayout layout = new PatternLayout("%d{ISO8601} [%t] %-5p %c %x - %m%n")
        rootLogger.addAppender(new ConsoleAppender(layout))

        Installation.instance().propertyServiceManager().loadPropertyManager(toolkitPropertiesFile)
        Installation.instance().warHome(getWarHome())

        Installation.instance().externalCache(getEc())
        if (!Installation.instance().externalCache()) {
            println 'External Cache location not set'
            System.exit(-1)
        }
    }
    static File getEc() {
        File home = new File(System.getProperty('user.home'))
        File ec = new File(new File(home, '.toolkitec').text.trim())
        if (!ec.exists())
            throw new Exception("Error loading EC location from ~/.toolkitec - EC ${ec} does not exist")
        println "External Cache is ${ec}"
        return ec
    }

    static String getToolkitName() {
        File home = new File(System.getProperty('user.home'))
        String name = new File(new File(home, '.toolkitname').text.trim())
        return name
    }

    static File getWarHome() {
        File home = new File(System.getProperty('user.home'))
        File war = new File(new File(home, '.toolkitwar').text.trim())
        if (!war.exists())
            throw new Exception("Error loading WAR location from ~/.toolkitwar - EC ${war} does not exist")
        println "warhome is ${war}"
        return war
    }

    static File getToolkitPropertiesFile() {
        File war = getWarHome()
        return new File(new File(new File(war, 'WEB-INF'), 'classes'), 'toolkit.properties')
    }

    static SimId parseSimId(String idString) {
        SimId simId = new SimId(idString)
        if (!simId.user) {
            println 'Invalid SimId - no Test Session in name'
            System.exit(-1)
        }
        if (!simId.id) {
            println 'Invalid SimId - no Id in name'
            System.exit(-1)
        }
        println "SimId.user is ${simId.user}"
        println "SimId.id is ${simId.id}"

        return simId
    }


//    static SimDb simDb(SimId simId, String actor) {
//        SimDb simDb
//        try {
//            simDb = new SimDb(simId)
//        } catch (NoSimException e) {
//            if (actor)
//                simDb = new SimDb().mkSim(simId, actor)
//            else
//                throw e
//        }
//
//        return new SimDb(simId, actor, 'FHIR')
//    }

    static ResDb resDb(SimId simId, String actor, String transaction) {
        if (!ResDb.exists(simId))
            new ResDb().mkSim(simId, actor)
        return new ResDb(simId, actor, transaction)
    }
}
