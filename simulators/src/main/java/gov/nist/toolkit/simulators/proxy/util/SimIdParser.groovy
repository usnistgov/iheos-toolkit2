package gov.nist.toolkit.simulators.proxy.util


import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.simcommon.server.SimDoesNotExistException

/**
 *
 */
class SimIdParser {
    static SimId parse(String url) throws SimDoesNotExistException {
        int i = url.indexOf('://')
        if (i>0)
            url = url.indexOf('://') + 3
        i = url.indexOf('/')
        if (i == -1) throw SimDoesNotExistException(url)
        url = url.substring((i+1))
        String[] parts = url.split('/')
        if (parts.size() == 0) throw SimDoesNotExistException(url)
        int simIdPart = 0
        if (parts[simIdPart] == 'sim' || parts[simIdPart] == 'fsim') simIdPart++
        if (parts.size() < simIdPart) throw SimDoesNotExistException(url)
        def simIdString = parts[simIdPart]
        return new SimId(simIdString)
    }
}
