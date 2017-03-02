import gov.nist.toolkit.actorfactory.SimDb
import gov.nist.toolkit.actorfactory.client.NoSimException
import gov.nist.toolkit.actorfactory.client.SimId

/**
 * Created by bill on 3/2/17.
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


    static SimDb simDb(SimId simId, String actor) {
        SimDb simDb
        try {
            simDb = new SimDb(simId)
        } catch (NoSimException e) {
            if (actor)
                simDb = SimDb.mkSim(simId, actor)
            else
                throw e
        }

        return new SimDb(simId, actor, 'FHIR')
    }
}
