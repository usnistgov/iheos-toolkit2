package gov.nist.toolkit.simcoresupport.proxy.util


import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.simcommon.client.SimIdFactory
import gov.nist.toolkit.simcommon.server.SimDoesNotExistException
import groovy.transform.TypeChecked

/**
 *
 */
@TypeChecked
class SimIdParser {
    static SimId parse(String url) throws SimDoesNotExistException {
        String[] parts = url.split('/')
        int simi =  parts.findIndexOf { sim -> sim == 'sim' || sim == 'fsim'}
        if (simi == -1)
            throw new SimDoesNotExistException(url)
        if (simi + 1 < parts.size() )
            return SimIdFactory.simIdBuilder(parts[simi+1])
        throw new SimDoesNotExistException(url)
    }
}
