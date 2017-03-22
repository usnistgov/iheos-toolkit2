import gov.nist.toolkit.actorfactory.client.SimId
import gov.nist.toolkit.fhir.support.ResDb
/**
 *
 */
class Tk {
    static File getEc() {
        File home = new File(System.getProperty('user.home'))
        File ec = new File(new File(home, '.toolkitec').text.trim())
        if (!ec.exists())
            throw new Exception("EC ${ec} does not exist")
        return ec
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
