package gov.nist.toolkit.fhir.servlet

import ca.uhn.fhir.rest.method.RequestDetails
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.simcommon.server.SimDb

/**
 *
 */
class Attributes {
    RequestDetails requestDetails

    static final String SIMID = 'SIMID'
    static final String SIMDB = 'SIMDB'


    Attributes(RequestDetails _requestDetails) { requestDetails = _requestDetails }

    def setSimId(SimId simId) {
        requestDetails.userData.put(SIMID, simId)
    }

    SimId getSimId() {
        return (SimId) requestDetails.userData.get(SIMID)
    }

    def setSimDb(SimDb simDb) {
        requestDetails.userData.put(SIMDB, simDb)
    }

    SimDb getSimDb() {
        (SimDb) requestDetails.userData.get(SIMDB)
    }


}
