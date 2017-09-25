package gov.nist.toolkit.simcommon.server

import org.apache.http.HttpHost

/**
 *
 */
class SimEndpoint {
    String schemeName = 'http'
    String hostName
    int port = 80
    String service
    String simServiceType = 'sim'
    String actorType
    String transactionType
    String simIdString

    SimEndpoint(String endpoint) {
        assert endpoint
        assert endpoint.indexOf(' ') == -1
        int i;
        i = endpoint.indexOf('://')
        if (i > -1) {
            schemeName = endpoint.substring(0, i)
            i += 3
        } else i = 0
        service = endpoint.substring(i)
        assert service.size() > 0
        i = service.indexOf('/')
        if (i == -1)
            throw new InvalidSimEndpointException(endpoint, 'Cannot find delimiting /')
        int coloni = service.indexOf(':')
        if (coloni > -1) {
            hostName = service.substring(0, coloni)
            String portStr = service.substring(coloni+1, i)
            setPort(portStr)
        } else {
            //hostName = service.substring(0, i)
        }

        service = service.substring(i)
        String[] parts = service.substring(1).split('/')
        if (parts.size() < 3) throw new InvalidSimEndpointException(endpoint, 'Must be at least 3 / delimited parts.')
        int simStart = 0
        if (!parts[simStart].contains('sim')) simStart++
        simServiceType = parts[simStart]

        if (!simServiceType.contains('sim')) throw new InvalidSimEndpointException(endpoint, 'Service name must start with /sim/')
        assert parts.size() >= 4
        simIdString = parts[simStart+1]
        actorType = parts[simStart+2]
        transactionType = parts[simStart+3]
    }

    HttpHost getHost() {
        return new HttpHost(hostName, port, schemeName)
    }

    def setHostName(HttpHost host) {
        hostName = host.hostName
        port = host.port
        schemeName = host.schemeName
    }

    def setHostName(String hostname) {
        hostName = hostname
    }

    def setPort(String portStr) {
        try {
            port = Integer.parseInt(portStr);
        } catch (NumberFormatException var7) {
            throw new IllegalArgumentException("Invalid HTTP host: " + portStr);
        }
    }
}
